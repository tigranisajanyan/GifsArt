package com.gifsart.studio.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.decoder.VideoDecoder;
import com.gifsart.studio.R;
import com.gifsart.studio.utils.CameraPreview;
import com.gifsart.studio.utils.GifsArtConst;

import java.io.File;
import java.io.IOException;


public class ShootingGifActivity extends ActionBarActivity {

    private Camera camera;
    private CameraPreview cameraPreview;
    private MediaRecorder mediaRecorder;
    private CheckBox capture;
    private ImageButton switchCamera;
    private Context context;
    private LinearLayout cameraPreviewLayout;
    private TextView secondsText;
    private boolean cameraFront = false;

    private static final String root = Environment.getExternalStorageDirectory().toString();

    private int currentCapturedTime;
    private int capturedTime;
    private Thread myThread = null;

    int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shooting_gif);
        context = this;
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

        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();

        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(800, 1280);
        //layoutParams.gravity= Gravity.CENTER_HORIZONTAL;
        cameraPreviewLayout = (LinearLayout) findViewById(R.id.camera_preview);
        //cameraPreviewLayout.setLayoutParams(layoutParams);

        cameraPreview = new CameraPreview(context, camera);

        capture = (CheckBox) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

        switchCamera = (ImageButton) findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);

        cameraPreviewLayout.addView(cameraPreview);

        secondsText = (TextView) findViewById(R.id.text_seconds);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        // Search for the back facing camera
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
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        //mediaRecorder.setVideoFrameRate(2);

        mediaRecorder.setOrientationHint(GifsArtConst.CAMERA_OUTPUT_ORIENTATION);

        File file = new File(root, GifsArtConst.VIDEO_NAME);
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        mediaRecorder.setMaxDuration(GifsArtConst.VIDEO_MAX_DURATION); // Set max duration 90 sec.
        mediaRecorder.setMaxFileSize(GifsArtConst.VIDEO_FILE_MAX_SIZE); // Set max file size 50M

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
                    // switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(context, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };


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
                final ProgressDialog progressDialog = new ProgressDialog(ShootingGifActivity.this);
                progressDialog.setTitle("Generating Frames");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.show();
                VideoDecoder videoDecoder = new VideoDecoder(ShootingGifActivity.this, root + GifsArtConst.SLASH + GifsArtConst.VIDEO_NAME, Integer.MAX_VALUE, 2, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR);
                videoDecoder.extractVideoFrames();
                videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                    @Override
                    public void onFinish(boolean isDone) {
                        Intent intent = new Intent(ShootingGifActivity.this, MakeGifActivity.class);
                        intent.putExtra(GifsArtConst.FRONT_CAMERA, cameraFront);
                        intent.putExtra(GifsArtConst.INDEX, GifsArtConst.SHOOT_GIF_INDEX);
                        intent.putExtra("frame_size", 2);
                        intent.putExtra(GifsArtConst.VIDEO_PATH, root + GifsArtConst.SLASH + GifsArtConst.VIDEO_NAME);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();
                    }
                });
            } else {
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
            }
        }
    };


    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {

                    if (currentCapturedTime / 10.0 > 10) {
                        // stop recording and release camera
                        myThread.interrupt();

                        mediaRecorder.stop(); // stop the recording
                        releaseMediaRecorder(); // release the MediaRecorder object
                        Toast.makeText(ShootingGifActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                        recording = false;
                        final ProgressDialog progressDialog = new ProgressDialog(ShootingGifActivity.this);
                        progressDialog.setTitle("Generating Frames");
                        progressDialog.setMessage("Please Wait");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        VideoDecoder videoDecoder = new VideoDecoder(ShootingGifActivity.this, root + GifsArtConst.SLASH + GifsArtConst.VIDEO_NAME, Integer.MAX_VALUE, 2, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR);
                        videoDecoder.extractVideoFrames();
                        videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                            @Override
                            public void onFinish(boolean isDone) {
                                Intent intent = new Intent(ShootingGifActivity.this, MakeGifActivity.class);
                                intent.putExtra(GifsArtConst.INDEX, GifsArtConst.SHOOT_GIF_INDEX);
                                intent.putExtra("frame_size", 2);
                                intent.putExtra(GifsArtConst.VIDEO_PATH, root + GifsArtConst.SLASH + GifsArtConst.MY_DIR);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }
                        });
                    }
                    secondsText.setText(" " + currentCapturedTime / 10.0);

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

}
