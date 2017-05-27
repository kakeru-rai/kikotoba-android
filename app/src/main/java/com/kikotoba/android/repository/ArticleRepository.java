package com.kikotoba.android.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.kikotoba.android.model.entity.Article;
import com.kikotoba.android.model.entity.ArticlePair;
import com.kikotoba.android.model.LanguagePair;
import com.kikotoba.android.model.entity.ArticlePairDummy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by raix on 2017/03/25.
 */
public class ArticleRepository extends BaseRepository {

    private static final String PATH = "/master/article";

    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_IMAGE = "image";
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
            Map<String, String> children = (Map<String, String>) articlePairSnapshot.getValue();
            String origin = children.get(KEY_ORIGIN);
            String image = children.get(KEY_IMAGE);
            DataSnapshot languageSnapthot = articlePairSnapshot.child(KEY_LANGUAGE);

            // 言語ごとのテキスト抽出
            Article target = null;
            Article translated = null;
            for (DataSnapshot articleSnapshot : languageSnapthot.getChildren()) {
                // 言語リスト
                if (articleSnapshot.getKey().equals(LanguagePair.getInstance().getTarget())) {
                    target = articleSnapshot.getValue(Article.class);
                    target.setId(id);
                    target.setOrigin(origin);
                    target.setImage(image);
                    target.setLanguage(LanguagePair.getInstance().getTarget());
                } else if (articleSnapshot.getKey().equals(LanguagePair.getInstance().getMother())) {
                    translated = articleSnapshot.getValue(Article.class);
                    translated.setId(id);
                    translated.setOrigin(origin);
                    translated.setImage(image);
                    translated.setLanguage(LanguagePair.getInstance().getMother());
                }
            }

            ArticlePair articlePair = new ArticlePair(target, translated);
            articlePair.setImage(image);
            articlePair.setOrigin(origin);
            articlePair.setId(id);
            return articlePair;
        } catch (Exception e) {
            ArticlePairDummy articlePairDummy = new ArticlePairDummy();
            articlePairDummy.setId(id);
            return articlePairDummy;
        }
    }
}
