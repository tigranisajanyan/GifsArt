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
    public static String FILE_PREFIX = "file://";

    public static String GIFSART_LOG = "gifsart_log";

    public static String GIPHY_URL = "http://api.giphy.com/v1/gifs/search?q=";
    public static String GIPHY_TAG = "funny";
    public static String GIPHY_OFFSET = "&offset=";
    public static String GIPHY_API_KEY = "&api_key=dc6zaTOxFJmzC";
    public static String GIPHY_SIZE_ORIGINAL = "original";
    public static String GIPHY_SIZE_PREVIEW = "fixed_width_small";
    public static String GIPHY_SIZE_DOWNSAMPLED = "fixed_width_downsampled";
    public static String GIPHY_LIMIT = "&limit=";

    public static String GIPHY_STICKER = "http://api.giphy.com/v1/stickers/search?q=";

    public static String INTENT_VIDEO_PATH = "video_path";
    public static String INTENT_VIDEO_FRAME_SCALE_SIZE = "frame_scale_size";
    public static String INTENT_DECODED_IMAGE_PATHS = "image_paths";
    public static String INTENT_ACTIVITY_INDEX = "index";
    public static String INTENT_DECODED_IMAGES_OUTPUT_DIR = "output_dir";
    public static String INTENT_FRONT_CAMERA = "front_camera";
    public static String INTENT_GIF_PATH = "gif_path";
    public static String INTENT_CAMERA_BURST_MODE = "burst_mode";
    public static String INTENT_GIPHY_BYTE_ARRAY = "giphy_byte_array";
    public static String INTENT_IMAGE_BITMAP = "image_bitmap";
    public static String INTENT_SQUARE_FIT_MODE = "square_fit_mode";
    public static String INTENT_EFFECT_FILTER = "filter";

    public static String SHARED_PREFERENCES_IS_OPENED = "is_opened";
    public static String SHARED_PREFERENCES = "shared_preferences";

    public static String SHOOTING_VIDEO_OUTPUT_DIR = Environment.getExternalStorageDirectory() + "/" + MY_DIR;
    public static String VIDEOS_DECODED_FRAMES_DIR = Environment.getExternalStorageDirectory() + "/" + MY_DIR + "/video_frames";

    public static String DIR_VIDEO_FRAMES = "GifsArt/video_frames";
    public static String DIR_GIPHY = "GifsArt/giphy";
    public static String DIR_GPU_IMAGES = "Pictures/GPUImage";


    public static int VIDEO_FRAME_SCALE_SIZE = 2;
    public static int VIDEO_MAX_DURATION = 300000; // Set max duration 30 sec.
    public static int VIDEO_FILE_MAX_SIZE = 40000000; // Set max file size 40M
    public static int VIDEO_OUTPUT_ORIENTATION = 90;

    public static int INDEX_FROM_GALLERY_TO_GIF = 1;
    public static int INDEX_SHOOT_GIF = 2;
    public static int INDEX_GIPHY_TO_GIF = 3;

    public static int FIT_MODE_ORIGINAL = 1;
    public static int FIT_MODE_SQUARE = 2;
    public static int FIT_MODE_SQUARE_FIT = 3;

    public static int REQUEST_CODE_SQUARE_FIT_ACTIVITY = 100;
    public static int REQUEST_CODE_EDIT_FRAME_ACTIVITY = 200;
    public static int REQUEST_CODE_MAIN_ACTIVITY = 300;
    public static int REQUEST_CODE_EFFECTS_ACTIVITY = 400;
    public static int REQUEST_CODE_SHOOTING_GIF_REOPENED = 111;
    public static int REQUEST_CODE_GIPHY_REOPENED = 222;

    public static int GIF_FRAME_SIZE = 500;
    public static int IMAGE_FRAME_DURATION = 200;   // 200 mls

    public static int GIPHY_LIMIT_COUNT = 30;
    public static int GIF_MAX_FRAMES_COUNT = 100;



                /**/

}
