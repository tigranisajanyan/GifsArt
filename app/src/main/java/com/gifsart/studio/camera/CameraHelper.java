package com.gifsart.studio.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gifsart.studio.R;

import java.util.List;

/**
 * Created by Tigran on 12/10/15.
 */
public class CameraHelper {

    private static String[] flashLightModes = new String[]{
            Camera.Parameters.FLASH_MODE_OFF,
            Camera.Parameters.FLASH_MODE_TORCH,
            Camera.Parameters.FLASH_MODE_AUTO
    };

    private static int currentBurstMode = 5;
    private static int[] burstModes = new int[]{
            1, 5, 10, 15
    };

    public static boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public static int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static int findBackFacingCamera() {
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
                break;
            }
        }
        return cameraId;
    }


    public static void setNextFlashLightMode(Activity activity, Camera camera) {
        Camera.Parameters cameraParams = camera.getParameters();
        ImageButton flashLightSwitchButton = (ImageButton) activity.findViewById(R.id.flash_light_button);
        if (cameraParams.getFlashMode() == null) {
            // The phone has no flash or the choosen camera can not toggle the flash
            throw new RuntimeException("Can't turn the flash on !");
        } else {
            for (int i = 0; i < flashLightModes.length; i++) {
                if (cameraParams.getFlashMode().equals(flashLightModes[i])) {
                    int nextIndex = (i + 1) % flashLightModes.length;
                    cameraParams.setFlashMode(flashLightModes[nextIndex]);
                    camera.setParameters(cameraParams);
                    switch (nextIndex) {
                        case 0:
                            flashLightSwitchButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.flash_light_off));
                            return;
                        case 1:
                            flashLightSwitchButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.flash_light_on));
                            return;
                        case 2:
                            flashLightSwitchButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.flash_light_auto));
                            return;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public static void setNextBurstMode(Activity activity) {
        for (int i = 0; i < burstModes.length; i++) {
            if (burstModes[i] == currentBurstMode) {
                int nextIndex = (i + 1) % burstModes.length;
                currentBurstMode = burstModes[nextIndex];
                break;
            }
        }
        ((TextView) activity.findViewById(R.id.burst_mode_count)).setText("x" + currentBurstMode);
    }

    public static int getCurrentBurstMode() {
        return currentBurstMode;
    }

    public static void resetBurstMode(Activity activity) {
        currentBurstMode = 5;
        ((TextView) activity.findViewById(R.id.burst_mode_count)).setText("x" + currentBurstMode);
    }

    public static Camera.Size getBestPictureSize(List<Camera.Size> sizes, int w, int h) {
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

    private static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
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


}
