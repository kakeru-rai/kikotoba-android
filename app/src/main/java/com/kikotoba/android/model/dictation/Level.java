package com.kikotoba.android.model.dictation;

/**
 * Created by raix on 2017/06/12.
 */

public enum Level {
    EASY("a"),
    NORMAL("b"),
    HARD("c"),
    ;

    public final String firebaseKey;
    Level(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }
}
