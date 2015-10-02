package com.gifsart.studio.textart;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;

import java.io.Serializable;

public class TextArtStyle implements Parcelable, Serializable{
	
	private static final long serialVersionUID = -5320926717807014541L;
	
	public static final int STROKE_WIDTH = 1;

	private static final int DEFAULT_FONT_SIZE = 30;
	private static final int DEFAULT_FILL_COLOR = Color.WHITE;

	
	private int fontSize;
	private boolean hasGradient;

	private int fillColor;
	private int fillGradientBottomColor;

	private boolean hasStroke = false;
	private int strokeColor;
	private int alignment = Gravity.LEFT | Gravity.CENTER_VERTICAL;
	private int textHeight;

	public TextArtStyle(Parcel in) {
		readFromParcel(in);
	}

	public TextArtStyle() {
        reset();
	}

	public void reset() {
		fontSize = DEFAULT_FONT_SIZE;
		hasGradient = false;
		
		fillColor = DEFAULT_FILL_COLOR;
		fillGradientBottomColor = 0;

		hasStroke = false;
		strokeColor = 0;
	}
	
	public final void setTextArtStyle(TextArtStyle style) {
		
		fontSize = style.fontSize;
		hasGradient = style.hasGradient;

		fillColor = style.fillColor;
		fillGradientBottomColor = style.fillGradientBottomColor;
		
		hasStroke = style.hasStroke;
		strokeColor = style.strokeColor;
		alignment = style.alignment;
		textHeight = style.textHeight;
	}


	public boolean hasStroke() {
		return hasStroke;
	}

	public void setHasStroke(boolean hasStroke) {
		this.hasStroke = hasStroke;
	}

	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	public int getTextHeight() {
		return textHeight;
	}

	public void setTextHeight(int textHeight) {
		this.textHeight = textHeight;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public boolean hasGradient() {
		return hasGradient;
	}

	public void setHasGradient(boolean hasGradient) {
		this.hasGradient = hasGradient;
	}

	public int getFillColor() {
		return fillColor;
	}

	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

	public int getFillGradientBottomColor() {
		return fillGradientBottomColor;
	}

	public void setFillGradientBottomColor(int fillGradientBottomColor) {
		this.fillGradientBottomColor = fillGradientBottomColor;
	}

	public int getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
	}

	public static TextArtStyle getPreviewStyleObj(boolean fontFromSd, String fontPath) {
		TextArtStyle previewStyle = new TextArtStyle();

		return previewStyle;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(fontSize);
		int isHasGradient = hasGradient ? 1 : 0;
		dest.writeInt(isHasGradient);
		dest.writeInt(fillColor);
		dest.writeInt(fillGradientBottomColor);
		int isHasStroke = hasStroke ? 1 : 0;
		dest.writeInt(isHasStroke);
		dest.writeInt(strokeColor);
        dest.writeInt(alignment);
		dest.writeInt(textHeight);
	}

	private void readFromParcel(Parcel in) {
		
		fontSize = in.readInt();
		int isHasGradient = in.readInt();
		hasGradient = isHasGradient == 1;
		fillColor = in.readInt();
		fillGradientBottomColor = in.readInt();
		int isHasStroke = in.readInt();
		hasStroke = isHasStroke == 1;
		strokeColor = in.readInt();
        alignment = in.readInt();
		textHeight = in.readInt();
	}

	public static final Parcelable.Creator<TextArtStyle> CREATOR = new Parcelable.Creator<TextArtStyle>() {
		public TextArtStyle createFromParcel(Parcel in) {
			return new TextArtStyle(in);
		}

		public TextArtStyle[] newArray(int size) {
			return new TextArtStyle[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fillColor;
		result = prime * result + fillGradientBottomColor;
		result = prime * result + fontSize;
		result = prime * result + (hasGradient ? 1231 : 1237);
		result = prime * result + (hasStroke ? 1231 : 1237);
		result = prime * result + strokeColor;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof TextArtStyle)) return false;
		TextArtStyle other = (TextArtStyle) obj;
		if (fillColor != other.fillColor) return false;
		if (fillGradientBottomColor != other.fillGradientBottomColor) return false;
		if (fontSize != other.fontSize) return false;
		if (hasGradient != other.hasGradient) return false;
		if (hasStroke != other.hasStroke) return false;
		if (strokeColor != other.strokeColor) return false;

		return true;
	}

}
