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
            this.allocatedSpace += (Utils.checkVideoFrameDuration(filePath, 25));

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
            int videoFrameCount = (Utils.checkVideoFrameDuration(filePath, 25));
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
            int videoFrameCount = (Utils.checkVideoFrameDuration(filePath, 25));
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

    /**
     *  Checking app heap memory size and choosing which memory type it should be
     */
    private void checkDeviceMemoryCategory() {
        int memorySize = PicsartContext.memoryType.getSize();
        // app heap is from 64Mb to 128Mb
        if (memorySize >= PicsartContext.MemoryType.NORMAL_PLUS.getSize() && memorySize < PicsartContext.MemoryType.HIGH.getSize()) {
            GIF_MAX_FRAMES_COUNT = 50;
        }// app heap is from 128Mb to 256Mb
        else if (memorySize >= PicsartContext.MemoryType.HIGH.getSize() && memorySize < PicsartContext.MemoryType.XHIGH.getSize()) {
            GIF_MAX_FRAMES_COUNT = 80;
        }// app heap is from 256Mb to 512Mb
        else if (memorySize >= PicsartContext.MemoryType.XHIGH.getSize() && memorySize < PicsartContext.MemoryType.XXHIGH.getSize()) {
            GIF_MAX_FRAMES_COUNT = 100;
        }// app heap is from 512Mb and higher
        else if (memorySize >= PicsartContext.MemoryType.XXHIGH.getSize()) {
            GIF_MAX_FRAMES_COUNT = 150;
        }// app heap is lower than 64Mb
        else {
            GIF_MAX_FRAMES_COUNT = 30;
        }
    }

}
