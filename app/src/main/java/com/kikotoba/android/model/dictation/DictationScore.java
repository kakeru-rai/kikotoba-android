package com.kikotoba.android.model.dictation;

/**
 * Created by raix on 2017/06/17.
 */

/**
 * ディクテーション中に管理するスコア情報
 */
public class DictationScore {

    public static final int MAX_LIFE_COUNT = 5;
    private static final int MIN_GOLD_LIFE = 4;
    private static final int MIN_SILVER_LIFE = 1;

    private final int questionCount;
    private int lifeCount = MAX_LIFE_COUNT;
    private int correctAnswerCount = 0;

    public DictationScore(int questionCount) {
        this.questionCount = questionCount;
    }

    public ScoreRank calcScoreRank() {
        if (correctAnswerCount < questionCount) {
            return ScoreRank.NOT_CLEARED;
        }
        if (lifeCount < MIN_SILVER_LIFE) {
            return ScoreRank.BRONZE;
        }
        if (lifeCount < MIN_GOLD_LIFE) {
            return ScoreRank.SILVER;
        }
        return ScoreRank.GOLD;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void incrementCorrectAnswer() {
        ++correctAnswerCount;
    }

    public void decrementLife() {
        if (lifeCount > 0) {
            --lifeCount;
        }
    }

    public int getCorrectAnsweredCount() {
        return correctAnswerCount;
    }

    public int getLifeCount() {
        return lifeCount;
    }
}
