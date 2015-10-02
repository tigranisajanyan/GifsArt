package com.gifsart.studio.clipart.util;

import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;


public final class ScaleRotateGestureDetector {
    private static final String LOG_TAG = ScaleRotateGestureDetector.class.getSimpleName();
    private static final boolean LOG_ENABLED = false;

    private static final float ROTATE_THRESHOLD = 15f;

    private static final float DEFAULT_MAX_ZOOM = 5;
    private static final float DEFAULT_MIN_ZOOM = 0.5f;

    private float maxZoom;
    private float minZoom;

    private boolean rotationEnabled;
    private boolean draggingEnabled;
    private boolean scalingEnabled;

    private boolean edgeCheckingEnabled;
    private boolean singlePointerDragEnabled;

    private boolean rotationToggled;

    private float totalRotation;

    private Matrix matrix = new Matrix();

    private int primaryPointerId;
    private int secondaryPointerId;

    private PointF primaryPoint = new PointF();
    private PointF secondaryPoint = new PointF();

    private PointF oldPrimaryPoint = new PointF();
    private PointF oldSecondaryPoint = new PointF();

    private PointF midPoint = new PointF();

    private PointF translateXY = new PointF();

    private float oldPrimaryX, oldPrimaryY;
    private float oldSecX, oldSecY;

    private float touchTolerance;

    private GestureListener gestureListener;

    /**
     * Constructs a new object using default zoom edges and enabling rotation
     * events.
     *
     * */
    public ScaleRotateGestureDetector(Resources res) {
        this(res, DEFAULT_MAX_ZOOM, DEFAULT_MIN_ZOOM, true, true);
    }

    /**
     * @param maxZoom
     *            Maximum scale value for
     * */
    public ScaleRotateGestureDetector(Resources res, float maxZoom, float minZoom, boolean rotationEnabled, boolean edgeCheckingEnabled) {
        setZoomEdges(maxZoom, minZoom);

        this.rotationEnabled = rotationEnabled;
        this.edgeCheckingEnabled = edgeCheckingEnabled;

        draggingEnabled = true;
        scalingEnabled = true;

        touchTolerance = TouchToleranceLevel.MEDIUM.getToleranceInPixels(res);
    }

    public void setToleranceLevel(TouchToleranceLevel level, Resources res) {
        touchTolerance = level.getToleranceInPixels(res);
    }

    /**
     * Sets whether rotations enabled.
     *
     * @param enabled
     *            {@code true} if rotation is enabled, {@code false} otherwise
     * */
    public void setRotationEnabled(boolean enabled) {
        rotationEnabled = enabled;
    }

    /**
     * Sets whether dragging is enabled. If not enabled then the underlying matrix will not be affected by dragging operations
     * */
    public void setDraggingEnabled(boolean enabled) {
        draggingEnabled = enabled;
    }

    /**
     * Sets whether scaling is enabled. If not enabled then the underlying matrix will not be affected by scaling operations
     * */
    public void setScalingEnabled(boolean enabled) {
        scalingEnabled = enabled;
    }

    /**
     * Sets whether single pointer dragging is enabled
     *
     * @param enabled
     *            {@code true} if single pointer dragging is enabled,
     *            {@code false} otherwise
     * */
    public final void setSinglePointerDraggingEnabled(boolean enabled) {
        singlePointerDragEnabled = enabled;
    }

    /**
     * Sets whether zoom edge checking enabled.
     *
     * @param enabled
     *            {@code true} if checking is enabled, {@code false} otherwise
     * */
    public void setEdgeCheckingEnabled(boolean enabled) {
        edgeCheckingEnabled = enabled;
    }

    /**
     * Sets whether historical events enabled. If enabled, then
     * {@code ACTION_MOVE} events will
     * */
    public final void setHistoricalEventsEnabled(boolean enabled) {
//		historicalEventsEnabled = enabled;
    }

    /**
     * Copies the src into underlying matrix. If src is null that matrix will be
     * set to identity.
     * */
    public void setMatrix(Matrix src) {
        matrix.set(src);
    }

    /**
     * Returns the underlying matrix without making defensive copy.
     * */
    public Matrix getMatrix() {
        return matrix;
    }

    /**
     * Writes the content of underlying matrix to outMatrix.
     *
     * @param outMatrix
     *            out matrix which will hold the values. Throws an
     *            AssertionError when outMatrix is null.
     * */
    public void copyMatrixTo(Matrix outMatrix) {
        assert outMatrix != null;

        outMatrix.set(matrix);
    }

    /**
     * Sets the zoom edges.
     *
     * @param max
     *            Max zoom value, should be positive value
     * @param min
     *            Min zoom value, should be positive and smaller then max
     * */
    public void setZoomEdges(float max, float min) {
        // Note. use `adb shell setprop debug.assert 1` command to enable
        // assertions

        assert max >= min : "Max should be bigger then min, but is less";
        assert max > 0 && min > 0 : "Max and min should be bigger then 0";

        maxZoom = max;
        minZoom = min;
    }

    /**
     * Sets the gesture listener. Can be null.
     * */
    public void setGestureListener(GestureListener listener) {
        gestureListener = listener;
    }

    /**
     * Call this in every onTouchEvent of the View.
     * */
    public void onTouchEvent(MotionEvent event) {
        final int pointerCount = event.getPointerCount();
        final int actionIndex = event.getActionIndex();
        final int actionMasked = event.getActionMasked();

        if (LOG_ENABLED) Log.d(LOG_TAG, "pointerCount : " + pointerCount);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX(0);
                final float y = event.getY(0);

                primaryPointerId = event.getPointerId(0);
                primaryPoint.set(x, y);
                oldPrimaryPoint.set(primaryPoint);

                oldPrimaryX = x;
                oldPrimaryY = y;

                if (gestureListener != null) {
                    gestureListener.onSinglePointer(x, y);
                    gestureListener.onSinglePointerAction(MotionEvent.ACTION_DOWN, x, y, event.getEventTime(), event.getPressure(0));
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                if (pointerCount == 2) {
                    oldPrimaryX = event.getX(1 - actionIndex);
                    oldPrimaryY = event.getY(1 - actionIndex);

                    oldSecX = event.getX(actionIndex);
                    oldSecY = event.getY(actionIndex);

                    primaryPoint.set(oldPrimaryX, oldPrimaryY);
                    oldPrimaryPoint.set(primaryPoint);

                    secondaryPointerId = event.getPointerId(actionIndex);
                    secondaryPoint.set(oldSecX, oldSecY);
                    oldSecondaryPoint.set(secondaryPoint);

                    totalRotation = 0;
                    rotationToggled = false;

                    if (gestureListener != null) {
                        gestureListener.onDoublePointer(oldPrimaryX, oldPrimaryY, oldSecX, oldSecY);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                if (pointerCount == 2) {
                    int newPrimaryIndex = 1 - actionIndex;
                    primaryPointerId = event.getPointerId(newPrimaryIndex);
                    primaryPoint.set(event.getX(newPrimaryIndex), event.getY(newPrimaryIndex));
                    oldPrimaryPoint.set(primaryPoint);

                    if (gestureListener != null) {
                        gestureListener.onSinglePointer(primaryPoint.x, primaryPoint.y);
                    }
                } else if (pointerCount > 2) {
                    getValidPointers(event, pointerCount, actionIndex);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1) {
                    if (draggingEnabled) {
                        final int primaryPointerIndex = event.findPointerIndex(primaryPointerId);

                        primaryPoint.set(event.getX(primaryPointerIndex), event.getY(primaryPointerIndex));

                        final float dx = primaryPoint.x - oldPrimaryPoint.x;
                        final float dy = primaryPoint.y - oldPrimaryPoint.y;

                        if (singlePointerDragEnabled) matrix.postTranslate(dx, dy);

                        oldPrimaryPoint.set(primaryPoint);

                        if (gestureListener != null) {
                            if (singlePointerDragEnabled) gestureListener.onDrag(dx, dy);
                            gestureListener.onSinglePointerAction(MotionEvent.ACTION_MOVE, primaryPoint.x, primaryPoint.y, event.getEventTime(), event.getPressure(primaryPointerIndex));
                        }
                    }
                } else if (pointerCount > 1) {

                    final int primaryPointerIndex = event.findPointerIndex(primaryPointerId);
                    final int secondaryPointerIndex = event.findPointerIndex(secondaryPointerId);

                    final float primPointerX = event.getX(primaryPointerIndex);
                    final float primPointerY = event.getY(primaryPointerIndex);

                    final float secPointerX = event.getX(secondaryPointerIndex);
                    final float secPointerY = event.getY(secondaryPointerIndex);

                    primaryPoint.set(primPointerX, primPointerY);
                    secondaryPoint.set(secPointerX, secPointerY);

                    final float newDistance = Math.max(ClipArtUtils.getDistance(primaryPoint, secondaryPoint), 1);

                    if (LOG_ENABLED) Log.d(LOG_TAG, "distance : " + newDistance);

                    if (newDistance >= touchTolerance) {
                        if (scalingEnabled) {
                            final float oldDistance = Math.max(ClipArtUtils.getDistance(oldPrimaryPoint, oldSecondaryPoint), 1);

                            float scale = newDistance / oldDistance;

                            if (edgeCheckingEnabled) {
                                final float currentScale = ClipArtUtils.getCurrentScaleFromMatrix(matrix);

                                float postScale = currentScale * scale;

                                if (postScale > maxZoom)
                                    postScale = maxZoom;
                                else if (postScale < minZoom) postScale = minZoom;

                                scale = postScale / currentScale;
                            }

                            ClipArtUtils.getMidPoint(primaryPoint, secondaryPoint, midPoint);
                            matrix.postScale(scale, scale, midPoint.x, midPoint.y);

                            if (gestureListener != null) {
                                float oldDx = oldPrimaryPoint.x - oldSecondaryPoint.x;
                                float oldDy = oldPrimaryPoint.y - oldSecondaryPoint.y;

                                if (oldDx < 1 && oldDx >= 0) {
                                    oldDx = 1;
                                }

                                if (oldDx > -1 && oldDx <= 0) {
                                    oldDx = -1;
                                }

                                if (oldDy < 1 && oldDy >= 0) {
                                    oldDy = 1;
                                }

                                if (oldDy > -1 && oldDy <= 0) {
                                    oldDy = -1;
                                }

                                final float newDx = primaryPoint.x - secondaryPoint.x;
                                final float newDy = primaryPoint.y - secondaryPoint.y;

                                float scaleX = newDx / oldDx;
                                float scaleY = newDy / oldDy;

                                gestureListener.onScale(scale, scaleX, scaleY, midPoint.x, midPoint.y);
                            }
                        }

                        if (rotationEnabled) {
                            final float rotation = ClipArtUtils.getAngleBetweenLines(oldPrimaryPoint, oldSecondaryPoint, primaryPoint, secondaryPoint);

                            if (LOG_ENABLED) Log.d(LOG_TAG, "rotation : " + rotation);

                            totalRotation += rotation;

                            final float absRotation = Math.abs(totalRotation);

                            if (absRotation >= ROTATE_THRESHOLD || rotationToggled) {
                                matrix.postRotate(rotation, midPoint.x, midPoint.y);
                                rotationToggled = true;
                            }

                            if (gestureListener != null) {
                                gestureListener.onRotate(rotation, midPoint.x, midPoint.y);
                            }
                        }
                    }

                    if (draggingEnabled) {
                        if (newDistance < touchTolerance) {
                            float dx = primaryPoint.x - oldPrimaryPoint.x;
                            float dy = primaryPoint.y - oldPrimaryPoint.y;

                            matrix.postTranslate(dx, dy);
                            if (gestureListener != null) {
                                gestureListener.onDrag(dx, dy);
                            }
                        } else {
                            ClipArtUtils.getMidPointDelta(oldPrimaryPoint, oldSecondaryPoint, primaryPoint, secondaryPoint, translateXY);
                            matrix.postTranslate(translateXY.x, translateXY.y);
                            if (gestureListener != null) {
                                gestureListener.onDrag(translateXY.x, translateXY.y);
                            }
                        }
                    }

                    oldPrimaryPoint.set(primaryPoint);
                    oldSecondaryPoint.set(secondaryPoint);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (gestureListener != null) {
                    gestureListener.onSinglePointerAction(MotionEvent.ACTION_UP, event.getX(), event.getY(), event.getEventTime(), event.getPressure());
                }
                break;
        }
    }

    private void getValidPointers(MotionEvent event, int pointerCount, int invalidIndex) {
        int primaryPointerIndex = -1;
        int secondaryPointerIndex = -1;

        for (int i = 0; i < pointerCount; ++i) {
            if (i != invalidIndex) {
                if (primaryPointerIndex == -1) {
                    primaryPointerIndex = i;
                } else if (secondaryPointerIndex == -1) {
                    secondaryPointerIndex = i;
                } else break;
            }
        }

        oldPrimaryX = event.getX(primaryPointerIndex);
        oldPrimaryY = event.getY(primaryPointerIndex);

        oldSecX = event.getX(secondaryPointerIndex);
        oldSecY = event.getY(secondaryPointerIndex);

        primaryPointerId = event.getPointerId(primaryPointerIndex);
        primaryPoint.set(oldPrimaryX, oldPrimaryY);
        oldPrimaryPoint.set(primaryPoint);

        secondaryPointerId = event.getPointerId(secondaryPointerIndex);
        secondaryPoint.set(oldSecX, oldSecY);
        oldSecondaryPoint.set(secondaryPoint);

    }

    public interface GestureListener {
        /**
         * Called when the primary pointer goes down or the secondary goes up.
         *
         * @param x, y coordinate of pointer
         * */
        void onSinglePointer(float x, float y);

        /**
         * Called when secondary pointer goes down.
         *
         * @param x1, y1 coordinate of first pointer
         * @param x2, y2 coordinate of second pointer (newly added)
         * */
        void onDoublePointer(float x1, float y1, float x2, float y2);

        /**
         * Called when drag event occurs by a single pointer or double.
         *
         * @param dx
         *            delta x
         * @param dy
         *            delta y
         * */
        void onDrag(float dx, float dy);

        /**
         * Called when scale event occurs.
         *
         * @param s
         *            delta scale of finger distance
         * @param sx
         *            delta scale by x relative to point C
         * @param sy
         *            delta scale by y relative to point C
         * @param cx
         *            the x coordinate of point C
         * @param cy
         *            the y coordinate of point C
         * */
        void onScale(float s, float sx, float sy, float cx, float cy);

        /**
         * Called when rotate event occurs. <b>Note</b> you'll not receive
         * rotate events if you disabled it.
         *
         * @param r
         *            the rotation relative to point C
         * @param cx
         *            the x coordinate of point C
         * @param cy
         *            the y coordinate of point C
         * */
        void onRotate(float r, float cx, float cy);

        /**
         * Called when single pointer event occurs, e.g. ACTION_DOWN,
         * ACTION_MOVE, ACTION_UP
         *
         * @param action
         *            one of {@code MotionEvent.ACTION_DOWN},
         *            {@code MotionEvent.ACTION_MOVE},
         *            {@code MotionEvent.ACTION_UP}
         * @param x
         *            event's x coordinate
         * @param y
         *            event's y coordinate
         * @param eventTime
         *            when this event occurred
         * @param pressure
         *            pointer's pressure value
         * */
        void onSinglePointerAction(int action, float x, float y, long eventTime, float pressure);
    }

    public interface GestureListener2 {
        void onPanStart(float px, float py);
//		void onPan(float startX, float startY, float );
    }

    public static class SimpleGestureListener implements GestureListener {

        @Override
        public void onSinglePointer(float x, float y) {

        }

        @Override
        public void onDoublePointer(float x1, float y1, float x2, float y2) {

        }

        @Override
        public void onDrag(float dx, float dy) {

        }

        @Override
        public void onScale(float s, float sx, float sy, float cx, float cy) {

        }

        @Override
        public void onRotate(float r, float cx, float cy) {

        }

        @Override
        public void onSinglePointerAction(int action, float x, float y, long eventTime, float pressure) {

        }
    }

    public enum TouchToleranceLevel {
        NONE(0), MEDIUM(1), HIGH(2);
        private final float toleranceInDip;

        private TouchToleranceLevel(float doleranceInDip) {
            this.toleranceInDip = doleranceInDip;
        }

        private float getToleranceInPixels(Resources res) {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toleranceInDip, res.getDisplayMetrics());
        }
    }
}