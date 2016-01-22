package com.gifsart.studio.item;

import com.gifsart.studio.social.ShareContraller;

/**
 * Created by Tigran on 10/30/15.
 */
public class ShareGifItem {

    private int resourceId;
    private int colorId;
    private ShareContraller.ShareGifType shareGifType;

    public ShareGifItem(int resourceId, int colorId, ShareContraller.ShareGifType shareGifType) {
        this.resourceId = resourceId;
        this.colorId = colorId;
        this.shareGifType = shareGifType;
    }

    public ShareContraller.ShareGifType getItemType() {
        return shareGifType;
    }

    public void setItemType(ShareContraller.ShareGifType shareGifType) {
        this.shareGifType = shareGifType;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }


}
