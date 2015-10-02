package com.gifsart.studio.clipart;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by ani on 7/24/15.
 */
public class Clipart {

    private Bitmap bitmap;
    private int x = 0;
    private int y = 0;
    private float scaleX = 1f;
    private float scaleY = 1f;
	private float rotation = 0f;

    public Clipart() {

    }

    public Clipart(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getRotation() {
		return rotation;
	}


    public void draw(Canvas canvas, int index){

    }


}

