package com.kikotoba.android.model.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * ArticlePairのNullオブジェクト
 * 公開予定の記事などダミー記事としての利用など
 */
public class ArticlePairDummy extends ArticlePair {

    private Article target;
    private Article translated;
    private String image;
    private String origin;
    private String id;
    private UserLogByArticle userLogByArticle;

    public ArticlePairDummy() {
        super();
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
        return gson.toJson(this, new TypeToken<ArticlePairDummy>() {}.getType());
    }

}
