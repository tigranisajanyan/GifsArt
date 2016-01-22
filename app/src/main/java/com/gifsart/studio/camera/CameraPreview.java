package com.gifsart.studio.camera;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String LOG_TAG = CameraPreview.class.getSimpleName();
    private static final int PREVIEW_ORIENTATION = 90;
    private static final int PREVIEW_FIXED_WIDTH = 640;
    private static final int PREVIEW_FIXED_HEIGHT = 480;
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private List<Camera.Size> mPreviewSizeList;
    private List<Camera.Size> mPictureSizeList;

    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;

    private boolean recording = false;

    private int pictureFormat;

    public static ArrayList<byte[]> datas = new ArrayList<>();
    private StartRendering startRendering;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(LOG_TAG, "Surface Created");
        try {
            // create the surface and start camera preview
            if (mCamera == null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        Log.d(LOG_TAG, "Surface Changed: " + w + "   /  " + h);
        refreshCamera(mCamera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_TAG, "Surace Destroyed");
        //stopPreviewAndFreeCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (recording) {
            addPreviewDataToList(camera, data);
        }
    }


    public void setCamera(Camera camera) {
        //method to set a camera instance
        if (mCamera == camera) {
            return;
        }
        //stopPreviewAndFreeCamera();

        mCamera = camera;
        if (mCamera != null) {
            Camera.Parameters cameraParams = mCamera.getParameters();
            mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
            mPictureSizeList = cameraParams.getSupportedPictureSizes();
        }
    }

    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null) {
            Log.d(LOG_TAG, "preview surface does not exist");
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.d(LOG_TAG, "tried to stop a non-existent preview");
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);

        try {
            Camera.Parameters parameters = mCamera.getParameters();

            //mPreviewSize = getOptimalPreviewSize(mPreviewSizeList, 1280, 960);
            //mPictureSize = getBestPictureSize(mPictureSizeList, 640, 480);

            mPreviewSize = CameraHelper.getBestAspectPreviewSize(PREVIEW_ORIENTATION, PREVIEW_FIXED_WIDTH, PREVIEW_FIXED_HEIGHT, parameters);

            /*List<int[]> fpsRange = parameters.getSupportedPreviewFpsRange();
            for (int i = 0; i < fpsRange.size(); i++) {
                Log.d("gaga", fpsRange.get(i)[0] + " / " + fpsRange.get(i)[1]);
            }*/

            /*if (fpsRange.size() == 1) {
                //fpsRange.get(0)[0] < CAMERA_PREVIEW_FPS < fpsRange.get(0)[1]
                parameters.setPreviewFpsRange(29000, 50000);
            } else {
                //pick first from list to limit framerate or last to maximize framerate
                parameters.setPreviewFpsRange(fpsRange.get(0)[0], fpsRange.get(0)[1]);
            }*/

            //parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

            parameters.setPreviewSize(PREVIEW_FIXED_WIDTH, PREVIEW_FIXED_HEIGHT);
            parameters.setPictureSize(PREVIEW_FIXED_WIDTH, PREVIEW_FIXED_HEIGHT);

            int w = parameters.getPreviewSize().width;
            int h = parameters.getPreviewSize().height;
            setupLayout(w, h);

            /*if (parameters.getFlashMode().contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }*/

            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            pictureFormat = camera.getParameters().getPreviewFormat();

            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(PREVIEW_ORIENTATION);
            mCamera.setParameters(parameters);
            mCamera.autoFocus(null);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

            Log.d(LOG_TAG, "camera_preview_size :   " + parameters.getPreviewSize().width + "  /  " + parameters.getPreviewSize().height);
            Log.d(LOG_TAG, "camera_picture_size :   " + parameters.getPictureSize().width + "  /  " + parameters.getPictureSize().height);

        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


    /**
     * @param camera
     * @param data
     */
    public void addPreviewDataToList(Camera camera, byte[] data) {
        YuvImage yuv = new YuvImage(data, pictureFormat, PREVIEW_FIXED_WIDTH, PREVIEW_FIXED_HEIGHT, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, PREVIEW_FIXED_WIDTH, PREVIEW_FIXED_HEIGHT), PREVIEW_ORIENTATION, out);
        byte[] bytes = out.toByteArray();
        datas.add(bytes);
        if (datas.size() == 5) {
            startRendering.toStart(true);
        }
        /*Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);*/


        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, pictureFixedWidth / 2, pictureFixedHeight / 2, true);
        //Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        /*OutputStream imagefile = null;
        try {
            imagefile = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, imagefile);*/
        //PhotoUtils.saveRawBitmap(rotatedBitmap, filePath);
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {
        try {
            if (mCamera != null) {
                //mHolder.removeCallback(this);
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void setupLayout(int w, int h) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.getLayoutParams();
        layoutParams.height = getResources().getDisplayMetrics().widthPixels * w / h;
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        this.setLayoutParams(layoutParams);
    }


    public void startRecord() {
        recording = true;
    }

    public void stopRecord() {
        recording = false;
    }

    public interface StartRendering {
        void toStart(boolean start);
    }

    public void setRenderingListener(StartRendering rendering) {
        this.startRendering = rendering;
    }

}