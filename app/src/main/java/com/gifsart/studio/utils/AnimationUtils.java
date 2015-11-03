package com.gifsart.studio.utils;

import android.view.View;
import android.view.animation.RotateAnimation;

/**
 * Created by Tigran on 10/30/15.
 */
public class AnimationUtils {

    public static void rotateAnimation(View view, float fromDegree, float toDegree) {
        final RotateAnimation rotateAnim = new RotateAnimation(fromDegree, toDegree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(200);
        rotateAnim.setFillAfter(true);
        view.startAnimation(rotateAnim);
    }

}
