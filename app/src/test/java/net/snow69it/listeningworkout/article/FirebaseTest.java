package net.snow69it.listeningworkout.article;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.snow69it.listeningworkout.util.FirebaseUtil;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FirebaseTest {
    @Test
    public void convertApiToArticleJa() throws Exception {
        FirebaseDatabase database = FirebaseUtil.getFirebaseDatabase();
        DatabaseReference myRef = database.getReference("/article");

        Log.i("FirebaseTest", "START ");
//        myRef.addValueEventListener(new ValueEventListener() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.i("FirebaseTest", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("FirebaseTest", "Failed to read value.", error.toException());
            }
        });

    }


}