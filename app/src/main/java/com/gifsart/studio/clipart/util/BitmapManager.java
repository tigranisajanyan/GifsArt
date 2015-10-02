package com.gifsart.studio.clipart.util;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

public class BitmapManager {

    public static final String TAG = BitmapManager.class.getSimpleName();
    public static final int SCALE_PROPOTIONAL = 0;
    public static final int SCALE_ABSOLUTE = 1;
    private static int allocMemory = 0;

    private static ConcurrentHashMap<String, ArrayList<WeakReference<Bitmap>>> createdBitmaps = new ConcurrentHashMap<>();

    private static final Object lockObj = new Object();

    private static boolean isFakeDeallocatingAvailable = true;

    public static boolean isThrowOutOfMemory = false;

    private static void addToCreatedBitmaps(Bitmap bmp, String tag) {
        if (tag != null) {
            ArrayList<WeakReference<Bitmap>> tagedList = createdBitmaps.get(tag);
            if (tagedList != null)
                tagedList.add(new WeakReference<>(bmp));
            else {
                tagedList = new ArrayList<>();
                tagedList.add(new WeakReference<>(bmp));
                createdBitmaps.put(tag, tagedList);
            }

        }
    }

    public static void addBitmapByTag(Bitmap bmp, String tag, boolean addMemory) {
        if (bmp != null) {
            synchronized (lockObj) {
                if (addMemory) {
                    addMemory(bmp.getRowBytes() * bmp.getHeight());
                    Log.d(TAG, "creating bitmap width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " config:" + bmp.getConfig() + " memoryKB: " + getAllocMemoryInKb());
                }
                addToCreatedBitmaps(bmp, tag);
            }
        }
    }

    public static Bitmap createBitmap(int w, int h, Bitmap.Config config, String tag) {
        Bitmap bmp = null;
        try {
            bmp = Bitmap.createBitmap(w, h, config);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OOM while creating bitmap");
            System.gc();
            try {
                bmp = Bitmap.createBitmap(w, h, config);
            } catch (OutOfMemoryError e2) {
                Log.e(TAG, "OOM while creating bitmap 2");
                if (isThrowOutOfMemory) {
                    isThrowOutOfMemory = false;
                    throw new OutOfMemoryError(e2.getMessage());
                }
            }
        }
        int size = 0;
        if (bmp != null) {
            addMemory(size = bmp.getRowBytes() * bmp.getHeight());
            Log.d(TAG, "creating bitmap width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " config:" + bmp.getConfig() + " memoryKB: " + getAllocMemoryInKb());
            addToCreatedBitmaps(bmp, tag);
        }
        deallocateMemory(size);
        return bmp;
    }

    public static Bitmap decodeResource(Resources res, int id) {
        return decodeResource(res, id, null);
    }

    public static Bitmap decodeResource(Resources res, int id, BitmapFactory.Options opts) {
        return decodeResource(res, id, opts, null);
    }

    public static Bitmap decodeResource(Resources res, int id, BitmapFactory.Options opts, String tag) {
        Bitmap bmp = null;

        try {
            bmp = BitmapFactory.decodeResource(res, id, opts);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OOM while loading bitmap from resource , total allocated memory == " + getAllocMemoryInKb());
            System.gc();
            try {
                bmp = BitmapFactory.decodeResource(res, id, opts);
            } catch (OutOfMemoryError e2) {
                Log.e(TAG, "OOM while creating bitmap 2");
                if (isThrowOutOfMemory) {
                    isThrowOutOfMemory = false;
                    throw new OutOfMemoryError(e2.getMessage());
                }
            }
        }

        int size = 0;

        if (bmp != null && (opts == null || !opts.inJustDecodeBounds)) {
            addMemory(size = bmp.getRowBytes() * bmp.getHeight());
            if (opts == null)
                Log.d(TAG, "decodeResource from stream width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " memoryKB: " + getAllocMemoryInKb());
            else {
                Log.d(TAG, "decodeResource from stream width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " memoryKB: " + getAllocMemoryInKb() + getOptionsString(opts));
            }
            addToCreatedBitmaps(bmp, tag);
        }
        deallocateMemory(size);
        return bmp;
    }


    public static boolean recycle(Bitmap bmp, String tag) {
        if (bmp == null) {
            Log.d(TAG, "bmp is null while recycling " + " tag: " + tag);
            return false;
        }
        if (bmp.isRecycled()) {
            Log.d(TAG, "bmp is already recycled " + " tag: " + tag);
            return false;
        }

        addMemory(-(bmp.getRowBytes() * bmp.getHeight()));
        Log.d(TAG, "bmp is recycled width: " + bmp.getWidth() + " height: " + bmp.getHeight() + " memoryKB: " + getAllocMemoryInKb() + " tag: " + tag);
        bmp.recycle();
        return true;
    }

    public static String getOptionsString(BitmapFactory.Options opts) {
        String result = " opts is null";
        if (opts != null) {
            result = " inSampleSize:" + opts.inSampleSize + " outWidth:" + opts.outWidth + " outHeight:" + opts.outHeight + " inDither:" + opts.inDither + " inJustDecodeBounds:" + opts.inJustDecodeBounds + " inPurgeable:" + opts.inPurgeable + " inPreferredConfig:" + opts.inPreferredConfig + " mCancel:" + opts.mCancel;
        }
        return result;
    }

    public static Bitmap createBitmap(int w, int h, Bitmap.Config config) {
        return createBitmap(w, h, config, null);
    }

    public static Bitmap decodeFile(String path) {
        return decodeFile(path, null);
    }

    public static Bitmap decodeFile(String path, BitmapFactory.Options opts, String tag) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeFile(path, opts);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OOM while loading bitmap path: " + path);
            System.gc();
            try {
                bmp = BitmapFactory.decodeFile(path, opts);
            } catch (OutOfMemoryError e2) {
                Log.e(TAG, "OOM while creating bitmap 2");
                if (isThrowOutOfMemory) {
                    isThrowOutOfMemory = false;
                    throw new OutOfMemoryError(e2.getMessage());
                }
            }
        }
        int size = 0;
        if (bmp != null && (opts == null || !opts.inJustDecodeBounds)) {
            addMemory(size = bmp.getRowBytes() * bmp.getHeight());
            Log.d(TAG, "loadBitmap from path: " + path + " width: " + bmp.getWidth() + " memoryKB: " + getAllocMemoryInKb() + " height: " + bmp.getHeight() + getOptionsString(opts));
            addToCreatedBitmaps(bmp, tag);
        }
        deallocateMemory(size);
        return bmp;
    }

    public static Bitmap decodeFile(String path, BitmapFactory.Options opts) {
        return decodeFile(path, opts, null);
    }


    public static boolean recycle(Bitmap bmp) {
        return recycle(bmp, null);
    }

    public static float getAllocMemoryInKb() {
        return (float) allocMemory / 1024.0f;
    }

    private static void addMemory(int val) {
        synchronized (lockObj) {
            allocMemory += val;
        }
    }

    public static void deallocateMemory(long size) {
        if (Build.VERSION.SDK_INT < 10 && isFakeDeallocatingAvailable) {
            ImageOpCommon.deallocMemory(true);
        }
    }

    public static Bitmap copy(Bitmap src, Bitmap.Config config, boolean isMutable, String tag) {
        Bitmap bmp = null;
        try {
            bmp = src.copy(config, isMutable);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OOM while copying bitmap");
            System.gc();
            try {
                bmp = src.copy(config, isMutable);
            } catch (OutOfMemoryError e2) {
                Log.e(TAG, "OOM while creating bitmap 2");
                if (isThrowOutOfMemory) {
                    isThrowOutOfMemory = false;
                    throw new OutOfMemoryError(e2.getMessage());
                }
            }
        }
        int size = 0;
        if (bmp != null) {
            addMemory(size = bmp.getRowBytes() * bmp.getHeight());
            Log.d(TAG, "copying bitmap width:" + bmp.getWidth() + " height:" + bmp.getHeight() + " config:" + bmp.getConfig() + " memoryKB: " + getAllocMemoryInKb());
            addToCreatedBitmaps(bmp, tag);
        }
        deallocateMemory(size);
        return bmp;
    }

    public static Bitmap copy(Bitmap src, Bitmap.Config config, boolean isMutable) {
        return copy(src, config, isMutable, null);
    }


    public static HashMap<Object, Object> saveBitmapBufferToSDCard(String folderName, String fileName, Bitmap image, Activity context) throws UnsatisfiedLinkError {

        HashMap<Object, Object> bufferData = null;

        File saveToDir = createCustomDir(folderName, context);

        if (saveToDir == null) {
            return null;
        }

        if (saveToDir.exists() && saveToDir.canWrite()) {
            File file = new File(saveToDir, fileName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file.exists() && file.canWrite()) {
                bufferData = saveBitmapBufferToSDCard(file.getPath(), image, context);
            } else {
                //error writing to file
            }
        } else {
            //msg_text_no_sdcard
        }

        return bufferData;
    }

    public static HashMap<Object, Object> saveBitmapBufferToSDCard(String filePath, Bitmap image, Context context) throws UnsatisfiedLinkError {
        final int width = image.getWidth();
        final int height = image.getHeight();

        ByteBuffer buffer;

        final boolean useNativeMemory = ImageOpCommon.IS_COMMON_LIBRARY_LOADED;

        if (useNativeMemory)
            buffer = ImageOpCommon.allocNativeBuffer(4 * width * height);
        else
            buffer = ByteBuffer.allocate(4 * width * height);

        image.copyPixelsToBuffer(buffer);
        buffer.position(0);
        try {
            writeToSd(filePath, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageOpCommon.freeNativeBuffer(buffer);

        HashMap<Object, Object> bufferData = new HashMap<Object, Object>();
        bufferData.put("width", width);
        bufferData.put("height", height);
        bufferData.put("path", filePath);

        return bufferData;
    }

    public static File createCustomDir(String folderName, Context context) {
        File sdDir = Environment.getExternalStorageDirectory();

        File customDir = null;

        if (sdDir.exists() && sdDir.canWrite()) {
            File pmDir = new File(sdDir, "VideoEditor");
            pmDir.mkdirs();

            if (pmDir.exists() && pmDir.canWrite()) {
                // create custom folder by name
                if (folderName != null) {
                    customDir = new File(pmDir, folderName);
                    customDir.mkdirs();
                } else {
                    customDir = pmDir;
                }
            }
        }
        return customDir;
    }


    public static void writeToSd(String path, ByteBuffer buffer) throws IOException {
        File file = new File(path);
        FileChannel wChannel = new FileOutputStream(file).getChannel();
        wChannel.write(buffer);
        wChannel.close();
    }

    ////

    public static Bitmap getScaledBitmapFromRealPath_ARGB8(String realPath, int land_width, int land_height, int degree) throws Exception {
        return getScaledBitmapFromRealPath_ARGB8(realPath, land_width, land_height, degree, SCALE_PROPOTIONAL);
    }

    public static Bitmap getScaledBitmapFromRealPath_ARGB8(String realPath, int land_width, int land_height, int degree, int type) throws Exception {
//        long time = System.currentTimeMillis();

        Bitmap bitmap = null;

        if (type == SCALE_ABSOLUTE) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap origBitmap = decodeFile(realPath, options);
            bitmap = getScaledBitmap_ARGB8(origBitmap, land_width, land_height, SCALE_ABSOLUTE);

            BitmapManager.recycle(origBitmap);

            return bitmap;
        }

        int port_width = land_height;
        int port_height = land_width;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeFile(realPath, options);
        int origWidth = options.outWidth;
        int origHeight = options.outHeight;

        if (origWidth >= origHeight) {
            if (origWidth > land_width) {
                // /////
                float scale = 1;
                while (bitmap == null) {
                    try {
                        if (scale > 4) {
                            bitmap = null;
                            break;
                        }
                        bitmap = getScaledBitmap_ARGB8(realPath, (int) (land_width / scale), (int) (land_height / scale), origWidth, origHeight, true, degree);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        bitmap = null;
                    } finally {
                        scale *= 1.1;
                    }
                }
                // ///
            } else {
                bitmap = getBitmapFromFile_ARGB8(realPath, degree);
            }
        } else if (origWidth < origHeight) {
            if (origHeight > port_height) {
                // /////
                float scale = 1;
                while (bitmap == null) {
                    try {
                        if (scale > 4) {
                            bitmap = null;
                            break;
                        }
                        bitmap = getScaledBitmap_ARGB8(realPath, (int) (port_width / scale), (int) (port_height / scale), origWidth, origHeight, true, degree);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        bitmap = null;
                    } finally {
                        scale *= 1.1;
                    }
                }
                // ///

            } else {
                bitmap = getBitmapFromFile_ARGB8(realPath, degree);
            }
        }

        return bitmap;
    }

    public static Bitmap getBitmapFromFile_ARGB8(String realPath, int degree) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        return getRotatedBitmapAndRecycle_ARGB8(BitmapManager.decodeFile(realPath, options), degree);
    }

    public static Bitmap getRotatedBitmapAndRecycle_ARGB8(Bitmap bitmap, int rotation) {
        if (bitmap == null || bitmap.isRecycled()) {
            return bitmap;
        }

        if (rotation == 0 || rotation == 360) {
            return bitmap;
        }

        Bitmap rotatedBitmap;

        Matrix matrix = new Matrix();
        matrix.reset();
        if (rotation == 90 || rotation == 270) {
            rotatedBitmap = BitmapManager.createBitmap(bitmap.getHeight(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            if (rotation == 90) {
                matrix.postRotate(90);
                matrix.postTranslate(rotatedBitmap.getWidth(), 0);
            } else { // 270
                matrix.postRotate(270);
                matrix.postTranslate(0, rotatedBitmap.getHeight());
            }
        } else {
            rotatedBitmap = BitmapManager.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            matrix.postRotate(180);
            matrix.postTranslate(rotatedBitmap.getWidth(), rotatedBitmap.getHeight());
        }
        Canvas c = new Canvas(rotatedBitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        c.drawBitmap(bitmap, matrix, paint);

        BitmapManager.recycle(bitmap);

        return rotatedBitmap;

    }


    public static Bitmap getScaledBitmap_ARGB8(String path, int width, int height, int origWidth, int origHeight, boolean proportionally, int degree) {
        final float scale = Math.min((float) width / (float) origWidth, (float) height / (float) origHeight);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = (int) (1 / scale);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmapFromFile = BitmapManager.decodeFile(path, options);

        if (bitmapFromFile != null) {
            Matrix matrix = new Matrix();
            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            paint.setDither(false);

            final float scale2 = Math.min((float) width / (float) bitmapFromFile.getWidth(), (float) height / (float) bitmapFromFile.getHeight());

            Bitmap scaledBitmap = BitmapManager.createBitmap(Math.round(bitmapFromFile.getWidth() * scale2), Math.round(bitmapFromFile.getHeight() * scale2), Bitmap.Config.ARGB_8888);
            Log.d(TAG, "scaledBitmapWidth: " + scaledBitmap.getWidth() + " scaledBitmapHeight: " + scaledBitmap.getHeight());
            // scale with canvas
            Canvas c = new Canvas(scaledBitmap);

            matrix.postScale(scale2, scale2);
            c.drawBitmap(bitmapFromFile, matrix, paint);

            BitmapManager.recycle(bitmapFromFile);

            if (degree == 0 || degree == 360) {
                return scaledBitmap;
            }

            return getRotatedBitmapAndRecycle_ARGB8(scaledBitmap, degree);
        }

        return null;
    }


    public static Bitmap getScaledBitmap_ARGB8(Bitmap bitmap, int width, int height, int type) {

        final float scale = Math.min((float) width / (float) bitmap.getWidth(), (float) height / (float) bitmap.getHeight());

        Bitmap scaledBitmap;
        if (type == SCALE_ABSOLUTE) {
            scaledBitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } else {
            scaledBitmap = createBitmap(Math.round(bitmap.getWidth() * scale), Math.round(bitmap.getHeight() * scale), Bitmap.Config.ARGB_8888);
        }

        Canvas c = new Canvas(scaledBitmap);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setDither(false);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        c.drawBitmap(bitmap, matrix, paint);

        return scaledBitmap;

    }

    public static Bitmap getScaledBitmapFromBufferPathFast(HashMap<Object, Object> bufferData, int pixelsMaxCount, int degree) {
        int width = (Integer) bufferData.get("width");
        int height = (Integer) bufferData.get("height");
        String path = (String) bufferData.get("path");

        final boolean useNativeMemory = ImageOpCommon.IS_COMMON_LIBRARY_LOADED;

        ByteBuffer buffer;

        if (useNativeMemory)
            buffer = ImageOpCommon.allocNativeBuffer(4 * width * height);
        else
            buffer = ByteBuffer.allocate(4 * width * height);

        try {
            readFromFile(path, buffer, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (pixelsMaxCount == 0 && degree == 0) {
            Bitmap image = BitmapManager.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            buffer.position(0);
            if (image != null)
                image.copyPixelsFromBuffer(buffer);
            if (useNativeMemory) {
                ImageOpCommon.freeNativeBuffer(buffer);
            }

            return image;
        } else {

            Bitmap scaledBitmap;

            if (width * height > pixelsMaxCount) {
                Log.e("ex1", "scale < 1");
                ImageSize imageNewSize = calculateImageSize(new ImageSize(width, height), pixelsMaxCount);
                int scaledHeight = imageNewSize.height;
                int scaledWidth = imageNewSize.width;

                ByteBuffer scaledBitmapBuffer;
                if (useNativeMemory)
                    scaledBitmapBuffer = ImageOpCommon.allocNativeBuffer(4 * scaledWidth * scaledHeight);
                else
                    scaledBitmapBuffer = ByteBuffer.allocate(4 * scaledWidth * scaledHeight);

                ImageResize.resize(buffer, width, height, scaledBitmapBuffer, scaledWidth, scaledHeight, ImageResize.RESIZE_LANCZOS);
                if (useNativeMemory) {
                    ImageOpCommon.freeNativeBuffer(buffer);
                }
                scaledBitmapBuffer.position(0);
                scaledBitmap = BitmapManager.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (scaledBitmap != null)
                    scaledBitmap.copyPixelsFromBuffer(scaledBitmapBuffer);

                if (useNativeMemory) {
                    ImageOpCommon.freeNativeBuffer(scaledBitmapBuffer);
                }

            } else {
                Log.e("ex1", "scale >= 1");
                Bitmap image = BitmapManager.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                buffer.position(0);
                if (image != null)
                    image.copyPixelsFromBuffer(buffer);
                if (useNativeMemory) {
                    ImageOpCommon.freeNativeBuffer(buffer);
                }
                scaledBitmap = image;
            }

            if (scaledBitmap == null) {
                return null;
            }

            if (degree == 0 || degree == 360)
                return scaledBitmap;

            // rotate with canvas
            return getRotatedBitmapAndRecycle_ARGB8(scaledBitmap, degree);
        }
    }

    public static Bitmap getScaledBitmapFromBufferPathFast(HashMap<Object, Object> bufferData, int new_width, int new_height, int degree) {
        int width = (Integer) bufferData.get("width");
        int height = (Integer) bufferData.get("height");
        String path = (String) bufferData.get("path");

        final boolean useNativeMemory = ImageOpCommon.IS_COMMON_LIBRARY_LOADED;

        ByteBuffer buffer;

        if (useNativeMemory) {
            buffer = ImageOpCommon.allocNativeBuffer(4 * width * height);
        } else {
            buffer = ByteBuffer.allocate(4 * width * height);
        }
        try {
            readFromFile(path, buffer, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (new_width == 0 && new_height == 0 && degree == 0) {
            Bitmap image = BitmapManager.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            buffer.position(0);
            image.copyPixelsFromBuffer(buffer);
            if (useNativeMemory) {
                ImageOpCommon.freeNativeBuffer(buffer);
            }

            return image;
        } else {
            Bitmap scaledBitmap;
            final float scale = Math.min((float) new_width / width, (float) new_height / height);

            if (scale < 1) {
                int scaledWidth = Math.round(width * scale);
                int scaledHeight = Math.round(height * scale);

                ByteBuffer scaledBitmapBuffer;
                if (useNativeMemory)
                    scaledBitmapBuffer = ImageOpCommon.allocNativeBuffer(4 * scaledWidth * scaledHeight);
                else
                    scaledBitmapBuffer = ByteBuffer.allocate(4 * scaledWidth * scaledHeight);

                ImageResize.resize(buffer, width, height, scaledBitmapBuffer, scaledWidth, scaledHeight, ImageResize.RESIZE_LANCZOS);
                if (useNativeMemory) {
                    ImageOpCommon.freeNativeBuffer(buffer);
                }
                scaledBitmapBuffer.position(0);
                scaledBitmap = BitmapManager.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                scaledBitmap.copyPixelsFromBuffer(scaledBitmapBuffer);

                if (useNativeMemory) {
                    ImageOpCommon.freeNativeBuffer(scaledBitmapBuffer);
                }

            } else {
                Bitmap image = BitmapManager.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                buffer.position(0);
                image.copyPixelsFromBuffer(buffer);
                if (useNativeMemory) {
                    ImageOpCommon.freeNativeBuffer(buffer);
                }
                scaledBitmap = image;
            }

            if (degree == 0 || degree == 360) {
                return scaledBitmap;
            }

            // rotate with canvas
            return getRotatedBitmapAndRecycle_ARGB8(scaledBitmap, degree);
        }
    }


    public static void readFromFile(String path, ByteBuffer toReadIn, int bytesToSkip) throws IOException {
        FileChannel fch = new FileInputStream(path).getChannel();
        fch.position(bytesToSkip);
        fch.read(toReadIn);
    }

    public static Bitmap getScaledBitmapForMaxPixels(String imgPath, int maxPixel, int degree) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapManager.decodeFile(imgPath, options);
        int origWidth = options.outWidth;
        int origHeight = options.outHeight;

        if (origWidth * origHeight > maxPixel) {
            ImageSize imageNewSize = calculateImageSize(new ImageSize(origWidth, origHeight), maxPixel);

            return getScaledBitmap_ARGB8(imgPath, imageNewSize.width, imageNewSize.height, origWidth, origHeight, true, degree);
        } else {
            return getBitmapFromFile_ARGB8(imgPath, degree);
        }
    }

    public static ImageSize calculateImageSize(ImageSize imageSize, int maxPixels) {
        if (imageSize.pixels <= maxPixels) {
            return imageSize;
        }
        float ratio = (float) imageSize.width / imageSize.height;
        int newHeight = Math.round((float) Math.sqrt(maxPixels / ratio));
        int newWidth = Math.round(ratio * newHeight);
        while (newWidth * newHeight > maxPixels) {
            if (newWidth < newHeight) {
                newHeight -= 1;
            } else {
                newWidth -= 1;
            }
        }
        return new ImageSize(newWidth, newHeight);
    }

    public static boolean isSdcardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File writeSdCard(String folderName, String fileName, InputStream is) {
        return writeSdCard(folderName, fileName, is, null, 0);
    }

    public static File writeSdCard(String folderName, String fileName, InputStream is, Observer o, int contentLent) {
        File file = null;

        File sdDir = Environment.getExternalStorageDirectory();

        if (sdDir.exists() && sdDir.canWrite()) {
            Log.d("sdcard state:", Environment.getExternalStorageState());
            // create custom folder by name
            File customDir = new File(sdDir, folderName);
            customDir.mkdirs();

            file = new File(customDir, fileName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("error creating file", e.getMessage());
            }
            if (file.exists() && file.canWrite()) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int written = 0;
                    int len;
                    int progress = 1;
                    int progressStep = contentLent / 10;
                    while ((len = is.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                        if (o != null) {
                            written = written + len;
                            if (written >= progressStep * progress) {
                                o.update(null, progress);
                                progress++;
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    Log.e("writeToSDCard", "ERROR", e);
                } catch (IOException e) {
                    Log.e("writeToSDCard", "ERROR", e);
                } finally {
                    if (fos != null) {
                        try {
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            // swallow
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // swallow
                        }
                    }
                }
                if (o != null) {
                    o.update(null, -1);
                }
            } else {
                Log.e("writeToSDCard", "error writing to file");
            }

        } else {
            Log.e("writeToSDCard", "ERROR, /sdcard path not available");
        }
        return file;
    }

    public static File writeSdCard(String realPath, InputStream is) throws IOException {
        File f = new File(realPath);
        f.getParentFile().mkdirs();
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(f);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024 * 8];
        int len;
        while ((len = is.read(buf)) > 0) {
            fos.write(buf, 0, len);
        }
        fos.flush();
        fos.close();
        return f;
    }

    public static String getRealPathFromAlbumPic(Activity activity, Intent data) {
        if (data == null)
            return "";
        Uri currImageURI = data.getData();
        Log.d(TAG, "currImageURI:" + currImageURI);
        if (currImageURI == null)
            return "";
        String realPath = currImageURI.toString();

        // check if it is already real path
        if (currImageURI.getScheme() != null && currImageURI.getScheme().startsWith("content")) {
            realPath = getRealPathFromURI(currImageURI, activity);
        } else if (currImageURI.getScheme() != null && currImageURI.getScheme().startsWith("file") && currImageURI.getPath() != null) {
            realPath = currImageURI.getPath();// .substring(6,currImageURI.getBitmap().length());
            Log.d(TAG, "realPath:" + realPath);
        }

        try {
            if (realPath != null && !(new File(realPath)).exists()) {
                realPath = null;
            }
        } catch (Exception ex) {
            Log.e(TAG, "getRealPathFromAlbumPic", ex);
        }

        // /read from inputstream
        if (realPath == null) {
            InputStream is = null;
            try {

                try {
                    ContentResolver res = activity.getContentResolver();
                    Uri uri = Uri.parse(data.getData().toString());
                    is = res.openInputStream(uri);
                } catch (SecurityException e) {
                    Log.e(TAG, "getRealPathFromAlbumPic", e);
                }

                if (is == null) {
                    return "";
                }
                // create tmp file
                File customDir = createCustomDir(".tmp", activity);

                File tmpFile = new File(customDir, "tmp_" + System.currentTimeMillis());
                try {
                    tmpFile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, "getRealPathFromAlbumPic", e);
                }
                // /

                if (tmpFile != null && tmpFile.exists() && tmpFile.canRead()) {
                    FileOutputStream out = new FileOutputStream(tmpFile);
                    copyInputStream(is, out);
                    realPath = tmpFile.getAbsolutePath();
                }
            } catch (IOException e) {
                Log.e(TAG, "getRealPathFromAlbumPic", e);
            }
        }

        Log.d(TAG, "realPath:" + realPath);
        Log.d("startMainActivityFor realPath:", realPath);

        return realPath;
    }


    public static String getRealPathFromURI(Uri contentUri, Context context) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String realPath = cursor.getString(idx);
                cursor.close();
                return realPath;
            } else {
                return contentUri.getPath();
            }
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI", e);
            return contentUri.getPath();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    Log.e(TAG, "getRealPathFromURI", e);
                }
            }
        }
    }


    public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }


}

