package com.gifsart.studio.utils;

import android.util.Log;

import com.gifsart.studio.gifutils.GifUtils;

/**
 * Created by Tigran on 10/21/15.
 */
public class CheckFreeSpaceSingleton {

    private static CheckFreeSpaceSingleton mInstance = null;
    private int allocatedSpace;
    private int GIF_MAX_FRAMES_COUNT = 30;

    private CheckFreeSpaceSingleton() {
        allocatedSpace = 0;
        checkDeviceMemoryCategory();
    }

    public static CheckFreeSpaceSingleton getInstance() {

        if (mInstance == null) {
            mInstance = new CheckFreeSpaceSingleton();
        }
        return mInstance;
    }

    public int getAllocatedSpace() {
        return allocatedSpace;
    }

    public void setAllocatedSpace(int allocatedSpace) {
        this.allocatedSpace = allocatedSpace;
    }

    public void addAllocatedSpaceInt(int allocatedSpace) {
        this.allocatedSpace += allocatedSpace;
    }

    public void addAllocatedSpaceFromFilePath(String filePath) {
        if (Utils.getMimeType(filePath) == Type.IMAGE) {
            this.allocatedSpace += 1;
        } else if (Utils.getMimeType(filePath) == Type.GIF) {
            allocatedSpace += GifUtils.getGifFramesCount(filePath);

        } else if (Utils.getMimeType(filePath) == Type.VIDEO) {
            this.allocatedSpace += (Utils.checkVideoFrameDuration(filePath, 25) * 2 / 3);

        }
    }

    public void clearAllocatedSpace() {
        this.allocatedSpace = 0;
    }

    public void deleteAllocatedSpace(String filePath) {
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
        int freeSpace = GIF_MAX_FRAMES_COUNT - allocatedSpace;
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

    public boolean haveEnoughSpaceInt(int allocatedSpace) {
        int freeSpace = GIF_MAX_FRAMES_COUNT - this.allocatedSpace;
        if (freeSpace > allocatedSpace) {
            return true;
        } else {
            return false;
        }
    }

    public int getFreeSpaceSize() {
        return GIF_MAX_FRAMES_COUNT - allocatedSpace;
    }

    private void checkDeviceMemoryCategory() {
        long memorySize = Utils.getTotalRAMinMB();
        // devices with RAM from 899MB to 1500MB
        if (memorySize > 899 && memorySize < 1500) {
            GIF_MAX_FRAMES_COUNT = 80;
        }// devices with RAM from 1500MB to 2500MB
        else if (memorySize >= 1500 && memorySize < 2500) {
            GIF_MAX_FRAMES_COUNT = 110;
        }// devices with RAM from 2500MB and higher
        else if (memorySize >= 2500) {
            GIF_MAX_FRAMES_COUNT = 150;
        }// devices with RAM from 899MB and lower
        else {
            GIF_MAX_FRAMES_COUNT = 30;
        }
    }

}
