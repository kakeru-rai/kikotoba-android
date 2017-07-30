package com.kikotoba.android.repository.user;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kikotoba.android.model.entity.user.Summary;
import com.kikotoba.android.repository.BaseRepository;
import com.kikotoba.android.util.FirebaseUtil;

/**
 * 集計されたユーザー活動の記録やユーザーの状態情報
 */
public class SummaryRepository extends BaseRepository {

    private static final Class<Summary> ENTITY_CLASS = Summary.class;

    /**
     * uid
     */
    private static final String PATH = "/user/log/%s/summary";

    private String getPath(String uid) {
        return String.format(PATH, uid);
    }

    public void get(String uid, final EntityEventListener<Summary> listener) {
        DatabaseReference ref = firebaseDatabase.getReference(getPath(uid));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot.getValue(ENTITY_CLASS));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onError(error);
            }
        });
    }

    public Task update(String uid, Summary entity, OnCompleteListener completionListener) {
        return FirebaseUtil.getDatabaseReference().child(getPath(uid)).setValue(entity)
                .addOnCompleteListener(completionListener);
    }

}
