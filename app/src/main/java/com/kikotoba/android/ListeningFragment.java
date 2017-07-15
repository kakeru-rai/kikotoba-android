package com.kikotoba.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.kikotoba.android.model.entity.master.ArticlePair;
import com.kikotoba.android.model.entity.user.UserLogByArticle;
import com.kikotoba.android.model.listening.AudioController;
import com.kikotoba.android.model.listening.ViewerWebView;
import com.kikotoba.android.model.listening.WebAppInterface;
import com.kikotoba.android.repository.BaseRepository;
import com.kikotoba.android.repository.UserLogRepository;
import com.kikotoba.android.util.Pref;
import com.kikotoba.android.util.WebChromeClientDefault;

import butterknife.BindView;
import butterknife.ButterKnife;

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
//    private Article mTargetArticle;
//    private Article mTranscriptArticle;

    private View mRootView;

    @BindView(R.id.nowShadowingPopup) TextView mNowShadowingPopup;
    @BindView(R.id.btnRepeat) ImageView mBtnRepeat;
    @BindView(R.id.btnBlind) ImageView mBtnBlind;
    @BindView(R.id.progressBar) View mProgressBar;



    private View.OnClickListener repeaBtntListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean enabled = mWebAppInterface.toggleRepeatMode();
            mBtnRepeat.setSelected(enabled);

            String onoff = enabled ? ": On" : ": Off";
            Toast.makeText(getActivity(), getString(R.string.listening_repeat_mode) + onoff, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    private View.OnClickListener blindBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean enabled = mWebAppInterface.toggleBlindMode();
            mBtnBlind.setSelected(enabled);

            String onoff = enabled ? ": On" : ": Off";
            Toast.makeText(getActivity(), getString(R.string.listening_blind_mode) + onoff, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflateRootView(R.layout.fragment_listening, container, inflater);
        ButterKnife.bind(this, mRootView);

        mMediaController = (AudioController) mRootView.findViewById(R.id.mediaController);

        ArticlePair entity = ArticlePair.fromJson(getArguments().getString(ARG_ARTICLE_PAIR, ""));

//        mTargetArticle = entity._getTarget();
//        mTranscriptArticle = entity._getTranslated();
        setHasOptionsMenu(true);
        init(mRootView);

        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.listening, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Pref pref = new Pref(getActivity());
        switch (pref.getSpeechGap()) {
            case NORMAL:
                menu.findItem(R.id.action_speech_gap_normal).setChecked(true);
                break;
            case REPETITION:
                menu.findItem(R.id.action_speech_gap_repetition).setChecked(true);
                break;
            case SHADOWING:
                menu.findItem(R.id.action_speech_gap_shadowing).setChecked(true);
                break;
            default:
                menu.findItem(R.id.action_speech_gap_normal).setChecked(true);
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_speech_gap_normal:
            case R.id.action_speech_gap_repetition:
            case R.id.action_speech_gap_shadowing:
                item.setChecked(!item.isChecked());

                onChangeSpeechGap(Pref.SpeechGap.of(id));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onChangeSpeechGap(Pref.SpeechGap speechGap) {
        Pref pref = new Pref(getActivity());
        pref.putSpeechGap(speechGap);
        mWebAppInterface.setSpeechGap(getActivity(), speechGap);
    }

    private void init(View rootView) {
        webView = (ViewerWebView) rootView.findViewById(R.id.webview);
        webView.setWebChromeClient(new WebChromeClientDefault(webView) {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        mBtnRepeat.setOnClickListener(repeaBtntListener);
        mBtnBlind.setOnClickListener(blindBtnListener);

        mWebAppInterface = new WebAppInterface(
                webView,
                mMediaController,
                getOwner().getArticlePair(),
                getCurrentReadingIndex(),
                mNowShadowingPopup);
        Pref pref = new Pref(getActivity());
        mWebAppInterface.setSpeechGap(getActivity(), pref.getSpeechGap());
        mWebAppInterface.load(getActivity());
    }

    private ListeningActivity getOwner() {
        return (ListeningActivity) getActivity();
    }

    private int getCurrentReadingIndex() {
        return getArguments().getInt(ARG_CURRENT_READING_INDEX, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebAppInterface != null) {
            Pref pref = new Pref(getActivity());
            mWebAppInterface.setSpeechGap(getActivity(), pref.getSpeechGap());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mWebAppInterface.pause();
        final int partIndex = getOwner().getPartIndex();
        final UserLogRepository repo = new UserLogRepository();
        final ArticlePair articlePair = getOwner().getArticlePair();
        repo.getUserLogByArticle(getUser().getUid(), getOwner().getArticlePair()._getId(), new BaseRepository.EntityEventListener<UserLogByArticle>() {
            @Override
            public void onSuccess(UserLogByArticle entity) {
                if (mWebAppInterface == null) {
                    return;
                }
                if (entity == null) {
                    entity = new UserLogByArticle();
                }
                repo.setListeningPlaybackTime(
                        getUser().getUid(),
                        articlePair._getId(),
                        mWebAppInterface.clearPlaybackTimeSec() + entity.getListeningPlaybackTime());
                repo.setCurrentReadingIndex(
                        getUser().getUid(),
                        articlePair._getId(),
                        partIndex,
                        articlePair._getTarget().getSentences().size() - 1 > mWebAppInterface.getCurrentIndex() ? mWebAppInterface.getCurrentIndex() : 0);
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }
}