package com.gifsart.studio.utils;

import android.os.Environment;

/**
 * Created by Tigran on 8/25/15.
 */
public class GifsArtConst {

    public static String SLASH = "/";
    public static String MY_DIR = "GifsArt";
    public static String GIF_NAME = "test.gif";
    public static String VIDEO_NAME = "myvideo.mp4";
    public static String VIDEO_TYPE = "video/*";
    public static String IMAGE_PATHS = "image_paths";
    public static String INDEX = "index";
    public static String FILE_PREFIX = "file://";
    public static String VIDEO_PATH = "video_path";
    public static String GIFSART_LOG = "gifsart_log";
    public static String FRONT_CAMERA = "front_camera";

    public static String GIPHY_URL = "http://api.giphy.com/v1/gifs/search?q=";
    public static String GIPHY_TAG = "funny";
    public static String GIPHY_OFFSET = "&offset=";
    public static String GIPHY_API_KEY = "&api_key=dc6zaTOxFJmzC";
    public static String GIPHY_SIZE_ORIGINAL = "original";
    public static String GIPHY_SIZE_PREVIEW = "fixed_width_small";
    public static String GIPHY_SIZE_DOWNSAMPLED = "fixed_width_downsampled";
    public static String GIPHY_LIMIT = "&limit=";

    public static int VIDEO_MAX_DURATION = 300000; // Set max duration 30 sec.
    public static int VIDEO_FILE_MAX_SIZE = 40000000; // Set max file size 40M
    public static int CAMERA_OUTPUT_ORIENTATION = 90;
    public static int VIDEO_MAX_SECONDS = 30;
    public static int FRAME_SIZE = 500;
    public static int FROM_GALLERY_TO_GIF_INDEX = 1;
    public static int SHOOT_GIF_INDEX = 2;
    public static int GIPHY_TO_GIF_INDEX = 3;
    public static int GENERATED_FRAMES_MAX_COUNT = 25;




            /*GifEncoder gifEncoder = new GifEncoder();
            gifEncoder.init(root + "/test_images/test.gif", 640, 640, 256, 100, 100);
            int[] pixels = new int[640 * 640];
            for (int i = 0; i < bitmaps.size(); i++) {
                bitmaps.get(i).getPixels(pixels, 0, 640, 0, 0, 640, 640);
                gifEncoder.addFrame(pixels);
            }
            gifEncoder.close();*/


}
