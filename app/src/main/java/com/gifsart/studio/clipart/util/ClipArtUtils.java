package com.gifsart.studio.clipart.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.FloatMath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ClipArtUtils {


    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024 * 16];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static Bitmap getFrameAtTime(String path, long time) {
        Bitmap b = null;


        // Sort the list.
        //Collections.sort(entries, ALPHA_COMPARATOR);

        // Done!
        return b;
    }

    public static List<Bitmap> getFramesAtTimes(String path, List<Long> timeArray) {
        List<Bitmap> frames = new ArrayList<>();

        if (path == null) {
            return frames;
        }

      /*  FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
        try {
            fmmr.setDataSource(path);

            for (int i = 0; i < timeArray.size(); i++) {
                Bitmap b = fmmr.getFrameAtTime();

                if (b != null) {
                    Bitmap b2 = fmmr.getFrameAtTime(timeArray.get(i) * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    if (b2 != null) {
                        b = b2;
                    }
                }

                if (b != null) {
                    frames.add(b);
                    Log.i(Utils.class.getName(), "Extracted frame");
                } else {
                    Log.e(Utils.class.getName(), "Failed to extract frame");
                }
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } finally {
            fmmr.release();
        }

        // Sort the list.
        //Collections.sort(entries, ALPHA_COMPARATOR);*/

        // Done!
        return frames;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }


    //Graphic methods
    public static float getCurrentScaleFromMatrix(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        float scalex = values[Matrix.MSCALE_X];
        float skewy = values[Matrix.MSKEW_Y];
        return (float) Math.sqrt(scalex * scalex + skewy * skewy);
    }

    public static float getDistance(PointF start, PointF end) {
        return getDistance(start.x, start.y, end.x, end.y);
    }

    public static float getDistance(float startX, float startY, float endX, float endY) {
        final float dx = endX - startX;
        final float dy = endY - startY;

        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public static void getMidPointDelta(PointF lineStart1, PointF lineEnd1, PointF lineStart2, PointF lineEnd2, PointF outPoint) {
        float midPoint1X = (lineStart1.x + lineEnd1.x) / 2;
        float midPoint1Y = (lineStart1.y + lineEnd1.y) / 2;

        float midPoint2X = (lineStart2.x + lineEnd2.x) / 2;
        float midPoint2Y = (lineStart2.y + lineEnd2.y) / 2;

        outPoint.set(midPoint2X - midPoint1X, midPoint2Y - midPoint1Y);
    }

    public static void getMidPoint(PointF lineStart, PointF lineEnd, PointF outPoint) {
        outPoint.set((lineStart.x + lineEnd.x) / 2, (lineStart.y + lineEnd.y) / 2);
    }

    public static float getAngleBetweenLines(PointF lineStart1, PointF lineEnd1, PointF lineStart2, PointF lineEnd2) {
        float dx1 = lineStart1.x - lineEnd1.x;
        float dy1 = lineStart1.y - lineEnd1.y;

        float dx2 = lineStart2.x - lineEnd2.x;
        float dy2 = lineStart2.y - lineEnd2.y;

        double radians = Math.atan2(dy2, dx2) - Math.atan2(dy1, dx1);

        return (float) Math.toDegrees(radians);
    }
    //END Graphic methods

}