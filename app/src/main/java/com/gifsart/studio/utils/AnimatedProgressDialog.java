package com.gifsart.studio.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

/**
 * Created by Tigran on 11/6/15.
 */
public class AnimatedProgressDialog extends Dialog {

    private View rect1, rect2, rect3, rect4, rect5;
    private RelativeLayout progressLayout;
    private int count = 0;

    public AnimatedProgressDialog(Context context) {
        super(context);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressLayout = new RelativeLayout(context);
        int inPixelss = (int) Utils.dpToPixel(150, getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(inPixelss, inPixelss);
        progressLayout.setLayoutParams(layoutParams);
        progressLayout.setBackgroundColor(Color.WHITE);
        progressLayout.setGravity(Gravity.CENTER);

        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setStroke(3, Color.GRAY);
        gd.setColor(Color.WHITE);
        gd.setCornerRadius(15.0f);

        progressLayout.setBackground(gd);
        setContentView(progressLayout, layoutParams);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        rect1 = new View(context);
        rect2 = new View(context);
        rect3 = new View(context);
        rect4 = new View(context);
        rect5 = new View(context);

        addRectangleToProgressLayout(rect1, Color.parseColor("#191919"));
        addRectangleToProgressLayout(rect2, Color.parseColor("#FF8C00"));
        addRectangleToProgressLayout(rect3, Color.parseColor("#FF5800"));
        addRectangleToProgressLayout(rect4, Color.parseColor("#ff3232"));
        addRectangleToProgressLayout(rect5, Color.parseColor("#0198E1"));

        reapeatAnim();
    }

    public View addRectangleToProgressLayout(View rect, int color) {

        int inPixels = (int) Utils.dpToPixel(30, getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(inPixels, inPixels);

        layoutParams.setMargins(inPixels, inPixels, 0, 0);

        rect.setLayoutParams(layoutParams);
        rect.setBackgroundColor(color);

        progressLayout.addView(rect);

        return rect;
    }

    public void startAnimationSet(final View rlayout, int startoffset, int dur, float toDegress) {

        Animation fadeIn = new AlphaAnimation(0.0f, 1);
        fadeIn.setDuration(dur);
        fadeIn.setRepeatCount(0);

        Animation fadeOut = new AlphaAnimation(1, 0.5f);
        fadeOut.setRepeatCount(0);
        fadeOut.setDuration(dur);

        RotateAnimation rotate = new RotateAnimation(0.0f, toDegress, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);

        AnimationSet animation = new AnimationSet(true);
        animation.setStartOffset(startoffset);
        animation.addAnimation(fadeOut);
        animation.addAnimation(fadeIn);
        animation.addAnimation(rotate);

        rlayout.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                count++;
                if (count == 4) {
                    setInvisibility();
                    reapeatAnim();
                    count = 0;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void reapeatAnim() {
        int dur = 320;
        setInvisibility();
        startAnimationSet(rect1, 0, dur, 45f);
        startAnimationSet(rect2, dur / 4, dur, 0.0f);
        startAnimationSet(rect3, dur / 3, dur, -45f);
        startAnimationSet(rect4, dur / 2, dur, -90f);
        startAnimationSet(rect5, dur, dur, 225f);
    }

    public void setInvisibility() {
        rect1.setVisibility(View.INVISIBLE);
        rect2.setVisibility(View.INVISIBLE);
        rect3.setVisibility(View.INVISIBLE);
        rect4.setVisibility(View.INVISIBLE);
        rect5.setVisibility(View.INVISIBLE);
    }

}