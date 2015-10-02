package com.gifsart.studio.clipart.util;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.MotionEvent;

public abstract class Geom {

    /**
     * Doesn't validate arguments *
     */
    public static float clip(float c, float min, float max) {
        if (c < min) c = min;
        if (c > max) c = max;
        return c;
    }

    public static float distSquared(float x0, float y0, float x1, float y1) {
        final float dx = x0 - x1;
        final float dy = y0 - y1;
        return dx * dx + dy * dy;
    }

    // can be optimized
    public static float distSquared(PointF p0, PointF p1) {
        return distSquared(p0.x, p0.y, p1.x, p1.y);
    }

    // can be optimized
    public static float distSquared(Point p0, Point p1) {
        return distSquared(p0.x, p0.y, p1.x, p1.y);
    }

    // can be optimized
    public static float dist(float x0, float y0, float x1, float y1) {
        return (float)Math.sqrt(distSquared(x0, y0, x1, y1));
    }

    // can be optimized
    public static float dist(PointF p0, PointF p1) {
        return dist(p0.x, p0.y, p1.x, p1.y);
    }

    // can be optimized
    public static float dist(Point p0, Point p1) {
        return dist(p0.x, p0.y, p1.x, p1.y);
    }

    public static float vectorAngle(float x0, float y0, float x1, float y1) {
        return (float) Math.atan2(y1 - y0, x1 - x0);
    }

    public static float vectorAngle(PointF p0, PointF p1) {
        return vectorAngle(p0.x, p0.y, p1.x, p1.y);
    }

    public static void moveCenterTo(RectF rect, float cx, float cy) {
        rect.offset(cx - rect.centerX(), cy - rect.centerY());
    }

    public static enum Fit {
        WIDTH, HEIGHT, CENTER, START, END
    }

    public static void fitRect(RectF src, RectF dst, final Fit fit) {
        src.sort();
        dst.sort();

        float newWidth = 1;
        float newHeight = 1;

        switch (fit) {
            case WIDTH:
                newWidth = dst.width();
                newHeight = src.height() * newWidth / src.width();
                break;
            case HEIGHT:
                newHeight = dst.height();
                newWidth = src.width() * newHeight / src.height();
                break;
            case CENTER:
            case START:
            case END:
                if (src.height() / src.width() < dst.height() / dst.width()) {
                    // fit width
                    newWidth = dst.width();
                    newHeight = src.height() * newWidth / src.width();
                } else {
                    // fit height
                    newHeight = dst.height();
                    newWidth = src.width() * newHeight / src.height();
                }
                break;
        }

        src.set(0f, 0f, newWidth, newHeight);
        if (fit == Fit.START) {
            src.offsetTo(dst.left, dst.top);
        } else if (fit == Fit.END) {
            src.offsetTo(dst.right, dst.bottom);
        } else moveCenterTo(src, dst.centerX(), dst.centerY());
    }

    public static void tryFitRect(RectF src, RectF dst, final Fit fit, final float minWidth, final float maxWidth) {
        if (minWidth > maxWidth) throw new IllegalArgumentException();

        final float minHeight = minWidth * src.height() / src.width();
        final float maxHeight = maxWidth * src.height() / src.width();

        src.sort();
        dst.sort();

        float newWidth = 1;
        float newHeight = 1;

        switch (fit) {
            case WIDTH:
                newWidth = clip(dst.width(), minWidth, maxWidth);
                newHeight = src.height() * newWidth / src.width();
                break;
            case HEIGHT:
                newHeight = clip(dst.height(), minHeight, maxHeight);
                newWidth = src.width() * newHeight / src.height();
                break;
            case CENTER:
                if (src.height() / src.width() < dst.height() / dst.width()) {
                    // fit width
                    newWidth = clip(dst.width(), minWidth, maxWidth);
                    newHeight = src.height() * newWidth / src.width();
                } else {
                    // fit height
                    newHeight = clip(dst.height(), minHeight, maxHeight);
                    newWidth = src.width() * newHeight / src.height();
                }
                break;
        }

        src.set(0f, 0f, newWidth, newHeight);
        moveCenterTo(src, dst.centerX(), dst.centerY());
    }

    /**
     * fits src rectangle into dst rectangle with minimal adjustments *
     */
    public static void fitRectIntoRectByMoving(RectF src, RectF dst) {
        src.sort();
        dst.sort();

        if (src.width() > dst.width()) {
            src.left = dst.left;
            src.right = dst.right;
        }
        if (src.height() > dst.height()) {
            src.top = dst.top;
            src.bottom = dst.bottom;
        }

        if (src.left < dst.left) {
            src.offset(dst.left - src.left, 0f);
        } else if (src.right > dst.right) {
            src.offset(dst.right - src.right, 0f);
        }

        if (src.top < dst.top) {
            src.offset(0f, dst.top - src.top);
        } else if (src.bottom > dst.bottom) {
            src.offset(0f, dst.bottom - src.bottom);
        }
    }

    public static void growToRatio(RectF rect, float heightToWidthRatio, boolean moveLeft, boolean moveTop, boolean moveRight, boolean moveBottom) {
        boolean result = tryGrowToRatio(rect, heightToWidthRatio, moveLeft, moveTop, moveRight, moveBottom);
        if (!result) throw new IllegalArgumentException();
    }

    public static boolean tryGrowToRatio(RectF rect, float heightToWidthRatio, boolean moveLeft, boolean moveTop, boolean moveRight, boolean moveBottom) {
        final float oldHeightToWidthRatio = rect.height() / rect.width();
        if (oldHeightToWidthRatio < heightToWidthRatio) {
            // grow height

            final float newHeight = rect.width() * heightToWidthRatio;

            if (moveTop) {
                if (moveBottom) {
                    final float cy = rect.centerY();
                    rect.top = cy - newHeight / 2f;
                    rect.bottom = cy + newHeight / 2f;
                } else {
                    rect.top = rect.bottom - newHeight;
                }
            } else {
                if (moveBottom) {
                    rect.bottom = rect.top + newHeight;
                } else {
                    return false;
                }
            }

        } else {
            // grow width

            final float newWidth = rect.height() / heightToWidthRatio;

            if (moveLeft) {
                if (moveRight) {
                    final float cx = rect.centerX();
                    rect.left = cx - newWidth / 2f;
                    rect.right = cx + newWidth / 2f;
                } else {
                    rect.left = rect.right - newWidth;
                }
            } else {
                if (moveRight) {
                    rect.right = rect.left + newWidth;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static void growToRatio(RectF rect, float heightToWidthRatio, VerticalSide vSide, HorizontalSide hSide) {
        boolean result = tryGrowToRatio(rect, heightToWidthRatio, vSide, hSide);
        if (!result) throw new IllegalArgumentException();
    }

    public static boolean tryGrowToRatio(RectF rect, float heightToWidthRatio, VerticalSide vSide, HorizontalSide hSide) {
        boolean moveLeft = vSide == VerticalSide.LEFT || vSide == null;
        boolean moveTop = hSide == HorizontalSide.TOP || hSide == null;
        boolean moveRight = vSide == VerticalSide.RIGHT || vSide == null;
        boolean moveBottom = hSide == HorizontalSide.BOTTOM || hSide == null;

        return tryGrowToRatio(rect, heightToWidthRatio, moveLeft, moveTop, moveRight, moveBottom);
    }

    public static void shrinkToRatio(RectF rect, float heightToWidthRatio, boolean moveLeft, boolean moveTop, boolean moveRight, boolean moveBottom) {
        boolean result = tryShrinkToRatio(rect, heightToWidthRatio, moveLeft, moveTop, moveRight, moveBottom);
        if (!result) throw new IllegalArgumentException();
    }

    public static boolean tryShrinkToRatio(RectF rect, float heightToWidthRatio, boolean moveLeft, boolean moveTop, boolean moveRight, boolean moveBottom) {
        final float oldHeightToWidthRatio = rect.height() / rect.width();
        if (oldHeightToWidthRatio < heightToWidthRatio) {
            // reduce width

            final float newWidth = rect.height() / heightToWidthRatio;
            if (moveLeft) {
                if (moveRight) {
                    final float cx = rect.centerX();
                    rect.left = cx - newWidth / 2f;
                    rect.right = cx + newWidth / 2f;
                } else {
                    rect.left = rect.right - newWidth;
                }
            } else {
                if (moveRight) {
                    rect.right = rect.left + newWidth;
                } else {
                    return false;
                }
            }
        } else {
            // reduce height
            final float newHeight = rect.width() * heightToWidthRatio;
            if (moveTop) {
                if (moveBottom) {
                    final float cy = rect.centerY();
                    rect.top = cy - newHeight / 2f;
                    rect.bottom = cy + newHeight / 2f;
                } else {
                    rect.top = rect.bottom - newHeight;
                }
            } else {
                if (moveBottom) {
                    rect.bottom = rect.top + newHeight;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static void shrinkToRatio(RectF rect, float heightToWidthRatio, VerticalSide vSide, HorizontalSide hSide) {
        boolean result = tryShrinkToRatio(rect, heightToWidthRatio, vSide, hSide);
        if (!result) throw new IllegalArgumentException();
    }

    public static boolean tryShrinkToRatio(RectF rect, float heightToWidthRatio, VerticalSide vSide, HorizontalSide hSide) {
        boolean moveLeft = vSide == VerticalSide.LEFT || vSide == null;
        boolean moveTop = hSide == HorizontalSide.TOP || hSide == null;
        boolean moveRight = vSide == VerticalSide.RIGHT || vSide == null;
        boolean moveBottom = hSide == HorizontalSide.BOTTOM || hSide == null;

        return tryShrinkToRatio(rect, heightToWidthRatio, moveLeft, moveTop, moveRight, moveBottom);
    }

    public static void fitRectIntoRect(RectF src, RectF dst, boolean moveLeft, boolean moveTop, boolean moveRight, boolean moveBottom, boolean keepProportion) {
        final float heightToWidth = src.height() / src.width();

        if (moveLeft && src.left < dst.left) {
            float dx = dst.left - src.left;
            src.left += dx;
            if (moveRight) src.right -= dx;
        }

        if (moveTop && src.top < dst.top) {
            float dy = dst.top - src.top;
            src.top += dy;
            if (moveBottom) src.bottom -= dy;
        }

        if (moveRight && src.right > dst.right) {
            float dx = dst.right - src.right;
            src.right += dx;
            if (moveLeft) src.left -= dx;
        }

        if (moveBottom && src.bottom > dst.bottom) {
            float dy = dst.bottom - src.bottom;
            src.bottom += dy;
            if (moveTop) src.top -= dy;
        }

        if (keepProportion)
            shrinkToRatio(src, heightToWidth, moveLeft, moveTop, moveRight, moveBottom);
    }

    public static void tryFitRectOverRectByMoving(RectF src, RectF dst) {
        float dx = 0f;
        float dy = 0f;

        if (src.left > dst.left) {
            dx = dst.left - src.left;
        } else if (src.right < dst.right) {
            dx = dst.right - src.right;
        }

        if (src.top > dst.top) {
            dy = dst.top - src.top;
        } else if (src.bottom < dst.bottom) {
            dy = dst.bottom - src.bottom;
        }

        src.offset(dx, dy);
    }

    public static void fitRectOverRectByMoveScale(RectF src, RectF dst) {
        if (src.width() < dst.width() || src.height() < dst.height()) {
            final float s = Math.max(dst.width() / src.width(), dst.height() / src.height());
            final float dx = s * src.width() / 2f;
            final float dy = s * src.height() / 2f;
            final float cx = src.centerX();
            final float cy = src.centerY();
            src.set(cx - dx, cy - dy, cx + dx, cy + dy);
        }
        float dx = 0;
        float dy = 0;

        if (src.left > dst.left)
            dx = dst.left - src.left;
        else if (src.right < dst.right) dx = dst.right - src.right;

        if (src.top > dst.top)
            dy = dst.top - src.top;
        else if (src.bottom < dst.bottom) dy = dst.bottom - src.bottom;

        src.offset(dx, dy);
    }

    public static float focalX(MotionEvent event, int id0, int id1) {
        return (event.getX(event.findPointerIndex(id0)) + event.getX(event.findPointerIndex(id1))) / 2f;
    }

    public static float focalY(MotionEvent event, int id0, int id1) {
        return (event.getY(event.findPointerIndex(id0)) + event.getY(event.findPointerIndex(id1))) / 2f;
    }

    public static float getSpan(MotionEvent event, int id0, int id1) {
        final int index0 = event.findPointerIndex(id0);
        final int index1 = event.findPointerIndex(id1);

        final float x0 = event.getX(index0);
        final float y0 = event.getY(index0);
        final float x1 = event.getX(index1);
        final float y1 = event.getY(index1);

        return dist(x0, y0, x1, y1);
    }

    public static void pinchScaleRect(RectF rect, MotionEvent e0, MotionEvent e1, int id0, int id1, float maxScale) {
        final float s = Math.min(getSpan(e1, id0, id1) / getSpan(e0, id0, id1), maxScale);
        final float fx0 = focalX(e0, id0, id1);
        final float fy0 = focalY(e0, id0, id1);
        final float fx1 = focalX(e1, id0, id1);
        final float fy1 = focalY(e1, id0, id1);
        final float newWidth = s * rect.width();
        final float newHeight = s * rect.height();

        rect.left = fx1 + s * (rect.left - fx0);
        rect.top = fy1 + s * (rect.top - fy0);
        rect.right = rect.left + newWidth;
        rect.bottom = rect.top + newHeight;
    }

    public static enum HorizontalSide {
        TOP, BOTTOM
    }

    public static enum VerticalSide {
        LEFT, RIGHT
    }

    public static class Corner {
        public Corner(HorizontalSide h, VerticalSide v) {
            this.h = h;
            this.v = v;
        }

        public HorizontalSide h;
        public VerticalSide v;
    }

    public static float getRectFSide(RectF r, VerticalSide vSide) {
        switch (vSide) {
            case LEFT:
                return r.left;
            case RIGHT:
                return r.right;
        }
        throw new IllegalArgumentException();
    }

    public static float getRectFSide(RectF r, HorizontalSide hSide) {
        switch (hSide) {
            case TOP:
                return r.top;
            case BOTTOM:
                return r.bottom;
        }
        throw new IllegalArgumentException();
    }

    public static void setRectFSide(RectF r, VerticalSide vSide, float val) {
        switch (vSide) {
            case LEFT:
                r.left = val;
                break;
            case RIGHT:
                r.right = val;
                break;
        }
    }

    public static void setRectFSide(RectF r, HorizontalSide hSide, float val) {
        switch (hSide) {
            case TOP:
                r.top = val;
                break;
            case BOTTOM:
                r.bottom = val;
                break;
        }
    }

    public static void moveCornerTo(RectF rect, Corner corner, float x, float y) {
        switch (corner.v) {
            case LEFT:
                rect.left = x;
                break;
            case RIGHT:
                rect.right = x;
                break;
        }

        switch (corner.h) {
            case TOP:
                rect.top = y;
                break;
            case BOTTOM:
                rect.bottom = y;
                break;
        }
    }

    public static void moveCornerTo(RectF rect, Corner corner, float x, float y, RectF bounds) {
        switch (corner.v) {
            case LEFT:
                rect.left = clip(x, bounds.left, bounds.right);
                break;
            case RIGHT:
                rect.right = clip(x, bounds.left, bounds.right);
                break;
        }

        switch (corner.h) {
            case TOP:
                rect.top = clip(y, rect.top, rect.bottom);
                break;
            case BOTTOM:
                rect.bottom = clip(y, rect.top, rect.bottom);
                break;
        }

    }

    public static VerticalSide getAdjacentVerticalSide(RectF rect, float x, float y, float precision) {
        if (rect.top - precision < y && y < rect.bottom + precision) {
            if (Math.abs(x - rect.left) < precision) return VerticalSide.LEFT;
            if (Math.abs(x - rect.right) < precision) return VerticalSide.RIGHT;
        }

        return null;
    }

    public static HorizontalSide getAdjacentHorizontalSide(RectF rect, float x, float y, float precision) {
        if (rect.left - precision < x && x < rect.right + precision) {
            if (Math.abs(y - rect.top) < precision) return HorizontalSide.TOP;
            if (Math.abs(y - rect.bottom) < precision) return HorizontalSide.BOTTOM;
        }

        return null;
    }

    public static Corner getAdjacentCorner(RectF rect, float x, float y, float precision) {
        Corner minDistCorner = null;
        float minDist = dist(rect.left, rect.top, x, y);
        float dist;

        dist = dist(rect.left, rect.top, x, y);
        if (dist <= minDist) {
            minDist = dist;
            minDistCorner = new Corner(HorizontalSide.TOP, VerticalSide.LEFT);
        }
        dist = dist(rect.right, rect.top, x, y);
        if (dist <= minDist) {
            minDist = dist;
            minDistCorner = new Corner(HorizontalSide.TOP, VerticalSide.RIGHT);
        }
        dist = dist(rect.left, rect.bottom, x, y);
        if (dist <= minDist) {
            minDist = dist;
            minDistCorner = new Corner(HorizontalSide.BOTTOM, VerticalSide.LEFT);
        }
        dist = dist(rect.right, rect.bottom, x, y);
        if (dist <= minDist) {
            minDist = dist;
            minDistCorner = new Corner(HorizontalSide.BOTTOM, VerticalSide.RIGHT);
        }

        if (minDist < precision)
            return minDistCorner;
        else return null;
    }

    public static void scaleDownToFit(RectF src, RectF dst, VerticalSide vSide, HorizontalSide hSide) {
        boolean moveLeft = vSide == VerticalSide.LEFT || vSide == null;
        boolean moveTop = hSide == HorizontalSide.TOP || hSide == null;
        boolean moveRight = vSide == VerticalSide.RIGHT || vSide == null;
        boolean moveBottom = hSide == HorizontalSide.BOTTOM || hSide == null;

        scaleDownToFit(src, dst, moveLeft, moveTop, moveRight, moveBottom);
    }

    public static void scaleDownToFit(RectF src, RectF dst, boolean moveLeft, boolean moveTop, boolean moveRight, boolean moveBottom) {
        final float heightToWidth = src.height() / src.width();

        src.intersect(dst);
        shrinkToRatio(src, heightToWidth, moveLeft, moveTop, moveRight, moveBottom);
    }

    public static float cornerX(RectF rect, Corner corner) {
        switch (corner.v) {
            case LEFT:
                return rect.left;
            case RIGHT:
                return rect.right;
            default:
                throw new IllegalStateException();
        }
    }

    public static float cornerY(RectF rect, Corner corner) {
        switch (corner.h) {
            case TOP:
                return rect.top;
            case BOTTOM:
                return rect.bottom;
            default:
                throw new IllegalStateException();
        }
    }

    public static void scaleAroundCenter(RectF rect, float s) {
        final float cx = rect.centerX();
        final float cy = rect.centerY();
        final float dx = rect.width() * s / 2f;
        final float dy = rect.height() * s / 2f;
        rect.set(cx - dx, cy - dy, cx + dx, cy + dy);
    }

    public static void scaleRect(RectF rect, float cx, float cy, float s) {
        scaleRect(rect, cx, cy, s, s);
    }

    public static void scaleRect(RectF rect, float cx, float cy, float sx, float sy) {
        rect.right = rect.left + sx * rect.width();
        rect.bottom = rect.top + sy * rect.height();
        rect.offsetTo(cx * (1f - sx) + sx * rect.left, cy * (1f - sy) + sy * rect.top);
    }

    public static void scalePoint(PointF point, float cx, float cy, float s) {
        point.x = (point.x - cx) * s + cx;
        point.y = (point.y - cy) * s + cy;
    }

    private static final Matrix tmpMatrix = new Matrix();

    public static void rotatePointAroundPivot(float[] point, float degrees, float px, float py) {
        tmpMatrix.setRotate(degrees, px, py);
        tmpMatrix.mapPoints(point);
    }

    public static float diff(RectF r0, RectF r1) {
        float diff = 0f;
        diff = Math.max(diff, Math.abs(r0.left - r1.left));
        diff = Math.max(diff, Math.abs(r0.top - r1.top));
        diff = Math.max(diff, Math.abs(r0.right - r1.right));
        diff = Math.max(diff, Math.abs(r0.bottom - r1.bottom));

        return diff;
    }

    public static float mapXRectToRect(float x, RectF src, RectF dst) {
        return (x - src.left) * dst.width() / src.width() + dst.left;
    }

    public static float mapYRectToRect(float y, RectF src, RectF dst) {
        return (y - src.top) * dst.height() / src.height() + dst.top;
    }

    public static void mapRectToRect(RectF rect, RectF src, RectF dst) {
        rect.left = mapXRectToRect(rect.left, src, dst);
        rect.top = mapYRectToRect(rect.top, src, dst);
        rect.right = mapXRectToRect(rect.right, src, dst);
        rect.bottom = mapYRectToRect(rect.bottom, src, dst);
    }

    private static float[] tmpPoints = new float[2];

    public static void calculateScaleForMatrix(Matrix matrix, float[] values) {
        tmpPoints[0] = 1f;
        tmpPoints[1] = 1f;

        matrix.mapPoints(values, tmpPoints);
    }

    public static boolean areRectsEqual(RectF rect1, RectF rect2) {
        return rect1.left == rect2.left && rect1.right == rect2.right &&
                rect1.top == rect2.top && rect1.bottom == rect2.bottom;
    }

    /**
     * @return the sign of {@code value}, -1 for negative numbers, 1 for positive numbers and zero
     */
    public static float getSign(float value) {
        return value < 0 ? -1f : 1f;
    }

    public static void interpolateRectF(RectF target, RectF src, RectF dst, float fraction) {
        target.left = src.left + (dst.left - src.left) * fraction;
        target.top = src.top + (dst.top - src.top) * fraction;
        target.right = src.right + (dst.right - src.right) * fraction;
        target.bottom = src.bottom + (dst.bottom - src.bottom) * fraction;
    }
}
