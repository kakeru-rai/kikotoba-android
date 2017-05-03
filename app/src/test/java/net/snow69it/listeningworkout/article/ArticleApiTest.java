package net.snow69it.listeningworkout.article;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ArticleApiTest {
    @Test
    public void convertApiToArticleJa() throws Exception {
        ArticleApi api = ArticleTestUtil.getArticleListJa();
        api.exec();

        assertEquals(1, api.getArticles().size());
        Article article = api.getArticles().get(0);
        assertEquals("科学者グループ、脳の発達の新しい段階を発見", article.getTitle());
        assertEquals("voa_001", article.getId());
        assertEquals("https://gdb.voanews.com/1067F4DF-C746-4284-BA7B-C30F08CC3555_w987_r1_s.jpg", article.getImage());
        assertEquals("http://learningenglish.voanews.com/a/scientists-find-new-step-in-brain-development-health-and-lifestyle/3633193.html", article.getOrigin());
        assertEquals("ja", article.getLanguage());
        assertEquals(27, article.getSentences().size());
        Sentence sentence = article.getSentences().get(0);
        assertEquals("長年にわたって、科学者や医療研究者は人間の脳を研究するのに多くの時間を費やしてきた。", sentence.getText());
        assertEquals(1, sentence.getTrack());
        assertEquals(1, sentence.getParagraph());
    }

    @Test
    public void convertApiToArticleEn() throws Exception {
        ArticleApi api = ArticleTestUtil.getArticleListEn();
        api.exec();

        assertEquals(1, api.getArticles().size());
        Article article = api.getArticles().get(0);
        assertEquals("Scientists Find New Step in Brain Development", article.getTitle());
        assertEquals("voa_001", article.getId());
        assertEquals("https://gdb.voanews.com/1067F4DF-C746-4284-BA7B-C30F08CC3555_w987_r1_s.jpg", article.getImage());
        assertEquals("http://learningenglish.voanews.com/a/scientists-find-new-step-in-brain-development-health-and-lifestyle/3633193.html", article.getOrigin());
        assertEquals("en", article.getLanguage());
        assertEquals(27, article.getSentences().size());
        Sentence sentence = article.getSentences().get(0);
        assertEquals("Over the years, scientists and medical researchers have spent much time studying the human brain.", sentence.getText());
        assertEquals(1, sentence.getTrack());
        assertEquals(1, sentence.getParagraph());
    }

}