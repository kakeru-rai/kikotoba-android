
package net.snow69it.listeningworkout.strage;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.snow69it.listeningworkout.WaitUtil;
import net.snow69it.listeningworkout.model.entity.Article;
import net.snow69it.listeningworkout.util.FileUtil;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AudioStrageTest {

    private Article target = null;
    private Article translated = null;

    @Test
    public void useAppFirebase() throws Exception {
        final WaitUtil waitUtil = new WaitUtil();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://listeningworkout.appspot.com/");
        StorageReference pathReference = storageRef.child("article/gutenberg_001/gutenberg_001.mp3");

        File localFile = File.createTempFile("images", "jpg");

        pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                waitUtil.hasDone();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                assertTrue(false);
            }
        });
        waitUtil.waitForDone();

        try {
            File toDir = FileUtil.createPrivateExternalDir(InstrumentationRegistry.getContext(), "/asdf001/asdf002");
            File toFile = new File(toDir.getParentFile(), "asdf.mp3");
            FileUtils.copyFile(localFile, toFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        assertTrue(true);
    }
}
