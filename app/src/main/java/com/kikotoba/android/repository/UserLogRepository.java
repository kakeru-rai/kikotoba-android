package com.kikotoba.android.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kikotoba.android.model.dictation.Level;
import com.kikotoba.android.model.entity.user.UserLogByArticle;
import com.kikotoba.android.util.FirebaseUtil;
import com.kikotoba.android.util.Util;

/**
 * Created by raix on 2017/03/26.
 */

public class UserLogRepository extends BaseRepository {

    private static final String PATH = "/user/log/%s/by_article/%s";
    private static final String PATH_PART = "/user/log/%s/by_article/%s/part/%s";

    private static final String PATH_LISTENING_PLAYBACK_TIME = "/user/log/%s/by_article/%s/listeningPlaybackTime";

    private static final String KEY_LISTENING_PLAYBACK_TIME = "listeningPlaybackTime";
    private static final String KEY_CURRENT_READING_INDEX = "currentReadingIndex";
    private static final String KEY_DICTATION_SCORE = "score/%s";

    public void setListeningPlaybackTime(String uid, String articleId, long playbackTimeSec) {
//    public void setListeningPlaybackTime(String uid, String articleId, int partIndex, long playbackTimeSec) {
        DatabaseReference ref = firebaseDatabase.getReference(String.format(PATH_LISTENING_PLAYBACK_TIME, uid, articleId));
//        DatabaseReference refChild = ref.child(KEY_LISTENING_PLAYBACK_TIME);
        ref.setValue(playbackTimeSec);
    }

    public void setCurrentReadingIndex(String uid, String articleId, int partIndex, int index) {
        DatabaseReference ref = firebaseDatabase.getReference(String.format(PATH_PART, uid, articleId, partIndex));
        DatabaseReference refChild = ref.child(KEY_CURRENT_READING_INDEX);
        refChild.setValue(index);
    }

    public Task setDictationScore(String uid, String articleId, Level level, int partIndex, int score) {
        DatabaseReference ref = firebaseDatabase.getReference(String.format(PATH_PART, uid, articleId, Util.fbIindex(partIndex)));
        DatabaseReference refChild = ref.child(String.format(KEY_DICTATION_SCORE, level.firebaseKey));
        return refChild.setValue(score);
    }

    public void bindUserLogByArticle(String uid, String articleId, final EntityEventListener<UserLogByArticle> listener) {
        DatabaseReference ref = firebaseDatabase.getReference(String.format(PATH, uid, articleId));
        ref.addValueEventListener(newValueEventListener(listener));
    }

    public void getUserLogByArticle(String uid, String articleId, final EntityEventListener<UserLogByArticle> listener) {
        DatabaseReference ref = firebaseDatabase.getReference(String.format(PATH, uid, articleId));
        ref.addListenerForSingleValueEvent(newValueEventListener(listener));
    }

    private ValueEventListener newValueEventListener(final EntityEventListener<UserLogByArticle> listener) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.toString();
                UserLogByArticle log = dataSnapshot.getValue(UserLogByArticle.class);
                listener.onSuccess(log);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onError(error);
            }
        };
    }

    public Task setUserLogByArticle(String uid, String articleId, UserLogByArticle log) {
        return FirebaseUtil.getDatabaseReference().child(String.format(PATH, uid, articleId)).setValue(log);

//        return FirebaseUtil.getDatabaseReference()
//                .child("user_log")
//                .child(uid)
//                .child("by_article")
//                .child(articleId)
//                .setValue(log);
    }

}
