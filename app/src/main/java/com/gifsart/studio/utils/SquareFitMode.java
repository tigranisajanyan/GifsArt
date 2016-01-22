package com.gifsart.studio.utils;

import android.app.Activity;
import android.widget.ImageButton;

import com.gifsart.studio.R;

/**
 * Created by Tigran on 1/19/16.
 */

public enum SquareFitMode {
    FIT_MODE_ORIGINAL,
    FIT_MODE_SQUARE,
    FIT_MODE_SQUARE_FIT;

    public static SquareFitMode fromInt(int val) {
        SquareFitMode[] codes = values();

        if (val < 0 || val >= codes.length) {
            return null;
        } else {
            return values()[val];
        }
    }

    public int toInt() {
        return ordinal();
    }

    public static void switchSquareFitMode(Activity activity,SquareFitMode square_fit_mode) {
        switch (square_fit_mode) {
            case FIT_MODE_ORIGINAL:
                ((ImageButton) activity.findViewById(R.id.original_fit_button)).setImageDrawable(Utils.changeDrawableColor(activity, activity.getResources().getColor(R.color.pink), R.drawable.origina_size_icon));
                ((ImageButton) activity.findViewById(R.id.square_button)).setImageDrawable(Utils.changeDrawableColor(activity, activity.getResources().getColor(R.color.font_main_color), R.drawable.square_icon));
                ((ImageButton) activity.findViewById(R.id.square_fit_button_1)).setImageDrawable(Utils.changeDrawableColor(activity, activity.getResources().getColor(R.color.font_main_color), R.drawable.square_fit_icon));
                break;
            case FIT_MODE_SQUARE:
                ((ImageButton) activity.findViewById(R.id.original_fit_button)).setImageDrawable(Utils.changeDrawableColor(activity, activity.getResources().getColor(R.color.font_main_color), R.drawable.origina_size_icon));
                ((ImageButton) activity.findViewById(R.id.square_button)).setImageDrawable(Utils.changeDrawableColor(activity, activity.getResources().getColor(R.color.pink), R.drawable.square_icon));
                ((ImageButton) activity.findViewById(R.id.square_fit_button_1)).setImageDrawable(Utils.changeDrawableColor(activity, activity.getResources().getColor(R.color.font_main_color), R.drawable.square_fit_icon));
                break;
            case FIT_MODE_SQUARE_FIT:
                ((ImageButton) activity.findViewById(R.id.original_fit_button)).setImageDrawable(Utils.changeDrawableColor(activity, activity.getResources().getColor(R.color.font_main_color), R.drawable.origina_size_icon));
                ((ImageButton) activity.findViewById(R.id.square_button)).setImageDrawable(Utils.changeDrawableColor(activity, activity.getResources().getColor(R.color.font_main_color), R.drawable.square_icon));
                ((ImageButton) activity.findViewById(R.id.square_fit_button_1)).setImageDrawable(Utils.changeDrawableColor(activity, activity.getResources().getColor(R.color.pink), R.drawable.square_fit_icon));
                break;
        }
    }
}