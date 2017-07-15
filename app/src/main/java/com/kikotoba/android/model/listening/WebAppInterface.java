package com.kikotoba.android.model.listening;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import com.kikotoba.android.model.LanguagePair;
import com.kikotoba.android.model.WorkingDirectory;
import com.kikotoba.android.model.entity.master.Article;
import com.kikotoba.android.model.entity.master.ArticlePair;
import com.kikotoba.android.model.entity.master.Sentence;
import com.kikotoba.android.util.IOUtil;
import com.kikotoba.android.util.Pref;
import com.kikotoba.android.util.Util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by raix on 2016/11/13.
 */

public class WebAppInterface implements AudioController.Player {
    public static String INTERFACE_NAME = "Android";

    private Pref.SpeechGap mSpeechGap = Pref.SpeechGap.NORMAL;
    private WebView mWebView;
    private int mCurrentSentenceIndex = 0;
    private ArticlePair articlePair;
    private Article mTargetArticle;
    private Article mTranscriptArticle;
    private boolean mIsPlaying = false;
    private Handler mHandler = new Handler();
    private AudioController mMediaController;
    private TextView mNowShadowing;

    private AlphaAnimation mAnimation;

    // 状態変数
    private Calendar playStartCalender;
    private long playTimeSec = 0;
    private boolean mIsRepeateMode = false;
    private boolean mIsBlindMode = false;
    private boolean mIsPlayerFunctionCalledDuringTrackGapSleep = false;

    public WebAppInterface(WebView webView,
                           AudioController mediaController,
                           ArticlePair articlePair,
                           int currentIndex,
                           TextView nowShadowing
    ) {
        mMediaController = mediaController;
        mediaController.setPlayer(this);
        this.articlePair = articlePair;
        mTargetArticle = articlePair._getTarget();
        mTranscriptArticle = articlePair._getTranslated();
        mWebView = webView;
        mWebView.addJavascriptInterface(this, INTERFACE_NAME);
        mCurrentSentenceIndex = currentIndex;
        mNowShadowing = nowShadowing;
        mNowShadowing.setVisibility(View.GONE);
        mAnimation = createAnimation();
    }

    private AlphaAnimation createAnimation() {
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(300);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        return animation;
    }

    public void load(Context context) {
        File sd = IOUtil.getPrivateExternalDir(context, "");
        String url = "file://" + sd.getPath() + "/html/listening/index.html";
        mWebView.loadUrl(url);
    }

    public void setSpeechGap(Context context, Pref.SpeechGap speechGap) {
        mSpeechGap = speechGap;
        mNowShadowing.setText(speechGap.description(context));
    }

    public boolean toggleRepeatMode() {
        mIsRepeateMode = !mIsRepeateMode;
        return mIsRepeateMode;
    }

    public boolean toggleBlindMode() {
        mIsBlindMode = !mIsBlindMode;
        jsIsBlindMode(mIsBlindMode);
        return mIsBlindMode;
    }

    public int getCurrentIndex() {
        return mCurrentSentenceIndex;
    }

    @JavascriptInterface
    public void onReady() {
        mHandler.post(new Runnable() {
            public void run() {
                buildArticle();
            }
        });
    }

    @JavascriptInterface
    public void onTrackEnded() {
        if (mSpeechGap != Pref.SpeechGap.NORMAL) {
            mHandler.post(new Runnable() {
                public void run() {
                    mNowShadowing.setVisibility(View.VISIBLE);
                    mNowShadowing.startAnimation(mAnimation);
                }
            });
        }

        // ここはwebviewのAppInterfaceから呼ばれる非UIスレッド
        // ここでsleepするとAppInterfaceへのリクエスト（url(javascript:xxx)）が止まってしまうので
        // さらに別のスレッドをスタートしたうえで、UIへの変更はhandlerを使う
        mIsPlayerFunctionCalledDuringTrackGapSleep = false;
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(calcCurrentSentenceSpeechGapMsec());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mIsPlayerFunctionCalledDuringTrackGapSleep) {
                    // スリープ中に操作されたらその後の動作はキャンセルする
                    return;
                }
                mHandler.post(new Runnable() {
                    public void run() {
                        if (mIsRepeateMode) {
                            rew();
                        } else if (hasNext()) {
                            next();
                        } else {
                            pause();
                        }
                        cancelTrackGapSleep();
                    }
                });
            }
        }).start();
    }

    @JavascriptInterface
    public void onSentenceSelected(final int sentenceIndex) {
        mHandler.post(new Runnable() {
            public void run() {
                mCurrentSentenceIndex = sentenceIndex;
                playIfPlaying();
            }
        });
    }

    @JavascriptInterface
    public void onTextSelected(String text) {

    }

    @JavascriptInterface
    public void onGetCurrentTranscript(final String script, final String transcript) {

    }

    /**
     * AudioController.Player
     */
    @Override
    public void play() {
        cancelTrackGapSleep();
        if (playStartCalender == null) {
            playStartCalender = Calendar.getInstance();
        }

        jsRefreshUI();
        jsPlay();
        jsScrollUpToSentence();
        setIsPlaying(true);
    }

    /**
     * AudioController.Player
     */
    @Override
    public void pause() {
        cancelTrackGapSleep();
        playTimeSec = flushPlayTimeSec();

        jsPause();
        setIsPlaying(false);
    }

    /**
     * AudioController.Player
     */
    @Override
    public void next() {
        cancelTrackGapSleep();
        if (hasNext()) {
            mCurrentSentenceIndex += 1;
        }
        jsSetCurrentSentenceIndex(mCurrentSentenceIndex);

        playIfPlaying();
    }

    @Override
    public void rew() {
        cancelTrackGapSleep();
        playIfPlaying();
    }

    @Override
    public void popup() {
        int translationIndex = mTargetArticle.getSentences().get(mCurrentSentenceIndex).getTranslationIndex();
        String translation = mTranscriptArticle.getTranslations().get(translationIndex);
        jsPopup(translation);
    }


    /**
     * AudioController.Player
     */
    @Override
    public void prev() {
        cancelTrackGapSleep();
        if (mCurrentSentenceIndex > 0) {
            mCurrentSentenceIndex -= 1;
        }
        jsSetCurrentSentenceIndex(mCurrentSentenceIndex);

        playIfPlaying();
    }

    private void cancelTrackGapSleep() {
        mIsPlayerFunctionCalledDuringTrackGapSleep = true;
        mNowShadowing.clearAnimation();
        mNowShadowing.setVisibility(View.GONE);
    }

    public long clearPlaybackTimeSec() {
        return flushPlayTimeSec();
    }

    private long calcCurrentSentenceSpeechGapMsec() {
        Sentence sentence = mTargetArticle.getSentences().get(mCurrentSentenceIndex);
        float speechDurationSec = sentence.getToSec() - sentence.getFromSec();

        long speechGapMsec = 0;
        switch (mSpeechGap) {
            case NORMAL:
                speechGapMsec = 10;
                break;
            case REPETITION:
                speechGapMsec = (long) (speechDurationSec * 1.1 * 1000);
                break;
            case SHADOWING:
                speechGapMsec = (long) (speechDurationSec * 0.5 * 1000);
                break;
            default:
                // TODO: 検知
                break;
        }
        return speechGapMsec;
    }

    private long diffCalender(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return 0;
        }
        return Math.abs(
                TimeUnit.MILLISECONDS.toSeconds(
                        cal1.getTime().getTime() - cal2.getTime().getTime()));
    }

    private long flushPlayTimeSec() {
        long total = playTimeSec + diffCalender(playStartCalender, Calendar.getInstance());
        playStartCalender = null;
        playTimeSec = 0;
        return total;
    }

    private void setAudioSrc(ArticlePair articlePair, String language) {
        String audioUrl = WorkingDirectory.getAudioPath(articlePair, language, "../../");
        jsSetAudioSrc(audioUrl);
    }

    private boolean hasNext() {
        return mCurrentSentenceIndex < mTargetArticle.getSentences().size() - 1;
    }

    private void buildArticle() {
        List<Sentence> targetSentences = mTargetArticle.getSentences();

        if (articlePair.getImage() != null) {
            jsSetPhotoSrc(articlePair.getImage());
        }
        jsSetTitle(mTargetArticle.getTitle());

        Sentence previousTargetSentence = null;
        for (int i = 0; i < targetSentences.size(); i++) {
            Sentence targetSentence = targetSentences.get(i);

            // 段落
            if (previousTargetSentence != null && previousTargetSentence.getParagraph() != targetSentence.getParagraph()) {
                jsFlushParagraph();
            }

            // 文を結合
            jsAddSentence(
                    targetSentence.getText(),
                    targetSentence.getFromSec(),
                    targetSentence.getToSec(),
                    targetSentence.getTranslationIndex());

            if (previousTargetSentence == null) {
                previousTargetSentence = targetSentence;
                continue;
            }

            previousTargetSentence = targetSentence;
        }
        jsFlushParagraph();
        jsSetCurrentSentenceIndex(mCurrentSentenceIndex);
        jsRefreshUI();
        if (0 < mCurrentSentenceIndex && mCurrentSentenceIndex < mTargetArticle.getSentences().size() - 1) {
            jsScrollUpToSentence();
        }

        setAudioSrc(articlePair, LanguagePair.getInstance().getTarget());
    }

    private void jsRefreshUI() {
        mWebView.loadUrl(String.format("javascript: web.refreshUI();"));
    }


    private void jsSetPhotoSrc(String src) {
        mWebView.loadUrl("javascript: web.setPhotoSrc('" + src + "');");
    }

    private void jsSetTitle(String title) {
        mWebView.loadUrl("javascript: web.setTitle('" + title + "');");
    }

    private void jsAddSentence(String sentence, float fromSec, float toSec, int translationIndex) {
        mWebView.loadUrl(String.format(
                "javascript: web.addSentence('%s', %f, %f, %d);",
                Util.escapeJsArgumentFromUrl(sentence),
                fromSec,
                toSec,
                translationIndex));
    }

    private void jsFlushParagraph() {
        mWebView.loadUrl(String.format("javascript: web.flushParagraph();"));
    }

    private void jsSetCurrentSentenceIndex(int index) {
        mWebView.loadUrl(String.format("javascript: web.setCurrentSentenceIndex(%d);", index));
    }

    private void jsPopup(String text) {
        mWebView.loadUrl(String.format("javascript: web.popup('%s');", Util.escapeJsArgumentFromUrl(text)));
    }

    private void jsPlay() {
        mWebView.loadUrl("javascript: web.play();");
    }

    private void jsSetSpeed(float speed) {
        mWebView.loadUrl(String.format("javascript: web.setSpeed(%f);", speed));
    }

    private void jsPause() {
        mWebView.loadUrl("javascript: web.pause();");
    }

    private void jsSetAudioSrc(String src) {
        try {
            String encoded = URLEncoder.encode(src, "UTF-8");
            mWebView.loadUrl(String.format("javascript: web.setAudioSrc('%s');", Util.escapeJsArgumentFromUrl(src)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void jsScrollUpToSentence() {
        mWebView.loadUrl("javascript: web.scrollUpToSentence();");
    }

    private void jsIsBlindMode(boolean isBlindMode) {
        mWebView.loadUrl(String.format("javascript: web.isBlindMode(%b);", isBlindMode));
    }

    private void jsSetCurrentTimeSec(double currentTimeSec) {
        mWebView.loadUrl(String.format("javascript: web.setCurrentTimeSec(%f);", currentTimeSec));
    }

    private void playIfPlaying() {
        if (mIsPlaying) {
            play();
        }
    }

    private void setIsPlaying(boolean isPlaying) {
        mIsPlaying = isPlaying;
        mMediaController.updatePausePlay(isPlaying);
    }

}