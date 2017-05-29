package com.kikotoba.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kikotoba.android.R;

/**
 * Created by raix on 2017/03/12.
 */

public class Pref {
    private SharedPreferences mPref;
    private Context mContext;
    public Pref(Context context) {
        mContext = context;
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public enum SpeechGap {
        NORMAL,
        REPETITION,
        SHADOWING,
        ;

        public static SpeechGap of(Context context, String typeString) {
            if (typeString.equals(context.getString(R.string.pref_speech_gap_val_normal))) {
                return SpeechGap.NORMAL;
            } else if (typeString.equals(context.getString(R.string.pref_speech_gap_val_repetition))) {
                return SpeechGap.REPETITION;
            } else if (typeString.equals(context.getString(R.string.pref_speech_gap_val_shadowing))) {
                return SpeechGap.SHADOWING;
            } else {
                // TODO: 検知
                return SpeechGap.NORMAL;
            }
        }

        public static SpeechGap of(int resId) {
            if (resId == R.id.action_speech_gap_normal) {
                return SpeechGap.NORMAL;
            } else if (resId == R.id.action_speech_gap_repetition) {
                return SpeechGap.REPETITION;
            } else if (resId == R.id.action_speech_gap_shadowing) {
                return SpeechGap.SHADOWING;
            } else {
                // TODO: 検知
                return SpeechGap.NORMAL;
            }
        }

        public String valueOf(Context context) {
            switch (this) {
                case NORMAL:
                    return context.getString(R.string.pref_speech_gap_val_normal);
                case REPETITION:
                    return context.getString(R.string.pref_speech_gap_val_repetition);
                case SHADOWING:
                    return context.getString(R.string.pref_speech_gap_val_shadowing);
                default:
                    // TODO: 検知
                    return context.getString(R.string.pref_speech_gap_val_normal);
            }
        }

        public String description(Context context) {
            switch (this) {
                case NORMAL:
                    return context.getString(R.string.menu_speech_gap_normal);
                case REPETITION:
                    return context.getString(R.string.menu_speech_gap_repetition);
                case SHADOWING:
                    return context.getString(R.string.menu_speech_gap_shadowing);
                default:
                    // TODO: 検知
                    return context.getString(R.string.pref_speech_gap_val_normal);
            }
        }
    }

    public void putSpeechGap(SpeechGap value) {
        String key = mContext.getString(R.string.pref_speech_gap_key);
        mPref.edit()
                .putString(key, value.valueOf(mContext))
                .apply();
    }

    public SpeechGap getSpeechGap() {
        String key = mContext.getString(R.string.pref_speech_gap_key);
        String[] values = mContext.getResources().getStringArray(R.array.pref_speech_gap_values);
        String value = mPref.getString(key, values[0]);
        return SpeechGap.of(mContext, value);
    }
}
