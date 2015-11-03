package com.gifsart.studio.item;

import android.graphics.Bitmap;

/**
 * Created by Tigran on 10/29/15.
 */
public class GalleryCategoryItem {

    private String filePath;
    private String categoryTitle;
    private int categoryItemsCount = 0;
    private boolean isSelected = false;

    public GalleryCategoryItem() {

    }

    public GalleryCategoryItem(String filePath, String categoryTitle, int categoryItemsCount, boolean isSelected) {
        this.filePath = filePath;
        this.categoryTitle = categoryTitle;
        this.categoryItemsCount = categoryItemsCount;
        this.isSelected = isSelected;
    }

    public String getCategoryCover() {
        return filePath;
    }

    public void setCategoryCover(String filePath) {
        this.filePath = filePath;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public int getCategoryItemsCount() {
        return categoryItemsCount;
    }

    public void setCategoryItemsCount(int categoryItemsCount) {
        this.categoryItemsCount = categoryItemsCount;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


}
