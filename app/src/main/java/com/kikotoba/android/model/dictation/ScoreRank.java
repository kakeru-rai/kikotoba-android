package com.kikotoba.android.model.dictation;

/**
 * Created by raix on 2017/06/12.
 */

public enum ScoreRank {
    NOT_CLEARED(0),
    BRONZE(1),
    SILVER(2),
    GOLD(3),
    ;

    public final int typeValue;
    ScoreRank(int typeValue) {
        this.typeValue = typeValue;
    }
}
