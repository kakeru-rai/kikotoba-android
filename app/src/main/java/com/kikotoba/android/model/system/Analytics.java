package com.kikotoba.android.model.system;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * 分析用
 */
public class Analytics {

    private Context context;

    public Analytics(Context context) {
        this.context = context;
    }

    public void sendDictation(String articleId, int partIndex, int level) {
        String feature = "dictation";

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, articleId);
        bundle.putInt(FirebaseAnalytics.Param.CHARACTER, partIndex);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, feature);
        bundle.putInt(FirebaseAnalytics.Param.LEVEL, level);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

        // パラメータが見れない対策
        // 記事
        firebaseAnalytics.logEvent(
                String.format("%s_%s", feature, articleId),
                new Bundle());
        // パート
        firebaseAnalytics.logEvent(
                String.format("%s_part%d", feature, partIndex),
                new Bundle());
        // レベル
        firebaseAnalytics.logEvent(
                String.format("%s_level%d", feature, level),
                new Bundle());
    }

    public void sendListening(String articleId, int partIndex) {
        String feature = "listening";
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, articleId);
        bundle.putInt(FirebaseAnalytics.Param.CHARACTER, partIndex);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, feature);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

        // パラメータが見れない対策
        // 記事
        firebaseAnalytics.logEvent(
                String.format("%s_%s", feature, articleId),
                new Bundle());
        // パート
        firebaseAnalytics.logEvent(
                String.format("%s_part%d", feature, partIndex),
                new Bundle());
    }

}
