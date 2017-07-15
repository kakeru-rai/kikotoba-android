package com.kikotoba.android.model.entity.user;

import com.kikotoba.android.util.Util;

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
 * ・valueEventListenerの中で対象entityの値を更新するとループしてしまう
 */
public class UserLogByArticle {
    private Map<String, Part> part = new HashMap();

    public Map<String, Part> getPart() {
        return part;
    }

    public void setPart(Map<String, Part> part) {
        if (part == null) {
            part = new HashMap();
        }
        this.part = part;
    }

    // v0.5.0でdeprecated
    private int listeningPlaybackTime = 0;
    private int currentReadingIndex = 0;
    private Map<String, Integer> score = new HashMap();

    public int getListeningPlaybackTime() {
        return listeningPlaybackTime;
    }

    public void setListeningPlaybackTime(int listeningPlaybackTime) {
        this.listeningPlaybackTime = listeningPlaybackTime;
    }

    @Deprecated
    public Map<String, Integer> getScore() {
        return score;
    }

    @Deprecated
    public void setScore(Map<String, Integer> score) {
        if (score == null) {
            score = new HashMap();
        }
        this.score = score;
    }

    @Deprecated
    public int getCurrentReadingIndex() {
        return currentReadingIndex;
    }

    public Part _getPart(int partIndex) {
        Part p = part.get(Util.fbIindex(partIndex));
        return p != null ? p : new Part();
    }

    public String calcListeningPlaybackTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(new Date(listeningPlaybackTime * 1000));
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime());
    }

    /**
     * v0.5.0へのマイグレーション
     */
    public boolean migratePartScore() {
        if (part != null && part.size() > 0) {
            // migrate済み
            return false;
        }
        if (listeningPlaybackTime == 0
                && currentReadingIndex == 0
                && (score == null || score.size() == 0)) {
            // 初期ユーザー。migrate必要なし
            return false;
        }
        Part log = new Part();
//        log.setListeningPlaybackTime(listeningPlaybackTime);
//        log.setCurrentReadingIndex(currentReadingIndex);
        log.setScore(score);
        part.put(Util.fbIindex(0), log); // part1のスコアとして移行
        return true;
    }

}
