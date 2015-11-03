package com.picsart.studio.gifencoder;


import android.graphics.Bitmap;


import java.io.File;
import java.nio.ByteBuffer;

public class GifEncoder{

    static {
        System.loadLibrary("gifencoder");
    }



    /**
     * @param gifName path to save gif
     * @param numColors generally 256 colors for best
     * @param quality max 100 to apply
     * @param frameDelay delay in ms
     *
     * */
    public native int init(String gifName, int w, int h, int numColors, int quality,int frameDelay);


    /**
     *
     * Bitmap should be 32-bit ARGB, e.g. like the ones when decoding
     * a JPEG using BitmapFactory
     * bitmap.getPixels(pixelsArray, 0, bitm_width, 0, 0, bitm_width, bitm_height);
     *
     * */
    public native int addFrame(int[] inArray);

    /**
     * Use to finalize and output gif
     *
     * */
    public native void close();


    public void cancelGifGeneration(String gifPath) {
        close();
        try{
            File f = new File(gifPath);
            if(f.exists())
                f.delete();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    /**
     * Converts RGBA input array to output YUV byte array
     * */
    public static native void convertToYUV21(int[] sourceBuf, byte[] outPutYuv,int width, int height);

    /**
     * Converts Bitmap input and returns YUV byteBuffer
     * */
    public static native ByteBuffer getBitmapInYUV(Bitmap btm);

}
