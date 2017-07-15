package com.kikotoba.android.model.entity.master;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kikotoba.android.model.entity.user.UserLogByArticle;

import java.util.ArrayList;
import java.util.List;

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
    private List<PartIndex> partIndexList = new ArrayList();

    public ArticlePairDummy() {
        super();
        partIndexList.add(new PartIndex());
    }

    public Article _getTarget() {
        return target;
    }

    public void _setTarget(Article target) {
        this.target = target;
    }

    public Article _getTranslated() {
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

    public String _getId() {
        return id;
    }

    public void _setId(String id) {
        this.id = id;
    }

    public UserLogByArticle _getUserLogByArticle() {
        return userLogByArticle;
    }

    public void _setUserLogByArticle(UserLogByArticle userLogByArticle) {
        this.userLogByArticle = userLogByArticle;
    }

    public List<PartIndex> getPartIndex() {
        return partIndexList;
    }

    public void setPartIndex(List<PartIndex> partIndex) {
        this.partIndexList = partIndex;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, new TypeToken<ArticlePairDummy>() {}.getType());
    }

}
