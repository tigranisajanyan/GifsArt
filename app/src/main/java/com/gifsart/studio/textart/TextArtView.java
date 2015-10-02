package com.gifsart.studio.textart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class TextArtView extends View {

	private TextArt textArtObj = null;
	private String text;
	private TextArtStyle style;

	public TextArtView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextArt initParams(TextArtStyle style, String text) {
		if (style == null || text == null) return null;
		// if non of params changed return
		if (style.equals(this.style) && text.equals(this.text)) {
			return textArtObj;
		}
		
		this.style = style;
		this.text = text;
		textArtObj = new TextArt(getContext(), style, text, this, 0, 0, false);
		invalidate();

		return textArtObj;
	}

	public void setTextArtObj(TextArt textArtObj) {
		if (textArtObj == null) return;
		// if param is not changed return
		if (textArtObj.equals(this.textArtObj)) return;
		
		this.textArtObj = textArtObj;
		this.text = textArtObj.getText();
		textArtObj.refreshProperties(text, false);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();

		float scale = adjustTextBoundsRect();
		Rect textBounds = textArtObj.getTextBounds();

		if (getWidth() != 0 && getHeight() != 0) {
			float cx = getWidth() * 0.5f;
			float cy = getHeight() * 0.5f;
			canvas.scale(1f / scale, 1f / scale, cx, cy);
			float X = cx - (textBounds.width() * 0.5f);
			float Y = cy - (textBounds.height() * 0.5f);
			canvas.drawText(text, X - textBounds.left, Y - textBounds.top, textArtObj.getFillPaint());

		} else {
			// in case
			canvas.drawText(text, 10, 10 - textBounds.top, textArtObj.getFillPaint());
		}

		canvas.restore();
	}

	private float adjustTextBoundsRect() {
		if (getWidth() == 0 || getHeight() == 0) { return 1f; }
		Rect textBounds = textArtObj.getTextBounds();
		if (textBounds.height() <= getHeight() && textBounds.width() <= getWidth()) { return 1f; }

		float scale = Math.max((float) textBounds.height() / (float) getHeight(), (float) textBounds.width() / (float) getWidth());
		return scale;
	}

	/*
	 * public Bitmap getTextArtBitmap(int width,int height){ Bitmap
	 * textArtBitmap = Bitmap.createBitmap(width, height,Config.ARGB_8888);
	 * Canvas c = new Canvas(textArtBitmap); c.save();
	 * c.scale(textArtObj.textScaleX,
	 * textArtObj.textScaleY,textArtObj.textX,textArtObj.textY);
	 * c.drawText(textArtObj.text, textArtObj.textX,
	 * textArtObj.textY+textArtObj.textYOffeset, textArtObj.textPaint);
	 * c.drawText(textArtObj.text, textArtObj.textX,
	 * textArtObj.textY+textArtObj.textYOffeset, textArtObj.borderPaint);
	 * c.restore();
	 * 
	 * return textArtBitmap; }
	 */

}
