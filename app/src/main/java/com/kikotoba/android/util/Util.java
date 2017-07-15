package com.kikotoba.android.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.io.File;

/**
 * Created by raix on 2017/03/12.
 */

public class Util {

    public static String getSdPath(Context context, String path) {
        File sd = IOUtil.getPrivateExternalDir(context, "");
        return "file://" + sd.getPath() + path;
    }

    /**
     * urlからjavascriptに文字列を渡す際に必要なエスケープ処理
     * String.format("javascript: alert('%s')", text)
     * @param text
     * @return エスケープされた文字
     */
    public static String escapeJsArgumentFromUrl(String text) {
        return text
                .replace("'", "\\'") // 'をエスケープ
                .replace("\"", "\\\""); // "をエスケープ
    }

    public static Animator startAnimationAttention(View view, long delayMsec) {
        long duration1 = 300;
        long duration2 = 200;

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 0.6f);
        animatorX.setDuration(duration1)
                .setInterpolator(new DecelerateInterpolator());
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 0.6f);
        animatorY.setDuration(duration1)
                .setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animatorX2 = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        animatorX2.setDuration(duration2)
                .setInterpolator(new OvershootInterpolator());
        ObjectAnimator animatorY2 = ObjectAnimator.ofFloat(view, "scaleY", 1f);
        animatorY2.setDuration(duration2)
                .setInterpolator(new OvershootInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.play(animatorX).with(animatorY);
        set.play(animatorX2).with(animatorY2).after(animatorX);
        set.setStartDelay(delayMsec);
        set.start();
        return set;
    }

    public static Animation createAnimationFlashing() {
        Animation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(300);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(1);
        return animation;
    }

    public static String fbIindex(int index) {
        return String.format("_%03d", index);
    }
}
