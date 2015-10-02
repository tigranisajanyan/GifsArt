package com.gifsart.studio.clipart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Parcelable;

public interface Item extends Parcelable {
	
	public static final int CLIPART = 0;
	public static final int TEXTART = 1;
	public static final int CALLOUT = 2;
	public static final int LENS_FLARE = 3;
	
	 int getType();
	 
	 float getWidth();
	 
	 float getHeight();
	 
	 float getImageZoom();
	 
	 float getImageLeft();
	 
	 float getImageTop();
	 
	 float getRotation();
	 
	 float getX();
	 
     float getY();
	 
     float getScaleX();
	 
     float getScaleY();
     
     int getOpacity();
     
     void setOpacity(int progress);
	 
	 boolean isInItem(float x, float y);
	 
	 boolean isActive();
	 
	 void setActive(boolean active);
	 
     boolean isDrawHandle();
	 
	 void setDrawHandle(boolean drawHandle);
	 
	 void clearData();
	
	 void draw(Canvas c);
	 
	 void draw(Canvas c, float left, float top, float scaleFactor, float imageZoom, float rotate);

	 boolean touch_down(float x, float y);

	 void touch_move(float x, float y);

	 void touch_up();
	 
	 void activate(float left, float top, float imageZoom);
	 
	 //on restore state
	 void initSpecStateObjects(Context context);

    public interface Recyclable {
        public void cleanBitmaps();
    }

}



