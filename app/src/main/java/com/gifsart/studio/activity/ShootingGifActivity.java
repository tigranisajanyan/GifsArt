package com.gifsart.studio.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.decoder.VideoDecoder;
import com.gifsart.studio.R;
import com.gifsart.studio.camera.BurstModeAper;
import com.gifsart.studio.camera.CameraPrepair;
import com.gifsart.studio.utils.AnimatedProgressDialog;
import com.gifsart.studio.camera.CameraPreview;
import com.gifsart.studio.utils.CheckSpaceSingleton;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ShootingGifActivity extends ActionBarActivity {

    private Context context;
    private Camera camera;
    private CameraPreview cameraPreview;
    private MediaRecorder mediaRecorder;

    private ImageButton captureButton, screenModeBtn, switchCameraButton;

    private LinearLayout cameraPreviewLayout;
    private boolean cameraFront = false;

    private int currentCapturedTime;
    private ArrayList<Integer> burstModeCounts = new ArrayList();

    private int burstMode = 5;

    private SharedPreferences sharedPreferences;
    private ArrayList<byte[]> bytes = new ArrayList<>();

    private MyCountDownTimer myCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shooting_gif);

        context = this;
        sharedPreferences = getApplicationContext().getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);

        Utils.clearDir(new File(Environment.getExternalStorageDirectory() + "/" + GifsArtConst.DIR_VIDEO_FRAMES));

        burstModeCounts.add(5);
        burstModeCounts.add(10);
        burstModeCounts.add(15);

        init();
    }

    public void onResume() {
        super.onResume();
        if (!CameraPrepair.getInstance().hasCamera(context)) {
            Toast toast = Toast.makeText(context, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (camera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {

                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCameraButton.setVisibility(View.GONE);
            }
            camera = Camera.open(findBackFacingCamera());
            cameraPreview.refreshCamera(camera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void init() {
        cameraPreviewLayout = (LinearLayout) findViewById(R.id.camera_preview);
        cameraPreview = new CameraPreview(context, camera);

        captureButton = (ImageButton) findViewById(R.id.button_capture);
        captureButton.setOnTouchListener(captrureListener);
        captureButton.setOnLongClickListener(onLongClickListener);

        switchCameraButton = (ImageButton) findViewById(R.id.switch_camera_button);
        switchCameraButton.setOnClickListener(switchCameraListener);

        cameraPreviewLayout.addView(cameraPreview);

        screenModeBtn = (ImageButton) findViewById(R.id.screen_mode);

        findViewById(R.id.burst_mode_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                burstMode = burstModeCounts.get((burstModeCounts.indexOf(burstMode) + 1) % burstModeCounts.size());
                ((TextView) findViewById(R.id.burst_mode_count)).setText("x" + burstMode);

            }
        });

        findViewById(R.id.flashlightButton).setOnClickListener(new View.OnClickListener() {
            int count;

            @Override
            public void onClick(View v) {
                if (!cameraFront) {
                    Camera.Parameters cameraParams = camera.getParameters();
                    count++;
                    switch (count) {
                        case 1:
                            cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            camera.setParameters(cameraParams);
                            break;
                        case 2:
                            cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                            camera.setParameters(cameraParams);
                            break;
                        case 3:
                            //TODO flashlight off/deafault mode
                            cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            camera.setParameters(cameraParams);
                            count = 0;
                            break;
                    }
                }
            }
        });
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the corner facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {//TODO add other expression (&&)
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {
        File file = new File(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR, GifsArtConst.VIDEO_NAME);
        camera.unlock();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        if (profile.videoFrameHeight > 720) {
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        } else {
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        }
        mediaRecorder.setOrientationHint(GifsArtConst.VIDEO_OUTPUT_ORIENTATION);
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        mediaRecorder.setMaxDuration(GifsArtConst.VIDEO_MAX_DURATION); // Set max duration 30 sec.
        mediaRecorder.setMaxFileSize(GifsArtConst.VIDEO_FILE_MAX_SIZE); // Set max file size 40M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                camera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                cameraPreview.refreshCamera(camera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                camera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                cameraPreview.refreshCamera(camera);
            }
        }
    }

    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the corner and vice versa
                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(context, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };


    public void burstModeRecursion(final int n) {
        if (n == 0) {
            if (CheckSpaceSingleton.getInstance().haveEnoughSpaceInt(burstMode)) {
                BurstModeAper burstModeAper = new BurstModeAper(bytes);
                burstModeAper.setFramesSavedListener(new BurstModeAper.FramesSaved() {
                    @Override
                    public void done(boolean done) {
                        if (done) {
                            if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                                ArrayList<String> strings = new ArrayList<>();
                                for (int i = 0; i < burstMode; i++) {
                                    strings.add(Environment.getExternalStorageDirectory() + "/GifsArt/video_frames/img_" + i + ".jpg");
                                }

                                Intent intent = new Intent(ShootingGifActivity.this, MakeGifActivity.class);
                                intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                                intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, strings);
                                intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, true);
                                startActivity(intent);
                                finish();
                            } else {
                                ArrayList<String> strings = new ArrayList<>();
                                for (int i = 0; i < burstMode; i++) {
                                    strings.add(Environment.getExternalStorageDirectory() + "/GifsArt/video_frames/img_" + i + ".jpg");
                                }

                                Intent intent = new Intent();
                                intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                                intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, strings);
                                intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, true);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            CheckSpaceSingleton.getInstance().addAllocatedSpaceInt(burstMode);
                        }
                    }
                });
                burstModeAper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                setResult(RESULT_CANCELED);
                finish();
                Toast.makeText(ShootingGifActivity.this, "No enough space", Toast.LENGTH_SHORT).show();
            }
            return;
        } else {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    camera.startPreview();
                    bytes.add(data);
                    ((TextView) findViewById(R.id.burst_count)).setText(n + "");
                    burstModeRecursion(n - 1);
                }
            });
            camera.startPreview();
        }

    }

    boolean recording = false;

    View.OnTouchListener captrureListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    boolean finish = false;
                    if (recording) {
                        try {
                            mediaRecorder.stop(); // stop the recording
                        } catch (Exception e) {
                            finish = true;
                            recording = false;
                            cameraPreview.refreshCamera(camera);
                            findViewById(R.id.capture_time).setVisibility(View.INVISIBLE);
                            findViewById(R.id.burst_mode_image).setVisibility(View.VISIBLE);
                            findViewById(R.id.burst_count).setVisibility(View.VISIBLE);
                        }
                        if (!finish) {
                            captureButton.setOnTouchListener(null);
                            captureButton.setOnLongClickListener(null);
                            releaseMediaRecorder(); // release the MediaRecorder object
                            Toast.makeText(ShootingGifActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                            recording = false;
                            myCountDownTimer.cancel();
                            if (CheckSpaceSingleton.getInstance().haveEnoughSpace(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME)) {
                                saveCapturedVideoFrames();
                            } else {
                                setResult(RESULT_CANCELED);
                                finish();
                                Toast.makeText(context, "No enough space", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        findViewById(R.id.burst_mode_image).setOnClickListener(null);
                        captureButton.setOnTouchListener(null);
                        captureButton.setOnLongClickListener(null);
                        visibilitySwitcher(View.INVISIBLE);
                        findViewById(R.id.burst_mode_image).setVisibility(View.VISIBLE);
                        findViewById(R.id.capture_time).setVisibility(View.INVISIBLE);
                        findViewById(R.id.burst_count).setVisibility(View.VISIBLE);
                        int n = burstMode;
                        burstModeRecursion(n);
                    }
                default:
                    return false;
            }
            return false;
        }
    };

    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (!prepareMediaRecorder()) {
                Toast.makeText(ShootingGifActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                finish();
            }
            visibilitySwitcher(View.INVISIBLE);
            findViewById(R.id.capture_time).setVisibility(View.VISIBLE);
            findViewById(R.id.burst_mode_image).setVisibility(View.INVISIBLE);
            mediaRecorder.start();
            recording = true;
            myCountDownTimer = new MyCountDownTimer(7000, 1000);
            myCountDownTimer.start();
            return false;
        }
    };

    public void saveCapturedVideoFrames() {
        final AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(ShootingGifActivity.this);
        animatedProgressDialog.setCancelable(false);
        animatedProgressDialog.show();
        VideoDecoder videoDecoder = new VideoDecoder(ShootingGifActivity.this, GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME, Integer.MAX_VALUE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
        videoDecoder.extractVideoFrames();
        videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
            @Override
            public void onFinish(boolean isDone) {
                if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                    Intent intent = new Intent(ShootingGifActivity.this, MakeGifActivity.class);
                    intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_SHOOT_GIF);
                    intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
                    intent.putExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
                    intent.putExtra(GifsArtConst.INTENT_VIDEO_PATH, GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME);
                    intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, false);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
                    intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_SHOOT_GIF);
                    intent.putExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
                    intent.putExtra(GifsArtConst.INTENT_VIDEO_PATH, GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME);
                    intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, false);
                    setResult(RESULT_OK, intent);
                }
                CheckSpaceSingleton.getInstance().addAllocatedSpaceFromFilePath(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME);
                animatedProgressDialog.dismiss();
                finish();
            }
        });
    }

    public void visibilitySwitcher(int visibility) {
        findViewById(R.id.flashlightButton).setVisibility(visibility);
        switchCameraButton.setVisibility(visibility);
        findViewById(R.id.cancel_button).setVisibility(visibility);
        screenModeBtn.setVisibility(visibility);

    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
        }

        @Override
        public void onTick(long millisUntilFinished) {
            ((TextView) findViewById(R.id.capture_time)).setText("" + millisUntilFinished / 1000);
            Log.d("gagag", "" + millisUntilFinished);
            if (millisUntilFinished <= 2000) {
                if (recording) {
                    try {
                        mediaRecorder.stop(); // stop the recording
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureButton.setOnTouchListener(null);
                    captureButton.setOnLongClickListener(null);
                    releaseMediaRecorder(); // release the MediaRecorder object
                    Toast.makeText(ShootingGifActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                    recording = false;
                    //if (CheckSpaceSingleton.getInstance().haveEnoughSpace(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME)) {
                    saveCapturedVideoFrames();
                    /*} else {
                        setResult(RESULT_CANCELED);
                        finish();
                        Toast.makeText(context, "No enough space", Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
        }
    }

}
