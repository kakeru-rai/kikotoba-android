package net.snow69it.listeningworkout;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseError;

import net.snow69it.listeningworkout.model.entity.Article;
import net.snow69it.listeningworkout.model.entity.ArticlePair;
import net.snow69it.listeningworkout.model.listening.AudioController;
import net.snow69it.listeningworkout.model.entity.UserLogByArticle;
import net.snow69it.listeningworkout.repository.BaseRepository;
import net.snow69it.listeningworkout.repository.UserLogRepository;
import net.snow69it.listeningworkout.util.Pref;
import net.snow69it.listeningworkout.model.listening.ViewerWebView;
import net.snow69it.listeningworkout.model.listening.WebAppInterface;

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

        mMediaController = (AudioController) mRootView.findViewById(R.id.mediaController);

        ArticlePair entity = ArticlePair.fromJson(getArguments().getString(ARG_ARTICLE_PAIR, ""));

        mTargetArticle = entity.getTarget();
        mTranscriptArticle = entity.getTranslated();

        init(mRootView);

        return mRootView;
    }

    private void init(View rootView) {
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