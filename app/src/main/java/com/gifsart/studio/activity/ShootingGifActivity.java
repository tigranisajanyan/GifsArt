package com.gifsart.studio.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.decoder.VideoDecoder;
import com.gifsart.studio.R;
import com.gifsart.studio.utils.CameraPreview;
import com.gifsart.studio.utils.CheckSpaceSingleton;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Utils;
import com.socialin.android.photo.imgop.ImageOp;
import com.socialin.android.photo.imgop.ImageOpCommon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class ShootingGifActivity extends ActionBarActivity {

    private Context context;

    private Camera camera;
    private CameraPreview cameraPreview;
    private MediaRecorder mediaRecorder;

    private ImageButton capture;
    private ImageButton switchCamera;

    private LinearLayout cameraPreviewLayout;
    private boolean cameraFront = false;

    private int currentCapturedTime;
    private int capturedTime;
    private Thread myThread = null;

    ArrayList<Integer> burstModeCounts = new ArrayList();

    int burstMode = 5;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shooting_gif);
        context = this;

        Utils.clearDir(new File(Environment.getExternalStorageDirectory() + "/GifsArt/video_frames"));
        burstModeCounts.add(5);
        burstModeCounts.add(10);
        burstModeCounts.add(15);
        sharedPreferences = getApplicationContext().getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, MODE_PRIVATE);
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
                switchCamera.setVisibility(View.GONE);
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

        capture = (ImageButton) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);
        capture.setOnLongClickListener(onLongClickListener);

        switchCamera = (ImageButton) findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);

        cameraPreviewLayout.addView(cameraPreview);

        findViewById(R.id.burst_mode_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                burstMode = burstModeCounts.get((burstModeCounts.indexOf(burstMode) + 1) % burstModeCounts.size());
                ((TextView) findViewById(R.id.burst_mode_count)).setText("x" + burstMode);
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
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        camera.unlock();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

        //mediaRecorder.setVideoFrameRate(2);

        mediaRecorder.setOrientationHint(GifsArtConst.VIDEO_OUTPUT_ORIENTATION);

        File file = new File(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR, GifsArtConst.VIDEO_NAME);
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


    private boolean hasCamera(Context context) {
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


    public void takePic(final int n) {
        if (n == 0) {
            if (!sharedPreferences.getBoolean(GifsArtConst.SHARED_PREFERENCES_IS_OPENED, false)) {
                Intent intent = new Intent(ShootingGifActivity.this, MakeGifActivity.class);
                intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                ArrayList<String> strings = new ArrayList<>();
                for (int i = burstMode; i > 0; i--) {
                    strings.add(Environment.getExternalStorageDirectory() + "/GifsArt/video_frames/img" + i + ".jpg");
                }
                intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, strings);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent();
                intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_FROM_GALLERY_TO_GIF);
                ArrayList<String> strings = new ArrayList<>();
                for (int i = burstMode; i > 0; i--) {
                    strings.add(Environment.getExternalStorageDirectory() + "/GifsArt/video_frames/img" + i + ".jpg");
                }
                intent.putStringArrayListExtra(GifsArtConst.INTENT_DECODED_IMAGE_PATHS, strings);
                setResult(RESULT_OK, intent);
                finish();
            }

            return;
        } else {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                    /*File pictureFile = new File(Environment.getExternalStorageDirectory() + "/GifsArt/video_frames/", "img" + n + ".jpg");

                    if (pictureFile == null) {
                        return;
                    }
                    try {
                        //write the file
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        fos.write(data);
                        fos.close();

                    } catch (FileNotFoundException e) {
                    } catch (IOException e) {
                    }*/
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/GifsArt/video_frames/", "img" + n + ".jpg"));
                        bitmap = rotate(bitmap, 90);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
                        fileOutputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cameraPreview.refreshCamera(camera);
                    ((TextView) findViewById(R.id.burst_count)).setText(n + "");
                    takePic(n - 1);
                }
            });
        }

    }

    public Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    boolean recording = false;
    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording) {
                // stop recording and release camera
                myThread.interrupt();

                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(ShootingGifActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                recording = false;

                if (CheckSpaceSingleton.getInstance().haveEnoughSpace(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME)) {
                    saveCapturedVideoFrames();
                } else {
                    finish();
                    Toast.makeText(context, "No enough space", Toast.LENGTH_SHORT).show();
                }

            } else {
                findViewById(R.id.burst_mode_image).setVisibility(View.VISIBLE);
                findViewById(R.id.capture_time).setVisibility(View.INVISIBLE);
                findViewById(R.id.burst_count).setVisibility(View.VISIBLE);
                int n = burstMode;
                takePic(n);
            }
        }
    };

    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            findViewById(R.id.burst_mode_image).setVisibility(View.INVISIBLE);
            findViewById(R.id.capture_time).setVisibility(View.VISIBLE);
            if (!prepareMediaRecorder()) {
                Toast.makeText(ShootingGifActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                finish();
            }

            currentCapturedTime = 0;

            Runnable myRunnableThread = new CountDownRunner();
            myThread = new Thread(myRunnableThread);
            myThread.start();

            // work on UiThread for better performance
            runOnUiThread(new Runnable() {
                public void run() {
                    // If there are stories, add them to the table
                    try {
                        mediaRecorder.start();

                    } catch (final Exception ex) {
                        // Log.i("---","Exception in thread");
                    }
                }
            });

            recording = true;
            return true;
        }
    };

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {

                    ((TextView) findViewById(R.id.capture_time)).setText(currentCapturedTime / 10.0 + "");
                    if (currentCapturedTime / 10.0 > 6) {
                        // stop recording and release camera
                        myThread.interrupt();

                        mediaRecorder.stop(); // stop the recording
                        releaseMediaRecorder(); // release the MediaRecorder object
                        Toast.makeText(ShootingGifActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                        recording = false;

                        saveCapturedVideoFrames();
                    }

                    currentCapturedTime++;
                    if (currentCapturedTime == capturedTime) {
                        captrureListener.onClick(capture);
                    }

                } catch (Exception e) {
                }
            }
        });
    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void saveCapturedVideoFrames() {
        final ProgressDialog progressDialog = new ProgressDialog(ShootingGifActivity.this);
        progressDialog.setTitle("Generating Frames");
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(GifsArtConst.INTENT_FRONT_CAMERA, cameraFront);
                    intent.putExtra(GifsArtConst.INTENT_ACTIVITY_INDEX, GifsArtConst.INDEX_SHOOT_GIF);
                    intent.putExtra(GifsArtConst.INTENT_VIDEO_FRAME_SCALE_SIZE, GifsArtConst.VIDEO_FRAME_SCALE_SIZE);
                    intent.putExtra(GifsArtConst.INTENT_VIDEO_PATH, GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME);
                    setResult(RESULT_OK, intent);
                }
                CheckSpaceSingleton.getInstance().addAllocatedSpace(GifsArtConst.SHOOTING_VIDEO_OUTPUT_DIR + "/" + GifsArtConst.VIDEO_NAME);
                progressDialog.dismiss();
                finish();

            }
        });
    }

}
