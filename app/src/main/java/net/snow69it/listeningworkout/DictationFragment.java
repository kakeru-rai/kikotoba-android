package net.snow69it.listeningworkout;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import net.snow69it.listeningworkout.article.Article;
import net.snow69it.listeningworkout.article.ArticlePair;
import net.snow69it.listeningworkout.article.Sentence;
import net.snow69it.listeningworkout.dictation.BlankTokenPicker;
import net.snow69it.listeningworkout.dictation.DictationWebInterface;
import net.snow69it.listeningworkout.dictation.TextToken;
import net.snow69it.listeningworkout.dictation.TextTokenParser;
import net.snow69it.listeningworkout.entity.UserLogByArticle;
import net.snow69it.listeningworkout.repository.ArticleRepository;
import net.snow69it.listeningworkout.repository.BaseRepository;
import net.snow69it.listeningworkout.repository.UserLogRepository;
import net.snow69it.listeningworkout.util.WebViewDefault;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DictationFragment extends Fragment {

    private static final String TAG = "DictationFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_ARTICLE_ID = "article_id";
    private static final String ARG_POSITION = "position";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DictationFragment newInstance(String articleId, int position) {
        DictationFragment fragment = new DictationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_ID, articleId);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.webview) WebViewDefault webView;
    @BindView(R.id.buttonSubmit) Button buttonSubmit;
    @BindView(R.id.buttonPlay) Button buttonPlay;
    @BindView(R.id.is_cleared) TextView textViewIsCleared;

    private DictationWebInterface mDictationWebInterface;
    private List<TextToken> textTokenList;
    private Handler mHandler = new Handler();
    private Article mArticle;

    private View.OnClickListener mPlayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mDictationWebInterface.play();
            Sentence s = mArticle.getSentences().get(getTrack());
            mDictationWebInterface.play(s.getFromSec(), s.getToSec());
        }
    };

    private View.OnClickListener mSubmitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            submit();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dictation, container, false);
        ButterKnife.bind(this, rootView);

        // テキストロード
        ArticleRepository repo = new ArticleRepository();
        repo.queryArticle(getArticleId(), new BaseRepository.EntityEventListener<ArticlePair>() {
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
                if (entity != null && entity.isDictationCorrect(getTrack())) {
                    textViewIsCleared.setText(R.string.is_cleared);
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
        mDictationWebInterface.pause();
    }

    private void init(ArticlePair entity) {
//        mArticle = ArticleApi.getArticleEn(getActivity());

        mArticle = entity.getTarget();
        Sentence sentence = mArticle.getSentences().get(getTrack());


        // 虫食いviewを生成

        buttonSubmit.setOnClickListener(mSubmitOnClickListener);
        buttonPlay.setOnClickListener(mPlayOnClickListener);


        try {
            textTokenList = createTextToken(sentence.getText());
//            for (View view : blankTextViewList) {
//                blankTextContainer.addViewNewLine(view);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDictationWebInterface = createDictationWebInterface(webView);
        mDictationWebInterface.load(getActivity());

//        File sd = IOUtil.getPrivateExternalDir(getActivity(), "");
//        String url = "file://" + sd.getPath() + "/html/dictation/index.html";
//        webView.loadUrl(url);

    }

    /**
     *
     * @param text
     * @return
     * @throws Exception
     */
    private List<TextToken> createTextToken(String text) throws Exception {
        List<View> views = new ArrayList<>();

        TextTokenParser parser = new TextTokenParser();
        List<TextToken> tokenList = parser.parse(text);
        BlankTokenPicker picker = new BlankTokenPicker();
        picker.pick(tokenList);
        return tokenList;
    }

    private DictationWebInterface createDictationWebInterface(WebView webView){
        return new DictationWebInterface(webView) {
            @JavascriptInterface
            @Override
            public void submitCallback(boolean isCorrect) {
                if (!isCorrect) {
                    return;
                }
                updateCorrectArticleLog();
                mHandler.post(new Runnable() {
                    public void run() {
                        textViewIsCleared.setText(R.string.is_cleared);
                    }
                });
            }

            @JavascriptInterface
            @Override
            public void onReady() {
                mHandler.post(new Runnable() {
                    public void run() {
                        mDictationWebInterface.setAudioSrc(mArticle.getAudio());
//                        mDictationWebInterface.setAudioSrc(mArticle.getId(), getTrack());
                        // 穴埋めテキストを初期化
                        for (TextToken token : textTokenList) {
                            if (token.isBlank) {
                                addInput(token.text);
                            } else {
                                addText(token.text);
                            }
                        }
                    }
                });
            }
        };
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
                entity.setDictationCorrectByIndex(getTrack());
                Task task = repo.setUserLogByArticle(user.getUid(), getArticleId(), entity);
                task.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.getException() != null) {
                            task.getException().printStackTrace();
                        }
//                        Log.v(TAG, "" + task.getResult());
                        Log.v(TAG, task.toString());
                    }
                });
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }

    private int getTrack() {
        return getArguments().getInt(ARG_POSITION, 0);
    }

    private String getArticleId() {
        return getArguments().getString(ARG_ARTICLE_ID, "");
    }

    public void submit() {
        mDictationWebInterface.submit();
    }
}