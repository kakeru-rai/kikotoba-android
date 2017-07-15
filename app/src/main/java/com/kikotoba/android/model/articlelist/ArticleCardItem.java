package com.kikotoba.android.model.articlelist;

import com.kikotoba.android.model.entity.master.ArticlePair;

/**
 * Created by raix on 2017/07/01.
 */

public class ArticleCardItem {
    private ArticlePair articlePair;
    private int partIndex;
    private int partCount;

    public ArticleCardItem(ArticlePair articlePair, int partIndex, int partCount) {
        this.articlePair = articlePair;
        this.partIndex = partIndex;
        this.partCount = partCount;
    }

    public ArticlePair getArticlePair() {
        return articlePair;
    }

    public void setArticlePair(ArticlePair articlePair) {
        this.articlePair = articlePair;
    }

    public int getPartIndex() {
        return partIndex;
    }

    public void setPartIndex(int partIndex) {
        this.partIndex = partIndex;
    }

    public boolean isFirst() {
        return partIndex == 0;
    }

    public boolean isLast() {
        return partIndex == partCount - 1;
    }
}
