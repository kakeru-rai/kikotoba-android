package com.kikotoba.android.model.entity;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by raix on 2017/03/26.
 */

public class UserLogByArticle {

    private int listeningPlaybackTime = 0;
    private int currentReadingIndex = 0;
    private Map<String, Boolean> speakingCorrect = new HashMap();
    private int dictationScore = 0;

    public int getListeningPlaybackTime() {
        return listeningPlaybackTime;
    }

    public void setListeningPlaybackTime(int listeningPlaybackTime) {
        this.listeningPlaybackTime = listeningPlaybackTime;
    }

    public Map<String, Boolean> getSpeakingCorrect() {
        return speakingCorrect;
    }

    public void setSpeakingCorrect(@NonNull Map<String, Boolean> speakingCorrect) {
        this.speakingCorrect = speakingCorrect;
    }

    public int getDictationScore() {
        return dictationScore;
    }

    public void setDictationScore(int dictationScore) {
        this.dictationScore = dictationScore;
    }

    public void setSpeakingCorrectByIndex(int index) {
        speakingCorrect.put(String.valueOf(index) + "_", true);
    }

    public int getCurrentReadingIndex() {
        return currentReadingIndex;
    }

    public boolean isSpeakingCorrect(int index) {
//        return true;
        return getSpeakingCorrect().containsKey(String.valueOf(index) + "_");
    }

    public int calcSpeakingTotal() {
        return speakingCorrect.size();
    }

    public String calcListeningPlaybackTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(new Date(listeningPlaybackTime * 1000));
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime());
    }

}
