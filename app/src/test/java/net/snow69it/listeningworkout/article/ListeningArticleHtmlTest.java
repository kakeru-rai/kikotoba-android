package net.snow69it.listeningworkout.article;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ListeningArticleHtmlTest {
    @Test
    public void createHtml_success() throws Exception {
        Article targetArticle = ArticleFixture.newArticle();
        ListeningHtml html = new ListeningHtml(
                targetArticle,
                ArticleFixture.newArticle(),
                "<html>%s</html>"
                );
        String htmlString = html.createHtml();

        assertTrue("sentenceが出力されている", htmlString.indexOf(String.format(ArticleFixture.sentenceFormat, 0)) > 0);
        assertTrue("sentenceが出力されている", htmlString.indexOf(String.format(ArticleFixture.sentenceFormat, 1)) > 0);
        assertTrue("sentenceが出力されている", htmlString.indexOf(String.format(ArticleFixture.sentenceFormat, 2)) > 0);
        assertTrue("titleが出力されている", htmlString.indexOf(targetArticle.getTitle()) > 0);
    }
}