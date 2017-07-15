package com.kikotoba.android.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kikotoba.android.model.LanguagePair;
import com.kikotoba.android.model.entity.master.Article;
import com.kikotoba.android.model.entity.master.ArticlePair;
import com.kikotoba.android.model.entity.master.ArticlePairDummy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raix on 2017/03/25.
 */
public class ArticleRepository extends BaseRepository {

    private static final String SCHEME_VERSION = "v1";
    private static final String PATH = "/master/article_" + SCHEME_VERSION;
//    private static final String PATH = "/master/article";

    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_PART_INDEX = "partIndex";
    private static final String KEY_LANGUAGE = "language";

    public void queryArticle(String id, final EntityEventListener<ArticlePair> listener) {
        DatabaseReference ref = firebaseDatabase.getReference(PATH + "/" + id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArticlePair articlePair = null;
                try {
                    articlePair = map(dataSnapshot);
                } catch (IllegalArgumentException e) {
                    // TODO: ロギング
                    e.printStackTrace();
                }
                listener.onSuccess(articlePair);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onError(error);
            }
        });
    }

    public void queryArticles(final EntityListEventListener<ArticlePair> listener) {
        DatabaseReference ref = firebaseDatabase.getReference(PATH);
        ref.keepSynced(true);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ArticlePair> list = new ArrayList();
                for (DataSnapshot articlePairSnapshot: dataSnapshot.getChildren()) {
                    // 記事リスト
                    try {
                        list.add(map(articlePairSnapshot));
                    } catch (IllegalArgumentException e) {
                        // TODO: ロギング
                        e.printStackTrace();
                    }
                }
                listener.onSuccess(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onError(error);
            }
        });
    }

    private ArticlePair map(DataSnapshot articlePairSnapshot) {
        String id = articlePairSnapshot.getKey();
        try {
            ArticlePair articlePair_ = articlePairSnapshot.getValue(ArticlePair.class);

//            Map<String, Object> children = (Map<String, Object>) articlePairSnapshot.getValue();
//            String origin = (String) children.get(KEY_ORIGIN);
//            String image = (String) children.get(KEY_IMAGE);
//            DataSnapshot languageSnapshot = articlePairSnapshot.child(KEY_LANGUAGE);
//            Object list = children.get(KEY_PART_INDEX);
//
//            List<PartIndex> partIndexList = new ArrayList();
//            for (Map<String, Long> map : ((List<Map<String, Long>>)list)) {
//                PartIndex p = new PartIndex();
//                p.setEndSentence(map.get("end").intValue());
//                p.setStartSentence(map.get("start").intValue());
//                partIndexList.add(p);
//            }
//
//            // 言語ごとのテキスト抽出
//            Article target = null;
//            Article translated = null;
//            for (DataSnapshot articleSnapshot : languageSnapshot.getChildren()) {
//                // 言語リスト
//                if (articleSnapshot.getKey().equals(LanguagePair.getInstance().getTarget())) {
//                    target = articleSnapshot.getValue(Article.class);
//                    target.setId(id);
//                    target.setOrigin(origin);
//                    target.setImage(image);
//                    target.setLanguage(LanguagePair.getInstance().getTarget());
//                } else if (articleSnapshot.getKey().equals(LanguagePair.getInstance().getMother())) {
//                    translated = articleSnapshot.getValue(Article.class);
//                    translated.setId(id);
//                    translated.setOrigin(origin);
//                    translated.setImage(image);
//                    translated.setLanguage(LanguagePair.getInstance().getMother());
//                }
//            }

            Article translated = articlePair_.getLanguage().get(LanguagePair.getInstance().getMother());
            Article target = articlePair_.getLanguage().get(LanguagePair.getInstance().getTarget());
            articlePair_._setTarget(target);
            articlePair_._setTranslated(translated);
            articlePair_._setId(id);

//            ArticlePair articlePair = new ArticlePair(target, translated);
//            articlePair.setImage(image);
//            articlePair.setOrigin(origin);
//            articlePair._setId(id);
//            articlePair.setPartIndex(partIndexList);
            return articlePair_;
        } catch (Exception e) {
            ArticlePairDummy articlePairDummy = new ArticlePairDummy();
            articlePairDummy._setId(id);
            e.printStackTrace();
            return articlePairDummy;
        }
    }
}
