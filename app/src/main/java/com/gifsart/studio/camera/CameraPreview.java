package com.gifsart.studio.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    protected List<Camera.Size> mPreviewSizeList;
    protected List<Camera.Size> mPictureSizeList;
    protected Camera.Size mPreviewSize;
    protected Camera.Size mPictureSize;
    int orientation = 90;

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


    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
        Camera.Parameters cameraParams = mCamera.getParameters();
        mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
        mPictureSizeList = cameraParams.getSupportedPictureSizes();
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

            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

            //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mPreviewSize.height, mPreviewSize.width);
            //this.setLayoutParams(lp);

            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(orientation);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            /*for (int i = 0; i < parameters.getSupportedPictureSizes().size(); i++) {
                Log.d("gagaga", parameters.getSupportedPictureSizes().get(i).height + "  /  " + parameters.getSupportedPictureSizes().get(i).width);
            }*/
            Log.d("GifsArt", "camera_preview_size :   " + mPreviewSize.width + "  /  " + mPreviewSize.height);
            Log.d("GifsArt", "camera_picture_size :   " + mPictureSize.width + "  /  " + mPictureSize.height);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


    public void surfaceCreated(SurfaceHolder holder) {
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
        mPreviewSize = determinePreviewSize(true, w, h);
        mPictureSize = getBestPictureSize(mPictureSizeList, mPreviewSize.width, mPreviewSize.height);
        refreshCamera(mCamera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mCamera.release();
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

    protected Camera.Size determinePreviewSize(boolean portrait, int reqWidth, int reqHeight) {
        // Meaning of width and height is switched for preview when portrait,
        // while it is the same as user's view for surface and metrics.
        // That is, width must always be larger than height for setPreviewSize.
        int reqPreviewWidth; // requested width in terms of camera hardware
        int reqPreviewHeight; // requested height in terms of camera hardware
        if (portrait) {
            reqPreviewWidth = reqHeight;
            reqPreviewHeight = reqWidth;
        } else {
            reqPreviewWidth = reqWidth;
            reqPreviewHeight = reqHeight;
        }

        if (true) {
            Log.v("gag", "Listing all supported preview sizes");
            for (Camera.Size size : mPreviewSizeList) {
                Log.v("gag", "  w: " + size.width + ", h: " + size.height);
            }
            Log.v("gag", "Listing all supported picture sizes");
            for (Camera.Size size : mPictureSizeList) {
                Log.v("gag", "  w: " + size.width + ", h: " + size.height);
            }
        }

        // Adjust surface size with the closest aspect-ratio
        float reqRatio = ((float) reqPreviewWidth) / reqPreviewHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : mPreviewSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }

    protected Camera.Size determinePictureSize(Camera.Size previewSize) {
        Camera.Size retSize = null;
        for (Camera.Size size : mPictureSizeList) {
            if (size.equals(previewSize)) {
                return size;
            }
        }

        if (true) {
            Log.v("gag", "Same picture size not found.");
        }

        // if the preview size is not supported as a picture size
        float reqRatio = ((float) previewSize.width) / previewSize.height;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        for (Camera.Size size : mPictureSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
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

    private float scaleX = 1f;
    private float scaleY = 1f;

    /*public void updateScaling(int width, int height, boolean isPortrait) {
        //Camera.Size mPreviewSize = getOptimalPreviewSize(mCamera.getParameters().getSupportedPreviewSizes(), 960, 1280);
        float ratioSurface = width > height ? (float) width / height : (float) height / width;
        float ratioPreview = (float) mPreviewSize.getWidth() / mPreviewSize.getHeight();
        int scaledHeight = 0;
        int scaledWidth = 0;

        if (mPreviewSize != null) {

            if (isPortrait && ratioPreview > ratioSurface) {
                scaledWidth = width;
                scaledHeight = (int) (((float) mPreviewSize.getWidth() / mPreviewSize.getHeight()) * width);
                scaleX = 1f;
                scaleY = (float) scaledHeight / height;
            } else if (isPortrait && ratioPreview < ratioSurface) {
                scaledWidth = (int) (height / ((float) mPreviewSize.getWidth() / mPreviewSize.getHeight()));
                scaledHeight = height;
                scaleX = (float) scaledWidth / width;
                scaleY = 1f;
            } else if (!isPortrait && ratioPreview < ratioSurface) {
                scaledWidth = width;
                scaledHeight = (int) (width / ((float) mPreviewSize.getWidth() / mPreviewSize.getHeight()));
                scaleX = 1f;
                scaleY = (float) scaledHeight / height;
            } else if (!isPortrait && ratioPreview > ratioSurface) {
                scaledWidth = (int) (((float) mPreviewSize.getWidth() / mPreviewSize.getHeight()) * width);
                scaledHeight = height;
                scaleX = (float) scaledWidth / width;
                scaleY = 1f;
            }
        }

    }*/

}