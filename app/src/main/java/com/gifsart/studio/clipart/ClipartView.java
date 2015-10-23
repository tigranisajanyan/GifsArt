package com.gifsart.studio.clipart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import com.gifsart.studio.R;
import com.gifsart.studio.clipart.util.BitmapManager;
import com.gifsart.studio.clipart.util.Geom;
import com.gifsart.studio.clipart.util.GestureDetector;
import com.gifsart.studio.clipart.util.Graphics;
import com.gifsart.studio.clipart.view.AbstractItem;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ani on 7/24/15.
 */
public class ClipartView extends AbstractItem implements GestureDetector.GestureListener {

    private final String TAG = ClipartView.class.getSimpleName();

    private Rect onImageRect = new Rect();
    private int degree = 0;
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

    private int clipartResId = 0;

    private Bitmap handleCorner = null;
    private Bitmap handleRotate = null;
    private Bitmap handleSide = null;
    private boolean showRotateHandle = true;

    private Integer curZoomType = null;
    private int currentAction = Graphics.ACTION_DRAG;

    private View view = null;

    private Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    private Context context = null;

    private GestureDetector gestureDetector;

    private PointF pinchStartPoint1;
    private PointF pinchStartPoint2;

    private static final PorterDuffXfermode DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    private boolean clearMode = true;
    private Rect crectTrimmed = new Rect();

    private Paint dstOutPaint;
    private Paint maskPathPaint;

    private float brushSize = -1f;
    private float brushHardness = 50f;
    private List<DrawPath> drawPaths = new ArrayList<>();
    private Path drawPathCurrent = null;

    private Path updatePath = new Path();
    private RectF updateRectF = new RectF();
    private Rect updateRect = new Rect();

    private float lastXClipart, lastYClipart;

    private RectF clearBitmaskDstRectF = new RectF();

    private boolean touchDownHandled = false;
    private boolean notifyUndoEnabler = false;
    private boolean firstMoveEvent = true;
    float lastTouchX = -1f;
    float lastTouchY = -1f;

    // // *** GESTURE DETECTOR *** \\\\
    private float startRotateDegree1;
    private float pinchStartDistance;
    private PointF moveStartMidPoint;
    private boolean inPinchMode = false;
    public boolean pinchOutOfBounds = false;


    public ClipartView(Context context, int clipartResId, View view, int degree) {

        this.view = view;
        this.degree = degree;
        this.context = context;
        this.resId = clipartResId;
        this.clipartResId = clipartResId;

        isDrawHandle = true;
        initHandles(context);

        initBitmap(context, clipartResId);
        initPaintObjs();

        clearBitmaskDstRectF.set(0, 0, origWidth, origHeight);
        gestureDetector = new GestureDetector(this);
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


    private void initBitmap(Context context, int clipartResId) {
        String expMsg = "no exceptions";
        if (clipartResId != -1 && context != null) {
            bitmap = BitmapManager.decodeResource(context.getResources(), clipartResId);
        }

        if (bitmap == null && context instanceof Activity) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT);
            throw new IllegalArgumentException("bitmap resource is invalid. Params (initBitmapProperties): expMsg=" + expMsg + " clipartResId=" + clipartResId);
        }

        if (bitmap != null) {
            origWidth = bitmap.getWidth();
            origHeight = bitmap.getHeight();

            origRatio = origWidth / origHeight;

            Log.d("bitmapSize", bitmap.getWidth() + ", " + bitmap.getHeight() + ",   " + bitmap.getRowBytes() * bitmap.getHeight() / 1024f / 1024f);
        }
    }

    private void initPaintObjs() {
        handleRectPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        handleRectPaint1.setColor(Color.WHITE);
        handleRectPaint1.setStyle(Paint.Style.STROKE);
        handleRectPaint1.setStrokeWidth(1f);

        handleRectPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        handleRectPaint2.setColor(0x99000000);
        handleRectPaint2.setStyle(Paint.Style.STROKE);
        handleRectPaint2.setStrokeWidth(1f);

        dstOutPaint = new Paint();
        dstOutPaint.setXfermode(DST_OUT);

        // draw path over trans bitmap
        maskPathPaint = new Paint();
        initDrawingPaint(maskPathPaint);
        maskPathPaint.setColor(Color.WHITE);
    }

    public void initDrawingPaint(Paint paint) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setFilterBitmap(true);
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
    }


    public void draw(Canvas c) {
        if (isActive) {
            float xUnscaled = centerX - origWidth * 0.5f;
            float yUnscaled = centerY - origHeight * 0.5f;

            refreshClipartProperties();

            if (bitmap == null || bitmap.isRecycled()) {
                initBitmap(context, resId);
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
                }
                c.restore();
            }
            c.restore();
        }

        // test point coordinates
        if (isDrawHandle) {
            if (handleCorner == null || handleCorner.isRecycled() || handleRotate == null || handleRotate.isRecycled() || handleSide == null || handleSide.isRecycled()) {
                initHandles(context);
            }
            try {
                Graphics.showHandle(c, crectTrimmed, handleRectPaint1, handleRectPaint2, centerX, centerY, rotateDegree, handleCorner, handleRotate, handleSide, showRotateHandle, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        if (bitmap == null || bitmap.isRecycled()) {
            initBitmap(context, resId);
        }
        c.save();
        c.rotate(rotDegree, centerXImageRect, centerYImageRect);
        c.scale(sX, sY, centerXImageRect, centerYImageRect);
        c.translate(xUnscaled, yUnscaled);

        c.clipRect(0, 0, origWidth, origHeight);

        c.saveLayer(0, 0, origWidth, origHeight, null, Canvas.ALL_SAVE_FLAG);
        {
            reDrawBackgroundBitmap(c, sX, sY, rotDegree, centerXImageRect, centerYImageRect, xUnscaled, yUnscaled);

            if (bitmap != null && !bitmap.isRecycled()) {
                c.drawBitmap(bitmap, 0, 0, bitmapPaint);
            }
        }
        c.restore();
    }

    private void reDrawBackgroundBitmap(Canvas c, float xUnscaled, float yUnscaled) {
        c.translate(-xUnscaled, -yUnscaled);
        c.scale(1 / scaleX, 1 / scaleY, centerX, centerY);
        c.rotate(-rotateDegree, centerX, centerY);
        MainView mainView = (MainView) view;
        c.drawBitmap(mainView.getOrigBitmap(), null, mainView.getOnDrawRect(), null);
        c.rotate(rotateDegree, centerX, centerY);
        c.scale(scaleX, scaleY, centerX, centerY);
        c.translate(xUnscaled, yUnscaled);
    }

    private void reDrawBackgroundBitmap(Canvas canvas, float sX, float sY, float rotDegree, float centerXImageRect, float centerYImageRect, float xUnscaled, float yUnscaled) {
        // un-transform
        canvas.translate(-xUnscaled, -yUnscaled);
        canvas.scale(1 / sX, 1 / sY, centerXImageRect, centerYImageRect);
        canvas.rotate(-rotDegree, centerXImageRect, centerYImageRect);

        MainView mainView = (MainView) view;
        canvas.drawBitmap(mainView.getOrigBitmap(), 0f, 0f, null);
        // re-transform
        canvas.rotate(rotDegree, centerXImageRect, centerYImageRect);
        canvas.scale(sX, sY, centerXImageRect, centerYImageRect);
        canvas.translate(xUnscaled, yUnscaled);
    }


    private float calculateScaledCoordinate(float coord, float centerCoord, float scale) {
        return (coord - centerCoord) * scale + centerCoord;
    }

    private float calculateUnScaledCoordinate(float coord, float centerCoord, float scale) {
        return (coord - centerCoord) / scale + centerCoord;
    }

    /////////////////////////
    //touch handles

    private boolean handleTouchDown(float x, float y) {
        lastTouchX = x;
        lastTouchY = y;
        currentAction = Graphics.ACTION_DRAG;

        initHandles(context);

        if (!Graphics.getIsInRect(crectTrimmed, x, y, -rotateDegree)) {
            curZoomType = Graphics.checkVerticesAndInitZoomType(crectTrimmed, x, y, -rotateDegree, handleCorner, true, true);
            boolean isInVertices = true;
            if (curZoomType == null) {
                isInVertices = false;
            }

            // is in vertices
            if (isInVertices) {
                isDrawHandle = true;
                currentAction = Graphics.ACTION_ZOOM;
            }

            // is in rotate
            boolean isInRotate = Graphics.checkIsInRotate(crectTrimmed, x, y, -rotateDegree, handleCorner, handleRotate);
            if (isInRotate) {
                isDrawHandle = true;
                currentAction = Graphics.ACTION_ROTATE;
            }

            // out of handle scope
            if (!isInRotate && !isInVertices) {
                isDrawHandle = false;
            }

            if (currentAction == Graphics.ACTION_DRAG) {
                moveStartMidPoint = null;
                return false;
            }
        }

        if (currentAction == Graphics.ACTION_DRAG) {
            isDrawHandle = true;
        }

        if (currentAction == Graphics.ACTION_ROTATE) {
            preDegree = (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
        }

        return true;
    }

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
                case Graphics.ZOOM_RIGHT_BOTTOM:
                    zoomProportional((x - crectTrimmed.right), (y - crectTrimmed.bottom), false);
                    break;
                case Graphics.ZOOM_TOP:
                    zoomFree(0f, -(y - crectTrimmed.top));
                    break;
                case Graphics.ZOOM_RIGHT:
                    zoomFree(x - crectTrimmed.right, 0f);
                    break;
                case Graphics.ZOOM_BOTTOM:
                    zoomFree(0f, y - crectTrimmed.bottom);
                    break;
                case Graphics.ZOOM_LEFT:
                    zoomFree(-(x - crectTrimmed.left), 0f);
                    break;
            }
        }

        if (currentAction == Graphics.ACTION_DRAG) {
            centerX += dx;
            centerY += dy;


        }

        if (currentAction == Graphics.ACTION_ROTATE) {
            showRotateHandle = false;
            rotate(x, y);
        }

        lastTouchX = x;
        lastTouchY = y;
    }


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
        if (currentAction == Graphics.ACTION_ROTATE) {
            startRotateDegree = rotateDegree;
            showRotateHandle = true;
        }
        drawPathCurrent = null;
        firstMoveEvent = true;
        updateRect.setEmpty();
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
    public int getType() {
        return 0;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
        bitmapPaint.setAlpha(opacity);
    }

    @Override
    public boolean isInItem(float x, float y) {
        return false;
    }

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

        crectTrimmed.set(crectOrig);
    }

    public void initSizeParams(int viewWidth, int viewHeight) {
        final float minInitSize = Utils.convertDpToPixel(32, context);
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
        //if (centerX == -1f || centerY == -1f) {
        centerX = viewWidth * 0.5f;
        centerY = viewHeight * 0.5f;
        //}
    }

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
        if (drawPathCurrent != null) {
            return;
        }

        pinchOutOfBounds = false;
        if (!isActive() || !isDrawHandle()) {
            return;
        }
        inPinchMode = true;

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
        Utils.getMidPoint(p1, p2, p);
        moveStartMidPoint = p;

        touchDownHandled = false;
    }

    @Override
    public void onPinch(PointF p1, PointF p2) {
        if (pinchOutOfBounds) return;

        if (pinchStartPoint1 == null || pinchStartPoint2 == null || p1 == null || p2 == null) {
            return;
        }

        if (drawPathCurrent != null || !isActive() || !isDrawHandle()) {
            return;
        }

        // rotate
        rotateDegree = startRotateDegree1 + Utils.getAngleBetweenLines(pinchStartPoint1, pinchStartPoint2, p1, p2);

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
        Utils.getMidPoint(p1, p2, midPoint);
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
        if (drawPathCurrent != null) {
            return;
        }
        pinchOutOfBounds = false;
        inPinchMode = false;

        moveStartMidPoint = null;
        startRotateDegree = rotateDegree;
    }


    public float getClipartUnscaledX() {
        return centerX - origWidth * 0.5f * scaleX;
    }

    public float getClipartUnscaledY() {
        return centerY - origHeight * 0.5f * scaleY;
    }

    @Override
    public void onTap(PointF p) {

    }

    @Override
    public void onLongPress(PointF p) {

    }

    public boolean touch_down(float x, float y) {
        if (inPinchMode) {
            return false;
        }
        boolean touchDownResult = handleTouchDown(x, y);
        touchDownHandled = true;
        return touchDownResult;
    }

    public void touch_move(float x, float y) {
        if (inPinchMode || !touchDownHandled) {
            return;
        }
        handleTouchMove(x, y);
    }

    public void touch_up() {
        if (inPinchMode) {
            return;
        }
        handleTouchUp();
        touchDownHandled = false;
    }

    public void onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Rect getOnImageRect() {
        return onImageRect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

}
