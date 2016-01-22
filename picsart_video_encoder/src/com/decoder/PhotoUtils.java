package com.decoder;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.socialin.android.photo.imgop.ImageOp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Created by Tigran on 7/28/15.
 */
public class PhotoUtils {

    /**
     * Saving byte buffer to sd card
     *
     * @param filePath
     * @param buffer
     * @throws UnsatisfiedLinkError
     */
    public static void saveBufferToSDCard(String filePath, ByteBuffer buffer) throws UnsatisfiedLinkError {

        try {
            writeToSd(filePath, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reading byte buffer from given path
     *
     * @param bufferPath
     * @param bufferSize
     * @return
     */
    public static ByteBuffer readBufferFromFile(String bufferPath, int bufferSize) {
        FileInputStream inputStream = null;
        FileChannel channel = null;
        try {
            inputStream = new FileInputStream(bufferPath);

            //ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            ByteBuffer buffer = ImageOp.allocNativeBuffer(bufferSize);

            channel = inputStream.getChannel();
            channel.read(buffer);

            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static void writeToSd(String path, ByteBuffer buffer) throws IOException {
        File file = new File(path);
        // Create a writable file channel
        FileChannel wChannel = new FileOutputStream(file).getChannel();

        // Write the ByteBuffer contents; the bytes between the ByteBuffer's
        // position and the limit is written to the file
        wChannel.write(buffer);

        // Close the file
        wChannel.close();
    }

    public static ByteBuffer fromBitmapToBuffer(Bitmap b) {
        ByteBuffer result = ByteBuffer.allocate(b.getWidth() * b.getHeight() * 4); // ByteBuffer.allocate(capacity)
        result.position(0);
        b.copyPixelsToBuffer(result);
        result.position(0);
        return result;
    }

    public static Bitmap fromBufferToBitmap(int w, int h, ByteBuffer buffer) {
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        buffer.rewind();
        result.copyPixelsFromBuffer(buffer);

        Matrix m = new Matrix();
        //m.preScale(-1, 1);
        Bitmap dst = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        return dst;
    }

    public static int checkBufferSize(String videoPath, int frameSize) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        int height = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int width = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int newHeight = height / frameSize;
        int newWidth = width / frameSize;
        return (newHeight * newWidth * 4);
    }

    public static int checkFrameWidth(String videoPath, int frameSize) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        int height = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int width = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int orientation = checkFrameOrientation(videoPath);

        int x;
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        //if (SDK_INT >= 21) {

        if (orientation == 90 || orientation == 270) {
            x = height < width ? height : width;
        } else {
            x = width;
        }
        //} else {
        // x = width;
        //}
        return x / frameSize;
    }

    public static int checkFrameHeight(String videoPath, int frameSize) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        int height = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int width = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int orientation = checkFrameOrientation(videoPath);

        int x;
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        //if (SDK_INT >= 21) {

        if (orientation == 90 || orientation == 270) {
            x = height > width ? height : width;
        } else {
            x = height;
        }
        //} else {
        //  x = height;
        //}
        return x / frameSize;
    }

    public static int checkFrameOrientation(String videoPath) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        String orientation = metaRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        return Integer.parseInt(orientation);
    }

    public static void saveRawBitmap(Bitmap bitmap, String filePath) {
        if (bitmap == null || TextUtils.isEmpty(filePath)) {
            Log.w("PhotoUtils", "saveRawBitmap: " + " Bitmap is NULL or empty filePath !!!");
            return;
        }
        try {
            File file = new File(filePath);
            // make sure containing dir exists
            if (!file.exists() && file.getParent() != null) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
            }
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
            stream.writeInt(0x2E_52_41_57); // .RAW
            stream.writeInt(bitmap.getWidth());
            stream.writeInt(bitmap.getHeight());
            stream.writeInt(bitmap.getConfig() != null ? bitmap.getConfig().ordinal() : Bitmap.Config.ARGB_8888.ordinal());

            byte[] rawData = new byte[bitmap.getRowBytes() * bitmap.getHeight()];

            ByteBuffer buffer = ByteBuffer.wrap(rawData);
            bitmap.copyPixelsToBuffer(buffer);

            stream.write(rawData);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Bitmap loadRawBitmap(String filePath) {
        Bitmap result = null;

        try {
            DataInputStream stream = new DataInputStream(new FileInputStream(filePath));
            if (stream.readInt() == 0x2E_52_41_57) { // .RAW
                int width = stream.readInt();
                int height = stream.readInt();
                Bitmap.Config config = Bitmap.Config.values()[stream.readInt()];

                result = Bitmap.createBitmap(width, height, config);

                byte[] rawData = new byte[result.getRowBytes() * result.getHeight()];

                stream.readFully(rawData);

                result.copyPixelsFromBuffer(ByteBuffer.wrap(rawData));
            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void saveByteBufferToRawBitmap(ByteBuffer byteBuffer, int width, int height, Bitmap.Config config, String filePath) {
        try {
            File file = new File(filePath);
            // make sure containing dir exists
            if (!file.exists() && file.getParent() != null) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
            }
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
            stream.writeInt(0x2E_52_41_57); // .RAW
            stream.writeInt(width);
            stream.writeInt(height);
            stream.writeInt(config.ordinal());

            byte[] rawData = byteBuffer.array();
            stream.write(rawData);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

