package com.kikotoba.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseError;

import com.kikotoba.android.model.entity.Article;
import com.kikotoba.android.model.entity.ArticlePair;
import com.kikotoba.android.model.entity.UserLogByArticle;
import com.kikotoba.android.model.listening.AudioController;
import com.kikotoba.android.model.listening.ViewerWebView;
import com.kikotoba.android.model.listening.WebAppInterface;
import com.kikotoba.android.repository.BaseRepository;
import com.kikotoba.android.repository.UserLogRepository;
import com.kikotoba.android.util.Pref;

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
    private static final String ARG_CURRENT_READING_INDEX = "current_index";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ListeningFragment newInstance(ArticlePair articlePair, int currentIndex) {
        ListeningFragment fragment = new ListeningFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_PAIR, articlePair.toJson());
        args.putInt(ARG_CURRENT_READING_INDEX, currentIndex);
        fragment.setArguments(args);
        return fragment;
    }

    private AudioController mMediaController;
    private ViewerWebView webView;
    private WebAppInterface mWebAppInterface;
    private Article mTargetArticle;
    private Article mTranscriptArticle;

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

        mWebAppInterface = new WebAppInterface(webView, mMediaController, mTargetArticle, mTranscriptArticle, getCurrentReadingIndex());
        Pref pref = new Pref(getActivity());
        mWebAppInterface.setSpeed(pref.getSpeechSpeed());
        mWebAppInterface.load(getActivity());
    }

    private int getCurrentReadingIndex() {
        return getArguments().getInt(ARG_CURRENT_READING_INDEX, 0);
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
                repo.setCurrentReadingIndex(
                        getUser().getUid(),
                        mTargetArticle.getId(),
                        mTargetArticle.getSentences().size() - 1 > mWebAppInterface.getCurrentIndex() ? mWebAppInterface.getCurrentIndex() : 0);
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }
}