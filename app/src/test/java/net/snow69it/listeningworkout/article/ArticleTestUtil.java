package net.snow69it.listeningworkout.article;

/**
 * Created by raix on 2017/01/29.
 */

public class ArticleTestUtil {

    public static ArticleApi getArticleListEn() {
        return new ArticleApi(new FakeApiDataLoader(), "Article/dat/" + ArticleApi.testFileEn);
    }

    public static ArticleApi getArticleListJa() {
        return new ArticleApi(new FakeApiDataLoader(), "Article/dat/" + ArticleApi.testFileJa);
    }
}
