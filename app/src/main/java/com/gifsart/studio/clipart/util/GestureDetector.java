package com.gifsart.studio.clipart.util;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;

public class GestureDetector {
    private static final boolean LOG_ENABLED = false;
    private static final String LOG_TAG = "gestureDetector";

    private static final long DEFAULT_TAP_DELTA = 100;
    private static final long DEFAULT_LONG_PRESS_DELTA = 300;

    private static final float LONG_PRESS_MOVE_TOLLERANCE = 20f;

    private static final float TOUCH_TOLERANCE = 20f;

    private int primaryPointerId;
    private int secondaryPointerId;

    private GestureListener gestureListener;

    private long longPressTimeThreshold;
    private long tapTimeThreshold;

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public GestureDetector(GestureListener listener) {
        this.gestureListener = listener;

        longPressTimeThreshold = DEFAULT_LONG_PRESS_DELTA;
        tapTimeThreshold = DEFAULT_TAP_DELTA;
    }

    public void setLongPressThreshold(long time) {
        longPressTimeThreshold = time;
    }

    public void setTapThreshold(long time) {
        this.tapTimeThreshold = time;
    }

    /**
     * Sets the gesture listener. Can be null.
     */
    public void setGestureListener(GestureListener listener) {
        gestureListener = listener;
    }

    private boolean canTapEventOccur;
    private boolean canLongPressEventOccur;

    private boolean isPanning;

    private PointF tmpPoint1 = new PointF();
    private PointF tmpPoint2 = new PointF();

    private boolean panStarted;

    private PointF touchStartPoint = new PointF();

    private class LongPressChecker implements Runnable {

        public PointF point;

        @Override
        public void run() {
            if (canLongPressEventOccur) {
                gestureListener.onLongPress(point);
            }
        }
    }

    private final LongPressChecker longPressChecker = new LongPressChecker();

    /**
     * Call this in every onTouchEvent of the View.
     */
    public void onTouchEvent(MotionEvent event) {
        final int pointerCount = event.getPointerCount();
        final int actionIndex = event.getActionIndex();
        final int actionMasked = event.getActionMasked();

        if (LOG_ENABLED)
            Log.d(LOG_TAG, "pointerCount : " + pointerCount);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX(0);
                final float y = event.getY(0);

                touchStartPoint.set(x, y);

                primaryPointerId = event.getPointerId(0);

                canTapEventOccur = true;
                isPanning = false;
                canLongPressEventOccur = true;

                mainThreadHandler.removeCallbacks(longPressChecker);

                longPressChecker.point = new PointF(x, y);
                mainThreadHandler.postDelayed(longPressChecker, longPressTimeThreshold);

                panStarted = false;
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                canTapEventOccur = false;
                canLongPressEventOccur = false;
                if (pointerCount == 2) {
                    secondaryPointerId = event.getPointerId(1);
                    PointF primaryPoint = getPoint(event, primaryPointerId);
                    PointF secondaryPoint = getPoint(event, secondaryPointerId);
                    if (primaryPoint != null && secondaryPoint != null) {
                        gestureListener.onPanEnd(primaryPoint);
                        gestureListener.onPinchStart(primaryPoint, secondaryPoint);

                        isPanning = false;
                    }
                }

                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                if (pointerCount == 2) {
                    gestureListener.onPinchEnd(null, null);
                    if (actionIndex == 0) { // if the first pointer is released change the touchStartPoint to the second point
                        touchStartPoint.set(event.getX(1), event.getY(1));
                    } else {
                        touchStartPoint.set(event.getX(0), event.getY(0));
                    }
                } else if (pointerCount > 2) {
                    getValidPointers(event, pointerCount, actionIndex);
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1) {
                    final float eventX = event.getX();
                    final float eventY = event.getY();

                    if (Geom.dist(eventX, eventY, touchStartPoint.x, touchStartPoint.y) >= LONG_PRESS_MOVE_TOLLERANCE) {
                        canLongPressEventOccur = false;
                    }

                    if (event.getEventTime() - event.getDownTime() >= tapTimeThreshold) {
                        isPanning = true;
                    }

                    if (isPanning) {
                        tmpPoint1.set(eventX, eventY);
                        if (panStarted) {
                            gestureListener.onPan(tmpPoint1);
                        } else {
                            if (Geom.dist(tmpPoint1, touchStartPoint) >= TOUCH_TOLERANCE) {
                                panStarted = true;

                                gestureListener.onPanStart(touchStartPoint);
                                gestureListener.onPan(tmpPoint1);
                            }
                        }
                    }
                } else if (pointerCount > 1) {
                    boolean result1 = getPoint(event, primaryPointerId, tmpPoint1);
                    boolean result2 = getPoint(event, secondaryPointerId, tmpPoint2);
                    if (result1 && result2) {
                        gestureListener.onPinch(tmpPoint1, tmpPoint2);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                canLongPressEventOccur = false;
                if (canTapEventOccur && event.getEventTime() - event.getDownTime() <= tapTimeThreshold) {
                    gestureListener.onTap(new PointF(event.getX(), event.getY()));
                } else if (isPanning) {
                    gestureListener.onPanEnd(new PointF(event.getX(), event.getY()));
                    isPanning = false;
                }
                break;
        }
    }

    private void getValidPointers(MotionEvent event, int pointerCount, int invalidIndex) {
        if (event.getPointerCount() < 2) {
            primaryPointerId = event.getPointerId(0);
        } else {
            int primaryPointerIndex = -1;
            int secondaryPointerIndex = -1;

            for (int i = 0; i < pointerCount; ++i) {
                if (i != invalidIndex) {
                    if (primaryPointerIndex == -1) {
                        primaryPointerIndex = i;
                    } else if (secondaryPointerIndex == -1) {
                        secondaryPointerIndex = i;
                    } else
                        break;
                }
            }

            primaryPointerId = event.getPointerId(primaryPointerIndex);
            secondaryPointerId = event.getPointerId(secondaryPointerIndex);
        }
    }

    private PointF getPoint(MotionEvent event, int pointerId) {
        int pointerIndex = event.findPointerIndex(pointerId);
        if (pointerIndex >= 0 && pointerIndex < event.getPointerCount()) {
            return new PointF(event.getX(pointerIndex), event.getY(pointerIndex));
        }
        return null;
    }

    private boolean getPoint(MotionEvent event, int pointerId, PointF point) {
        int pointerIndex = event.findPointerIndex(pointerId);
        if (pointerIndex >= 0 && pointerIndex < event.getPointerCount()) {
            point.set(event.getX(pointerIndex), event.getY(pointerIndex));
            return true;
        }
        return false;
    }

    public interface GestureListener {
        void onPanStart(PointF p);

        void onPan(PointF p);

        void onPanEnd(PointF p);

        void onPinchStart(PointF p1, PointF p2);

        void onPinch(PointF p1, PointF p2);

        void onPinchEnd(PointF p1, PointF p2);

        void onTap(PointF p);

        void onLongPress(PointF p);
    }
}