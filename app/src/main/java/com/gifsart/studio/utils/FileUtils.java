package com.gifsart.studio.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tigran on 12/28/15.
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * Saving bitmap to External Cache directory
     *
     * @param context
     * @param bitmap
     * @param fileName
     */
    public static void saveImageToCache(Context context, Bitmap bitmap, String fileName) {
        String root = context.getCacheDir().toString();
        File file = new File(root, fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Deleting file from External Cache directory
     *
     * @param context
     * @param fileName
     */
    public static void deleteFileFromCache(Context context, String fileName) {
        String root = context.getCacheDir().toString();
        File file = new File(root, fileName);
        if (file.exists()) file.delete();
    }

    /**
     * Saving bitmap to External directory
     *
     * @param bmp
     * @param filename
     */
    public static void saveBitmapToFile(Bitmap bmp, String filename) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deleting file from External directory
     *
     * @param context
     * @param filePath
     */
    public static void deleteFileFromExternal(Context context, String filePath) {
        File file = new File(filePath);
        file.delete();

    }


    public static boolean clearDir(File dir) {
        if (dir == null)
            return false;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String aChildren : children) {
                    clearDir(new File(dir, aChildren));
                }
            }
        }
        return dir.delete();
    }

    public static void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            Log.d(TAG, "clear_cache: " + e.getMessage());
        }
    }

    public static boolean deleteDir(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (String child : children) {
                boolean success = deleteDir(new File(file, child));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return file.delete();
    }

    public static int updateFileCounter(SharedPreferences sharedPreferences) {
        try {
            int currentValue = sharedPreferences.getInt("file_name_counter", 0);
            currentValue++;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("file_name_counter", currentValue);
            editor.commit();
            return currentValue;
        } catch (NullPointerException e) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("file_name_counter", 0);
            editor.commit();
            return 0;
        }
    }

    public static int getFileCounter(SharedPreferences sharedPreferences) {
        try {
            return sharedPreferences.getInt("file_name_counter", 0);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public static void resetFileCounter(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("file_name_counter", 0);
        editor.commit();
    }

    public static int checkNextCounterValue() {
        File file = new File(Environment.getExternalStorageDirectory() + "/GifsArt/.video_frames");
        int size = file.listFiles().length;
        return size;
    }


}
