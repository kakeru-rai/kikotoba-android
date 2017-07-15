package com.kikotoba.android.model.audio;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.kikotoba.android.R;
import com.kikotoba.android.model.WorkingDirectory;
import com.kikotoba.android.model.entity.master.ArticlePair;
import com.kikotoba.android.util.FirebaseUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by raix on 2017/05/03.
 */

public class AudioDownloadTask {

    private StorageReference mAudioReference;
    private ArticlePair mArticle;
    private String language;
    private Context mContext;
    private AudioDownloadTaskListener mListener;

    public AudioDownloadTask(Context context, ArticlePair articlePair, String language, AudioDownloadTaskListener listener) {
        mAudioReference = FirebaseUtil.getStorageReference(articlePair, language, context.getString(R.string.firebase_storage_base_url));
        mArticle = articlePair;
        this.language = language;
        mContext = context;
        mListener = listener;
    }

    public void exec() {
        List<FileDownloadTask> tasks = mAudioReference.getActiveDownloadTasks();

        try {
            if (tasks.size() > 0) {
                FileDownloadTask task = tasks.get(0);
                startTask(task);
            } else {
                startNewDownloadTask();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startNewDownloadTask() throws IOException {
        WorkingDirectory wd = WorkingDirectory.getInstance();
        final File cacheFile = wd.createAudioCache();
        final File toFile = wd.createAudioDestinationFile(mContext, mArticle, language);

        FileUtils.forceMkdirParent(toFile);

        FileDownloadTask task = mAudioReference.getFile(toFile);
        startTask(task);
    }

    private void startTask(FileDownloadTask task) throws IOException {
        WorkingDirectory wd = WorkingDirectory.getInstance();
        final File cacheFile = wd.createAudioCache();
        final File toFile = wd.createAudioDestinationFile(mContext, mArticle, language);

        task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                mListener.onSuccess();

//                try {
////                    FileUtils.copyFile(cacheFile, toFile);
//
////                    File f = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "hoge.mp3");
////                    boolean mkdir = toFile.getParentFile().mkdir();
////                    FileUtil.copyFile(new FileInputStream(cacheFile),
////                            new FileOutputStream(f));
//
//                    mListener.onSuccess();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    mListener.onFailure(e);
//                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
                mListener.onFailure(exception);
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests") // taskSnapshot.getTotalByteCount()のため https://stackoverflow.com/questions/41105586/android-firebase-tasksnapshot-method-should-only-be-accessed-within-privat
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                mListener.onProgress(taskSnapshot.getTotalByteCount(), taskSnapshot.getBytesTransferred());
            }
        });
    }

    public interface AudioDownloadTaskListener {
        void onSuccess();
        void onFailure(Exception exception);
        void onProgress(long max, long progress);
    }
}
