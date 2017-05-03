
package net.snow69it.listeningworkout.article;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.snow69it.listeningworkout.datasource.ApiDataLoader;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ArticleApi implements IArticleApi {

    public static final String testFileJa = "api/ja/GetArticles.json";
    public static final String testFileEn = "api/en/GetArticles.json";

    public static Article getArticleEn(Context context) {
        ArticleApi api = new ArticleApi(new ApiDataLoader(context), ArticleApi.testFileEn);
        api.exec();
        return api.getArticles().get(0);
    }

    public static Article getArticleJa(Context context) {
        ArticleApi api = new ArticleApi(new ApiDataLoader(context), ArticleApi.testFileJa);
        api.exec();
        return api.getArticles().get(0);
    }

    private List<Article> articles;

    protected String rawResult = "";

    private ApiDataLoader loader;

    private final String endpoint;

    public ArticleApi(ApiDataLoader loader, String endpoint) {
        this.loader = loader;
        this.endpoint = endpoint;
    }

    @Override
    public void exec() {
        load();
        convert();
    }

    @Override
    public String get() {
        return rawResult;
    }

    protected void load() {
        rawResult = loader.load(endpoint);
    }

    protected void convert() {
        Gson gson = new Gson();
        articles = gson.fromJson(rawResult, new TypeToken<List<Article>>() {}.getType());
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}