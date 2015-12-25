package com.gifsart.studio.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.gifsart.studio.utils.GifsArtConst;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = "camera_preview";
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private List<Camera.Size> mPreviewSizeList;
    private List<Camera.Size> mPictureSizeList;

    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;

    private boolean recording = false;
    private int counter = 0;

    private int orientation = 90;
    private int pictureFixedWidth = 640;
    private int pictureFixedHeight = 480;

    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surface_created");
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
        Log.d(TAG, "surface_changed: " + w + "   /  " + h);
        refreshCamera(mCamera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surace_destroyed");
        stopPreviewAndFreeCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (recording) {
            String path = Environment.getExternalStorageDirectory() + "/" + GifsArtConst.DIR_VIDEO_FRAMES + "/img_" + counter + ".jpg";
            savePreviewDataToFile(camera, data, path);
            counter++;
        }
    }


    public void setCamera(Camera camera) {
        //method to set a camera instance
        if (mCamera == camera) {
            return;
        }
        stopPreviewAndFreeCamera();

        mCamera = camera;
        if (mCamera != null) {
            Camera.Parameters cameraParams = mCamera.getParameters();
            mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
            mPictureSizeList = cameraParams.getSupportedPictureSizes();
        }
    }

    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);

        try {
            Camera.Parameters parameters = mCamera.getParameters();

            //mPreviewSize = getOptimalPreviewSize(mPreviewSizeList, 1280, 960);
            mPreviewSize = getBestAspectPreviewSize(90, 1280, 960, parameters, 0.1);
            //mPictureSize = getBestPictureSize(mPictureSizeList, 640, 480);

            List<int[]> fpsRange = parameters.getSupportedPreviewFpsRange();

            for (int i = 0; i < fpsRange.size(); i++) {
                Log.d("gaga", fpsRange.get(i)[0] + " / " + fpsRange.get(i)[1]);
            }

            /*if (fpsRange.size() == 1) {
                //fpsRange.get(0)[0] < CAMERA_PREVIEW_FPS < fpsRange.get(0)[1]
                parameters.setPreviewFpsRange(29000, 50000);
            } else {
                //pick first from list to limit framerate or last to maximize framerate
                parameters.setPreviewFpsRange(fpsRange.get(0)[0], fpsRange.get(0)[1]);
            }*/

            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            parameters.setPictureSize(pictureFixedWidth, pictureFixedHeight);

            int w = parameters.getPreviewSize().width;
            int h = parameters.getPreviewSize().height;
            setupLayout(w, h);

            /*if (parameters.getFlashMode().contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }*/

            /*if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }*/

            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(orientation);
            mCamera.setParameters(parameters);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

            Log.d(TAG, "camera_preview_size :   " + parameters.getPreviewSize().width + "  /  " + parameters.getPreviewSize().height);
            Log.d(TAG, "camera_picture_size :   " + parameters.getPictureSize().width + "  /  " + parameters.getPictureSize().height);
        } catch (Exception e) {
            //e.printStackTrace();
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public static Camera.Size getBestAspectPreviewSize(int displayOrientation,
                                                       int width,
                                                       int height,
                                                       Camera.Parameters parameters,
                                                       double closeEnough) {


        double targetRatio = (double) width / height;
        Camera.Size bestSize = null;

        if (displayOrientation == 90 || displayOrientation == 270) {
            targetRatio = (double) height / width;
        }

        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        TreeMap<Double, List> diffs = new TreeMap<Double, List>();


        for (Camera.Size size : sizes) {

            double ratio = (double) size.width / size.height;

            double diff = Math.abs(ratio - targetRatio);
            if (diff < closeEnough) {
                if (diffs.keySet().contains(diff)) {
                    //add the value to the list
                    diffs.get(diff).add(size);
                } else {
                    List newList = new ArrayList<Camera.Size>();
                    newList.add(size);
                    diffs.put(diff, newList);
                }

            }
        }

        //diffs now contains all of the usable sizes
        //now let's see which one has the least amount of
        for (Map.Entry entry : diffs.entrySet()) {
            List<Camera.Size> entries = (List) entry.getValue();
            for (Camera.Size s : entries) {

                if (s.width >= width && s.height >= width) {
                    bestSize = s;
                }
            }
        }

        //if we don't have bestSize then just use whatever the default was to begin with
        if (bestSize == null) {
            if (parameters.getPreviewSize() != null) {
                bestSize = parameters.getPreviewSize();
                return bestSize;
            }

            //pick the smallest difference in ratio?  or pick the largest resolution?
            //right now we are just picking the lowest ratio difference
            for (Map.Entry entry : diffs.entrySet()) {
                List<Camera.Size> entries = (List) entry.getValue();
                for (Camera.Size s : entries) {
                    if (bestSize == null) {
                        bestSize = s;
                    }
                }
            }
        }

        return bestSize;
    }

    public Camera.Size getBestPictureSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    /**
     * @param camera
     * @param data
     * @param filePath
     */
    private void savePreviewDataToFile(Camera camera, byte[] data, String filePath) {

        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;

        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 90, out);

        byte[] bytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width / 2, height / 2, true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        OutputStream imagefile = null;
        try {
            imagefile = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, imagefile);
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
            Log.e(TAG, e.getMessage());
        }
    }

    private void setupLayout(int w, int h) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.getLayoutParams();
        layoutParams.height = getResources().getDisplayMetrics().widthPixels * w / h;
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        this.setLayoutParams(layoutParams);
    }


    public void start() {
        recording = true;
    }

    public void stop() {
        recording = false;
    }

}