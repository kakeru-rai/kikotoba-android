package com.kikotoba.android.model.entity.master;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kikotoba.android.model.LanguagePair;
import com.kikotoba.android.model.entity.user.Part;
import com.kikotoba.android.model.entity.user.UserLogByArticle;
import com.kikotoba.android.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 記事の言語ペア
 */
public class ArticlePair {

    private String image;
    private String origin;
    private List<PartIndex> partIndex = new ArrayList();
    private Map<String, Article> language = new HashMap();

    private String id;
    private Article target;
    private Article translated;
    private UserLogByArticle userLogByArticle;

    protected ArticlePair() {
    }

    public ArticlePair(Article target, Article tranlated) throws IllegalArgumentException {
        if (target == null) {
            throw new IllegalArgumentException("言語ペアがそろいませんでした。target:" + LanguagePair.getInstance().getTarget());
        }
        if (tranlated == null) {
            throw new IllegalArgumentException("言語ペアがそろいませんでした。translated:" + LanguagePair.getInstance().getMother());
        }
        this.target = target;
        this.translated = tranlated;
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

    public List<PartIndex> getPartIndex() {
        return partIndex;
    }

    public void setPartIndex(List<PartIndex> partIndex) {
        this.partIndex = partIndex;
    }

    public Map<String, Article> getLanguage() {
        return language;
    }

    public void setLanguage(Map<String, Article> language) {
        this.language = language;
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

    public void _setTranslated(Article translated) {
        this.translated = translated;
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

    public Part _getUserLogByArticle(int partIndex) {
        try {
            Part part = userLogByArticle.getPart().get(Util.fbIindex(partIndex));
            return part != null ? part : new Part();
        } catch (Exception e) {
            return new Part();
        }
    }

    public void _setUserLogByArticle(UserLogByArticle userLogByArticle) {
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

    public ArticlePair devideToPart(int partIndex) {
        ArticlePair partialArticlePair = ArticlePair.fromJson(toJson());
        int startSentenceIndex = getPartIndex().get(partIndex).getStartSentence();
        int endSentenceIndex = getPartIndex().get(partIndex).getEndSentence();
        List newTargetSentence = partialArticlePair._getTarget().getSentences().subList(startSentenceIndex, endSentenceIndex);
//        List newTranslatedSentence = partialArticlePair._getTranslated().getSentences().subList(startSentenceIndex, endSentenceIndex);
        partialArticlePair._getTarget().setSentences(newTargetSentence);
//        partialArticlePair._getTranslated().setSentences(newTranslatedSentence);
        return partialArticlePair;
    }

}
