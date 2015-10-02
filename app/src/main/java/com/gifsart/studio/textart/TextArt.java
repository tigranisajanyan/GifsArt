package com.gifsart.studio.textart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.os.Parcel;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.gifsart.studio.R;
import com.gifsart.studio.clipart.util.BitmapManager;
import com.gifsart.studio.clipart.util.Geom;
import com.gifsart.studio.clipart.util.GestureDetector;
import com.gifsart.studio.clipart.util.Graphics;
import com.gifsart.studio.clipart.view.AbstractItem;
import com.gifsart.studio.clipart.view.Item;
import com.gifsart.studio.utils.Utils;


/**
 * TODO: Eliminate this class' connection with font pangram previews
 * (TextArtView).
 */
public class TextArt extends AbstractItem implements Item.Recyclable, GestureDetector.GestureListener {
	public static final String LOG_TAG = TextArt.class.getSimpleName();

	public final static int FONT_PICSART = 0;
	public final static int FONT_MY = 1;
	public final static int FONT_SHOP = 2;
	public final static int FONT_FAVORITE = 3;

	private String text = "PicsArt";
	private String[] lines = {text};
	private String origText = null;

	private Context context = null;
	private View view = null;

	// the rect of this text art to be drawn on background image
	private Rect onImageRect = new Rect();

	// paints
	private Paint fillPaint;
	private Paint strokePaint;
	private Paint handleRectPaint1;
	private Paint handleRectPaint2;

//	private Paint testPaint;

	private int textSize = 80;
	private int strokeWidth = 5;

	// helpers (can be restored from centerX/Y coords and textBounds)
	// x,y coords of the origin of the text being drawn, non scaled
	// TODO: eliminate these like in ClipArt
	private float X = 10f;
	private float Y = 10f;
	// the amounts to scale by X and Y respectively
	private float scaleX = 1f;
	private float scaleY = 1f;

	// current text bounds, non scaled
	private Rect textBounds = new Rect();
	// width/height of textBounds, non scaled
	private int origTextWidth;
	private int origTextHeight;

	// selected area, scaled values
	private Rect trect = new Rect();
	// width/height of selected area, scaled values
	private float curWidth;
	private float curHeight;

	// center coords of this object, pivot point for scaling, so not affected by
	// center-scale
	private float centerX = -1f;
	private float centerY = -1f;
	// helper prop to store centerY value of non wrapped text when orientation
	// is horizontal
	private float horizontalTextCenterY;

	// rotation props
	private float rotateDegree = 0f;
	private float startRotateDegree = 0f;
	private float preDegree = 0f;

	// wrap props
	private boolean wrapEnabled = false;
	private boolean wrapWingsUp = true;
	// wrap absolute angle
	private int wrapAngle = 0;
	// the center y coord of the wrap arc
	private float wrapCy;
	// the radius of wrap arc
	private float wrapRadius;
	// selected area when wrap enabled, non scaled (trect is the scaled and
	// rounded version of this Rectf when wrap enabled)
	private RectF wrapBounds = new RectF();
	// width/height of wrapBounds, non scaled
	private float wrapOrigWidth;
	private float wrapOrigHeight;
	private int wrapProgress = 50;

	// orientaion props
	private boolean horizontal = true;

	// handle props
	// private Bitmap handle = null;
	private Bitmap handleSide = null;
	private Bitmap handleCorner = null;
	private Bitmap handleRotate = null;
	private boolean showRotateHandle = true;

	private Integer curZoomType = null;
	private int currentAction = Graphics.ACTION_DRAG;

	private boolean fromDrawing = false;

	private float verticalKoef = 0.9f;
	private long lastTouchTime = -1L;

	private TextArtStyle style = null;

	private int alignment = Gravity.LEFT | Gravity.CENTER_VERTICAL;

	private int maxWidth = 0;

	private GestureDetector gestureDetector;

	private PointF pinchStartPoint1;
	private PointF pinchStartPoint2;

	public TextArt(Context context, TextArtStyle style, String text, View view, int textSize, int maxWidth, boolean fromDrawing) {
		this.context = context;
		this.view = view;
		this.fromDrawing = fromDrawing;
		this.style = style;
		this.maxWidth = maxWidth;

		if (fromDrawing) this.textSize = textSize;

		refreshProperties(text, false);
		isDrawHandle = false;

		if (text.length() > 1) {
			isDrawHandle = true;
		}
		initHandles(context);

		gestureDetector = new GestureDetector(this);

		handleRectPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		handleRectPaint1.setColor(Color.WHITE);
		handleRectPaint1.setStyle(Style.STROKE);
		handleRectPaint1.setStrokeWidth(1);

		handleRectPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		handleRectPaint2.setColor(0x99000000);
		handleRectPaint2.setStyle(Style.STROKE);
		handleRectPaint2.setStrokeWidth(1f);
	}

	private void initHandles(Context context) {
		if (handleCorner == null) {
			handleCorner = BitmapManager.decodeResource(context.getResources(), R.drawable.handle_rect_corner_picsart_light2);
		}

		if (handleSide == null) {
			handleSide = BitmapManager.decodeResource(context.getResources(), R.drawable.handle_rect_side_picsart_light);
		}

		if (handleRotate == null) {
			handleRotate = BitmapManager.decodeResource(context.getResources(), R.drawable.handle_rotate_picsart_light);
		}
	}

	public void setHorizontal(boolean isHorizontal) {
		this.horizontal = isHorizontal;
		refreshProperties(text, true);
	}

	public void setAlignment() {
		fillPaint.setTextAlign(getAlign());
		strokePaint.setTextAlign(getAlign());
	}

	private Paint.Align getAlign() {
		switch (alignment) {
			case Gravity.CENTER | Gravity.CENTER_VERTICAL:
				return Paint.Align.CENTER;
			case Gravity.LEFT | Gravity.CENTER_VERTICAL:
				return Paint.Align.LEFT;
			case Gravity.RIGHT | Gravity.CENTER_VERTICAL:
				return Paint.Align.RIGHT;
			default:
				return Paint.Align.LEFT;
		}
	}

	public void refreshProperties(String text, boolean isEdit) {
		this.text = text;

		// check if in wrap mode the text is multi-line,
		// change the text to one line and keep the original
		// to restore when user exits wrap mode
		if (horizontal && wrapEnabled) {
			if (text.contains("\n")) {
				origText = text;
				this.text = text.replaceAll("\n", " ");
			}
		} else {
			if (origText != null) {
				this.text = origText;
			}
		}

		lines = this.text.trim().split("\n");

		if (!fromDrawing) {
			textSize = style.getFontSize();
		}
		alignment = style.getAlignment();

		strokeWidth = (Integer) TextArtStyle.STROKE_WIDTH;

		if (fromDrawing) {
			strokeWidth = (textSize * strokeWidth) / TextArtRes.TEXT_SIZE;
		}

		textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics());
		strokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, strokeWidth, context.getResources().getDisplayMetrics());

		fillPaint = new Paint();
		fillPaint.setStyle(Style.FILL);
		fillPaint.setAntiAlias(true);
		fillPaint.setTextSize(textSize);
		fillPaint.setColor(style.getFillColor());

		strokePaint = new Paint();
		strokePaint.setTextSize(textSize);
		strokePaint.setStyle(Style.STROKE);
		strokePaint.setStrokeWidth(strokeWidth);
		strokePaint.setAntiAlias(true);
		strokePaint.setColor(style.getStrokeColor());

		Rect tempBounds = new Rect();
		if (horizontal) {
			textBounds.setEmpty();
			if (wrapEnabled && origText != null) {
				// this.text already has the one-line text value
				fillPaint.getTextBounds(this.text, 0, this.text.length(), textBounds);
			} else {
				int lineSpacing = getLineSpacing();
				for (int i = 0; i < lines.length; i++) {
					fillPaint.getTextBounds(lines[i], 0, lines[i].length(), tempBounds);
					tempBounds.top += i * lineSpacing;
					tempBounds.bottom += i * lineSpacing;
					textBounds.union(tempBounds);
				}
			}
		} else {
			float horizontalOffset = 0f;
			float maxColumnHeight = 0f;
			for (String line : lines) {
				float currentColumnWidth = 0f;
				for (int i = 0; i < line.length(); i++) {
					Rect curSymbolRect = new Rect();
					fillPaint.getTextBounds(line, i, i + 1, curSymbolRect);
					currentColumnWidth = Math.max(currentColumnWidth, curSymbolRect.width());
				}
				horizontalOffset += currentColumnWidth + getLineSpacing() / 3f;
				maxColumnHeight = Math.max(maxColumnHeight, verticalKoef * fillPaint.getTextSize() * line.length());
			}
			horizontalOffset -= getLineSpacing() / 3f;

			textBounds.set(0, -(int) maxColumnHeight, (int) horizontalOffset, 0);
		}

		origTextWidth = textBounds.width();
		origTextHeight = textBounds.height();

		// set scales to according to specified maxWidth
		if (maxWidth != 0 && !isEdit && origTextWidth != 0) {
			float scaleValue = Math.max(((float) maxWidth) / origTextWidth, 0.5f);
			if (scaleValue < 1f) {
				scaleX = scaleValue;
				scaleY = scaleValue;
			}
		}
		// set to zero, so that this part work only first time
		maxWidth = 0;

		// restore curWidth/curHeight props
		if (horizontal && wrapEnabled) {
			curWidth = wrapOrigWidth * scaleX;
			curHeight = wrapOrigHeight * scaleY;
		} else {
			curWidth = origTextWidth * scaleX;
			curHeight = origTextHeight * scaleY;
		}
		// set centerX/Y
		if (centerX == -1f || centerY == -1f) {
			if (view.getWidth() != 0 && view.getHeight() != 0) {
				centerX = view.getWidth() * 0.5f;
				centerY = view.getHeight() * 0.5f;
			}
		}

		// restore X,Y coords
		initXY();

		// during text edit the font could be changed which will cause
		// a different wrapping so call wrapText() to refresh current wrap
		if (isEdit && horizontal && wrapEnabled) {
			wrapText(wrapProgress, wrapWingsUp ? wrapAngle : -wrapAngle);
		}

		setOpacity(opacity);
	}

	public void setFillPaintColor(int color) {
		fillPaint.setColor(color);
	}

	private void initXY() {
		if (centerX != -1 || centerY != -1) {
			X = centerX - origTextWidth * 0.5f;
			if (horizontal && wrapEnabled)
				// in wrap mode when (wrapAngle != 0) Y coord is not used
				// it will be used when wrapAngle == 0, so restore Y coord by
				// horizontal cy
				Y = horizontalTextCenterY - origTextHeight * 0.5f;
			else Y = centerY - origTextHeight * 0.5f;
		}
	}

	public static void initTextArtForDrawing(Context context, TextArtStyle style, Paint innerPaint, Paint outerPaint) {
		if (style == null) return;

		int textSize = style.getFontSize();

		int borderSize = TextArtStyle.STROKE_WIDTH;

		textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics());
		borderSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, borderSize, context.getResources().getDisplayMetrics());

		innerPaint.setStyle(Style.FILL);
		innerPaint.setAntiAlias(true);
		innerPaint.setTextSize(textSize);
		innerPaint.setColor(style.getFillColor());

		outerPaint.setTextSize(textSize);
		outerPaint.setStyle(Style.STROKE);
		outerPaint.setStrokeWidth(borderSize);
		outerPaint.setAntiAlias(true);
		outerPaint.setColor(style.getStrokeColor());
	}

	float lastTouchX = -1f;
	float lastTouchY = -1f;

	public void onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
	}

	float handleDeltaX = 0;
	float handleDeltaY = 0;

	private float scaleFactorOfMainView = 1f;

	public void touch_move(float x, float y) {
		if (inPinchMode) return;

		if (lastTouchX == -1 || lastTouchY == -1) {
			lastTouchX = x;
			lastTouchY = y;
			return;
		}

		float dx = (x - lastTouchX);
		float dy = (y - lastTouchY);

		if (currentAction == Graphics.ACTION_ZOOM) {
			// rotate x,y coordinate to find position in original rect
			float[] tmpPoint = {x, y};
			Graphics.rotatePoint(tmpPoint, centerX, centerY, -rotateDegree);
			x = tmpPoint[0];
			y = tmpPoint[1];

			x -= handleDeltaX;
			y -= handleDeltaY;

			switch (curZoomType) {
				case Graphics.ZOOM_RIGHT_BOTTOM:
					zoom((x - trect.right), (y - trect.bottom));
					break;
				case Graphics.ZOOM_TOP:
					zoom(0, -(y - trect.top));
					break;
				case Graphics.ZOOM_RIGHT:
					zoom(x - trect.right, 0);
					break;
				case Graphics.ZOOM_BOTTOM:
					zoom(0, y - trect.bottom);
					break;
				case Graphics.ZOOM_LEFT:
					zoom(-(x - trect.left), 0);
					break;
			}

		} else if (currentAction == Graphics.ACTION_DRAG) {
			X += dx;
			Y += dy;

			centerX += dx;
			centerY += dy;

			if (wrapEnabled) {
				wrapCy += dy;
				horizontalTextCenterY += dy;
				wrapBounds.offset(dx, dy);
			}

		} else if (currentAction == Graphics.ACTION_ROTATE) {
			showRotateHandle = false;
			rotate(x, y);
		}

		lastTouchX = x;
		lastTouchY = y;

		view.invalidate();
	}

	public void touch_up() {
		if (inPinchMode) return;

		if (currentAction == Graphics.ACTION_ROTATE) {
			startRotateDegree = rotateDegree;
			showRotateHandle = true;
		}
		lastTouchX = -1;
		lastTouchY = -1;
	}

	public void wrapText(int progress, int angle) {
		// load props
		if (progress == 0 || progress == 100) return;
		wrapProgress = progress;
		// isDrawHandle = true;
		wrapAngle = Math.abs(angle);
		if (wrapAngle == 360) wrapAngle = 359;
		if (angle > 0) {
			wrapWingsUp = true;
		} else {
			wrapWingsUp = false;
		}
		// check if wrap is 0
		if (wrapAngle == 0) {
			wrapEnabled = false;

			// restore props with original text
			if (origText != null) {
				refreshProperties(origText, false);
				origText = null;
			}

			// restore props for non-wrap view
			curWidth = (origTextWidth * scaleX);
			curHeight = (origTextHeight * scaleY);
			centerY = horizontalTextCenterY;

			return;
		} else {
			wrapEnabled = true;

			// check if text is multi-line
			if (text.contains("\n")) {
				refreshProperties(text, false);
			}
		}

		// calc wrap props
		// first calc the radius of arc
		wrapRadius = (float) (180 * (origTextWidth) / (wrapAngle * Math.PI));
		// calc the center of the wrap arc
		if (wrapWingsUp) {
			wrapCy = horizontalTextCenterY - wrapRadius;
		} else {
			wrapCy = horizontalTextCenterY + wrapRadius;
		}

		// compute wrap bounds
		wrapBounds.setEmpty();
		makeWrapPath(0, 0).computeBounds(wrapBounds, false);
		// fix wrapBounds' bottom or top
		if (wrapWingsUp) {
			wrapBounds.bottom = wrapCy + wrapRadius;
		} else {
			wrapBounds.top = wrapCy - wrapRadius;
		}
		float offset = origTextHeight * 0.5f;
		// widen the arc bounds by text's original height's half
		wrapBounds.inset(-offset, -offset);
		// set orig width/height of wrap
		wrapOrigWidth = wrapBounds.width();
		wrapOrigHeight = wrapBounds.height();
		// set trect's new y center coord
		centerY = wrapBounds.centerY();

	}

	private Path makeWrapPath(float dx, float dy) {
		Path wrapPath = new Path();
		RectF ovalRect = new RectF();

		ovalRect.set(centerX - wrapRadius, wrapCy - wrapRadius, centerX + wrapRadius, wrapCy + wrapRadius);
		ovalRect.offset(dx, dy);
		if (wrapWingsUp) {
			wrapPath.addArc(ovalRect, 90 + wrapAngle * 0.5f, -wrapAngle);
		} else {
			wrapPath.addArc(ovalRect, 270 - wrapAngle * 0.5f, wrapAngle);
		}
		return wrapPath;
	}

	@Override
	public void setOpacity(int progress) {
		opacity = progress;

		fillPaint.setAlpha(opacity);

		strokePaint.setAlpha(opacity);
	}

	private int getLineSpacing() {
		return (int) (3f * fillPaint.getFontSpacing() / 4f);
	}

	public void draw(Canvas c, float left, float top, float scaleFactor, float imageCurrentZoom, float rotate) {
		float scale = scaleFactor * imageCurrentZoom;
		imageZoom = imageCurrentZoom;
		imageLeft = left;
		imageTop = top;
		scaleFactorOfMainView = scaleFactor;

		refreshTextPropertiesForBitmapDraw(left, top, scale);

		c.save();
		c.scale(1 / scale, 1 / scale);

		float rotDegree = rotateDegree - rotate;
		c.rotate((float) rotDegree, centerX - imageLeft, centerY - imageTop);
		c.scale(scaleX, scaleY, centerX - imageLeft, centerY - imageTop);

		if (horizontal) {
			// vertical offset which will be used to position the text
			int textBottomOffset = textBounds.bottom;
			if (wrapEnabled) {
				// construct the path for text to draw
				Path wrapPath = makeWrapPath(-imageLeft, -imageTop);
				if (style.hasStroke())
					c.drawTextOnPath(text, wrapPath, 0, origTextHeight * 0.5f - textBottomOffset, strokePaint);
				c.drawTextOnPath(text, wrapPath, 0, origTextHeight * 0.5f - textBottomOffset, fillPaint);
			} else {
				int lineSpacing = getLineSpacing();
				if (style.hasStroke()) {
					Path textStrokePath = new Path();
					for (int i = 0; i < lines.length; i++) {
						strokePaint.getTextPath(lines[i], 0, lines[i].length(), getStartCoord(imageLeft), Y - imageTop + ((origTextHeight - textBounds.bottom)) + i * lineSpacing, textStrokePath);
						c.drawPath(textStrokePath, strokePaint);

						fillPaint.getTextPath(lines[i], 0, lines[i].length(), getStartCoord(imageLeft), Y - imageTop + ((origTextHeight - textBounds.bottom)) + i * lineSpacing, textStrokePath);
						c.drawPath(textStrokePath, fillPaint);

						textStrokePath.reset();
					}
				} else {
					for (int i = 0; i < lines.length; i++) {
						c.drawText(lines[i], 0, lines[i].length(), getStartCoord(imageLeft), Y - imageTop + ((origTextHeight - textBounds.bottom)) + i * lineSpacing, fillPaint);
					}
				}
			}
		} else {
			float horizontalOffset = 0f;
			for (String line : lines) {
				float currentColumnWidth = 0f;
				for (int i = 0; i < line.length(); i++) {
					Rect curSymbolRect = new Rect();
					fillPaint.getTextBounds(line, i, i + 1, curSymbolRect);
					currentColumnWidth = Math.max(currentColumnWidth, curSymbolRect.width());
					if (style.hasStroke()) {
						Path textStrokePath = new Path();

						strokePaint.getTextPath(line, i, i + 1, horizontalOffset + X - imageLeft + (currentColumnWidth - curSymbolRect.width()) / 2, Y - imageTop + verticalKoef * fillPaint.getTextSize() * (i + 1), textStrokePath);
						c.drawPath(textStrokePath, strokePaint);

						textStrokePath.reset();

						fillPaint.getTextPath(line, i, i + 1, horizontalOffset + X - imageLeft + (currentColumnWidth - curSymbolRect.width()) / 2, Y - imageTop + verticalKoef * fillPaint.getTextSize() * (i + 1), textStrokePath);
						c.drawPath(textStrokePath, fillPaint);

					} else {
						c.drawText(line, i, i + 1, horizontalOffset + X - imageLeft + (currentColumnWidth - curSymbolRect.width()) / 2, Y - imageTop + verticalKoef * fillPaint.getTextSize() * (i + 1), fillPaint);
					}
				}
				horizontalOffset += currentColumnWidth + getLineSpacing() / 3f;
			}
		}

		c.restore();
	}

	@Override
	public boolean touch_down(float x, float y) {
		return false;
	}

	public void draw(Canvas c) {
		refreshTextProperties();

		c.save(Canvas.MATRIX_SAVE_FLAG);

		c.rotate(rotateDegree, centerX, centerY);
		c.scale(scaleX, scaleY, centerX, centerY);

		if (horizontal) {
			// vertical offset which will be used to position the text
			int textBottomOffset = textBounds.bottom;


			if (wrapEnabled) {
				// construct the path for text to draw
				Path wrapPath = makeWrapPath(0, 0);
				if (style.hasStroke())
					c.drawTextOnPath(text, wrapPath, 0, origTextHeight * 0.5f - textBottomOffset, strokePaint);
				c.drawTextOnPath(text, wrapPath, 0, origTextHeight * 0.5f - textBottomOffset, fillPaint);
			} else {
				int lineSpacing = getLineSpacing();
				if (style.hasStroke()) {
					Path textStrokePath = new Path();
					for (int i = 0; i < lines.length; i++) {
						strokePaint.getTextPath(lines[i], 0, lines[i].length(), getStartCoord(0), Y + ((origTextHeight - textBounds.bottom)) + i * lineSpacing, textStrokePath);
						c.drawPath(textStrokePath, strokePaint);

						fillPaint.getTextPath(lines[i], 0, lines[i].length(), getStartCoord(0), Y + ((origTextHeight - textBounds.bottom)) + i * lineSpacing, textStrokePath);
						c.drawPath(textStrokePath, fillPaint);

						textStrokePath.reset();
					}
				} else {
					for (int i = 0; i < lines.length; i++) {
						c.drawText(lines[i], 0, lines[i].length(), getStartCoord(0), Y + ((origTextHeight - textBounds.bottom)) + i * lineSpacing, fillPaint);
					}
				}

			}
		} else {
			float horizontalOffset = 0f;
			for (String line : lines) {
				float currentColumnWidth = 0f;
				for (int i = 0; i < line.length(); i++) {
					Rect curSymbolRect = new Rect();
					fillPaint.getTextBounds(line, i, i + 1, curSymbolRect);
					currentColumnWidth = Math.max(currentColumnWidth, curSymbolRect.width());
					if (style.hasStroke()) {
						Path textStrokePath = new Path();

						strokePaint.getTextPath(line, i, i + 1, horizontalOffset + X + (currentColumnWidth - curSymbolRect.width()) / 2, Y + verticalKoef * fillPaint.getTextSize() * (i + 1), textStrokePath);
						c.drawPath(textStrokePath, strokePaint);

						textStrokePath.reset();

						fillPaint.getTextPath(line, i, i + 1, horizontalOffset + X + (currentColumnWidth - curSymbolRect.width()) / 2, Y + verticalKoef * fillPaint.getTextSize() * (i + 1), textStrokePath);
						c.drawPath(textStrokePath, fillPaint);
					} else {
						c.drawText(line, i, i + 1, horizontalOffset + X + (currentColumnWidth - curSymbolRect.width()) / 2, Y + verticalKoef * fillPaint.getTextSize() * (i + 1), fillPaint);
					}
				}
				horizontalOffset += currentColumnWidth + getLineSpacing() / 3f;
			}
		}

		c.restore();

		if (isDrawHandle) {
			if (handleSide == null || handleCorner == null || handleRotate == null) {
				initHandles(context);
			}
			Graphics.showHandle(c, trect, handleRectPaint1, handleRectPaint2, centerX, centerY, rotateDegree, handleCorner, handleRotate, handleSide, showRotateHandle, true);
		}
	}

	private float getStartCoord(float imageLeft) {
		switch (alignment) {
			case Gravity.CENTER | Gravity.CENTER_VERTICAL:
				return X - imageLeft + (textBounds.centerX());
			case Gravity.LEFT | Gravity.CENTER_VERTICAL:
				return X - imageLeft - (textBounds.left);
			case Gravity.RIGHT | Gravity.CENTER_VERTICAL:
				return X - imageLeft + (textBounds.right);
			default:
				return X - imageLeft - (textBounds.left);
		}
	}

	private void rotate(float x, float y) {
		float angle = (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
		angle = angle < 0 ? 2 * 180 + angle : angle;

		rotateDegree = (int) (angle + startRotateDegree - preDegree);
	}

	private void refreshTextPropertiesForBitmapDraw(float left, float top, float scale) {
		onImageRect.left = Math.round((trect.left - left) / scale);
		onImageRect.right = Math.round((trect.right - left) / scale);
		onImageRect.top = Math.round((trect.top - top) / scale);
		onImageRect.bottom = Math.round((trect.bottom - top) / scale);

		if (style.hasGradient()) {
			float gradTop = Y - imageTop;
			float gradBottom = Y - imageTop + origTextHeight;
			Shader textShader = new LinearGradient(0, gradTop, 0, gradBottom, style.getFillColor(), style.getFillGradientBottomColor(), TileMode.CLAMP);
			fillPaint.setShader(textShader);
		}

	}

	private void refreshTextProperties() {
		if (horizontal && wrapEnabled) {
			// wrap trect's scaled coords
			float trectX = calculateScaledCoordinate(wrapBounds.left, centerX, scaleX);
			float trectY = calculateScaledCoordinate(wrapBounds.top, centerY, scaleY);
			float trectRight = calculateScaledCoordinate(wrapBounds.right, centerX, scaleX);
			float trectBottom = calculateScaledCoordinate(wrapBounds.bottom, centerY, scaleY);
			// prepare scaled trect
			trect.left = Math.round(trectX);
			trect.top = Math.round(trectY);
			trect.right = Math.round(trectRight);
			trect.bottom = Math.round(trectBottom);
		} else {
			if (centerX == -1 || centerY == -1) {
				// TODO: this case occurs when this.view didn't have
				// width/height greater than zero values during init ->
				// refreshProperties()
				centerX = X + curWidth * 0.5f;
				centerY = Y + curHeight * 0.5f;
			}

			if (horizontal) {
				// store horizontal text's center y coord (used when wrapping)
				horizontalTextCenterY = centerY;
			}

			// prepare scaled trect
			float trectLeft = calculateScaledCoordinate(X, centerX, scaleX);
			float trectTop = calculateScaledCoordinate(Y, centerY, scaleY);
			// TODO: next 2 calls could be eliminated if centerX,Y didn't change in previous "if"
			float trectRight = calculateScaledCoordinate(centerX + (centerX - X), centerX, scaleX);
			float trectBottom = calculateScaledCoordinate(centerY + (centerY - Y), centerY, scaleY);
			trect.left = Math.round(trectLeft);
			trect.top = Math.round(trectTop);
			trect.right = Math.round(trectRight);
			trect.bottom = Math.round(trectBottom);
		}

		// TODO: could also be eliminated
		curWidth = trect.width();
		curHeight = trect.height();

		if (style.hasGradient()) {
			float gradTop = Y;
			float gradBottom = Y + textBounds.height();
			Shader textShader = new LinearGradient(0, gradTop, 0, gradBottom, style.getFillColor(), style.getFillGradientBottomColor(), TileMode.CLAMP);
			fillPaint.setShader(textShader);
		}
		setAlignment();

	}

	private float calculateScaledCoordinate(float coord, float centerCoord, float scale) {
		return (coord - centerCoord) * scale + centerCoord;
	}

	private void zoom(float moveX, float moveY) {
		// check for thresholds
		// final float MOVE_THRESHOLD = 1F;
		// float thresholdPx = Utils.convertDpToPixel(MOVE_THRESHOLD, context);
		// if (Math.abs(moveX) < thresholdPx && Math.abs(moveY) < thresholdPx) {
		// return;
		// }

		if (horizontal && wrapEnabled) {
			scaleX = (curWidth + 2 * moveX) / wrapOrigWidth;
			scaleY = (curHeight + 2 * moveY) / wrapOrigHeight;
		} else {
			scaleX = (curWidth + 2 * moveX) / origTextWidth;
			scaleY = (curHeight + 2 * moveY) / origTextHeight;
		}

		curWidth += 2 * moveX;
		curHeight += 2 * moveY;
	}

	public void zoomProportional(float moveX, float moveY, boolean minSize) {
		// check for thresholds
		// final float MOVE_MIN_THRESHOLD = 2F;
		// float thresholdMinPx = Utils.convertDpToPixel(MOVE_MIN_THRESHOLD,
		// context);
		// if (Math.abs(moveX) < thresholdMinPx && Math.abs(moveY) <
		// thresholdMinPx) {
		// return;
		// }

		float oldScaleX = scaleX;
		float oldScaleY = scaleY;

		float origWidth, origHeight;
		if (horizontal && wrapEnabled) {
			origWidth = wrapOrigWidth;
			origHeight = wrapOrigHeight;
		} else {
			origWidth = origTextWidth;
			origHeight = origTextHeight;
		}

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
				return;
			}
		}
	}

	boolean activateOnlyProps = false;

	public void sizeChanged(int left, int top, int oldLeft, int oldTop, float newScaleFactorDivOldScaleFactor, float imageCurrentZoom) {
		float zoomChangeScale = imageCurrentZoom / imageZoom;
		if (!isActive) {
			// activate to get initial props to adjust to new orientation
			activateOnlyProps = true;
			activate(oldLeft, oldTop, imageCurrentZoom);

			// store mainView's latest props (isActive=true case does this in
			// it's draw() method)
			imageZoom = imageCurrentZoom;
			imageLeft = left;
			imageTop = top;
			scaleFactorOfMainView *= newScaleFactorDivOldScaleFactor;
		}

		// adjust coords for new orientation
		centerX = adjustCoordOnScreenOrientationChanged(centerX, left, oldLeft, newScaleFactorDivOldScaleFactor);
		centerY = adjustCoordOnScreenOrientationChanged(centerY, top, oldTop, newScaleFactorDivOldScaleFactor);
		if (wrapEnabled) {
			horizontalTextCenterY = adjustCoordOnScreenOrientationChanged(horizontalTextCenterY, top, oldTop, newScaleFactorDivOldScaleFactor);
			wrapText(wrapProgress, wrapWingsUp ? wrapAngle : -wrapAngle);
		}
		initXY();

		// adjust scales
		if (isActive) {
			scaleX = scaleX * newScaleFactorDivOldScaleFactor;
			scaleY = scaleY * newScaleFactorDivOldScaleFactor;
		} else {
			scaleX = scaleX * newScaleFactorDivOldScaleFactor * zoomChangeScale;
			scaleY = scaleY * newScaleFactorDivOldScaleFactor * zoomChangeScale;

			// update trect for draw on bitmap isActive = false case
			refreshTextProperties();
			// update onImageRect to detect user's touch
			// TODO: probably redundant call, as this is called during bitmap draw (like in clipart). Test.
			refreshTextPropertiesForBitmapDraw(left, top, scaleFactorOfMainView * imageCurrentZoom);
		}

	}

	private float adjustCoordOnScreenOrientationChanged(float coord, int offset, int oldOffset, float scale) {
		return (coord - oldOffset) * scale + offset;
	}

	@Override
	public void activate(float left, float top, float imageCurrentZoom) {
		float oldCenterX = centerX;
		float oldCenterY = centerY;

		centerX = left + onImageRect.centerX() * (scaleFactorOfMainView * imageCurrentZoom);
		centerY = top + onImageRect.centerY() * (scaleFactorOfMainView * imageCurrentZoom);
		if (wrapEnabled) {
			float dx = centerX - oldCenterX;
			float dy = centerY - oldCenterY;
			wrapCy += dy;
			horizontalTextCenterY += dy;
			wrapBounds.offset(dx, dy);
		}
		initXY();

		if (activateOnlyProps) {
			activateOnlyProps = false;
			return;
		}

		float zoomChangeScale = imageCurrentZoom / imageZoom;
		scaleX = scaleX * zoomChangeScale;
		scaleY = scaleY * zoomChangeScale;

		// restore trect as touch down deals with trect before draw() call
		refreshTextProperties();
	}

	@Override
	public void cleanBitmaps() {
		BitmapManager.recycle(handleSide);
		BitmapManager.recycle(handleCorner);
		BitmapManager.recycle(handleRotate);

		handleSide = null;
		handleCorner = null;
		handleRotate = null;
	}

	@Override
	public int getType() {
		return Item.TEXTART;
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
	public float getWidth() {
		return curWidth;
	}

	@Override
	public float getHeight() {
		return curHeight;
	}

	@Override
	public float getX() {
		return X;
	}

	@Override
	public float getY() {
		return Y;
	}

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	@Override
	public float getRotation() {
		return rotateDegree;
	}

	public int getWrapProgress() {
		return wrapProgress;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public TextArtStyle getStyle() {
		return style;
	}

	public void setStyle(TextArtStyle style) {
		this.style = style;
	}

	public Rect getTextBounds() {
		return textBounds;
	}

	public Paint getFillPaint() {
		return fillPaint;
	}

	public String getText() {
		return text;
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
		if (trect == null || (!Graphics.getIsInRect(trect, p1.x, p1.y, -rotateDegree) && !Graphics.getIsInRect(trect, p2.x, p2.y, -rotateDegree))) {
			pinchOutOfBounds = true;
			return;
		}
		pinchOutOfBounds = false;

		if (!isActive() || !isDrawHandle()) return;

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
		GraphicUtils.getMidPoint(p1, p2, p);
		moveStartMidPoint = p;
	}

	@Override
	public void onPinch(PointF p1, PointF p2) {
		if (pinchOutOfBounds) return;

		if (pinchStartPoint1 == null || pinchStartPoint2 == null || p1 == null || p2 == null)
			return;

		if (!isActive() || !isDrawHandle()) return;

		// rotate
		rotateDegree = startRotateDegree1 + GraphicUtils.getAngleBetweenLines(pinchStartPoint1, pinchStartPoint2, p1, p2);

		// scale
		float dist = Geom.dist(p1, p2);
		float origWidth, origHeight;
		if (horizontal && wrapEnabled) {
			origWidth = wrapOrigWidth;
			origHeight = wrapOrigHeight;
		} else {
			origWidth = origTextWidth;
			origHeight = origTextHeight;
		}

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

			X += dx;
			Y += dy;
			centerX += dx;
			centerY += dy;
			if (wrapEnabled) {
				wrapCy += dy;
				horizontalTextCenterY += dy;
				wrapBounds.offset(dx, dy);
			}

		}
		moveStartMidPoint = midPoint;

		if (view != null) view.invalidate();
	}

	@Override
	public void onPinchEnd(PointF p1, PointF p2) {
		pinchOutOfBounds = false;
		inPinchMode = false;

		moveStartMidPoint = null;
		startRotateDegree = rotateDegree;
		// startRotateDegree1 = 0;
	}

	@Override
	public void onTap(PointF p) {
	}

	@Override
	public void onLongPress(PointF p) {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}
	// // end of GestureDetector

}