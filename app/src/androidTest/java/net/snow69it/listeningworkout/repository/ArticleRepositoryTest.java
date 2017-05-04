package net.snow69it.listeningworkout.repository;

import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DatabaseError;

import net.snow69it.listeningworkout.WaitUtil;
import net.snow69it.listeningworkout.article.Article;
import net.snow69it.listeningworkout.article.ArticlePair;
import net.snow69it.listeningworkout.article.Sentence;

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
                target = articlePairs.get(0).getTarget();
                translated = articlePairs.get(0).getTranslated();
                waitUtil.hasDone();
            }

            @Override
            public void onError(DatabaseError error) {
                assertTrue(false);
            }
        });
        waitUtil.waitForDone();

        assertEquals("科学者グループ、脳の発達の新しい段階を発見", translated.getTitle());
        assertEquals("voa_001", translated.getId());
        assertEquals("https://gdb.voanews.com/1067F4DF-C746-4284-BA7B-C30F08CC3555_w987_r1_s.jpg", translated.getImage());
        assertEquals("http://learningenglish.voanews.com/a/scientists-find-new-step-in-brain-development-health-and-lifestyle/3633193.html", translated.getOrigin());
        assertEquals("ja", translated.getLanguage());
        assertEquals(27, translated.getSentences().size());
        Sentence sentence = translated.getSentences().get(0);
        assertEquals("長年にわたって、科学者や医療研究者は人間の脳を研究するのに多くの時間を費やしてきた。", sentence.getText());
        assertEquals(1, sentence.getTrack());
        assertEquals(1, sentence.getParagraph());

        assertEquals("Scientists Find New Step in Brain Development", target.getTitle());
        assertEquals("voa_001", target.getId());
        assertEquals("https://gdb.voanews.com/1067F4DF-C746-4284-BA7B-C30F08CC3555_w987_r1_s.jpg", target.getImage());
        assertEquals("http://learningenglish.voanews.com/a/scientists-find-new-step-in-brain-development-health-and-lifestyle/3633193.html", target.getOrigin());
        assertEquals("en", target.getLanguage());
        assertEquals(27, target.getSentences().size());
        sentence = target.getSentences().get(0);
        assertEquals("Over the years, scientists and medical researchers have spent much time studying the human brain.", sentence.getText());
        assertEquals(1, sentence.getTrack());
        assertEquals(1, sentence.getParagraph());
        System.out.println("####### Done");

        assertTrue(waitUtil.isDone());
    }
}
