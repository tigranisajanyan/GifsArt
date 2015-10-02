package com.gifsart.studio.clipart.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;


public class Graphics {

    public static final int ZOOM_LEFT_TOP = 0;
    public static final int ZOOM_RIGHT_TOP = 1;
    public static final int ZOOM_LEFT_BOTTOM = 2;
    public static final int ZOOM_RIGHT_BOTTOM = 3;
    public static final int ZOOM_TOP = 4;
    public static final int ZOOM_RIGHT = 5;
    public static final int ZOOM_BOTTOM = 6;
    public static final int ZOOM_LEFT = 7;

    public static final int ACTION_DRAG = 1;
    public static final int ACTION_ZOOM = 2;
    public static final int ACTION_ROTATE = 3;

    public static final int ROTATE_UNDO = 1;
    public static final int ROTATE_REDO = 2;

    public static final int FLIP_HORIZONTAL = 1;
    public static final int FLIP_VERTICAL = 2;

    // //////////////////////////GENERAL////////////

    public static void rotatePoint(float point[], float centerX, float centerY, float angle) {
        float x = point[0];
        float y = point[1];

        double radians = Math.toRadians(angle);

        // rotate x,y coordinate to find position in original rect
        point[0] = (float) ((x - centerX) * Math.cos(radians) - (y - centerY) * Math.sin(radians) + centerX);
        point[1] = (float) ((x - centerX) * Math.sin(radians) + (y - centerY) * Math.cos(radians) + centerY);
    }

    public static float[] getRotatePoint(float point[], float centerX, float centerY, float angle) {
        float[] rotatedPoint = point.clone();
        rotatePoint(rotatedPoint, centerX, centerY, angle);
        return rotatedPoint;
    }

    // is in text,clipart
    public static boolean getIsInRect(Rect rect, float x, float y, float degree) {

        // rotate x,y coordinate to find position in original rect
        float[] point = {x, y};
        float[] rotatedPoint = getRotatePoint(point, rect.exactCenterX(), rect.exactCenterY(), degree);
        x = rotatedPoint[0];
        y = rotatedPoint[1];
        // //

        int minX = rect.left;
        int minY = rect.top;

        int maxX = rect.right;
        int maxY = rect.bottom;

        if (rect.left > rect.right) {
            minX = rect.right;
            maxX = rect.left;
        }

        if (rect.top > rect.bottom) {
            minY = rect.bottom;
            maxY = rect.top;
        }

        if (x > maxX || y > maxY || x < minX || y < minY) {
            return false;
        }

        return true;

    }

    public static boolean checkIsInRotate(Rect rect, float x, float y, float degree, Bitmap handle, Bitmap handleRotate) {
        // rotate x,y coordinate to find position in original rect
        float[] point = {x, y};
        float[] rotatedPoint = getRotatePoint(point, rect.exactCenterX(), rect.exactCenterY(), degree);
        x = rotatedPoint[0];
        y = rotatedPoint[1];
        // //

        int rotateHandleX = rect.right;
        int rotateHandleY = rect.top;

        if (rect.left > rect.right) {
            rotateHandleX = rect.left;
        }

        if (rect.top > rect.bottom) {
            rotateHandleY = rect.bottom;
        }

        // init rotate handle
        rotateHandleX = rotateHandleX - handle.getWidth() / 2;
        rotateHandleY = rotateHandleY - handle.getHeight() - handleRotate.getHeight() / 2;

        if (x >= rotateHandleX && x < (rotateHandleX + handleRotate.getWidth()) && y >= rotateHandleY && y < (rotateHandleY + handleRotate.getHeight())) {
            return true;
        }

        return false;
    }

    public static Integer checkVerticesAndInitZoomType(Rect rect, float x, float y, float degree, Bitmap handle, boolean checkSides, boolean checkOnlyRightBottom) {

        // rotate x,y coordinate to find position in original rect
        float[] point = {x, y};
        float[] rotatedPoint = getRotatePoint(point, rect.exactCenterX(), rect.exactCenterY(), degree);
        x = rotatedPoint[0];
        y = rotatedPoint[1];
        // //

        int w = handle.getWidth();
        int h = handle.getHeight();

        int left = rect.left - w / 2;
        int right = rect.right - w / 2;
        int top = rect.top - h / 2;
        int bottom = rect.bottom - h / 2;

        if (!checkOnlyRightBottom) {
            if (x >= left && x <= (w + left) && y >= top && y <= (h + top)) {
                return ZOOM_LEFT_TOP;
            }

            if (x >= right && x <= (w + right) && y >= top && y <= (h + top)) {
                return ZOOM_RIGHT_TOP;
            }

            if (x >= left && x <= (w + left) && y >= bottom && y <= (h + bottom)) {
                return ZOOM_LEFT_BOTTOM;
            }
        }

        if (x >= right && x <= (w + right) && y >= bottom && y <= (h + bottom)) {
            return ZOOM_RIGHT_BOTTOM;
        }

        if (checkSides) {
            if (x >= (left + rect.width() / 2) && x <= (w + left + rect.width() / 2) && y >= top && y <= (h + top)) {
                return ZOOM_TOP;
            }

            if (x >= right && x <= (w + right) && y >= (top + rect.height() / 2) && y <= (h + top + rect.height() / 2)) {
                return ZOOM_RIGHT;
            }

            if (x >= (right - rect.width() / 2) && x <= (w + right - rect.width() / 2) && y >= bottom && y <= (h + bottom)) {
                return ZOOM_BOTTOM;
            }

            if (x >= left && x <= (w + left) && y >= (bottom - rect.height() / 2) && y <= (h + bottom - rect.height() / 2)) {
                return ZOOM_LEFT;
            }
        }

        return null;
    }

    private static final Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    public static void showHandle(Canvas canvas, Rect rect, Paint rectPaint1, Paint rectPaint2, float centerX, float centerY, float degree, Bitmap handleCorner, Bitmap handleRotate, Bitmap handleSide, boolean showRotateHandle, boolean showOnlyRightBottomHandle) {
        canvas.save();
        canvas.rotate(degree, centerX, centerY);

        canvas.drawRect(rect, rectPaint1);
        rect.inset(1, 1);
        canvas.drawRect(rect, rectPaint2);
        rect.inset(-1, -1);

        // init resize handle
        int w = handleCorner.getWidth();
        int h = handleCorner.getHeight();

        int left = rect.left - w / 2;
        int right = rect.right - w / 2;
        int top = rect.top - h / 2;
        int bottom = rect.bottom - h / 2;

        if (!showOnlyRightBottomHandle) {
            canvas.drawBitmap(handleCorner, left, top, bitmapPaint);
            canvas.drawBitmap(handleCorner, right, top, bitmapPaint);
            canvas.drawBitmap(handleCorner, left, bottom, bitmapPaint);
        }
        canvas.drawBitmap(handleCorner, right, bottom, bitmapPaint);

        if (handleSide != null) {
            int w1 = handleSide.getWidth();
            int h1 = handleSide.getHeight();

            int left1 = rect.left - w1 / 2;
            int right1 = rect.right - w1 / 2;
            int top1 = rect.top - h1 / 2;
            int bottom1 = rect.bottom - h1 / 2;

            canvas.drawBitmap(handleSide, left1 + rect.width() / 2, top1, bitmapPaint);
            canvas.drawBitmap(handleSide, right1, top1 + rect.height() / 2, bitmapPaint);
            canvas.drawBitmap(handleSide, left1 + rect.width() / 2, bottom1, bitmapPaint);
            canvas.drawBitmap(handleSide, left1, top1 + rect.height() / 2, bitmapPaint);
        }

        // init rotate handle
        if (showRotateHandle) {
            int rotateHandleX = rect.right;
            int rotateHandleY = rect.top;

            if (rect.left > rect.right) {
                rotateHandleX = rect.left;
            }

            if (rect.top > rect.bottom) {
                rotateHandleY = rect.bottom;
            }

            rotateHandleX = rotateHandleX - handleCorner.getWidth() / 2;
            rotateHandleY = rotateHandleY - handleCorner.getHeight() - handleRotate.getHeight() / 2;
            canvas.drawBitmap(handleRotate, rotateHandleX, rotateHandleY, bitmapPaint);
            canvas.restore();
        }
    }

    public static int getAngle(float x1, float y1, float x2, float y2) {
        float deltaY = y2 - y1;
        float deltaX = x2 - x1;
        return getAngle(deltaX, deltaY);
    }

    public static int getAngle(float deltaX, float deltaY) {
        return (int) Math.toDegrees((Math.atan2(deltaY, deltaX)));
    }

    public static float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static void showDrawableHandles(Canvas canvas, RectF rect, Paint rectPaint1, Paint rectPaint2, float centerX, float centerY, float degree, Bitmap handleCorner,
                                           Bitmap handleSide, boolean showOnlyRightBottomHandle,
                                           Rect handleCornerSrcRect, float handleCornerWidth, float handleCornerHeight,
                                           Rect handleSideSrcRect, float handleSideWidth, float handleSideHeight, RectF handleDrawRect) {

        canvas.save();

        canvas.rotate(degree, centerX, centerY);

        canvas.drawRect(rect, rectPaint1);
        rect.inset(1, 1);
        canvas.drawRect(rect, rectPaint2);
        rect.inset(-1, -1);

        // init resize handle
        float w = handleCornerWidth;
        float h = handleCornerHeight;

        float left = rect.left - w / 2;
        float right = rect.right - w / 2;
        float top = rect.top - h / 2;
        float bottom = rect.bottom - h / 2;

        if (!showOnlyRightBottomHandle) {

            handleDrawRect.set(left, top, left + w, top + h);
            canvas.drawBitmap(handleCorner, handleCornerSrcRect, handleDrawRect, bitmapPaint);
            handleDrawRect.set(right, top, right + w, top + h);
            canvas.drawBitmap(handleCorner, handleCornerSrcRect, handleDrawRect, bitmapPaint);
            handleDrawRect.set(left, bottom, left + w, bottom + h);
            canvas.drawBitmap(handleCorner, handleCornerSrcRect, handleDrawRect, bitmapPaint);

			/*
			canvas.drawBitmap(handleCorner, left, top, bitmapPaint);
			canvas.drawBitmap(handleCorner, right, top, bitmapPaint);
			canvas.drawBitmap(handleCorner, left, bottom, bitmapPaint);*/
        }

        handleDrawRect.set(right, bottom, right + w, bottom + h);
        canvas.drawBitmap(handleCorner, handleCornerSrcRect, handleDrawRect, bitmapPaint);
        //canvas.drawBitmap(handleCorner, right, bottom, bitmapPaint);

        if (handleSide != null) {
            float w1 = handleSideWidth;
            float h1 = handleSideHeight;

            float left1 = rect.left - w1 / 2;
            float right1 = rect.right - w1 / 2;
            float top1 = rect.top - h1 / 2;
            float bottom1 = rect.bottom - h1 / 2;

            handleDrawRect.set(left1 + rect.width() / 2, top1, left1 + rect.width() / 2 + w1, top1 + h1);
            canvas.drawBitmap(handleSide, handleSideSrcRect, handleDrawRect, bitmapPaint);
            handleDrawRect.set(right1, top1 + rect.height() / 2, right1 + w1, top1 + rect.height() / 2 + h1);
            canvas.drawBitmap(handleSide, handleSideSrcRect, handleDrawRect, bitmapPaint);
            handleDrawRect.set(left1 + rect.width() / 2, bottom1, left1 + rect.width() / 2 + w1, bottom1 + h1);
            canvas.drawBitmap(handleSide, handleSideSrcRect, handleDrawRect, bitmapPaint);
            handleDrawRect.set(left1, top1 + rect.height() / 2, left1 + w1, top1 + rect.height() / 2 + h1);
            canvas.drawBitmap(handleSide, handleSideSrcRect, handleDrawRect, bitmapPaint);
			/*
			canvas.drawBitmap(handleSide, left1 + rect.width() / 2, top1, bitmapPaint);
			canvas.drawBitmap(handleSide, right1, top1 + rect.height() / 2, bitmapPaint);
			canvas.drawBitmap(handleSide, left1 + rect.width() / 2, bottom1, bitmapPaint);
			canvas.drawBitmap(handleSide, left1, top1 + rect.height() / 2, bitmapPaint);*/
        }

        canvas.restore();

    }

    public static void showRotateDrawable(Canvas canvas, RectF mainRect, Bitmap handleRotate,
                                          float handleCornerWidth, float handleCornerHeight,
                                          Rect handleRotateSrcRect, float handleRotateWidth, float handleRotateHeight, RectF handleDrawRect) {
        float rotateHandleX = mainRect.right;
        float rotateHandleY = mainRect.top;

        if (mainRect.left > mainRect.right) {
            rotateHandleX = mainRect.left;
        }

        if (mainRect.top > mainRect.bottom) {
            rotateHandleY = mainRect.bottom;
        }

        rotateHandleX = rotateHandleX - handleCornerWidth / 2;
        rotateHandleY = rotateHandleY - handleCornerHeight - handleCornerHeight / 2;
        handleDrawRect.set(rotateHandleX, rotateHandleY, rotateHandleX + handleRotateWidth, rotateHandleY + handleRotateHeight);
        canvas.drawBitmap(handleRotate, handleRotateSrcRect, handleDrawRect, bitmapPaint);
        //canvas.drawBitmap(handleRotate, rotateHandleX, rotateHandleY, bitmapPaint);
    }

    public static boolean getIsInRect(RectF rect, float x, float y, float degree) {

        // rotate x,y coordinate to find position in original rect
        float[] point = {x, y};
        float[] rotatedPoint = getRotatePoint(point, rect.centerX(), rect.centerY(), degree);
        x = rotatedPoint[0];
        y = rotatedPoint[1];
        // //

        float minX = rect.left;
        float minY = rect.top;

        float maxX = rect.right;
        float maxY = rect.bottom;

        if (rect.left > rect.right) {
            minX = rect.right;
            maxX = rect.left;
        }

        if (rect.top > rect.bottom) {
            minY = rect.bottom;
            maxY = rect.top;
        }

        if (x > maxX || y > maxY || x < minX || y < minY) {
            return false;
        }

        return true;

    }

    public static Integer setupZoomType(RectF rect, float x, float y, float degree, boolean checkSides,
                                        boolean checkOnlyRightBottom, float handleCornerWidth, float handleCornerHeight,
                                        float handleSideWidth, float handleSideHeight) {

        // rotate x,y coordinate to find position in original rect
        float[] point = {x, y};
        float[] rotatedPoint = getRotatePoint(point, rect.centerX(), (int) rect.centerY(), degree);
        x = rotatedPoint[0];
        y = rotatedPoint[1];
        // //

        float w = handleCornerWidth;
        float h = handleCornerHeight;

        //int w = handle.getWidth();
        //int h = handle.getHeight();

        float left = rect.left - w / 2;
        float right = rect.right - w / 2;
        float top = rect.top - h / 2;
        float bottom = rect.bottom - h / 2;

        if (!checkOnlyRightBottom) {
            if (x >= left && x <= (w + left) && y >= top && y <= (h + top)) {
                return ZOOM_LEFT_TOP;
            }

            if (x >= right && x <= (w + right) && y >= top && y <= (h + top)) {
                return ZOOM_RIGHT_TOP;
            }

            if (x >= left && x <= (w + left) && y >= bottom && y <= (h + bottom)) {
                return ZOOM_LEFT_BOTTOM;
            }
        }

        if (x >= right && x <= (w + right) && y >= bottom && y <= (h + bottom)) {
            return ZOOM_RIGHT_BOTTOM;
        }

        if (checkSides) {

            w = handleSideWidth;
            h = handleSideHeight;
            left = rect.left - w / 2;
            right = rect.right - w / 2;
            top = rect.top - h / 2;
            bottom = rect.bottom - h / 2;


            if (x >= (left + rect.width() / 2) && x <= (w + left + rect.width() / 2) && y >= top && y <= (h + top)) {
                return ZOOM_TOP;
            }

            if (x >= right && x <= (w + right) && y >= (top + rect.height() / 2) && y <= (h + top + rect.height() / 2)) {
                return ZOOM_RIGHT;
            }

            if (x >= (right - rect.width() / 2) && x <= (w + right - rect.width() / 2) && y >= bottom && y <= (h + bottom)) {
                return ZOOM_BOTTOM;
            }

            if (x >= left && x <= (w + left) && y >= (bottom - rect.height() / 2) && y <= (h + bottom - rect.height() / 2)) {
                return ZOOM_LEFT;
            }
        }

        return null;
    }

    public static boolean checkIsInRotate(RectF rect, float x, float y, float degree,
                                          float handleWidth, float handleHeight, float handleRotateWidth, float handleRotateHeight) {
        // rotate x,y coordinate to find position in original rect
        float[] point = {x, y};
        float[] rotatedPoint = getRotatePoint(point, rect.centerX(), rect.centerY(), degree);
        x = rotatedPoint[0];
        y = rotatedPoint[1];
        // //

        float rotateHandleX = rect.right;
        float rotateHandleY = rect.top;

        if (rect.left > rect.right) {
            rotateHandleX = rect.left;
        }

        if (rect.top > rect.bottom) {
            rotateHandleY = rect.bottom;
        }

        // init rotate handle
        rotateHandleX = rotateHandleX - handleWidth / 2;
        rotateHandleY = rotateHandleY - handleHeight - handleRotateHeight / 2;

        if (x >= rotateHandleX && x < (rotateHandleX + handleRotateWidth) && y >= rotateHandleY && y < (rotateHandleY + handleRotateHeight)) {
            return true;
        }

        return false;
    }

    public static Point getScaledSize(float origWidth, float origHeight, float maxSize, boolean checkIsSmall) {

        if (!checkIsSmall || (origWidth > maxSize || origHeight > maxSize)) {
            float scale = (maxSize / origWidth < maxSize / origHeight) ? maxSize / origWidth : maxSize / origHeight;
            origWidth *= scale;
            origHeight *= scale;
        }

        return new Point((int) origWidth, (int) origHeight);
    }
}
