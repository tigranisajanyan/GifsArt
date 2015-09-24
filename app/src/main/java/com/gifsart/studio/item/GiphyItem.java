package com.gifsart.studio.item;

/**
 * Created by Tigran on 9/8/15.
 */
public class GiphyItem {

    private String gifUrl;
    private int gifWidth;
    private int gifHeight;
    private boolean isSelected;

    public GiphyItem() {

    }

    public GiphyItem(String gifUrl, int gifWidth, int gifHeight) {
        this.gifUrl = gifUrl;
        this.gifWidth = gifWidth;
        this.gifHeight = gifHeight;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
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

}
