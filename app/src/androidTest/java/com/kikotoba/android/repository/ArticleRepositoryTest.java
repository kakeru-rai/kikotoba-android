package com.kikotoba.android.repository;

import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DatabaseError;

import com.kikotoba.android.WaitUtil;
import com.kikotoba.android.model.entity.master.Article;
import com.kikotoba.android.model.entity.master.ArticlePair;
import com.kikotoba.android.model.entity.master.Sentence;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ArticleRepositoryTest {

    private Article target = null;
    private Article translated = null;

    @Test
    public void useAppFirebase() throws Exception {
        final WaitUtil waitUtil = new WaitUtil();

        ArticleRepository repo = new ArticleRepository();
        repo.queryArticles(new BaseRepository.EntityListEventListener<ArticlePair>() {
            @Override
            public void onSuccess(List<ArticlePair> articlePairs) {
                target = articlePairs.get(0)._getTarget();
                translated = articlePairs.get(0)._getTranslated();
                waitUtil.hasDone();
            }

            @Override
            public void onError(DatabaseError error) {
                assertTrue(false);
            }
        });
        waitUtil.waitForDone();

        assertEquals("科学者グループ、脳の発達の新しい段階を発見", translated.getTitle());
        assertEquals(27, translated.getSentences().size());
        Sentence sentence = translated.getSentences().get(0);
        assertEquals("長年にわたって、科学者や医療研究者は人間の脳を研究するのに多くの時間を費やしてきた。", sentence.getText());
        assertEquals(1, sentence.getTrack());
        assertEquals(1, sentence.getParagraph());

        assertEquals("Scientists Find New Step in Brain Development", target.getTitle());
        assertEquals(27, target.getSentences().size());
        sentence = target.getSentences().get(0);
        assertEquals("Over the years, scientists and medical researchers have spent much time studying the human brain.", sentence.getText());
        assertEquals(1, sentence.getTrack());
        assertEquals(1, sentence.getParagraph());
        System.out.println("####### Done");

        assertTrue(waitUtil.isDone());
    }
}
