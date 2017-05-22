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

    public float getSpeechSpeed() {
        return Float.valueOf(mPref.getString(mContext.getString(R.string.pref_general_speech_speed_key), "1.0"));
    }
}
