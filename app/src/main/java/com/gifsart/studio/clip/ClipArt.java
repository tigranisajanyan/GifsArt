package com.gifsart.studio.clip;

/**
 * Copyright (C) 2010 Socialin Inc. All Rights Reserved.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.gifsart.studio.R;
import com.gifsart.studio.clipart.util.BitmapManager;
import com.gifsart.studio.clipart.util.Geom;
import com.gifsart.studio.clipart.util.GestureDetector;
import com.gifsart.studio.clipart.util.Graphics;
import com.gifsart.studio.clipart.util.ImageResize;
import com.gifsart.studio.clipart.view.AbstractItem;
import com.gifsart.studio.clipart.view.Item;
import com.gifsart.studio.textart.GraphicUtils;
import com.gifsart.studio.utils.Utils;
import com.nostra13.universalimageloader.utils.L;
import com.socialin.android.photo.imgop.ImageOpCommon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ClipArt extends AbstractItem implements GestureDetector.GestureListener {
    public static final int FILL_COLOR_ABSOLUTE = 0;
    public static final int FILL_COLOR_MASK = 1;

    public static final int BITMAP = 0;
    public static final int SVG = 1;

    private static final int DEFAULT_BRUSH_SIZE_DIP = 20;
    private static final float DEFAULT_BRUSH_HARDNESS = 50f;
    private static final String TAG = ClipArt.class.getSimpleName();

    // for exif
    private String imageSource = "";

    private Rect onImageRect = new Rect();
    private int degree = 0;

    private int clipartType = BITMAP;
    private int clipartResId = 0;
    private int clipartFillColorType = FILL_COLOR_MASK;

    private Paint handleRectPaint1;
    private Paint handleRectPaint2;

    private int resId = -1;

    private float startRotateDegree = 0f;
    private float preDegree = 0f;

    private Bitmap bitmap = null;

    // center coords of this object, pivot point for scaling, so not affected by
    // center-scale
    private float centerX = -1f;
    private float centerY = -1f;

    private float origWidth = 0;
    private float origHeight = 0;
    private float origRatio = -1f;

    // width/height of selected area, scaled values
    private float curWidth;
    private float curHeight;

    private Rect crectOrig = new Rect();

    private String filePath = null;
    private HashMap<Object, Object> bufferData = null;
    private boolean fromPicsinFile = false;
    private String picsinFile = null;

    private Bitmap handleCorner = null;
    private Bitmap handleRotate = null;
    private Bitmap handleSide = null;
    private boolean showRotateHandle = true;

    private Integer curZoomType = null;
    private int currentAction = Graphics.ACTION_DRAG;

    // TODO: view is (was) used only for invalidate()
    private View view = null;

    private Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    private Context context = null;

    private int color;

    private int addPhotoMaxSize;
    private GestureDetector gestureDetector;

    private PointF pinchStartPoint1;
    private PointF pinchStartPoint2;

    // drawing/clear mode
    private static final PorterDuffXfermode CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private static final PorterDuffXfermode DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    private boolean drawingMode = false;
    private boolean clearMode = true;
    private boolean trimmedAfterClear = false;
    private Rect crectTrimmed = new Rect();

    private Paint dstOutPaint;
    private Paint maskPathPaint;

    private Bitmap bitmapMask;
    private List<DrawPath> drawPaths = new ArrayList<DrawPath>();
    private Path drawPathCurrent = null;

    private Path updatePath = new Path();
    private RectF updateRectF = new RectF();
    private Rect updateRect = new Rect();

    private float lastXClipart, lastYClipart;
    private float brushSize = -1f;
    private float brushHardness = DEFAULT_BRUSH_HARDNESS;
    private int brushSizeDip = DEFAULT_BRUSH_SIZE_DIP;

    private RectF clearBitmaskDstRectF = new RectF();
    private Rect clearBitmaskSrcRect = new Rect();


    public ClipArt(Parcel in) {
        readFromParcel(in);

        if (clipartType == BITMAP) {
            this.imageSource = imageSource == null ? "" : imageSource; // exif
            initBitmap(context, clipartResId, filePath, bufferData, fromPicsinFile, picsinFile);
        }

        initPaintObjs();

        clearBitmaskDstRectF.set(0, 0, origWidth, origHeight);

        gestureDetector = new GestureDetector(this);
    }

    public ClipArt(Context context, int clipartResId, String path, HashMap<Object, Object> bufferData, int clipartType, int clipartFillColorType, boolean fromPicsinFile, String fileName, View view, String imageSource, int degree) {
        this.clipartType = clipartType;
        this.view = view;
        this.filePath = path;
        this.bufferData = bufferData;
        this.degree = degree;
        this.context = context;
        this.addPhotoMaxSize = 640;
        this.resId = clipartResId;
        this.clipartFillColorType = clipartFillColorType;

        this.clipartResId = clipartResId;

        this.fromPicsinFile = fromPicsinFile;
        this.picsinFile = fileName;

        isDrawHandle = true;
        initHandles(context);

        if (clipartType == BITMAP) {
            this.imageSource = imageSource == null ? "" : imageSource; // exif
            initBitmap(context, clipartResId, path, bufferData, fromPicsinFile, fileName);
        }

        initPaintObjs();

        setBrushSize(brushSizeDip);
        setBrushHardness(brushHardness);

        clearBitmaskDstRectF.set(0, 0, origWidth, origHeight);

        gestureDetector = new GestureDetector(this);
    }

    private void initPaintObjs() {
        handleRectPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        handleRectPaint1.setColor(Color.WHITE);
        handleRectPaint1.setStyle(Style.STROKE);
        handleRectPaint1.setStrokeWidth(1f);

        handleRectPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        handleRectPaint2.setColor(0x99000000);
        handleRectPaint2.setStyle(Style.STROKE);
        handleRectPaint2.setStrokeWidth(1f);

//		testPaint = new Paint();
//		testPaint.setStyle(Style.STROKE);
//		testPaint.setStrokeWidth(2f);

        dstOutPaint = new Paint();
        dstOutPaint.setXfermode(DST_OUT);

        // draw path over trans bitmap
        maskPathPaint = new Paint();
        initDrawingPaint(maskPathPaint);
        maskPathPaint.setColor(Color.WHITE);
    }

    public void initDrawingPaint(Paint paint) {
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setFilterBitmap(true);
    }

    /**
     * Javadoc copied from Jira issue 378
     * <p/>
     * Added photo max preview size is (screen size)/3
     * Added photo min preview size is ~32dp
     * <p/>
     * if (added photo size max(width,height)) < (min preview size) - > resize to min size
     * if (added photo size max(width,height)) > (max preview size) - > resize to max
     * if min < (added photo size max(width,height)) < max - > do nothing, show original size
     *
     * @param viewWidth
     * @param viewHeight
     */
    public void initSizeParams(int viewWidth, int viewHeight) {
        if (BITMAP == clipartType) {
            // the new logic
            // init scale
            final float minInitSize = 32;
            final float maxPhotoSize = Math.max(origWidth, origHeight);
            float scale = 1f;
            if (maxPhotoSize < minInitSize) {
                scale = minInitSize / maxPhotoSize;
            } else {
                final float maxInitSize;
                if (origWidth >= origHeight) {
                    maxInitSize = viewWidth / 3f;
                } else {
                    maxInitSize = viewHeight / 3f;
                }
                if (maxPhotoSize > maxInitSize) {
                    scale = maxInitSize / maxPhotoSize;
                }
            }

            scaleX = scale;
            scaleY = scale;

            // init size
            curWidth = origWidth * scale;
            curHeight = origHeight * scale;

            // init position if haven't been set
            if (centerX == -1f || centerY == -1f) {
                centerX = viewWidth * 0.5f;
                centerY = viewHeight * 0.5f;
            }
        } else {
            // for SVG cliparts use the old logic
            // init scale
            final float minInitSize = 32;
            final float minViewDimen = Math.min(viewWidth, viewHeight);
            final float initSize = Math.max(minViewDimen / 3f, minInitSize);
            float scale = initSize / Math.max(origWidth, origHeight);
            scaleX = scale;
            scaleY = scale;

            // init size
            curWidth = origWidth * scale;
            curHeight = origHeight * scale;

            // init position if haven't been set
            if (centerX == -1f || centerY == -1f) {
                centerX = viewWidth * 0.5f;
                centerY = viewHeight * 0.5f;
            }

        }
    }

    public void updateClipArt(String path, HashMap<Object, Object> bufferData) {
        this.filePath = path;
        this.bufferData = bufferData;

        BitmapManager.recycle(bitmap);
        bitmap = null;

        // init bitmap
        initBitmap(context, clipartResId, path, bufferData, fromPicsinFile, picsinFile);

        curWidth = origWidth * scaleX;
        curHeight = origHeight * scaleY;

        // TODO: ???
        refreshClipartProperties();

        clearDrawingInfo();
        clearBitmaskDstRectF.set(0, 0, origWidth, origHeight);
    }

    private void clearDrawingInfo() {
        if (drawPaths.isEmpty())
            return;

        drawPaths.clear();
        drawPathCurrent = null;

        BitmapManager.recycle(bitmapMask);
        bitmapMask = null;
        bitmapMaskCanvas = null;

        // reset trimming info
        trimmedAfterClear = false;
    }

    public String getImageSource() {
        return imageSource;
    }

    public String getFilePath() {
        return filePath;
    }

    public HashMap<Object, Object> getBufferData() {
        return bufferData;
    }

    private void initBitmap(Context context, int clipartResId, String path, HashMap<Object, Object> bufferData, boolean fromPicsinFile, String fileName) {
        String expMsg = "no exceptions";
        // shop items
        if (fromPicsinFile) {
            InputStream is = null;
            try {
                is = new FileInputStream(path);
                bitmap = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                expMsg = e.getMessage();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                }
            }
        } else {
            if (bufferData != null) {
                bitmap = getScaledBitmapFromBufferPathFast(bufferData, addPhotoMaxSize, addPhotoMaxSize, degree);
            } else {
                if (path != null) {
                    File file = new File(path);
                    if (!file.exists()) {
                        return;
                    }
                    try {
                        bitmap = getScaledBitmapFromRealPath_ARGB8(path, addPhotoMaxSize, addPhotoMaxSize, degree);
                    } catch (Exception e) {
                        expMsg = e.getMessage();
                    }
                } else {
                    if (clipartResId != -1 && context != null) {
                        bitmap = BitmapManager.decodeResource(context.getResources(), clipartResId);
                    }
                }
            }
        }

        if (bitmap == null && context instanceof Activity) {
            String dumpParams = "Params (initBitmapProperties): expMsg=" + expMsg + " clipartResId=" + clipartResId + " path=" + path + (bufferData != null ? " bufferData=" + bufferData + " bufferData.get(path)=" + bufferData.get("path") : " bufferData is null") + " fromPicsinFile=" + fromPicsinFile + " fileName=" + fileName;
            throw new IllegalArgumentException("bitmap resource is invalid " + dumpParams);
        }

        if (bitmap != null) {
            origWidth = bitmap.getWidth();
            origHeight = bitmap.getHeight();

            origRatio = origWidth / origHeight;

            Log.d("bitmapSize", bitmap.getWidth() + ", " + bitmap.getHeight() + ",   " + bitmap.getRowBytes() * bitmap.getHeight() / 1024f / 1024f);
        }
    }

    public static final int SCALE_ABSOLUTE = 1;

    public static Bitmap getScaledBitmapFromRealPath_ARGB8(String realPath, int land_width, int land_height, int degree, int type) throws Exception {
        long time = System.currentTimeMillis();

        Bitmap bitmap = null;

        if (type == SCALE_ABSOLUTE) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Config.ARGB_8888;
            Bitmap origBitmap = BitmapManager.decodeFile(realPath, options);
            bitmap = getScaledBitmap_ARGB8(origBitmap, land_width, land_height, SCALE_ABSOLUTE);

            BitmapManager.recycle(origBitmap);

            return bitmap;
        }

        int port_width = land_height;
        int port_height = land_width;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapManager.decodeFile(realPath, options);
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

        L.d("+++++++++++++++++++++++++" + "load done in " + (System.currentTimeMillis() - time) + " ms");

        return bitmap;
    }

    public static final int SCALE_PROPOTIONAL = 0;

    public static Bitmap getScaledBitmapFromRealPath_ARGB8(String realPath, int land_width, int land_height, int degree) throws Exception {
        return getScaledBitmapFromRealPath_ARGB8(realPath, land_width, land_height, degree, SCALE_PROPOTIONAL);
    }

    public static Bitmap getScaledBitmap_ARGB8(Bitmap bitmap, int width, int height, int type) {

        final float scale = Math.min((float) width / (float) bitmap.getWidth(), (float) height / (float) bitmap.getHeight());

        Bitmap scaledBitmap;
        if (type == SCALE_ABSOLUTE) {
            scaledBitmap = BitmapManager.createBitmap(width, height, Config.ARGB_8888);
        } else {
            scaledBitmap = BitmapManager.createBitmap(Math.round(bitmap.getWidth() * scale), Math.round(bitmap.getHeight() * scale), Config.ARGB_8888);
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

    public static Bitmap getBitmapFromFile_ARGB8(String realPath, int degree) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;

        return getRotatedBitmapAndRecycle_ARGB8(BitmapManager.decodeFile(realPath, options), degree);
    }

    public static Bitmap getScaledBitmap_ARGB8(String path, int width, int height, int origWidth, int origHeight, boolean proportionally, int degree) {
        final float scale = Math.min((float) width / (float) origWidth, (float) height / (float) origHeight);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = (int) (1 / scale);
        options.inPreferredConfig = Config.ARGB_8888;
        Bitmap bitmapFromFile = BitmapManager.decodeFile(path, options);

        if (bitmapFromFile != null) {
            Matrix matrix = new Matrix();
            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            paint.setDither(false);

            final float scale2 = Math.min((float) width / (float) bitmapFromFile.getWidth(), (float) height / (float) bitmapFromFile.getHeight());

            Bitmap scaledBitmap = BitmapManager.createBitmap(Math.round(bitmapFromFile.getWidth() * scale2), Math.round(bitmapFromFile.getHeight() * scale2), Config.ARGB_8888);
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

    public static Bitmap getScaledBitmapFromBufferPathFast(HashMap<Object, Object> bufferData, int new_width, int new_height, int degree) {
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
            readFromFile(path, buffer);
        } catch (Exception e) {
            return null;
        }

        if (new_width == 0 && new_height == 0 && degree == 0) {
            Bitmap image = BitmapManager.createBitmap(width, height, Config.ARGB_8888);
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
                scaledBitmap = BitmapManager.createBitmap(scaledWidth, scaledHeight, Config.ARGB_8888);
                scaledBitmap.copyPixelsFromBuffer(scaledBitmapBuffer);

                if (useNativeMemory) {
                    ImageOpCommon.freeNativeBuffer(scaledBitmapBuffer);
                }

            } else {
                Bitmap image = BitmapManager.createBitmap(width, height, Config.ARGB_8888);
                buffer.position(0);
                image.copyPixelsFromBuffer(buffer);
                if (useNativeMemory) {
                    ImageOpCommon.freeNativeBuffer(buffer);
                }
                scaledBitmap = image;
            }

            if (degree == 0 || degree == 360)
                return scaledBitmap;

            // rotate with canvas
            return getRotatedBitmapAndRecycle_ARGB8(scaledBitmap, degree);
        }
    }

    public static Bitmap getRotatedBitmapAndRecycle_ARGB8(Bitmap bitmap, int rotation) {
        if (bitmap == null || bitmap.isRecycled())
            return bitmap;

        if (rotation == 0 || rotation == 360)
            return bitmap;

        Bitmap rotatedBitmap;

        Matrix matrix = new Matrix();
        matrix.reset();
        if (rotation == 90 || rotation == 270) {
            rotatedBitmap = BitmapManager.createBitmap(bitmap.getHeight(), bitmap.getWidth(), Config.ARGB_8888);
            if (rotation == 90) {
                matrix.postRotate(90);
                matrix.postTranslate(rotatedBitmap.getWidth(), 0);
            } else { // 270
                matrix.postRotate(270);
                matrix.postTranslate(0, rotatedBitmap.getHeight());
            }
        } else {
            rotatedBitmap = BitmapManager.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            matrix.postRotate(180);
            matrix.postTranslate(rotatedBitmap.getWidth(), rotatedBitmap.getHeight());
        }
        Canvas c = new Canvas(rotatedBitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        c.drawBitmap(bitmap, matrix, paint);

        BitmapManager.recycle(bitmap);

        return rotatedBitmap;

    }

    public static void readFromFile(String path, ByteBuffer toReadIn) throws IOException {
        readFromFile(path, toReadIn, 0);
    }

    public static void readFromFile(String path, ByteBuffer toReadIn, int bytesToSkip) throws IOException {
        FileChannel fch = new FileInputStream(path).getChannel();
        fch.position(bytesToSkip);
        fch.read(toReadIn);
        fch.close();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return 0;
    }

    private void initHandles(Context context) {
        if (context == null) {
            return;
        }

        if (handleCorner == null || handleCorner.isRecycled()) {
            handleCorner = BitmapManager.decodeResource(context.getResources(), R.drawable.handle_rect_corner_picsart_light2);
        }

        if (handleSide == null || handleSide.isRecycled()) {
            handleSide = BitmapManager.decodeResource(context.getResources(), R.drawable.handle_rect_side_picsart_light);
        }

        if (handleRotate == null || handleRotate.isRecycled()) {
            handleRotate = BitmapManager.decodeResource(context.getResources(), R.drawable.handle_rotate_picsart_light);
        }
    }

    public void draw(Canvas c, float left, float top, float scaleFactor, float imageCurrentZoom, float rotate) {
        float scale = scaleFactor * imageCurrentZoom;
        imageZoom = imageCurrentZoom;
        imageLeft = left;
        imageTop = top;

        left = left / scale;
        top = top / scale;

        float xScaled = centerX - curWidth * 0.5f;
        float yScaled = centerY - curHeight * 0.5f;
        float x = xScaled / scale - left;
        float y = yScaled / scale - top;

        float sX = scaleX / scale;
        float sY = scaleY / scale;

        float rotDegree = rotateDegree - rotate;

        float w = curWidth / scale;
        float h = curHeight / scale;
        onImageRect.left = Math.round(x);
        onImageRect.right = Math.round(x + w);
        onImageRect.top = Math.round(y);
        onImageRect.bottom = Math.round(y + h);

        float centerXImageRect = onImageRect.exactCenterX();
        float centerYImageRect = onImageRect.exactCenterY();

        float xUnscaled = calculateUnScaledCoordinate(x, centerXImageRect, sX);
        float yUnscaled = calculateUnScaledCoordinate(y, centerYImageRect, sY);

        if (clipartType == ClipArt.SVG) {
            c.save();
            c.rotate(rotDegree, centerXImageRect, centerYImageRect);
            c.scale(sX, sY, centerXImageRect, centerYImageRect);
            c.translate(xUnscaled, yUnscaled);

//			if (!trimmedAfterClear) {
            c.clipRect(0, 0, origWidth, origHeight);
//			} else {
//				c.clipRect(out_rect_fld[0], out_rect_fld[1], out_rect_fld[2],out_rect_fld[3]);
//			}

            c.saveLayer(0, 0, origWidth, origHeight, null, Canvas.ALL_SAVE_FLAG);
            {
                reDrawBackgroundBitmap(c, sX, sY, rotDegree, centerXImageRect, centerYImageRect, xUnscaled, yUnscaled);

                // restore bitmask if it's been cleared
                if (!drawPaths.isEmpty() && !isClearMaskAvailable()) {
                    // create mask from paths
                    updateBitmapMask();
                }
                if (isClearMaskAvailable()) {
                    // draw mask, fit mask bitmap into clipart area
                    clearBitmaskSrcRect.set(0, 0, bitmapMask.getWidth(), bitmapMask.getHeight());
                    c.drawBitmap(bitmapMask, clearBitmaskSrcRect, clearBitmaskDstRectF, dstOutPaint);
                }

            }
            c.restore();

            c.restore();
        } else {
            if (bitmap == null || bitmap.isRecycled()) {
                initBitmap(context, resId, filePath, bufferData, fromPicsinFile, picsinFile);
            }
            c.save();
            c.rotate(rotDegree, centerXImageRect, centerYImageRect);
            c.scale(sX, sY, centerXImageRect, centerYImageRect);
            c.translate(xUnscaled, yUnscaled);

            c.clipRect(0, 0, origWidth, origHeight);

            c.saveLayer(0, 0, origWidth, origHeight, null, Canvas.ALL_SAVE_FLAG);
            {
                reDrawBackgroundBitmap(c, sX, sY, rotDegree, centerXImageRect, centerYImageRect, xUnscaled, yUnscaled);

                if (bitmap != null && !bitmap.isRecycled()) c.drawBitmap(bitmap, 0, 0, bitmapPaint);

                // restore bitmask if it's been cleared
                if (!drawPaths.isEmpty() && !isClearMaskAvailable()) {
                    // create mask from paths
                    updateBitmapMask();
                }
                if (isClearMaskAvailable()) {
                    clearBitmaskSrcRect.set(0, 0, bitmapMask.getWidth(), bitmapMask.getHeight());
                    // draw mask, fit mask bitmap into clipart area
                    c.drawBitmap(bitmapMask, clearBitmaskSrcRect, clearBitmaskDstRectF, dstOutPaint);
                }

            }
            c.restore();

            c.restore();
        }
    }

    private void reDrawBackgroundBitmap(Canvas c, float sX, float sY, float rotDegree, float centerXImageRect, float centerYImageRect, float xUnscaled, float yUnscaled) {
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if (!active) {
            if (isClearMaskAvailable()) {
                // clear mask
                BitmapManager.recycle(bitmapMask);
                bitmapMask = null;
            }
        } else {
            if (!drawPaths.isEmpty()) {
                // create mask
                updateBitmapMask();
            }
        }
    }

    private boolean firstMoveEvent = true;

    public void draw(Canvas c) {
        if (isActive) {
            float xUnscaled = centerX - origWidth * 0.5f;
            float yUnscaled = centerY - origHeight * 0.5f;

            // try to use updateRect in saveLayer
//			calculateUnScaledRect(updateRect, updateRect2);
//			updateRect2.inset(-updateRect2.width()*0.5f, -updateRect2.height()*0.5f);

//			Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//			strokePaint.setColor(Color.RED);
//			strokePaint.setStyle(Style.STROKE);
//			strokePaint.setStrokeWidth(1f);
//			Log.e("ex", "draw updateRect2="+updateRect2.toString());

            if (clipartType == ClipArt.SVG) {
                refreshClipartProperties();
                c.save();

                c.rotate(rotateDegree, centerX, centerY);
                c.scale(scaleX, scaleY, centerX, centerY);
                c.translate(xUnscaled, yUnscaled);

                // if clear/draw never been used
                if (drawPaths.isEmpty()) {
                } else {

                    //				if (!trimmedAfterClear) {
                    c.clipRect(0, 0, origWidth, origHeight);
                    //				} else {
                    //					c.clipRect(out_rect_fld[0], out_rect_fld[1], out_rect_fld[2],out_rect_fld[3]);
                    //				}

                    //				getDrawSaveLayerRectF();
                    //				Log.e("ex", "draw saveLayerRectF="+saveLayerRectF.toString());
                    //				c.drawRect(saveLayerRectF, strokePaint);

                    //				if (isInvalidateWithRect()) {
                    //					// normalize save layer rect
                    //					normalizeUpdateRectSaveLayer();
                    ////					Log.e("ex", "draw invalidateWithRect updateRect="+updateRect.toString());
                    ////					c.saveLayer(updateRectSaveLayer, null, Canvas.ALL_SAVE_FLAG);
                    ////					c.saveLayer(origWidth*0.25f, origHeight*0.25f, origWidth*0.75f, origHeight*0.75f, null, Canvas.ALL_SAVE_FLAG);
                    ////					c.saveLayer(10, 10, origWidth-10, origHeight-10, null, Canvas.ALL_SAVE_FLAG);
                    //					c.saveLayer(0, 0, origWidth, origHeight, null, Canvas.ALL_SAVE_FLAG);
                    //				} else {
                    //					Log.e("ex", "draw invalidate ALL updateRect="+updateRect.toString());
                    //					Log.e("ex", "draw invalidate ALL c.getClipBounds()="+c.getClipBounds().toString());
                    c.saveLayer(0, 0, origWidth, origHeight, null, Canvas.ALL_SAVE_FLAG);
                    //				}
                    {
                        reDrawBackgroundBitmap(c, xUnscaled, yUnscaled);

                        drawClearMask(c);

                    }
                    c.restore();
                }
//				c.drawRect(updateRectSaveLayer, strokePaint);

                c.restore();

//				strokePaint.setColor(Color.GREEN);
//				c.drawRect(updateRect, strokePaint);
            } else {
                refreshClipartProperties();

                // TODO: ?? and next if check ??
                if (bitmap == null || bitmap.isRecycled()) {
                    initBitmap(context, resId, filePath, bufferData, fromPicsinFile, picsinFile);
                }

                c.save();

                c.rotate(rotateDegree, centerX, centerY);
                c.scale(scaleX, scaleY, centerX, centerY);
                c.translate(xUnscaled, yUnscaled);

                // if clear/draw never been used
                if (drawPaths.isEmpty()) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        c.drawBitmap(bitmap, 0, 0, bitmapPaint);
                    }
                } else {
                    c.clipRect(0, 0, origWidth, origHeight);

                    c.saveLayer(0, 0, origWidth, origHeight, null, Canvas.ALL_SAVE_FLAG);
                    {
                        reDrawBackgroundBitmap(c, xUnscaled, yUnscaled);

                        if (bitmap != null && !bitmap.isRecycled()) {
                            c.drawBitmap(bitmap, 0, 0, bitmapPaint);
                        }

                        drawClearMask(c);

                    }
                    c.restore();

                    //				c.drawRect(updateRect, strokePaint);
                }
                c.restore();
            }
        }

        // test point coordinates
//		testPaint.setColor(Color.CYAN);
//		c.drawLine(0, 0, centerX, centerY, testPaint);

        if (isDrawHandle && !drawingMode) {
            if (handleCorner == null || handleCorner.isRecycled() || handleRotate == null || handleRotate.isRecycled() || handleSide == null || handleSide.isRecycled()) {
                initHandles(context);
            }
            try {
                Graphics.showHandle(c, crectTrimmed, handleRectPaint1, handleRectPaint2, centerX, centerY, rotateDegree, handleCorner, handleRotate, handleSide, showRotateHandle, true);
            } catch (Exception e) {
            }
        }
    }

    private void reDrawBackgroundBitmap(Canvas c, float xUnscaled, float yUnscaled) {
        // re-draw background image to blend with clipart
        // un-transform
        c.translate(-xUnscaled, -yUnscaled);
        c.scale(1 / scaleX, 1 / scaleY, centerX, centerY);
        c.rotate(-rotateDegree, centerX, centerY);

        // re-transform
        c.rotate(rotateDegree, centerX, centerY);
        c.scale(scaleX, scaleY, centerX, centerY);
        c.translate(xUnscaled, yUnscaled);
        // end of re-draw
    }

    private void drawClearMask(Canvas c) {
        // restore bitmask if it's been cleared
        if (!drawPaths.isEmpty() && !isClearMaskAvailable()) {
            // create mask from paths
            updateBitmapMask();
        }
        if (isClearMaskAvailable()) {
            clearBitmaskSrcRect.set(0, 0, bitmapMask.getWidth(), bitmapMask.getHeight());
//			if (drawPathCurrent != null) {
            if (isInvalidateWithRect()) {
                // complete drawing on bitmask with current path and in the end apply the bitmask with DstOut mode

//				c.saveLayer(updateRectSaveLayer, dstOutPaint, Canvas.ALL_SAVE_FLAG);
//				c.saveLayer(saveLayerRectF, dstOutPaint, Canvas.ALL_SAVE_FLAG);
                c.saveLayer(0, 0, origWidth, origHeight, dstOutPaint, Canvas.ALL_SAVE_FLAG);

                // draw the current bitmask (not yet complete with current path)
                c.drawBitmap(bitmapMask, clearBitmaskSrcRect, clearBitmaskDstRectF, null);

                // un-scale
                c.scale(1f / scaleX, 1f / scaleY);

                // draw current path which is not in the mask yet
                if (clearMode) {
                    maskPathPaint.setXfermode(null);
                    maskPathPaint.setColor(Color.WHITE);
                    c.drawPath(drawPathCurrent, maskPathPaint);
                } else {
                    maskPathPaint.setXfermode(CLEAR);
                    c.drawPath(drawPathCurrent, maskPathPaint);
                    maskPathPaint.setXfermode(null);
                }
                // re-scale
                c.scale(scaleX, scaleY);

                c.restore();
            } else {
                // draw mask, fit mask bitmap into clipart area
                c.drawBitmap(bitmapMask, clearBitmaskSrcRect, clearBitmaskDstRectF, dstOutPaint);
            }
        }
    }

    private void refreshClipartProperties() {
        float trectLeft, trectTop, trectRight, trectBottom;

        float xUnscaled = centerX - origWidth * 0.5f;
        float yUnscaled = centerY - origHeight * 0.5f;

        trectLeft = calculateScaledCoordinate(xUnscaled, centerX, scaleX);
        trectTop = calculateScaledCoordinate(yUnscaled, centerY, scaleY);
        trectRight = trectLeft + curWidth;
        trectBottom = trectTop + curHeight;
        crectOrig.left = (int) Math.ceil(trectLeft);
        crectOrig.top = (int) Math.ceil(trectTop);
        crectOrig.right = (int) Math.ceil(trectRight);
        crectOrig.bottom = (int) Math.ceil(trectBottom);

        if (trimmedAfterClear) {
            trectLeft = calculateScaledCoordinate(xUnscaled + out_rect_fld[0], centerX, scaleX);
            trectTop = calculateScaledCoordinate(yUnscaled + out_rect_fld[1], centerY, scaleY);
            trectRight = calculateScaledCoordinate(xUnscaled + out_rect_fld[2], centerX, scaleX);
            trectBottom = calculateScaledCoordinate(yUnscaled + out_rect_fld[3], centerY, scaleY);
            crectTrimmed.left = (int) Math.ceil(trectLeft);
            crectTrimmed.top = (int) Math.ceil(trectTop);
            crectTrimmed.right = (int) Math.ceil(trectRight);
            crectTrimmed.bottom = (int) Math.ceil(trectBottom);
        } else {
            crectTrimmed.set(crectOrig);
        }
    }

    private float calculateScaledCoordinate(float coord, float centerCoord, float scale) {
        return (coord - centerCoord) * scale + centerCoord;
    }

    private float calculateUnScaledCoordinate(float coord, float centerCoord, float scale) {
        return (coord - centerCoord) / scale + centerCoord;
    }

    private boolean touchDownHandled = false;

    private boolean handleTouchDown(float x, float y) {

        return true;
    }


    public static final int ZOOM_RIGHT_BOTTOM = 3;
    public static final int ZOOM_TOP = 4;
    public static final int ZOOM_RIGHT = 5;
    public static final int ZOOM_BOTTOM = 6;
    public static final int ZOOM_LEFT = 7;

    private void handleTouchMove(float x, float y) {
        if (lastTouchX == -1 || lastTouchY == -1) {
            lastTouchX = x;
            lastTouchY = y;
            return;
        }

        float dx = (x - lastTouchX);
        float dy = (y - lastTouchY);

        if (currentAction == Graphics.ACTION_ZOOM) {
            // rotate x,y coordinate to find position in original rect
            float[] point = {x, y};
            float[] rotatedPoint = Graphics.getRotatePoint(point, centerX, centerY, -rotateDegree);
            x = rotatedPoint[0];
            y = rotatedPoint[1];

            switch (curZoomType) {
                case ZOOM_RIGHT_BOTTOM:
                    zoomProportional((x - crectTrimmed.right), (y - crectTrimmed.bottom), false);
                    break;
                case ZOOM_TOP:
                    zoomFree(0f, -(y - crectTrimmed.top));
                    break;
                case ZOOM_RIGHT:
                    zoomFree(x - crectTrimmed.right, 0f);
                    break;
                case ZOOM_BOTTOM:
                    zoomFree(0f, y - crectTrimmed.bottom);
                    break;
                case ZOOM_LEFT:
                    zoomFree(-(x - crectTrimmed.left), 0f);
                    break;
            }
        }

        if (currentAction == Graphics.ACTION_DRAG) {
            if (!isDrawingModeOrZoomInDrawing()) {
                centerX += dx;
                centerY += dy;
            } else if (isDrawingMode()) {
                if (firstMoveEvent) {
                    startDrawPath(x, y);
                    firstMoveEvent = false;
                } else {
                    continueDrawPath(x, y);
                }
            }
        }

        if (currentAction == Graphics.ACTION_ROTATE) {
            showRotateHandle = false;
            rotate(x, y);
        }

        lastTouchX = x;
        lastTouchY = y;
    }

    private boolean notifyUndoEnabler = false;

    private void startDrawPath(float x, float y) {
        float[] point = {x, y};
        float[] rotatedPoint = Graphics.getRotatePoint(point, centerX, centerY, -rotateDegree);

        float xClipart = rotatedPoint[0] - crectOrig.left;
        float yClipart = rotatedPoint[1] - crectOrig.top;

        drawPathCurrent = new Path();
        drawPathCurrent.moveTo(xClipart, yClipart);

        updatePath.reset();
        updatePath.moveTo(x, y);

        // store path info
        drawPaths.add(new DrawPath(drawPathCurrent, new Transform(scaleX, scaleY), clearMode, brushSize, brushHardness));
        notifyUndoEnabler = true;

        lastXClipart = xClipart;
        lastYClipart = yClipart;
    }

    private void continueDrawPath(float x, float y) {
        if (drawPathCurrent == null) {
            // drawing interrupted
            return;
        }

        float[] point = {x, y};
        float[] rotatedPoint = Graphics.getRotatePoint(point, centerX, centerY, -rotateDegree);

        float xClipart = rotatedPoint[0] - crectOrig.left;
        float yClipart = rotatedPoint[1] - crectOrig.top;
        float middleXClipart = (xClipart + lastXClipart) * 0.5f;
        float middleYClipart = (yClipart + lastYClipart) * 0.5f;

        drawPathCurrent.quadTo(lastXClipart, lastYClipart, middleXClipart, middleYClipart);

        lastXClipart = xClipart;
        lastYClipart = yClipart;

        float middleX = (x + lastTouchX) * 0.5f;
        float middleY = (y + lastTouchY) * 0.5f;
        updatePath.quadTo(lastTouchX, lastTouchY, middleX, middleY);
        normalizeUpdateRect();
        updatePath.reset();
        updatePath.moveTo(middleX, middleY);
    }

    private void normalizeUpdateRect() {
        updatePath.computeBounds(updateRectF, true);
        float delta = brushSize * (1 + brushHardness / 100f) * 0.5f + 1;
        updateRectF.left -= delta;
        updateRectF.top -= delta;
        updateRectF.right += delta;
        updateRectF.bottom += delta;

        updateRectF.inset(-updateRectF.width() * 0.5f - 10, -updateRectF.height() * 0.5f - 10);

        updateRect.set((int) updateRectF.left, (int) updateRectF.top, (int) updateRectF.right, (int) updateRectF.bottom);
    }

    public Rect getUpdateRect() {
        return updateRect;
    }

    private void handleTouchUp() {
    }

    private Canvas bitmapMaskCanvas;

    private void updateBitmapMask() {
        // bitmask's width/height
        int newWidth = Math.abs((int) Math.ceil(curWidth));
        int newHeight = Math.abs((int) Math.ceil(curHeight));
        if (newWidth == 0 || newHeight == 0) return;
        int maxSize = 1024; // PicsartContext.getMaxImageSizePixel();
        if (newWidth > maxSize || newHeight > maxSize) {
//			float ratio = Math.abs( curWidth / curHeight );
            if (newWidth > newHeight) {
                // scale down by width
                newWidth = maxSize;
                newHeight = (int) Math.ceil(newWidth / origRatio);
            } else {
                // scale down by height
                newHeight = maxSize;
                newWidth = (int) Math.ceil(newHeight * origRatio);
            }
        } else {
            // FOLLOW clipart's original ratio
            newWidth = (int) Math.ceil(newHeight * origRatio);
        }

        // first time create new bitmap
        if (bitmapMask == null || bitmapMask.getWidth() != newWidth || bitmapMask.getHeight() != newHeight) {
            if (bitmapMask != null) {
                BitmapManager.recycle(bitmapMask);
                bitmapMask = null;
            }
            bitmapMask = BitmapManager.createBitmap(newWidth, newHeight, Config.ALPHA_8);
            bitmapMaskCanvas = new Canvas(bitmapMask);
        }
        if (bitmapMask == null) {
            Log.d("gogo", "#########    BITMASK IS NUUUUUUL newWidth=" + newWidth + " newHeight=" + newHeight);
            return;
        }
        // reset bitmask
        bitmapMask.eraseColor(Color.TRANSPARENT);

        // (re)draw all paths
        for (DrawPath dp : drawPaths) {
            if (dp.clear) {
                maskPathPaint.setColor(Color.WHITE);
            } else {
                maskPathPaint.setXfermode(CLEAR);
            }
            // recover scale
            float sx0 = curWidth / bitmapMask.getWidth();
            float sy0 = curHeight / bitmapMask.getHeight();
            float sxChange = ((scaleX) / dp.transform.sx) / sx0;
            float syChange = ((scaleY) / dp.transform.sy) / sy0;
            bitmapMaskCanvas.scale(sxChange, syChange);
            // recover brush size
            maskPathPaint.setStrokeWidth(dp.brushSize);
            // recover brush hardness
            maskPathPaint.setMaskFilter(buildBlurMaskFilter(dp.brushHardness));

            bitmapMaskCanvas.drawPath(dp.path, maskPathPaint);

            maskPathPaint.setXfermode(null);
            bitmapMaskCanvas.setMatrix(null);
        }
        // restore mask path paint
        maskPathPaint.setStrokeWidth(brushSize);
        maskPathPaint.setMaskFilter(buildBlurMaskFilter(brushHardness));

    }

    private BlurMaskFilter buildBlurMaskFilter(float hardness) {
        BlurMaskFilter filter = null;
        if (hardness > 0f) {
            filter = new BlurMaskFilter(maskPathPaint.getStrokeWidth() * hardness / 100f, BlurMaskFilter.Blur.NORMAL);
        }

        return filter;
    }

    private boolean isClearMaskAvailable() {
        return bitmapMask != null && !bitmapMask.isRecycled();
    }

    float lastTouchX = -1f;
    float lastTouchY = -1f;

    public void onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean touch_down(float x, float y) {
        if (inPinchMode) return false;

        boolean touchDownResult = handleTouchDown(x, y);
        touchDownHandled = true;

        return touchDownResult;
    }

    @Override
    public void touch_move(float x, float y) {
        if (inPinchMode || !touchDownHandled) return;

        handleTouchMove(x, y);
    }

    @Override
    public void touch_up() {
        if (inPinchMode) return;

        handleTouchUp();

        touchDownHandled = false;
    }

    public void sizeChanged(int left, int top, int oldLeft, int oldTop, float scaleFactor, float imageCurrentZoom) {
        if (!isActive) {
            activate(oldLeft, oldTop, imageCurrentZoom);
            imageZoom = imageCurrentZoom;
            imageLeft = (float) left;
            imageTop = (float) top;
        }

        // new scale
        scaleX *= scaleFactor;
        scaleY *= scaleFactor;

        // new scaled x,y
        float xScaled = centerX - curWidth * 0.5f;
        float yScaled = centerY - curHeight * 0.5f;
        xScaled = (xScaled - oldLeft) * scaleFactor + left;
        yScaled = (yScaled - oldTop) * scaleFactor + top;

        // new size
        curWidth = curWidth * scaleFactor;
        curHeight = curHeight * scaleFactor;

        // new center coords
        centerX = xScaled + curWidth * 0.5f;
        centerY = yScaled + curHeight * 0.5f;

        // new crect
        refreshClipartProperties();
    }

    @Override
    public void setOpacity(int opacity) {
        this.opacity = opacity;
        bitmapPaint.setAlpha(opacity);
    }

    public void rotate(float x, float y) {
        float angle = (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
        angle = angle < 0 ? 2 * 180 + angle : angle;

        rotateDegree = (int) (angle + startRotateDegree - preDegree);
    }

    public void zoomFree(float moveX, float moveY) {
        if (isSmallerThanThreshold(moveX, moveY)) {
            return;
        }

        curWidth += 2 * moveX;
        curHeight += 2 * moveY;

        scaleX = curWidth / origWidth;
        scaleY = curHeight / origHeight;
    }

    public void zoomProportional(float moveX, float moveY, boolean minSize) {
        if (isSmallerThanThreshold(moveX, moveY)) {
            return;
        }

        float oldScaleX = scaleX;
        float oldScaleY = scaleY;

        scaleX = (curWidth + moveX) / origWidth;
        scaleY = (curHeight + moveY) / origHeight;

        float deltaScaleX = Math.abs(oldScaleX) - Math.abs(scaleX);
        float deltaScaleY = Math.abs(oldScaleY) - Math.abs(scaleY);

        float scale = (deltaScaleY > deltaScaleX) ? Math.abs(scaleY) : Math.abs(scaleX);

        if (deltaScaleY > deltaScaleX) {
            scaleY = (scaleY < 0) ? -scale : scale;
            scaleX = Geom.getSign(scaleX) * Math.abs((oldScaleX / oldScaleY) * scaleY);
        } else {
            scaleX = (scaleX < 0) ? -scale : scale;
            scaleY = Geom.getSign(scaleY) * Math.abs((oldScaleY / oldScaleX) * scaleX);
        }

        // store old values
        float oldWidth = curWidth;
        float oldHeight = curHeight;
        curWidth = origWidth * scaleX;
        curHeight = origHeight * scaleY;

        if (minSize) {
            final float MIN_SIZE = 20F;
            float minSizePx = Utils.convertDpToPixel(MIN_SIZE, context);
            if (Math.abs(curWidth) < minSizePx && Math.abs(curHeight) < minSizePx) {
                curWidth = oldWidth;
                curHeight = oldHeight;
                scaleX = oldScaleX;
                scaleY = oldScaleY;
            }
        }
    }

    private boolean isSmallerThanThreshold(float dx, float dy) {
        // check for thresholds
        final float MOVE_MIN_THRESHOLD = 2F;
        float thresholdMinPx = Utils.convertDpToPixel(MOVE_MIN_THRESHOLD, context);
        return Math.abs(dx) < thresholdMinPx && Math.abs(dy) < thresholdMinPx;
    }

    public void cleanBitmaps() {
        BitmapManager.recycle(bitmap);
        bitmap = null;

        BitmapManager.recycle(handleCorner);
        handleCorner = null;

        BitmapManager.recycle(handleRotate);
        handleRotate = null;

        BitmapManager.recycle(handleSide);
        handleSide = null;

        BitmapManager.recycle(bitmapMask);
        bitmapMask = null;
    }

    @Override
    public int getType() {
        return Item.CLIPART;
    }

    @Override
    public boolean isInItem(float x, float y) {
        return Graphics.getIsInRect(onImageRect, x, y, -rotateDegree);
    }

    @Override
    public void clearData() {
        cleanBitmaps();
    }

    @Override
    public float getX() {
        return centerX - curWidth * 0.5f;
    }

    @Override
    public float getY() {
        return centerY - curHeight * 0.5f;
    }

    public void setX(float x) {
        centerX = x + curWidth * 0.5f;
        // update crect
        refreshClipartProperties();
    }

    public void setY(float y) {
        centerY = y + curHeight * 0.5f;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    @Override
    public float getScaleX() {
        return scaleX;
    }

    @Override
    public float getScaleY() {
        return scaleY;
    }

    @Override
    public float getWidth() {
        return curWidth;
    }

    @Override
    public float getHeight() {
        return curHeight;
    }

    @Override
    public void activate(float left, float top, float imageCurrentZoom) {
        float scale = imageZoom / imageCurrentZoom;

        left = imageLeft / scale - left;
        top = imageTop / scale - top;

        float xScaled = centerX - curWidth * 0.5f;
        float yScaled = centerY - curHeight * 0.5f;
        xScaled = xScaled / scale - left;
        yScaled = yScaled / scale - top;

        curWidth = curWidth / scale;
        curHeight = curHeight / scale;

        centerX = xScaled + curWidth * 0.5f;
        centerY = yScaled + curHeight * 0.5f;

        scaleX /= scale;
        scaleY /= scale;

        if (isDrawingModeOrZoomInDrawing()) {
            updateUndoButtonState();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(clipartType);
        dest.writeInt(clipartResId);
        dest.writeInt(clipartFillColorType);
        dest.writeInt(color);
        dest.writeString(filePath);
        dest.writeMap(bufferData);
        dest.writeInt(resId);
        dest.writeInt(clipartFillColorType);
        dest.writeInt(fromPicsinFile ? 1 : 0);
        dest.writeString(picsinFile);
        dest.writeString(imageSource);

        dest.writeFloat(centerX);
        dest.writeFloat(centerY);

        dest.writeFloat(curWidth);
        dest.writeFloat(curHeight);

        dest.writeFloat(scaleX);
        dest.writeFloat(scaleY);

        dest.writeInt(opacity);

        dest.writeFloat(rotateDegree);

        dest.writeInt(addPhotoMaxSize);

        // TODO:
        // clear/draw props

        // end of clear/draw props
    }

    private void readFromParcel(Parcel in) {
        this.clipartType = in.readInt();
        this.clipartResId = in.readInt();
        this.clipartFillColorType = in.readInt();
        this.color = in.readInt();
        this.filePath = in.readString();
        this.bufferData = in.readHashMap(getClass().getClassLoader());
        this.resId = in.readInt();
        this.clipartFillColorType = in.readInt();
        this.fromPicsinFile = in.readInt() == 1;
        this.picsinFile = in.readString();
        this.imageSource = in.readString();

        this.centerX = in.readFloat();
        this.centerY = in.readFloat();

        this.curWidth = in.readFloat();
        this.curHeight = in.readFloat();

        this.scaleX = in.readFloat();
        this.scaleY = in.readFloat();

        this.opacity = in.readInt();

        this.rotateDegree = in.readFloat();

        this.addPhotoMaxSize = in.readInt();

        // clear/draw props

        // end of clear/draw props
    }

    public static final Parcelable.Creator<ClipArt> CREATOR = new Parcelable.Creator<ClipArt>() {
        public ClipArt createFromParcel(Parcel in) {
            return new ClipArt(in);
        }

        public ClipArt[] newArray(int size) {
            return new ClipArt[size];
        }
    };

    @Override
    public void initSpecStateObjects(Context context) {
        this.context = context;
        initHandles(context);
    }

    // // *** GESTURE DETECTOR *** \\\\

    private float startRotateDegree1;
    private float pinchStartDistance;
    private PointF moveStartMidPoint;
    private boolean inPinchMode = false;

    public boolean pinchOutOfBounds = false;

    @Override
    public void onPanStart(PointF p) {
    }

    @Override
    public void onPan(PointF p) {
    }

    @Override
    public void onPanEnd(PointF p) {
    }

    private float startScaleXPinch;
    private float startScaleYPinch;

    @Override
    public void onPinchStart(PointF p1, PointF p2) {
        if (crectTrimmed == null ||
                (!Graphics.getIsInRect(crectTrimmed, p1.x, p1.y, -rotateDegree) && !Graphics.getIsInRect(crectTrimmed, p2.x, p2.y, -rotateDegree))) {
            pinchOutOfBounds = true;
            return;
        }
        if (drawPathCurrent != null) return;

        pinchOutOfBounds = false;

        if (!isActive() || !(isDrawHandle() || isDrawingMode())) return;

        inPinchMode = true;

        if (isDrawingModeOrZoomInDrawing()) return;

        // rotate
        pinchStartPoint1 = new PointF(p1.x, p1.y);
        pinchStartPoint2 = new PointF(p2.x, p2.y);
        startRotateDegree1 = rotateDegree;
        preDegree = rotateDegree;

        // scale
        pinchStartDistance = Geom.dist(p1, p2);
        startScaleXPinch = scaleX;
        startScaleYPinch = scaleY;

        // move
        PointF p = new PointF();
        GraphicUtils.getMidPoint(p1, p2, p);
        moveStartMidPoint = p;

        touchDownHandled = false;
    }

    @Override
    public void onPinch(PointF p1, PointF p2) {
        if (pinchOutOfBounds) return;

        if (pinchStartPoint1 == null || pinchStartPoint2 == null || p1 == null || p2 == null)
            return;

        if (drawPathCurrent != null) return;

        if (isDrawingModeOrZoomInDrawing()) return;

        if (!isActive() || !(isDrawHandle() || isDrawingMode())) return;

        // rotate
        rotateDegree = startRotateDegree1 + GraphicUtils.getAngleBetweenLines(pinchStartPoint1, pinchStartPoint2, p1, p2);

        // scale
        float dist = Geom.dist(p1, p2);
        if (dist != 0 && pinchStartDistance != 0) {
            float oldScaleX = scaleX;
            float oldScaleY = scaleY;
            float oldWidth = curWidth;
            float oldHeight = curHeight;

            float pinchRatio = dist / pinchStartDistance;
            scaleX = startScaleXPinch * pinchRatio;
            scaleY = startScaleYPinch * pinchRatio;
            curWidth = origWidth * scaleX;
            curHeight = origHeight * scaleY;

            final float MIN_SIZE = 20F;
            float minSizePx = Utils.convertDpToPixel(MIN_SIZE, context);
            if (Math.abs(curWidth) < minSizePx && Math.abs(curHeight) < minSizePx) {
                curWidth = oldWidth;
                curHeight = oldHeight;
                scaleX = oldScaleX;
                scaleY = oldScaleY;
                return;
            }
        }

        // move
        PointF midPoint = new PointF();
        GraphicUtils.getMidPoint(p1, p2, midPoint);
        if (moveStartMidPoint != null) {
            float dx = midPoint.x - moveStartMidPoint.x;
            float dy = midPoint.y - moveStartMidPoint.y;
            centerX += dx;
            centerY += dy;
        }
        moveStartMidPoint = midPoint;

        if (view != null) view.invalidate();
    }

    @Override
    public void onPinchEnd(PointF p1, PointF p2) {
        if (drawPathCurrent != null) return;

        pinchOutOfBounds = false;
        inPinchMode = false;

        moveStartMidPoint = null;
        startRotateDegree = rotateDegree;
        // startRotateDegree1 = 0;

        if (isClearMaskAvailable()) {
            updateBitmapMask();
        }
    }

    @Override
    public void onTap(PointF p) {
    }

    @Override
    public void onLongPress(PointF p) {
    }

    // // end of GestureDetector

    public void setDrawingMode(boolean isChecked) {
        this.drawingMode = isChecked;
        if (drawingMode) {
            setBrushSize(brushSizeDip);
            setBrushHardness(100f - brushHardness);

            if (!isClearMaskAvailable()) {
                // create mask
                updateBitmapMask();
            }
            firstMoveEvent = true;
        } else {
            updateBitmapMask();
            drawPathCurrent = null;
            if (drawPaths.isEmpty()) {
                // we assume that drawPaths are in sync with mask
                BitmapManager.recycle(bitmapMask);
                bitmapMask = null;
            }
        }
    }

    public boolean isDrawingMode() {
        return drawingMode;
    }

    public boolean isDrawingModeOrZoomInDrawing() {
        return false;
    }

    public void setClearMode(boolean isChecked) {
        this.clearMode = isChecked;
    }

    public boolean isClearMode() {
        return clearMode;
    }

    public void setBrushSize(int brushSizeDip) {
        this.brushSizeDip = brushSizeDip;
        this.brushSize = Utils.convertDpToPixel(brushSizeDip, context);

        maskPathPaint.setStrokeWidth(brushSize);

        maskPathPaint.setMaskFilter(buildBlurMaskFilter(brushHardness));
    }

    public int getBrushSizeDip() {
        return brushSizeDip;
    }

    public Path getDrawPathCurrent() {
        return drawPathCurrent;
    }

    public boolean isInvalidateWithRect() {
        return isDrawingMode() && drawPathCurrent != null && currentAction == Graphics.ACTION_DRAG && !inPinchMode && !updateRect.isEmpty();
    }

    // UNDO

    private int clearDrawHighestUnchangeableIndex = -1;
    private int clearDrawLastSessionStartIndex = -1;

    public void startClearDrawSession() {
        clearDrawLastSessionStartIndex = drawPaths.size() - 1;
        clearDrawHighestUnchangeableIndex = clearDrawLastSessionStartIndex;
    }

    public void undoClearDrawLastStep() {
        if ((drawPaths.size() - 1) <= clearDrawHighestUnchangeableIndex) {
            updateUndoButtonState();
            return;
        }

        drawPaths.remove(drawPaths.size() - 1);
        updateBitmapMask();

        updateUndoButtonState();
    }

    public void cancelLastClearDrawSession() {
        if (drawPaths.isEmpty()) return;
        if ((drawPaths.size() - 1) <= clearDrawLastSessionStartIndex) return;

        for (int i = drawPaths.size() - 1; i > clearDrawLastSessionStartIndex; i--) {
            drawPaths.remove(i);
        }
        updateBitmapMask();

        clearDrawHighestUnchangeableIndex = clearDrawLastSessionStartIndex;

        updateUndoButtonState();
    }

    public void doneLastClearDrawSession() {
        clearDrawHighestUnchangeableIndex = drawPaths.size() - 1;

        trimmedAfterClear = trimAfterClear();
        // crect must be trimmed
        refreshClipartProperties();

    }

    private float[] out_rect_fld = new float[4];

    private boolean trimAfterClear() {
        Bitmap tmpBitmap = BitmapManager.createBitmap(Math.round(origWidth), Math.round(origHeight), Config.ARGB_8888);

        if (tmpBitmap == null) {
            return false;
        }

        int opacitySaved = opacity;
        setOpacity(255);
        Canvas maskCanvas = new Canvas(tmpBitmap);
        if (clipartType == SVG) {
        } else {
            if (clipartType == BITMAP && bitmap != null && !bitmap.isRecycled()) {
                maskCanvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
            } else {
                return false;
            }
        }
        maskCanvas.drawBitmap(bitmapMask, clearBitmaskSrcRect, clearBitmaskDstRectF, dstOutPaint);
        setOpacity(opacitySaved);

        // trim code
//		ByteBuffer buffer8 = SelectionActivity.allocNativeBuffer(4 * tmpBitmap.getWidth() * tmpBitmap.getHeight());
//		int[] out_rect = new int[4];
//		buffer8.position(0);
//		tmpBitmap.copyPixelsToBuffer(buffer8);
//		buffer8.position(0);
//		ImageOpCommon.getCropRect(buffer8, tmpBitmap.getWidth(), tmpBitmap.getHeight(), out_rect);
//
//		ImageOpCommon.freeNativeBuffer(buffer8);

        BitmapManager.recycle(tmpBitmap);

//		int leftOffset = out_rect[0];
//		int topOffset = out_rect[1];
//		int rightOffset = out_rect[2];
//		int bottomOffset = out_rect[3];
//
        // illogical result rect
//		if (leftOffset >= rightOffset || topOffset >= bottomOffset) {
//			return false;
//		}
//
//		int cropedWidth = rightOffset - leftOffset;
//		int cropedHeight = bottomOffset - topOffset;
//
//		 illogical result rect
//		if (cropedWidth > origWidth || cropedHeight > origHeight) {
//			return false;
//		}
//
//		 probably not trimmed or insignificant
//		final int minChange = 3;
//		if (Math.abs(origWidth - cropedWidth) < minChange && Math.abs(origHeight - cropedHeight) < minChange) {
//			return false;
//		}
//		 restore minor trims
//		int rightBottomShift = 0;
//		if (clipartType == SVG) {
//			rightBottomShift = 1;
//		}
//		 width
//		if (Math.abs(origWidth - cropedWidth) < minChange) {
//			out_rect_fld[0] = 0;
//			out_rect_fld[2] = origWidth;
//		} else {
//			out_rect_fld[0] = out_rect[0];
//			out_rect_fld[2] = out_rect[2] + rightBottomShift;
//		}
//		 height
//		if (Math.abs(origHeight - cropedHeight) < minChange) {
//			out_rect_fld[1] = 0;
//			out_rect_fld[3] = origHeight;
//		} else {
//			out_rect_fld[1] = out_rect[1];
//			out_rect_fld[3] = out_rect[3] + rightBottomShift;
//		}

        return true;
    }

    private void updateUndoButtonState() {
    }

    public boolean isStepsToUndo() {
        return (drawPaths.size() - 1) > clearDrawHighestUnchangeableIndex;
    }

    // end of UNDO

    public void setBrushHardness(float hardness) {
        brushHardness = 100f - hardness;
        maskPathPaint.setMaskFilter(buildBlurMaskFilter(brushHardness));

        maskPathPaint.setStrokeWidth(brushSize);
    }

    public float getBrushHardness() {
        return (100f - brushHardness);
    }

    public int getBrushStrength() {
        return 255;
    }

    public void setBrushDefaults() {
        brushHardness = DEFAULT_BRUSH_HARDNESS;
        brushSizeDip = DEFAULT_BRUSH_SIZE_DIP;
    }

    public void setInPinchMode(boolean inPinchMode) {
        this.inPinchMode = inPinchMode;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getClipartType() {
        return clipartType;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

}