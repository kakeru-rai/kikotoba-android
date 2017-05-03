package net.snow69it.listeningworkout;

import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import net.snow69it.listeningworkout.article.Article;
import net.snow69it.listeningworkout.article.ArticlePair;
import net.snow69it.listeningworkout.article.Sentence;
import net.snow69it.listeningworkout.entity.UserLogByArticle;
import net.snow69it.listeningworkout.repository.ArticleRepository;
import net.snow69it.listeningworkout.repository.BaseRepository;
import net.snow69it.listeningworkout.repository.UserLogRepository;
import net.snow69it.listeningworkout.speaking.JudgeSpeech;
import net.snow69it.listeningworkout.speaking.WebAppInterface;
import net.snow69it.listeningworkout.util.WebViewDefault;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class SpeakingFragment extends Fragment {

    private static final String ARG_ARTICLE_ID = "article_id";
    private static final String ARG_POSITION = "position";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SpeakingFragment newInstance(String articleId, int position) {
        SpeakingFragment fragment = new SpeakingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_ID, articleId);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.speech_status) TextView speechStatus;
    @BindView(R.id.speech_result) TextView speechResult;
    @BindView(R.id.speaking_correct) TextView correct;
    @BindView(R.id.speaking_incorrect) TextView incorrect;
    @BindView(R.id.is_cleared) TextView tvCleared;

    @BindView(R.id.buttonPlay) Button mPlayButton;
    @BindView(R.id.buttonSpeech) Button mSpeechButton;

    @BindView(R.id.webview) WebViewDefault webView;

    private boolean isCleared = false;
    private boolean isCorrectSpeech = false;
    private boolean hasSpeechResult = false;
    private String sentenceText;
    private Article mArticle;

    private Handler mHandler = new Handler();
    private WebAppInterface mWebAppInterface;
    private View.OnClickListener mPlayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Sentence s = mArticle.getSentences().get(getTrack());
            mWebAppInterface.play(s.getFromSec(), s.getToSec());
        }
    };

    private View.OnClickListener mSpeechOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((SpeakingActivity)getActivity()).startSpeech();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_speaking, container, false);

        ButterKnife.bind(this, rootView);
        ArticleRepository repo = new ArticleRepository();
        repo.getArticle(getArticleId(), new BaseRepository.EntityEventListener<ArticlePair>() {
            @Override
            public void onSuccess(ArticlePair entity) {
                init(entity);
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });

        // 回答状況ロード
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final UserLogRepository userLogRepo = new UserLogRepository();
        userLogRepo.getUserLogByArticle(user.getUid(), getArticleId(), new BaseRepository.EntityEventListener<UserLogByArticle>() {
            @Override
            public void onSuccess(UserLogByArticle entity) {
                if (entity != null && entity.isSpeakingCorrect(getTrack())) {
                    tvCleared.setText(R.string.is_cleared);
                }
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mWebAppInterface.pause();
    }

    private void init(ArticlePair entity) {
        mArticle = entity.getTarget();
        Sentence sentence = mArticle.getSentences().get(getTrack());
        sentenceText = sentence.getText();

        refreshView();

        mPlayButton.setOnClickListener(mPlayOnClickListener);
        mSpeechButton.setOnClickListener(mSpeechOnClickListener);

        mWebAppInterface = createDictationWebInterface(webView, mArticle, getTrack());
        mWebAppInterface.load(getActivity());
//        File sd = IOUtil.getPrivateExternalDir(getActivity(), "");
//        String url = "file://" + sd.getPath() + "/html/speaking/index.html";
//        webView.loadUrl(url);
    }

    private WebAppInterface createDictationWebInterface(WebView webView, Article article, int sentenceIndex){
        return new WebAppInterface(webView, article, sentenceIndex) {
            @JavascriptInterface
            @Override
            public void onReady() {
                mHandler.post(new Runnable() {
                    public void run() {
                        mWebAppInterface.setAudioSrc(mArticle.getAudio());
                        mWebAppInterface.setText(sentenceText);
                    }
                });
            }
        };
    }

    private int getTrack() {
        return getArguments().getInt(ARG_POSITION, 0);
    }

    private String getArticleId() {
        return getArguments().getString(ARG_ARTICLE_ID, "");
    }

    public void onSpeechPreparing() {
        speechResult.setVisibility(View.GONE);
        speechStatus.setText(R.string.speaking_speech_preparing);
        speechStatus.setVisibility(View.VISIBLE);
        correct.setVisibility(View.GONE);
        incorrect.setVisibility(View.GONE);
    }

    public void onReadyForSpeech() {
        ButterKnife.bind(this, getView());
        speechStatus.setText(R.string.speaking_speech_ready);
    }

    public void onSpeechResult(String speechText) {
        speechResult.setVisibility(View.VISIBLE);
        speechResult.setText(speechText);
        speechStatus.setVisibility(View.GONE);

        hasSpeechResult = true;
        isCorrectSpeech = JudgeSpeech.validate(sentenceText, speechText);

        if (isCorrectSpeech) {
            updateCorrectArticleLog();
            tvCleared.setText(R.string.is_cleared);
        }

        refreshView();
    }

    public void onSpeechError(int errorCode) {
        speechResult.setVisibility(View.GONE);
        speechStatus.setVisibility(View.GONE);

        // メッセージ表示
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_NETWORK:
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = getString(R.string.speaking_speech_error_network);
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = getString(R.string.speaking_speech_error_retry);
                break;
            default:
                message = getString(R.string.speaking_speech_error)
                        + String.valueOf(errorCode);
        }
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .show();
    }

    private void updateCorrectArticleLog() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final UserLogRepository repo = new UserLogRepository();
        repo.getUserLogByArticle(user.getUid(), getArticleId(), new BaseRepository.EntityEventListener<UserLogByArticle>() {
            @Override
            public void onSuccess(UserLogByArticle entity) {
                if (entity == null) {
                    entity = new UserLogByArticle();
                }
                entity.setSpeakingCorrectByIndex(getTrack());
                Task task = repo.setUserLogByArticle(user.getUid(), getArticleId(), entity);
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }

    private void refreshView() {
//        tvCleared.setVisibility(isCleared ? View.VISIBLE : View.GONE);

        if (!hasSpeechResult) {
            correct.setVisibility(View.GONE);
            incorrect.setVisibility(View.GONE);
            speechResult.setVisibility(View.GONE);
            speechStatus.setVisibility(View.GONE);
        } else if (isCorrectSpeech) {
            correct.setVisibility(View.VISIBLE);
            incorrect.setVisibility(View.GONE);
        } else if (!isCorrectSpeech) {
            correct.setVisibility(View.GONE);
            incorrect.setVisibility(View.VISIBLE);
        } else {
            // 予期せぬ状態
        }

    }

}