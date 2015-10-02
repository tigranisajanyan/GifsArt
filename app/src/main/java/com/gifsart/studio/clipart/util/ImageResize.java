package com.gifsart.studio.clipart.util;

import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Image operations.
 */
public class ImageResize {

    public final static String LIB_NAME = "imageresize";
    public static boolean isLibraryLoaded;

    // Resizing method.
    public static final int RESIZE_NN = 0;
    public static final int RESIZE_LINEAR = 1;
    public static final int RESIZE_CUBIC = 2;
    public static final int RESIZE_AREA = 3;
    public static final int RESIZE_LANCZOS = 4;

    // ByteBuffer management in native code.

    /**
     * Allocation ByteBuffer in native code (memory will not be counted in VM heap).
     * Be aware about memory leaks: buffer allocated in native memory must be released by freeNativeBuffer function.
     */
    static public native ByteBuffer allocNativeBuffer(long size);

    /**
     * Free ByteBuffer allocated in native code.
     */
    static public native void freeNativeBuffer(ByteBuffer buffer);

    /**
     * Resize buffer 8888.
     */
    static public native void resize(ByteBuffer src, int src_width, int src_height, ByteBuffer dst, int dst_width, int dst_height, int method);

    /**
     * Load image from file and resize if necessary.
     *
     * @param path             path to image file in file system
     * @param max_size         max allowed value for width or height. Use zero or negative value to load image in original size
     * @param method           resize method used in case resizing is required
     * @param rotation         if 0 - do not rotate image at all, if 90 or 180 or 270 then rotate image counter-clockwise to specified angle, if -1 then read EXIF from file and rotate according to EXIF.
     * @param out_width_height output integer array which will contain width and height of loaded image. Array must be allocated before function is called and contain 2 elements
     * @return ByteBuffer which contain pixel data in 8888 format. Be aware about memory leaks: buffer allocated in native memory must be released by freeNativeBuffer function.
     */
    static public native ByteBuffer load(String path, int max_size, int method, int rotation, int[] out_width_height);

    // Loading library.
    static {
        boolean libraryLoaded = false;
        try {
            System.loadLibrary(LIB_NAME);
            libraryLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            try {
                System.load("/data/data/" + System.getProperty("packageName") + "/lib/" + LIB_NAME + ".so");
                libraryLoaded = true;
            } catch (UnsatisfiedLinkError e1) {
                Log.e(ImageOpCommon.class.getSimpleName(), "Can't load \"" + LIB_NAME + "\" library.");
                Log.e(ImageOpCommon.class.getSimpleName(), e1.getMessage());
            }

        }

        Log.e("ex1", "ImageResize libraryLoaded = " + libraryLoaded);

        isLibraryLoaded = libraryLoaded;
    }
}
