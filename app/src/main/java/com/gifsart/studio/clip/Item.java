package com.gifsart.studio.clip;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public abstract class Item implements Parcelable {
	public static final int DEFAULT_COLOR = Color.WHITE;
	public static final boolean DEFAULT_VISIBLE = true;

	protected boolean visible;
	protected int blendingMode;
	protected int color;

	private OnChangeListener onChangeListener;
	protected ItemActionsListener itemActionsListener;

	protected Item() {
		visible = DEFAULT_VISIBLE;
		setColor(DEFAULT_COLOR);
	}

	protected Item(Parcel source) {
		visible = source.readInt() == 1;
		setBlendingMode(source.readInt());
        setColor(source.readInt());
	}

	public final void setOnChangeListener(OnChangeListener listener) {
		this.onChangeListener = listener;
	}

	public void setItemActionsListener(ItemActionsListener itemActionsListener) {
		this.itemActionsListener = itemActionsListener;
	}

	public final boolean isVisible() {
		return visible;
	}

	public final void setVisible(boolean visible) {
		this.visible = visible;
		notifyAboutItemChange();
	}

	public final void setColor(int color) {
		this.color = color;

		notifyAboutItemChange();
	}

	public final int getColor() {
		return color;
	}

	public final void setRGB(int rgb) {
		setColor(getOpacity() << 24 | (rgb & 0x00FFFFFF));
	}

	/**
	 * @return the color of the item with alpha equal to 255
	 */

	public final int getOpaqueColor() {
		return getColor() | 0xFF000000;
	}

	public final void setOpacity(int opacity) {
		int r = Color.red(getColor());
		int g = Color.green(getColor());
		int b = Color.blue(getColor());

		setColor(Color.argb(opacity, r, g, b));
	}

	public final int getOpacity() {
		return Color.alpha(color);
	}
	public final int getOpacityRangedTo100() {
		return (int)(Color.alpha(color)/255f*100);
	}

	public final void setBlendingMode(int mode) {
		blendingMode = mode;
		notifyAboutItemChange();
	}

	public final int getBlendingMode() {
		return blendingMode;
	}

	public abstract boolean supportsColor();
	public abstract boolean supportsBlendingMode();
	public abstract List<Integer> getSupportedBlendingModes();

	public abstract void draw(Canvas paperCanvas);

	public final void notifyAboutItemChange() {
		if (onChangeListener != null) onChangeListener.onChanged(this);
	}

	public interface OnChangeListener {
		void onChanged(Item item);
	}

	public interface ItemActionsListener {
		void onEdit(Item item);
	}

	public interface DoubleTapEditable {
		void edit();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(visible ? 1 : 0);
		dest.writeInt(blendingMode);
        dest.writeInt(color);
	}
}