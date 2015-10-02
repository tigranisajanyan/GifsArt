package com.gifsart.studio.clipart.view;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.MotionEvent;


public class MultitouchHandler {
    private static MultitouchHandler instance;

    public static int MODE_NONE = 0;
    public static int MODE_ZOOM = MODE_NONE + 1;
    public static int MODE_DRAG = MODE_ZOOM + 1;
    public int mode;

    private RectF savedRect = new RectF();
    private RectF initRect = new RectF();
    private PointF mid = new PointF();
    private PointF oldMid = new PointF();
    private PointF dragPoint = new PointF();

    private float oldDist;

    private float maxZoom = 20f;
    private float minZoom = 0.2f;

    public static MultitouchHandler getInstance() {
        if (instance == null) instance = new MultitouchHandler();
        return instance;
    }

    private MultitouchHandler() {
    }

    public void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public float spacing(MotionEvent event) {
        if (event.getPointerCount() < 2)
            return -1;
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    public void transform(MotionEvent e, RectF rect, float newDist, int pointer) {
        if (newDist > 10 && mode == MODE_ZOOM) {
            if (e.getPointerCount() < 2)
                return;
            midPoint(mid, e);
            float scale = newDist / oldDist;
            float newWidth = savedRect.width() * scale;
            float scale2 = newWidth / rect.width();
            float dx = mid.x - oldMid.x;
            float dy = mid.y - oldMid.y;
            // if (dx >= 5 || dy >= 5) {
            rect.offset(dx, dy);
            oldMid.set(mid);
            // }
            if (scale2 != 1.0f
                    && minZoom * initRect.width() <= newWidth
                    && maxZoom * initRect.width() >= newWidth
                    && newDist > 10) {

                scaleRect(rect, scale2, mid);
            }
        } else if (mode == MODE_DRAG) {
            float dx = e.getX(pointer) - dragPoint.x;
            float dy = e.getY(pointer) - dragPoint.y;
            dragPoint.set(e.getX(pointer), e.getY(pointer));
            rect.offset(dx, dy);
        }
    }

    public boolean handlePitchZoom(MotionEvent e, final RectF rect, final RectF pInitRect, float maxZoom, float minZoom) {
        this.maxZoom = maxZoom;
        this.minZoom = minZoom;
        int val = e.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = (e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        switch (val) {
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(e);
//			L.d("oldDist=" + oldDist);
                if (oldDist > 10f) {
                    initRect.set(pInitRect);
                    savedRect.set(rect);
                    midPoint(mid, e);
                    oldMid.set(mid.x, mid.y);
                    mode = MODE_ZOOM;
//				L.d("mode=ZOOM");
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == MODE_ZOOM || mode == MODE_DRAG) {
                    transform(e, rect, spacing(e), pointerIndex);
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if (mode == MODE_DRAG || mode == MODE_ZOOM) {
                    if (e.getPointerCount() == 1) {
                        mode = MODE_NONE;
                    } else if (e.getPointerCount() == 2) {
                        mode = MODE_DRAG;
                        if (val == MotionEvent.ACTION_UP) {
                            dragPoint.set(e.getX(), e.getY());
                        } else { // MotionEvent.ACTION_POINTER_UP
                            pointerIndex = pointerIndex == 1 ? 0 : 1;
                            dragPoint.set(e.getX(pointerIndex), e.getY(pointerIndex));
                        }
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    public void scaleRect(RectF rect, float scale, PointF scalePoint) {
        float scaleX = 0;
        float scaleY = 0;
        float leftOld = rect.left;
        float topOld = rect.top;
        float oldWidth = rect.width();
        float oldHeight = rect.height();
        float newWidth = oldWidth * scale;
        float newHeight = oldHeight * scale;
        float scalePointX = leftOld;
        float scalePointY = topOld;
        if (rect.contains(scalePoint.x, scalePoint.y)) {
            float offsetX = scalePoint.x - leftOld;
            float offsetY = scalePoint.y - topOld;
            scaleX = offsetX / oldWidth;
            scaleY = offsetY / oldHeight;
            scalePointX = scalePoint.x;
            scalePointY = scalePoint.y;
        }

        rect.left = scalePointX - newWidth * scaleX;
        rect.top = scalePointY - newHeight * scaleY;
        rect.right = scalePointX + newWidth * (1 - scaleX);
        rect.bottom = scalePointY + newHeight * (1 - scaleY);

    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
