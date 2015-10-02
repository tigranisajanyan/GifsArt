package com.gifsart.studio.textart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class TextPreview extends AutoCompleteTextView {
	private final Rect textBounds = new Rect();

	private final Paint innerTextPaint;
	private final Paint outerTextPaint;

	private boolean strokeEnabled;
	private boolean gradientEnabled;

	private int fillColor;
	private int gradientColor;
	private int strokeColor;
	private int textHeight;

	public TextPreview(Context context, AttributeSet attrs) {
		super(context, attrs);

		innerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		innerTextPaint.setStyle(Style.FILL);

		outerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		outerTextPaint.setStyle(Style.STROKE);
		outerTextPaint.setStrokeWidth(1);
	}

	public void setTextSize(int size) {
		innerTextPaint.setTextSize(size);
		outerTextPaint.setTextSize(size);
	}

	public void setTextHeight(int textHeight) {
		this.textHeight = textHeight;
	}

	public int getTextHeight() {
		return textHeight;
	}

	public void setFillColor(int color) {
		fillColor = color;
	}

	public void setStrokeEnabled(boolean enabled, int color) {
		strokeEnabled = enabled;
		strokeColor = color;
		if (enabled) {
			outerTextPaint.setColor(color);
		}
	}

	public void setGradientEnabled(boolean enabled, int color) {
		gradientEnabled = enabled;

		if (enabled) {
			gradientColor = color;
		}
	}

	private void updateGradient() {
		innerTextPaint.getTextBounds(getText().toString(), 0, getText().toString().length(), textBounds);

		if (gradientEnabled) {
			innerTextPaint.setShader(new LinearGradient(0, 0, 0, textHeight, fillColor, gradientColor, TileMode.CLAMP));
		} else {
			innerTextPaint.setColor(fillColor);
			innerTextPaint.setShader(new LinearGradient(0, 0, 0, textHeight, fillColor, fillColor, TileMode.CLAMP));
			setTextColor(fillColor);
		}
	}

	public final void update() {
		invalidate();
	}

	public final void setTypeFace(Typeface typeFace) {
		innerTextPaint.setTypeface(typeFace);
		outerTextPaint.setTypeface(typeFace);
		super.setTypeface(typeFace);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getWidth() != 0) {
			computeTextHeight(innerTextPaint.getTextSize(), getWidth());
		}
		updateGradient();
		TextPaint p1 = new TextPaint(innerTextPaint);
		super.getPaint().set(p1);
		super.onDraw(canvas);
		if (strokeEnabled) {
			super.getPaint().set(outerTextPaint);
			setTextColor(strokeColor);
			if (TextUtils.isEmpty(getText().toString())) {
				setHintTextColor(strokeColor);
			}
			super.onDraw(canvas);
		}
	}

	public void computeTextHeight(float textSize, int maxWidth) {
		if (maxWidth == 0) {
			return;
		}
		TextPaint paint = new TextPaint();
		paint.setTextSize(textSize);
		String[] splitLines = getText().toString().split("\n");
		int textLinesCount = 0;
		int index;
		for (String line : splitLines) {
			index = 0;
			if (line.length() == 0) {
				textLinesCount++;
				continue;
			}
			while (index < line.length()) {
				if (index != line.length()){
					index += paint.breakText(line, index, line.length(), true, maxWidth, null);
					textLinesCount++;
				}
			}
		}
		if (textLinesCount == 1) {
			textHeight = getHeight() / 2 ;
		} else {
			textHeight = getHeight() / 2 + textLinesCount * textBounds.height();
		}
	}
}