package com.gifsart.studio.item;

/**
 * Created by Tigran on 10/30/15.
 */
public class ShareGifItem {

    private int resourceId;
    private String itemTitle;

    public ShareGifItem(int resourceId, String itemTitle) {
        this.resourceId = resourceId;
        this.itemTitle = itemTitle;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }


}
