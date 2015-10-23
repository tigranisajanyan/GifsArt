package com.gifsart.studio.utils;

import android.util.Log;

import com.gifsart.studio.gifutils.GifUtils;

/**
 * Created by Tigran on 10/21/15.
 */
public class CheckSpaceSingleton {

    private static CheckSpaceSingleton mInstance = null;
    private int allocatedSpace;

    private CheckSpaceSingleton() {
        allocatedSpace = 0;
    }

    public static CheckSpaceSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new CheckSpaceSingleton();
        }
        return mInstance;
    }

    public int getAllocatedSpace() {
        return allocatedSpace;
    }

    public void setAllocatedSpace(int allocatedSpace) {
        this.allocatedSpace += allocatedSpace;
    }

    public void removeAllSpace() {
        this.allocatedSpace = 0;
    }

    public void addAllocatedSpace(String filePath) {
        if (Utils.getMimeType(filePath) == Type.IMAGE) {
            this.allocatedSpace += 1;
        } else if (Utils.getMimeType(filePath) == Type.GIF) {
            allocatedSpace += GifUtils.getGifFramesCount(filePath);

        } else if (Utils.getMimeType(filePath) == Type.VIDEO) {
            this.allocatedSpace += (Utils.checkVideoFrameDuration(filePath, 25) * 2 / 3);

        }
    }

    public void removeAllocatedSpace(String filePath) {
        if (Utils.getMimeType(filePath) == Type.IMAGE && allocatedSpace != 0) {
            this.allocatedSpace -= 1;
        } else if (Utils.getMimeType(filePath) == Type.GIF && allocatedSpace != 0) {
            int gifFrameCount = GifUtils.getGifFramesCount(filePath);
            this.allocatedSpace -= gifFrameCount;

        } else if (Utils.getMimeType(filePath) == Type.VIDEO && allocatedSpace != 0) {
            int videoFrameCount = (Utils.checkVideoFrameDuration(filePath, 25) * 2 / 3);
            this.allocatedSpace -= videoFrameCount;
        }
    }

    public boolean haveEnoughSpace(String filePath) {
        int freeSpace = GifsArtConst.GIF_MAX_FRAMES_COUNT - allocatedSpace;
        Log.d("gagaaa", allocatedSpace + "");
        if (Utils.getMimeType(filePath) == Type.IMAGE) {
            if (freeSpace > 1) {
                return true;
            }
        } else if (Utils.getMimeType(filePath) == Type.GIF) {
            int gifFrameCount = GifUtils.getGifFramesCount(filePath);
            if (freeSpace - 5 > gifFrameCount) {
                return true;
            }
        } else if (Utils.getMimeType(filePath) == Type.VIDEO) {
            int videoFrameCount = (Utils.checkVideoFrameDuration(filePath, 25) * 2 / 3);
            if (freeSpace - 5 > videoFrameCount) {
                return true;
            }
        }
        return false;
    }

}
