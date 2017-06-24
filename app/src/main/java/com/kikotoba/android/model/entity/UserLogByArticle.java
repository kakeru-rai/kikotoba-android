package com.kikotoba.android.model.entity;

import android.support.annotation.NonNull;

import com.kikotoba.android.model.dictation.Level;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * ※firebaseの注意点
 * ・getter/setterを作るとプロパティと判定されるので、マッピング対象以外にget/setは使わないこと
 * ・mapのキーに文字列で整数をセットしても配列として扱われるので、本質的に配列データでなければ数値以外の文字をキーとする
 * ・型を変更するために、ルールとサーバーデータを更新して、クラス内のメンバの型を変えても、ローカルに保存済みの旧データがある場合エラーとなる
 * 　リリース済みのデータは型の変更は行わず、変数の追加とした方が無難。
 */
public class UserLogByArticle {

    private int listeningPlaybackTime = 0;
    private int currentReadingIndex = 0;
    private Map<String, Boolean> speakingCorrect = new HashMap();
    private Map<String, Integer> score = new HashMap();
//    private int score = 0;

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

    public Map<String, Integer> getScore() {
        return score;
    }

    public void setScore(Map<String, Integer> score) {
        if (score == null) {
            score = new HashMap();
        }
        this.score = score;
    }

//    public int getScore() {
//        return score;
//    }
//
//    public void setScore(int score) {
//        this.score = score;
//    }
//
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

    public void _setScore(Level level, int score) {
        this.score.put(level.firebaseKey, score);
    }

    public int _getScore(Level level) {
        Integer score = this.score.get(level.firebaseKey);
        return score == null ? 0 : score;
    }

}
