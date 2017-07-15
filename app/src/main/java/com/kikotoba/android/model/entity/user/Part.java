package com.kikotoba.android.model.entity.user;

import com.kikotoba.android.model.dictation.Level;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class Part {

//    private int listeningPlaybackTime = 0;
//    private int currentReadingIndex = 0;
    private Map<String, Integer> score = new HashMap();

//    public int getListeningPlaybackTime() {
//        return listeningPlaybackTime;
//    }
//
//    public void setListeningPlaybackTime(int listeningPlaybackTime) {
//        this.listeningPlaybackTime = listeningPlaybackTime;
//    }

    public Map<String, Integer> getScore() {
        return score;
    }

    public void setScore(Map<String, Integer> score) {
        if (score == null) {
            score = new HashMap();
        }
        this.score = score;
    }

//    public int getCurrentReadingIndex() {
//        return currentReadingIndex;
//    }
//
//    public void setCurrentReadingIndex(int currentReadingIndex) {
//        this.currentReadingIndex = currentReadingIndex;
//    }

//    public String calcListeningPlaybackTime() {
//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        cal.setTime(new Date(listeningPlaybackTime * 1000));
//        DateFormat df = new SimpleDateFormat("HH:mm:ss");
//        df.setTimeZone(cal.getTimeZone());
//        return df.format(cal.getTime());
//    }

    public void _setScore(Level level, int score) {
        this.score.put(level.firebaseKey, score);
    }

    public int _getScore(Level level) {
        Integer score = this.score.get(level.firebaseKey);
        return score == null ? 0 : score;
    }

}
