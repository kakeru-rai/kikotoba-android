package net.snow69it.listeningworkout;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.StorageReference;

import net.snow69it.listeningworkout.article.Article;
import net.snow69it.listeningworkout.article.ArticlePair;
import net.snow69it.listeningworkout.audio.AudioController;
import net.snow69it.listeningworkout.entity.UserLogByArticle;
import net.snow69it.listeningworkout.repository.BaseRepository;
import net.snow69it.listeningworkout.repository.UserLogRepository;
import net.snow69it.listeningworkout.util.Pref;
import net.snow69it.listeningworkout.viewer.ViewerWebView;
import net.snow69it.listeningworkout.viewer.WebAppInterface;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListeningFragment extends BaseFragment {

    private static final String TAG = "ListeningFragment";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_ARTICLE_PAIR = "article_pair";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ListeningFragment newInstance(ArticlePair articlePair) {
        ListeningFragment fragment = new ListeningFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_PAIR, articlePair.toJson());
        fragment.setArguments(args);
        return fragment;
    }

    private AudioController mMediaController;
    private ViewerWebView webView;
    private WebAppInterface mWebAppInterface;
    private Article mTargetArticle;
    private Article mTranscriptArticle;

    private Handler handler = new Handler();

    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflateRootView(R.layout.fragment_listening, container, inflater);
//        final View mRootView = inflater.inflate(R.layout.fragment_listening, container, false);

        mMediaController = (AudioController) mRootView.findViewById(R.id.mediaController);

        ArticlePair entity = ArticlePair.fromJson(getArguments().getString(ARG_ARTICLE_PAIR, ""));

        mTargetArticle = entity.getTarget();
        mTranscriptArticle = entity.getTranslated();

        init(mRootView);

//        try {
//            WorkingDirectory wd = new WorkingDirectory();
//            final File toFile = wd.createAudioDestinationFile(getActivity(), mTargetArticle);
//            if (toFile.exists()) {
//                init(mRootView);
//            } else {
//                downloadAudio(mTargetArticle, mRootView);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//
//        ArticleRepository repo = new ArticleRepository();
//        repo.getArticle(articleId, new BaseRepository.EntityEventListener<ArticlePair>() {
//            @Override
//            public void onSuccess(ArticlePair entity) {
//                mTargetArticle = entity.getTarget();
//                mTranscriptArticle = entity.getTranslated();
//
//                try {
//                    WorkingDirectory wd = new WorkingDirectory();
//                    final File toFile = wd.createAudioDestinationFile(getActivity(), mTargetArticle);
//                    if (toFile.exists()) {
//                        init(mRootView);
//                        return;
//                    } else {
//                        downloadAudio(mTargetArticle, mRootView);
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(DatabaseError error) {
//                // TODO: エラーハンドラ
//                error.toException().printStackTrace();
//            }
//        });

        return mRootView;
    }

    private StorageReference mAudioReference;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's a download in progress, save the reference so you can query it later
        if (mAudioReference != null) {
            outState.putString("reference", mAudioReference.toString());
        }
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        // If there was a download in progress, get its reference and create a new StorageReference
//        if (savedInstanceState == null) {
//            return;
//        }
//        final String stringRef = savedInstanceState.getString("reference");
//        if (stringRef == null) {
//            return;
//        }
//        mAudioReference = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);
//
//        // Find all DownloadTasks under this StorageReference (in this example, there should be one)
//        List<FileDownloadTask> tasks = mAudioReference.getActiveDownloadTasks();
//        if (tasks.size() > 0) {
//            // Get the task monitoring the download
//            FileDownloadTask task = tasks.get(0);
//            try {
//                downloadAudio(task);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private void downloadAudio(Article mTargetArticle, final View rootView) throws IOException {
//        WorkingDirectory wd = new WorkingDirectory();
//        final File cacheFile = wd.createAudioCache();
//        final File toFile = wd.createAudioDestinationFile(getActivity(), mTargetArticle);
//
//
//        mAudioReference = FirebaseUtil.getStorageReference(mTargetArticle);
//        FileDownloadTask task = null;
//        List<FileDownloadTask> tasks = mAudioReference.getActiveDownloadTasks();
//        if (tasks.size() > 0) {
//            task = tasks.get(0);
//        } else {
//            task = mAudioReference.getFile(cacheFile);
//        }
//        downloadAudio(task);
//    }
//
//    private void downloadAudio(FileDownloadTask task) throws IOException {
//        WorkingDirectory wd = new WorkingDirectory();
//        final File cacheFile = wd.createAudioCache();
//        final File toFile = wd.createAudioDestinationFile(getActivity(), mTargetArticle);
//
//        task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                try {
//                    FileUtils.copyFile(cacheFile, toFile);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                init(mRootView);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                exception.printStackTrace();
//            }
//        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//            }
//        });
//    }

    private void init(View rootView) {
//        File sd = IOUtil.getPrivateExternalDir(getActivity(), "");
//        Log.v("####### ", "###########################");
//        D.dir(sd);
//        Log.v("####### ", "###########################");
//
//        FileUtil fileUtil = new FileUtil(getActivity());
//        try {
//            fileUtil.copyFiles(null, "", sd);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//

        webView = (ViewerWebView) rootView.findViewById(R.id.webview);

        mWebAppInterface = new WebAppInterface(webView, mMediaController, mTargetArticle, mTranscriptArticle);
        Pref pref = new Pref(getActivity());
        mWebAppInterface.setSpeed(pref.getSpeechSpeed());
        mWebAppInterface.load(getActivity());
    }

    public void onResume() {
        super.onResume();
        if (mWebAppInterface != null) {
            Pref pref = new Pref(getActivity());
            mWebAppInterface.setSpeed(pref.getSpeechSpeed());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mWebAppInterface.pause();
        final UserLogRepository repo = new UserLogRepository();
        repo.getUserLogByArticle(getUser().getUid(), mTargetArticle.getId(), new BaseRepository.EntityEventListener<UserLogByArticle>() {
            @Override
            public void onSuccess(UserLogByArticle entity) {
                if (mWebAppInterface == null || mWebAppInterface == null) {
                    return;
                }
                if (entity == null) {
                    entity = new UserLogByArticle();
                }
                repo.setListeningPlaybackTime(
                        getUser().getUid(),
                        mTargetArticle.getId(),
                        mWebAppInterface.clearPlaybackTimeSec() + entity.getListeningPlaybackTime());

            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }
}