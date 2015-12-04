package com.gifsart.studio.social;

import android.text.TextUtils;

public interface FacebookConstants {

    public static final String FB_GRAPH_SECURE_URL = "https://graph.facebook.com/v2.4/";
    public static final String FB_STAGING_URL = FB_GRAPH_SECURE_URL + "me/staging_resources";
    public static final String FB_STAGIND_KEY = "fbstaging:";
    public static final int AUTH_ACTIVITY_CODE = 135;

    public static final String KEY_GRAPH_PATH = "graph.path";
    public static final String KEY_GRAPH_ENDPOINT_ID = "graph.endpoint";
    public static final String KEY_FB_FRIENDS_MULTICHECK_MODE = "friend.multi";
    public static final String FB_ALBUM_GRAPH_PATH = "/albums";
    public static final String FB_PHOTO_GRAPH_PATH = "/photos";
    public static final String FB_FRIENDS_GRAPH_PATH = "/friends";
    public static final String ME_ENDPOINT = "me/";
    public static final String FB_MY_PAGES_PATH = "me/accounts";
    public static final String FB_TIMELINE_PATH = "me/feed";

    String FB_APP_POST_MSG = "post";


    public static final int REQUEST_POST_TO_FB_WALL = 49;
    public static final int REQUEST_LOGIN_TO_PICSIN_GALLERY = 59;
    public static final int REQUEST_FB_PERMISSIONS = 60;

    public static final int RESULT_FACEBOOK_LOGOUT_FINISH = 129;
    public static final int RESULT_FB_LOGIN_SUCCESS = 131;

    public static final String KEY_METHOD = "method";
    public static final String KEY_APP_ID = "fbAppId";
    public static final String KEY_TOKEN = "fbToken";
    public static final String KEY_SAVED_USER_ID = "savedUserId";
    public static final String KEY_PATH = "path";

    public static final String KEY_POST_MESSAGE = "postMessage";

    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_NAME = "userScreenName";
    public static final String KEY_USER_DISPLAY_NAME = "userDisplayName";
    public static final String KEY_USER_PROFILE_IMG_URL = "userProfileImgUrl";
    public static final String KEY_ACCESS_TOKEN = "accessToken";
    public static final String ACCESS_TOKEN_KEY = "access_token";

    public static final String KEY_FREE_TO_EDIT = "freeToEdit";
    public static final String KEY_GROUP_ID = "fbGroupId";
    public static final String KEY_GROUP_NAME = "fbGroupName";
    public static final String KEY_PAGE_ID = "fbPageId";
    public static final String KEY_PAGE_NAME = "fbPageName";
    public static final String KEY_VIDEO_POST = "videoPost";
    public static final String METHOD_MAIN = "main";
    public static final String METHOD_CHECK_SESSION = "fbSessionCheck";
    public static final String METHOD_ASK_FOR_POST_PERMISSIONS = "fbAskPostPermissions";
    public static final String METHOD_ASK_FOR_PAGE_PERMISSIONS = "fbAskPagePermissions";
    public static final String METHOD_ASK_FOR_WRITE_PERMISSIONS = "fbAskWritePermissions";

    public static final String TYPE_ACCOUNTS_FRAGMENT = "accountsFragment";
    public static final String TYPE_PROGRESS_DIALOG_FRAGMENT = "progressDialogFragment";

    // --------- Permissions
    public static final String PUBLISH_ACTIONS = "publish_actions";

    public static final String USER_PHOTOS = "user_photos";
    public static final String USER_FRIENDS = "user_friends";
    public static final String EMAIL = "email";

    public static final String USER_GROUPS = "user_groups";
    public static final String MANAGE_PAGES = "manage_pages";
    public static final String PUBLIC_PROFILE = "public_profile";
    public static final String PUBLISH_PAGES = "publish_pages";

    public static final String[] BASIC_READ_PERMISSIONS = new String[]{PUBLIC_PROFILE, EMAIL, USER_FRIENDS};


    public static final String F_NAME = "name";
    public static final String F_FIRST_NAME = "first_name";
    public static final String F_ID = "id";
    public static final String F_PICTURE = "picture";
    public static final String F_COVER = "cover";
    public static final String F_COVER_PHOTO = "cover_photo";
    public static final String F_SOURCE = "source";
    public static final String F_PHOTO_COUNT = "count";
    public static final String F_EMAIL = "email";
    public static final String F_GENDER = "gender";
    public static final String F_LINK = "link";


    public static final String REQUEST_FIELDS =
            TextUtils.join(",", new String[]{F_PICTURE, F_EMAIL, F_GENDER, F_FIRST_NAME, F_NAME, F_ID, F_COVER, F_LINK});


    //"picture, email, gender, first_name, name, link, timezone, cover"
    public static final String FIELDS_KEY = "fields";

    public static final String PERMISSION_GRANTED = "granted";
    public static final String PERMISSION_DECLINED = "declined";

    public static final String CONFIRM_FRAGMENT_TAG = "permission_grant";

    // Open Graph
    public static final int OG_IMAGE_MAX_WIDTH = 600;
    public static final int OG_IMAGE_MAX_HEIGHT = 315;

    public static final String SMALL_IMAGE_SIZE_PREFIX = "?r240x240";

    public static final String OG_NAMESPACE = "picsartphotostudio";

    public static final String OG_ACTION_ADD = "add";
    public static final String OG_ACTION_COMMENT = "comment";
    public static final String OG_ACTION_LIKE = "like";
    public static final String OG_NATIVE_LIKE_ACTION = "og.likes";
    public static final String OG_ACTION_FOLLOW = "follow";
    public static final String OG_ACTION_BUILDIN_FOLLOW = "og.follows";
    public static final String OG_ACTION_SHARE = "share";
    public static final String OG_ACTION_VOTE = "vote";
    public static final String OG_ACTION_CHANGE = "change";

    public static final String OG_OBJECT_PHOTO = "photo";
    public static final String OG_OBJECT_PROFILE = "profile";
    public static final String OG_OBJECT_WEBSITE = "website";
    public static final String FB_MAIN_PAGE_APP_LINK = "main_page";
    public static final String FB_PHOTO_APP_LINK_HOST = "gallery_item";
    public static final String FB_APP_LINK_TARGET_KEY = "target_url";

    public static final String SESSION_DATA_PREFS_NAME = "fb.picsartprefs";
    public static final String SESSION_USER_CONNECTED = "fb.userconnected";
    public static final String SESSION_SINGLE_SIGN_ON = "fb.ssomode";
    public static final String SESSION_USER_DATA = "fb.userdata";

    // Action Preferences
    public static final String ALL_CHECKBOX_NAME = "enable_fb_action_all";
    public static final String ADD_CHECKBOX_NAME = "enable_fb_action_add";
    public static final String LIKE_CHECKBOX_NAME = "enable_fb_action_like";
    public static final String COMMENT_CHECKBOX_NAME = "enable_fb_action_comment";
    public static final String FOLLOW_CHECKBOX_NAME = "enable_fb_action_follow";
    public static final String VOTE_CHECKBOX_NAME = "enable_fb_action_vote";
    public static final String SHOW_IN_TIMELINE_NAME = "enable_fb_explicit";
}