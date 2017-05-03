package net.snow69it.listeningworkout.article;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.snow69it.listeningworkout.common.LanguagePair;
import net.snow69it.listeningworkout.entity.UserLogByArticle;

/**
 * 記事の言語ペア
 */
public class ArticlePair {

    private Article target;
    private Article translated;
    private String image;
    private String origin;
    private String id;
    private UserLogByArticle userLogByArticle;

    public ArticlePair(Article target, Article tranlated) throws IllegalArgumentException {
        if (target == null) {
            throw new IllegalArgumentException("言語ペアがそろいませんでした。target:" + LanguagePair.getInstance().getTarget());
        }
        if (tranlated == null) {
            throw new IllegalArgumentException("言語ペアがそろいませんでした。translated:" + LanguagePair.getInstance().getMother());
        }
        if (target.getSentences().size() != tranlated.getSentences().size()) {
            throw new IllegalArgumentException("文の数が合っていません。id:" + target.getId());
        }
        this.target = target;
        this.translated = tranlated;
    }

    public Article getTarget() {
        return target;
    }

    public void setTarget(Article target) {
        this.target = target;
    }

    public Article getTranslated() {
        return translated;
    }

    public void setTranslated(Article translated) {
        this.translated = translated;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserLogByArticle getUserLogByArticle() {
        return userLogByArticle;
    }

    public void setUserLogByArticle(UserLogByArticle userLogByArticle) {
        this.userLogByArticle = userLogByArticle;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, new TypeToken<ArticlePair>() {}.getType());
    }

    public static ArticlePair fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ArticlePair>() {}.getType());
    }

}
