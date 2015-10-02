package com.gifsart.studio.clipart.util;

public final class ImageSize {
    public final int width, height;
    public final int maxSize;
    public final int pixels;

    public ImageSize(int width, int height) {
        this.width = width;
        this.height = height;
        maxSize = Math.max(width, height);
        pixels = width * height;
    }

    @Override
    public String toString() {
        return Integer.toString(width) + " x " + height;
    }
}
