package com.kikotoba.android.model.dictation;

import com.kikotoba.android.util.JVersatile;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class DictationScoreTest {

    private static final int QUESTION_COUNT = 5;

    @Test
    public void calcScoreRank_correctAnswerLessThanQuestionCount_Rank_0() throws Exception {
        DictationScore score = new DictationScore(QUESTION_COUNT);

        assertEquals(ScoreRank.NOT_CLEARED, score.calcScoreRank());

        score.incrementCorrectAnswer();
        assertEquals(ScoreRank.NOT_CLEARED, score.calcScoreRank());

        score.incrementCorrectAnswer();
        assertEquals(ScoreRank.NOT_CLEARED, score.calcScoreRank());

        score.incrementCorrectAnswer();
        assertEquals(ScoreRank.NOT_CLEARED, score.calcScoreRank());

        score.incrementCorrectAnswer();
        assertEquals(ScoreRank.NOT_CLEARED, score.calcScoreRank());

        // QUESTION_COUNT 回目
        score.incrementCorrectAnswer();
        assertEquals(ScoreRank.GOLD, score.calcScoreRank());
    }

    @Test
    public void calcScoreRank_eachLifeCount_decisionRank() throws Exception {
        DictationScore score = new DictationScore(QUESTION_COUNT);
        for (int i : JVersatile.range(1, QUESTION_COUNT)) {
            score.incrementCorrectAnswer();
        }

        assertEquals(5, score.getLifeCount());
        assertEquals(ScoreRank.GOLD, score.calcScoreRank());

        score.decrementLife();
        assertEquals(4, score.getLifeCount());
        assertEquals(ScoreRank.GOLD, score.calcScoreRank());

        score.decrementLife();
        assertEquals(3, score.getLifeCount());
        assertEquals(ScoreRank.SILVER, score.calcScoreRank());

        score.decrementLife();
        assertEquals(2, score.getLifeCount());
        assertEquals(ScoreRank.SILVER, score.calcScoreRank());

        score.decrementLife();
        assertEquals(1, score.getLifeCount());
        assertEquals(ScoreRank.SILVER, score.calcScoreRank());

        score.decrementLife();
        assertEquals(0, score.getLifeCount());
        assertEquals(ScoreRank.BRONZE, score.calcScoreRank());
    }
}