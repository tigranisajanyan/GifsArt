package com.gifsart.studio.textart;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.view.Display;


import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class GraphicUtils {
    private static final String TAG = GraphicUtils.class.getSimpleName();

    public static float dist(float x0, float y0, float x1, float y1) {
		float dx = x0 - x1;
		float dy = y0 - y1;
		return (float)Math.sqrt(dx * dx + dy * dy);
	}

	private static float[] hsv = new float[3];

	public static int hue(float h) {
		hsv[0] = h;
		hsv[1] = 1.0f;
		hsv[2] = 1.0f;

		return Color.HSVToColor(hsv);
	}

	public static float getVelocity(int screenDensity, float dist, long timeDelta) {
		return (1000.0f / screenDensity) * dist / timeDelta;
	}

	public static int shiftHue(int color, float shift) {
		Assert.assertTrue(-360.0f < shift && shift < 360.0f);

		Color.colorToHSV(color, hsv);
		hsv[0] = hsv[0] + shift;

		if (hsv[0] < 0.0f)
			hsv[0] += 360.0f;
		else if (hsv[0] >= 360) hsv[0] -= 360.0f;
		return Color.HSVToColor(Color.alpha(color), hsv);
	}

	private static final float[] matrixValues = new float[9];

	private static final RectF rect1 = new RectF(0, 0, 1, 1);
	private static final RectF rect2 = new RectF();

	public static float getScaleFromMatrix(Matrix matrix) {
		matrix.mapRect(rect2, rect1);

		return rect2.width() / rect1.width();
	}
	
	public static float getCurrentScaleFromMatrix(Matrix matrix) {
		float[] values = new float[9];
		matrix.getValues(values);
		float scalex = values[Matrix.MSCALE_X];
	    float skewy = values[Matrix.MSKEW_Y];
	    return (float) Math.sqrt(scalex * scalex + skewy * skewy);
	}

	public static void setMatrixScale(Matrix matrix, float scale) {
		setMatrixScale(matrix, scale, scale);
	}

	public static void setMatrixScale(Matrix matrix, float scaleX, float scaleY) {
		matrix.getValues(matrixValues);

		matrixValues[Matrix.MSCALE_X] = scaleX;
		matrixValues[Matrix.MSCALE_Y] = scaleY;

		matrix.setValues(matrixValues);
	}

	public static void setMatrixTranslation(Matrix matrix, float translateX, float translateY) {
		matrix.getValues(matrixValues);

		matrixValues[Matrix.MTRANS_X] = translateX;
		matrixValues[Matrix.MTRANS_Y] = translateY;

		matrix.setValues(matrixValues);
	}

	public static void getTranslationFromMatrix(Matrix matrix, PointF outTranslate) {
		matrix.getValues(matrixValues);

		outTranslate.set(matrixValues[Matrix.MTRANS_X], matrixValues[Matrix.MTRANS_Y]);
	}

	private static final Matrix matrix = new Matrix();

	/**
	 * Transforms rect to fit into fitTo
	 * */
	public static void fitToRect(RectF rect, RectF fitTo) {
		matrix.setRectToRect(rect, fitTo, ScaleToFit.CENTER);
		matrix.mapRect(rect);
	}

	/**
	 * Fits rect to fitTo and writes result to result
	 * */
	public static void fitToRect(RectF rect, RectF fitTo, RectF result) {
		matrix.setRectToRect(rect, fitTo, ScaleToFit.CENTER);
		matrix.mapRect(result, rect);
	}

	public static void scaleRect(RectF src, RectF dst, float scale) {
		scaleRect(src, dst, scale, scale, src.centerX(), src.centerY());
	}

	public static void scaleRect(RectF rect, float scale) {
		scaleRect(rect, scale, scale, rect.centerX(), rect.centerY());
	}

	public static void scaleRect(RectF rect, float scaleX, float scaleY, float centerX, float centerY) {
		matrix.setScale(scaleX, scaleY, centerX, centerY);
		matrix.mapRect(rect);
	}

	public static void scaleRect(RectF src, RectF dst, float scaleX, float scaleY, float centerX, float centerY) {
		matrix.setScale(scaleX, scaleY, centerX, centerY);
		matrix.mapRect(dst, src);
	}

	public static float getScaleForFit(float w1, float h1, float w2, float h2) {
		return Math.min(w2 / w1, h2 / h1);
	}

	public static float getDistance(PointF start, PointF end) {
		return getDistance(start.x, start.y, end.x, end.y);
	}

	public static float getDistance(float startX, float startY, float endX, float endY) {
		final float dx = endX - startX;
		final float dy = endY - startY;

		return (float)Math.sqrt(dx * dx + dy * dy);
	}

	public static float getAngleBetweenLines(PointF lineStart1, PointF lineEnd1, PointF lineStart2, PointF lineEnd2) {
		float dx1 = lineStart1.x - lineEnd1.x;
		float dy1 = lineStart1.y - lineEnd1.y;

		float dx2 = lineStart2.x - lineEnd2.x;
		float dy2 = lineStart2.y - lineEnd2.y;

		double radians = Math.atan2(dy2, dx2) - Math.atan2(dy1, dx1);

		return (float) Math.toDegrees(radians);
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

    public static void getMidPoint(float x1, float y1, float x2, float y2, PointF outPoint) {
        outPoint.set((x1 + x2) / 2, (y1 + y2) / 2);
    }

	public static void getVectorPoint(float x0, float y0, float x1, float y1, float d, PointF result) {
		final float dx = x1 - x0;
		final float dy = y1 - y0;
		final float distance = (float)Math.sqrt(dx * dx + dy * dy);

		result.x = x0 + dx * d / distance;
		result.y = y0 + dy * d / distance;
	}

	public static void getVectorPoint2(float x0, float y0, float x1, float y1, float scale, PointF result) {
		final float dx = x1 - x0;
		final float dy = y1 - y0;

		result.x = x0 + dx * scale;
		result.y = y0 + dy * scale;
	}

	@SuppressWarnings("deprecation")
	public static Point getRealDisplaySize(Activity activity) {
		int width, height;
		Display display = activity.getWindowManager().getDefaultDisplay();

		// if (Build.VERSION.SDK_INT >= 17) {
		//
		// } else
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			try {
				Method getRawW = Display.class.getMethod("getRawWidth");
				Method getRawH = Display.class.getMethod("getRawHeight");

				width = (Integer) getRawW.invoke(display);
				height = (Integer) getRawH.invoke(display);
			} catch (NoSuchMethodException e) {
				width = display.getWidth();
				height = display.getHeight();
			} catch (IllegalArgumentException e) {
				width = display.getWidth();
				height = display.getHeight();
			} catch (IllegalAccessException e) {
				width = display.getWidth();
				height = display.getHeight();
			} catch (InvocationTargetException e) {
				width = display.getWidth();
				height = display.getHeight();
			}
		} else {
			width = display.getWidth();
			height = display.getHeight();
		}

		return new Point(width, height);
	}
	
	public static Matrix getMatrixFromJSONArray(JSONArray jsonArray) {
		float[] values = new float[9];
		
		if (jsonArray.length() != 9)
			return null;
		
		for (int i = 0; i < 9; ++i) {
			try {
				double value = jsonArray.getDouble(i);
				values[i] = (float) value;
			} catch (JSONException e) {
				return null;
			}
		}
		
		Matrix matrix = new Matrix();
		matrix.setValues(values);
		
		return matrix;
	}
	
	public static void writeMatrixValuesToJSONArray(Matrix matrix, JSONArray jsonArray) {
		float[] values = new float[9];
		matrix.setValues(values);
		
		for (float val : values) {
			try {
				jsonArray.put(val);
			} catch (JSONException e) {
			}
		}
	}
	
	public static void convertPointInRectPercent(PointF point,PointF percentPoint, RectF rect){
		percentPoint.x =(point.x - rect.left)/rect.width();
		percentPoint.y = (point.y - rect.top)/rect.height();
	}
	
	public static void convertPointFromRectPercent(PointF point,PointF percentPoint, RectF rect){
		point.x = percentPoint.x*rect.width()+rect.left;
		point.y = percentPoint.y*rect.height()+rect.top;
	}
	
	public static PointF getFitSize(float maxWidth,float maxHeight,float propWidth,float propHeight){
		float scaleFactor = Math.min((float) maxWidth / (float) propWidth, (float) maxHeight / (float) propHeight);
		int scaledWidth  = Math.round(propWidth * scaleFactor);
		int scaledHeight = Math.round(propHeight * scaleFactor);
		return new PointF(scaledWidth, scaledHeight);
	}
	
}