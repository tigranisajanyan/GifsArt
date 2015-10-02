package com.gifsart.studio.clipart.view;

import android.content.Context;
import android.graphics.Path;

public abstract class AbstractItem implements Item {
	public float curWidth = 0f;
	public float curHeight = 0f;

	public float centerX = 0;
	public float centerY = 0;

	protected float imageZoom = 1f;
	protected float imageLeft = 0f;
	protected float imageTop = 0f;

	protected float rotateDegree = 0f;

	public void setX(float x) {
		X = x;
	}

	public void setY(float y) {
		Y = y;
	}

	public float X = 10f;
	public float Y = 10f;

	protected float scaleX = 1f;
	protected float scaleY = 1f;

	protected boolean isActive = true;
	protected boolean isDrawHandle = false;

	protected int opacity = 255;


	@Override
	public float getWidth() {
		return curWidth;
	}

	@Override
	public float getHeight() {
		return curHeight;
	}

	@Override
	public float getImageZoom() {
		return imageZoom;
	}

	@Override
	public float getImageLeft() {
		return imageLeft;
	}

	@Override
	public float getImageTop() {
		return imageTop;
	}

	@Override
	public float getRotation() {
		return rotateDegree;
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

	@Override
	public int getOpacity() {
		return opacity;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public void setActive(boolean active) {
		isActive = active;
	}

	@Override
	public boolean isDrawHandle() {
		return isDrawHandle;
	}

	@Override
	public void setDrawHandle(boolean drawHandle) {
		isDrawHandle = drawHandle;
	}

	@Override
	public void clearData() {
	}

	@Override
	public void initSpecStateObjects(Context context) {
	}

	protected class DrawPath {
		public Path path;
		public boolean clear;
		public Transform transform;
		public float brushSize;
		public float brushHardness;

		public DrawPath(Path path, Transform transform, boolean clear, float brushSize, float brushHardness) {
			this.path = path;
			this.transform = transform;
			this.clear = clear;
			this.brushSize = brushSize;
			this.brushHardness = brushHardness;
		}
	}

	protected class Transform {
		public float sx = 1f;
		public float sy = 1f;

		public Transform(float sx, float sy) {
			this.sx = sx;
			this.sy = sy;
		}
	}

}
