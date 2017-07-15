package com.kikotoba.android;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kikotoba.android.model.dictation.BlankTokenPicker;
import com.kikotoba.android.model.dictation.DictationScore;
import com.kikotoba.android.model.dictation.DictationWebInterface;
import com.kikotoba.android.model.dictation.TextToken;
import com.kikotoba.android.model.dictation.TextTokenParser;
import com.kikotoba.android.model.entity.master.Article;
import com.kikotoba.android.model.entity.master.Sentence;
import com.kikotoba.android.util.Util;
import com.kikotoba.android.util.Versatile;
import com.kikotoba.android.util.WebViewDefault;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DictationFragment extends Fragment {

    private static final String TAG = "DictationFragment";

    private static final String ARG_SENTENCE_POSITION = "sentence_position";
    private static final String ARG_PAGER_POSITION = "pager_position";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DictationFragment newInstance(int sentencePosition, int pagerPosition) {
        DictationFragment fragment = new DictationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SENTENCE_POSITION, sentencePosition);
        args.putInt(ARG_PAGER_POSITION, pagerPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.webview) WebViewDefault webView;
    @BindView(R.id.buttonSubmit) Button buttonSubmit;
    @BindView(R.id.buttonPlay) Button buttonPlay;
    @BindView(R.id.dictationTextCleared) TextView textViewIsCleared;
    @BindView(R.id.buttonNext) TextView buttonNext;
    @BindView(R.id.buttonStart) Button buttonStart;
    @BindView(R.id.textQuestionNo) TextView textQuestionNo;
    @BindView(R.id.dictationStartLayout) View dictationStartLayout;
    @BindView(R.id.dictationFinishLayout) View dictationFinishLayout;
    @BindView(R.id.buttonFinish) View buttonFinish;

    @BindView(R.id.dictationNormalButtonLayout) View dictationNormalButtonLayout;
    @BindView(R.id.dictationNextButtonLayout) View dictationNextButtonLayout;

    @BindView(R.id.textViewTranslation) TextView textViewTranslation;
    @BindView(R.id.buttonGiveUp) View buttonGiveUp;


    @BindView(R.id.dictationCorrectAnswerIcon) View correctAnswerIcon;
    @BindView(R.id.dictationCorrectAnswerCount) TextView dictationCorrectAnswerCount;
    @BindView(R.id.dictationLifeIcon) View lifeIcon;
    @BindView(R.id.dictationLifeCount) TextView dictationLifeCount;

    @BindView(R.id.dictationFinishLifeCount) TextView finishLifeCount;
    @BindView(R.id.dictationFinishLifeMaxCount) TextView finishLifeMaxCount;
    @BindView(R.id.dictationFinishCorrectAnswerCount) TextView finishCorrectAnswerCount;
    @BindView(R.id.dictationFinishCorrectAnswerMaxCount) TextView finishCorrectAnswerMaxCount;
    @BindView(R.id.dictationFinishMessage) TextView finishMessage;

    @BindView(R.id.dictationStarFinish1) ImageView dictationStarFinish1;
    @BindView(R.id.dictationStarFinish2) ImageView dictationStarFinish2;
    @BindView(R.id.dictationStarFinish3) ImageView dictationStarFinish3;

    private DictationWebInterface mDictationWebInterface;
    private List<TextToken> textTokenList;
    private Handler mHandler = new Handler();
    private Article mArticle;
    private Article mTargetArticle;

    private View.OnClickListener mStartOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getOwner().play(getSentencePosition());
            dictationStartLayout.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener mPlayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getOwner().play(getSentencePosition());
            getOwner().getDictationScore().decrementLife();
            refreshScoreCount();
            Util.startAnimationAttention(lifeIcon, 0);
        }
    };

    private View.OnClickListener mSubmitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Versatile.dismissIME(getActivity(), view);
            submit();
        }
    };

    private View.OnClickListener mNextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getOwner().isLastQuestion()) {
                getOwner().updateDictationScore();
                showCompleteState();
            } else {
                getOwner().next();
            }
        }
    };

    private View.OnClickListener giveUpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showGiveUpState();
            mDictationWebInterface.showAnswer();
        }
    };

    private View.OnClickListener mFinishOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().finish();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dictation, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        initVar();
        initWebView();
        return rootView;
    }

    private void initVar() {
        mArticle = getArticle();
        mTargetArticle = getTranslationArticle();
    }

    public void initView() {
        // view
        textQuestionNo.setText(getString(R.string.dictation_question_number, getPagerPosition() + 1));
        textViewIsCleared.setVisibility(View.GONE);

        dictationStartLayout.setVisibility(View.VISIBLE);
        dictationFinishLayout.setVisibility(View.GONE);

        dictationNormalButtonLayout.setVisibility(View.VISIBLE);
        dictationNextButtonLayout.setVisibility(View.GONE);
//        dictationGiveUpButtonLayout.setVisibility(View.GONE);

        textViewTranslation.setVisibility(View.GONE);
        buttonGiveUp.setVisibility(View.GONE);

        // listener
        buttonStart.setOnClickListener(mStartOnClickListener);
        buttonSubmit.setOnClickListener(mSubmitOnClickListener);
        buttonPlay.setOnClickListener(mPlayOnClickListener);
        buttonNext.setOnClickListener(mNextOnClickListener);
        buttonFinish.setOnClickListener(mFinishOnClickListener);
        buttonGiveUp.setOnClickListener(giveUpOnClickListener);

        finishLifeMaxCount.setText(String.valueOf(DictationScore.MAX_LIFE_COUNT));
        finishCorrectAnswerMaxCount.setText(String.valueOf(getOwner().getDictationScore().getQuestionCount()));
    }

    private void initWebView() {
        // 虫食いviewを生成
        Sentence sentence = mArticle.getSentences().get(getSentencePosition());
        try {
            textTokenList = createTextToken(sentence.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDictationWebInterface = createDictationWebInterface(webView);
    }

    public void onPageSelected() {
        refreshScoreCount();
    }

    /**
     * @param text
     * @return
     * @throws Exception
     */
    private List<TextToken> createTextToken(String text) throws Exception {
        TextTokenParser parser = new TextTokenParser();
        List<TextToken> tokenList = parser.parse(text);

        BlankTokenPicker picker = new BlankTokenPicker(getOwner().getLevel());
        picker.pick(tokenList);
        return tokenList;
    }

    private DictationWebInterface createDictationWebInterface(WebView webView){
        return new DictationWebInterface(webView) {
            @JavascriptInterface
            @Override
            public void submitCallback(final boolean isCorrect) {
//                _setScore();
                mHandler.post(new Runnable() {
                    public void run() {
                        if (isCorrect) {
                            showCorrectSubmitState();
                            getOwner().getDictationScore().incrementCorrectAnswer();
                            Util.startAnimationAttention(correctAnswerIcon, 0);
                        } else {
                            showInCorrectSubmitState();
                            getOwner().getDictationScore().decrementLife();
                            Util.startAnimationAttention(lifeIcon, 0);
                        }
                        refreshScoreCount();
                    }
                });
            }

            @JavascriptInterface
            @Override
            public void onReady() {
                mHandler.post(new Runnable() {
                    public void run() {
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

    private int getSentencePosition() {
        return getArguments().getInt(ARG_SENTENCE_POSITION, 0);
    }

    private int getPagerPosition() {
        return getArguments().getInt(ARG_PAGER_POSITION, 0);
    }

    private Article getArticle() {
        return getOwner().getArticle();
    }

    private Article getTranslationArticle() {
        return getOwner().getTranslationArticle();
    }


    private void submit() {
        mDictationWebInterface.submit();
    }

    private void showTranslation() {
        int translationIndex = mArticle.getSentences().get(getSentencePosition()).getTranslationIndex();
        String text = mTargetArticle.getTranslations().get(translationIndex);
        textViewTranslation.setText(text);
        textViewTranslation.setVisibility(View.VISIBLE);
    }

    private void showGiveUpState() {
        showTranslation();

        buttonGiveUp.setVisibility(View.GONE);

        dictationNormalButtonLayout.setVisibility(View.GONE);
        dictationNextButtonLayout.setVisibility(View.VISIBLE);
    }

    private void showCorrectSubmitState() {
        showTranslation();

        buttonGiveUp.setVisibility(View.GONE);

        dictationNormalButtonLayout.setVisibility(View.GONE);
        dictationNextButtonLayout.setVisibility(View.VISIBLE);

        textViewIsCleared.setVisibility(View.VISIBLE);
    }

    private void showInCorrectSubmitState() {
        buttonGiveUp.setVisibility(View.VISIBLE);
    }

    private void showCompleteState() {
        refreshScoreCount();

        int startCount = getOwner().getDictationScore().calcScoreRank().typeValue;
        if (startCount == 0) {
            finishMessage.setText(R.string.dictation_msg_finish_not_cleared);

            dictationStarFinish1.setAlpha(0.1f);
            dictationStarFinish2.setAlpha(0.1f);
            dictationStarFinish3.setAlpha(0.1f);
        } else {
            finishMessage.setText(R.string.dictation_msg_finish);

            if (startCount >= 1) {
                startAnimation(dictationStarFinish1, 300);
            }
            if (startCount >= 2) {
                startAnimation(dictationStarFinish2, 450);
            }
            if (startCount >= 3) {
                startAnimation(dictationStarFinish3, 600);
            }
        }

        dictationFinishLayout.setVisibility(View.VISIBLE);
    }

    private void startAnimation(final ImageView iv, final long delayMsec) {
        Util.startAnimationAttention(iv, delayMsec);

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(delayMsec + 300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    public void run() {
                        iv.setImageResource(R.drawable.ic_star_black_48dp);
                    }
                });
            }
        }).start();
    }

    private void refreshScoreCount() {
        String lifeCount = String.valueOf(getOwner().getDictationScore().getLifeCount());
        dictationLifeCount.setText(lifeCount);
        finishLifeCount.setText(lifeCount);

        String correctAnswerCount = String.valueOf(getOwner().getDictationScore().getCorrectAnsweredCount());
        dictationCorrectAnswerCount.setText(correctAnswerCount);
        finishCorrectAnswerCount.setText(correctAnswerCount);
    }

    private DictationActivity getOwner() {
        return (DictationActivity) super.getActivity();
    }

}