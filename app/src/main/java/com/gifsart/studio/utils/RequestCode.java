package com.gifsart.studio.utils;

/**
 *
 * For each layout separate request code
 *
 * Created by Tigran on 1/19/16.
 */
public enum RequestCode {

    SELECT_SQUARE_FIT,
    SELECT_EFFECTS,
    SELECT_CLIPART,
    SELECT_MASKS,
    SELECT_TEXT,
    EDIT_FRAME;

    public static RequestCode fromInt(int val) {
        RequestCode[] codes = values();

        if (val < 0 || val >= codes.length) {
            return null;
        } else {
            return values()[val];
        }
    }

    public int toInt() {
        return ordinal();
    }
}