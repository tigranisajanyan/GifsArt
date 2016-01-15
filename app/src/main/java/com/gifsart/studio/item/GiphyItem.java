package com.gifsart.studio.item;

/**
 * Created by Tigran on 9/8/15.
 */
public class GiphyItem {

    private String downsampledGifUrl;
    private String originalGifUrl;
    private int gifWidth;
    private int gifHeight;
    private int framesCount;

    private int byteBufferSize;

    private boolean isSelected;

    public GiphyItem() {

    }

    public GiphyItem(String downsampledGifUrl, int gifWidth, int gifHeight, String originalGifUrl) {
        this.downsampledGifUrl = downsampledGifUrl;
        this.gifWidth = gifWidth;
        this.gifHeight = gifHeight;
        this.originalGifUrl = originalGifUrl;
    }

    public String getDownsampledGifUrl() {
        return downsampledGifUrl;
    }

    public void setDownsampledGifUrl(String downsampledGifUrl) {
        this.downsampledGifUrl = downsampledGifUrl;
    }

    public int getGifWidth() {
        return gifWidth;
    }

    public void setGifWidth(int gifWidth) {
        this.gifWidth = gifWidth;
    }

    public int getGifHeight() {
        return gifHeight;
    }

    public void setGifHeight(int gifHeight) {
        this.gifHeight = gifHeight;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getOriginalGifUrl() {
        return originalGifUrl;
    }

    public void setOriginalGifUrl(String originalGifUrl) {
        this.originalGifUrl = originalGifUrl;
    }

    public int getFramesCount() {
        return framesCount;
    }

    public void setFramesCount(int framesCount) {
        this.framesCount = framesCount;
    }

    public int getByteBufferSize() {
        return byteBufferSize;
    }

    public void setByteBufferSize(int byteBufferSize) {
        this.byteBufferSize = byteBufferSize;
    }

}
