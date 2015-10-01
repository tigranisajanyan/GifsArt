package com.gifsart.studio.utils;

/**
 * Created by Tigran on 9/30/15.
 */
public interface DrawableClickListener {

    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}