package com.kikotoba.android.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kikotoba.android.model.entity.master.ArticlePair;

/**
 * Created by raix on 2017/03/12.
 */

public class FirebaseUtil {

    private static FirebaseDatabase firebaseDatabase;
    public static synchronized FirebaseDatabase getFirebaseDatabase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        return firebaseDatabase;
    }

    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static StorageReference getStorageReference(ArticlePair articlePair, String language, String baseUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(baseUrl);
        StorageReference pathReference = storageRef.child(getAudioPath(articlePair, language));
        return pathReference;
    }

    private static String getAudioPath(ArticlePair articlePair, String language) {
        return String.format("article/%s/%s_%d.mp3",
                articlePair._getId(),
                language,
                articlePair.getLanguage().get(language).getAudioVersion()
        );
    }
}
