package com.gifsart.studio.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.decoder.VideoDecoder;
import com.gifsart.studio.R;
import com.gifsart.studio.camera.BurstModeFramesSaving;
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
    private ProgressBar captureCicrleButtonProgressBar;

    private ImageButton captureButton, screenModeBtn, switchCameraButton;

    private LinearLayout cameraPreviewLayout;
    private boolean cameraFront = false;
    private boolean recording = false;

    private ArrayList<Integer> burstModeCounts = new ArrayList();

    private SharedPreferences sharedPreferences;
    private ArrayList<byte[]> bytes = new ArrayList<>();

    private MyCountDownTimer myCountDownTimer;

    public String[] flashLightModes = new String[]{
            Camera.Parameters.FLASH_MODE_OFF,
            Camera.Parameters.FLASH_MODE_TORCH,
            Camera.Parameters.FLASH_MODE_AUTO
    };

    private String flashMode = flashLightModes[0];
    private int burstMode = 5;

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
        if (!hasCamera(context)) {
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

        captureCicrleButtonProgressBar = (ProgressBar) findViewById(R.id.circle_progress_bar);

        ViewGroup.LayoutParams layoutParams = cameraPreviewLayout.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().widthPixels * 4 / 3;
        cameraPreviewLayout.setLayoutParams(layoutParams);

        findViewById(R.id.screen_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.burst_mode_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                burstMode = burstModeCounts.get((burstModeCounts.indexOf(burstMode) + 1) % burstModeCounts.size());
                ((TextView) findViewById(R.id.burst_mode_count)).setText("x" + burstMode);

            }
        });

        findViewById(R.id.flash_light_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cameraFront) {
                    setNextFlashLightMode(flashMode, camera);
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

    public boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
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

    View.OnTouchListener captrureListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    boolean finish = false;
                    if (recording) {
                        try {
                            mediaRecorder.stop(); // stop the recording
                        } catch (Exception e) {
                            // when mediaRecorder haven't prepared yet, will thraw an exaption which we catch here
                            finish = true;
                            recording = false;
                            cameraPreview.refreshCamera(camera);
                            myCountDownTimer.cancel();
                            captureCicrleButtonProgressBar.setVisibility(View.GONE);
                            visibilitySwitcher(View.VISIBLE);
                            visibiltySwitcherBurstMode(View.VISIBLE);
                        }
                        if (!finish) {
                            captureButton.setOnTouchListener(null);
                            captureButton.setOnLongClickListener(null);
                            releaseMediaRecorder(); // release the MediaRecorder object
                            Toast.makeText(ShootingGifActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                            recording = false;
                            myCountDownTimer.cancel();
                            captureCicrleButtonProgressBar.setVisibility(View.INVISIBLE);
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
                        visibiltySwitcherBurstMode(View.VISIBLE);
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
            visibiltySwitcherBurstMode(View.INVISIBLE);
            mediaRecorder.start();
            recording = true;
            myCountDownTimer = new MyCountDownTimer(7100, 1000);
            myCountDownTimer.start();
            captureCicrleButtonProgressBar.setProgress(0);
            captureCicrleButtonProgressBar.setVisibility(View.VISIBLE);
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
                    sendCapturedVideoFramesWithIntent(new Intent(ShootingGifActivity.this, MakeGifActivity.class));
                } else {
                    sendCapturedVideoFramesWithIntent(new Intent());
                }
                File file = new File(GifsArtConst.VIDEOS_DECODED_FRAMES_DIR);
                CheckSpaceSingleton.getInstance().addAllocatedSpaceInt(file.listFiles().length * 2 / 3);
                animatedProgressDialog.dismiss();
                finish();
            }
        });
    }

    public void saveBurstModeFrames() {
        final AnimatedProgressDialog animatedProgressDialog = new AnimatedProgressDialog(ShootingGifActivity.this);
        animatedProgressDialog.setCancelable(false);
        animatedProgressDialog.show();
        BurstModeFramesSaving burstModeFramesSaving = new BurstModeFramesSaving(bytes);
        burstModeFramesSaving.setFramesSavedListener(new BurstModeFramesSaving.FramesSaved() {
            @Override
            public void done(boolean done) {
                if (done) {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int i = 0; i < burstMode; i++) {
                        strings.add(Environment.getExternalStorageDirectory() + "/GifsArt/video_frames/img_" + i + ".jpg");
                    }
                    if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                        Intent intent = new Intent(ShootingGifActivity.this, MakeGifActivity.class);
                        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                        intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, strings);
                        intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, true);
                        intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                        intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, strings);
                        intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, true);
                        intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
                        setResult(RESULT_OK, intent);
                    }
                    CheckSpaceSingleton.getInstance().addAllocatedSpaceInt(burstMode);
                    animatedProgressDialog.dismiss();
                    finish();
                }
            }
        });
        burstModeFramesSaving.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void sendCapturedVideoFramesWithIntent(Intent intent) {
        intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_SHOOT_GIF);
        intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
        intent.putExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
        intent.putExtra(GifsArtConst.INTENT_VIDEO_PATH, GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME);
        intent.putExtra(GifsArtConst.INTENT_CAMERA_BURST_MODE, false);
        if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
            startActivity(intent);
        } else {
            setResult(RESULT_OK, intent);
        }
    }

    public void visibilitySwitcher(int visibility) {
        findViewById(R.id.flash_light_button).setVisibility(visibility);
        findViewById(R.id.cancel_button).setVisibility(visibility);
        screenModeBtn.setVisibility(visibility);
        switchCameraButton.setVisibility(visibility);
    }

    public void visibiltySwitcherBurstMode(int visibility) {
        findViewById(R.id.burst_mode_image).setVisibility(visibility);
        findViewById(R.id.capture_time).setVisibility(visibility);
        findViewById(R.id.burst_count).setVisibility(visibility);
        findViewById(R.id.capture_time).setVisibility(visibility == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
    }

    public void burstModeRecursion(final int n) {
        if (n == 0) {
            findViewById(R.id.burst_count).setVisibility(View.INVISIBLE);
            if (CheckSpaceSingleton.getInstance().haveEnoughSpaceInt(burstMode)) {
                saveBurstModeFrames();
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

    public void setNextFlashLightMode(String currentFlashMode, Camera camera) {
        Camera.Parameters cameraParams = camera.getParameters();
        ImageButton flashLightSwitchButton = (ImageButton) findViewById(R.id.flash_light_button);
        for (int i = 0; i < flashLightModes.length; i++) {
            if (currentFlashMode.equals(flashLightModes[i])) {
                int nextIndex = (i + 1) % flashLightModes.length;
                flashMode = flashLightModes[nextIndex];
                cameraParams.setFlashMode(flashMode);
                camera.setParameters(cameraParams);
                switch (nextIndex) {
                    case 0:
                        flashLightSwitchButton.setImageDrawable(getResources().getDrawable(R.drawable.flash_light_off));
                        break;
                    case 1:
                        flashLightSwitchButton.setImageDrawable(getResources().getDrawable(R.drawable.flash_light_on));
                        break;
                    case 2:
                        flashLightSwitchButton.setImageDrawable(getResources().getDrawable(R.drawable.flash_light_auto));
                        break;
                    default:
                        break;
                }
            }
        }
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
            ((TextView) findViewById(R.id.capture_time)).setText("00:0" + ((millisUntilFinished / 1000) - 1));
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
                    recording = false;
                    captureCicrleButtonProgressBar.setVisibility(View.INVISIBLE);
                    if (CheckSpaceSingleton.getInstance().haveEnoughSpace(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME)) {
                        saveCapturedVideoFrames();
                    } else {
                        setResult(RESULT_CANCELED);
                        finish();
                        Toast.makeText(context, "No enough space", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}
