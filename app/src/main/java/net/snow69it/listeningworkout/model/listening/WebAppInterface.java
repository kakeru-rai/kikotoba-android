package net.snow69it.listeningworkout.model.listening;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import net.snow69it.listeningworkout.model.entity.Article;
import net.snow69it.listeningworkout.model.entity.Sentence;
import net.snow69it.listeningworkout.util.IOUtil;

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

    private long gapMilliSec = 0;
    private WebView mWebView;
    private int mCurrentSenetenceIndex = 0;
    private Article mTargetArticle;
    private Article mTranscriptArticle;
    private boolean mIsPlaying = false;
    private Handler mHandler = new Handler();
    private AudioController mMediaController;
    private float mSpeed = 1.0f;

    private Calendar playStartCalender;
    private long playTimeSec = 0;

    public WebAppInterface(WebView webView,
                           AudioController mediaController,
                           Article targetArticle,
                           Article transcriptArticle,
                           int currentIndex
    ) {
        mMediaController = mediaController;
        mediaController.setPlayer(this);
        mTargetArticle = targetArticle;
        mTranscriptArticle = transcriptArticle;
        mWebView = webView;
        mWebView.addJavascriptInterface(this, INTERFACE_NAME);
        mCurrentSenetenceIndex = currentIndex;
    }

    public void load(Context context) {
        File sd = IOUtil.getPrivateExternalDir(context, "");
        String url = "file://" + sd.getPath() + "/html/listening/index.html";
        mWebView.loadUrl(url);
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
        jsSetSpeed(speed);
    }

    public int getCurrentIndex() {
        return mCurrentSenetenceIndex;
    }

    @JavascriptInterface
    public void onReady() {
        mHandler.post(new Runnable() {
            public void run() {
                setArticle();
            }
        });
    }

    @JavascriptInterface
    public void onTrackEnded() {
        mHandler.post(new Runnable() {
            public void run() {
                if (hasNext()) {
                    try {
                        Thread.sleep(gapMilliSec);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    next();
                } else {
                    pause();
                }
            }
        });
    }

    @JavascriptInterface
    public void onSentenceSelected(final int sentenceIndex) {
        mHandler.post(new Runnable() {
            public void run() {
                mCurrentSenetenceIndex = sentenceIndex;
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
        if (playStartCalender == null) {
            playStartCalender = Calendar.getInstance();
        }

        jsSetSpeed(mSpeed);
        jsRefreshUI();
        jsPlay();
        setIsPlaying(true);
    }

    /**
     * AudioController.Player
     */
    @Override
    public void pause() {
        playTimeSec = flushPlayTimeSec();

        jsPause();
        setIsPlaying(false);
    }

    /**
     * AudioController.Player
     */
    @Override
    public void next() {
        if (hasNext()) {
            mCurrentSenetenceIndex += 1;
        }
        jsSetCurrentSentenceIndex(mCurrentSenetenceIndex);

        playIfPlaying();
    }

    @Override
    public void rew() {
        playIfPlaying();
    }

    @Override
    public void popup() {
        String transcript = mTranscriptArticle.getSentences().get(mCurrentSenetenceIndex).getText();
        jsPopup(transcript);
    }


    /**
     * AudioController.Player
     */
    @Override
    public void prev() {
        if (mCurrentSenetenceIndex > 0) {
            mCurrentSenetenceIndex -= 1;
        }
        jsSetCurrentSentenceIndex(mCurrentSenetenceIndex);

        playIfPlaying();
    }

    public long clearPlaybackTimeSec() {
        return flushPlayTimeSec();
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

    private void setAudioSrc(String audioUrl) {
        jsSetAudioSrc(audioUrl);
    }

    private boolean hasNext() {
        return mCurrentSenetenceIndex < mTargetArticle.getSentences().size() - 1;
    }

    private void setArticle() {
        List<Sentence> targetSentences = mTargetArticle.getSentences();

        if (mTargetArticle.getImage() != null) {
            jsSetPhotoSrc(mTargetArticle.getImage());
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
            jsAddSentence(targetSentence.getText(), targetSentence.getFromSec(), targetSentence.getToSec());

            if (previousTargetSentence == null) {
                previousTargetSentence = targetSentence;
                continue;
            }

            previousTargetSentence = targetSentence;
        }
        jsFlushParagraph();
        jsSetCurrentSentenceIndex(mCurrentSenetenceIndex);
        jsRefreshUI();
        
        setAudioSrc(this.mTargetArticle.getAudio());
        jsSetSpeed(mSpeed);
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

    private void jsAddSentence(String sentence, float fromSec, float toSec) {
        String escaped = sentence.replace("'", "\\'");
        mWebView.loadUrl(String.format("javascript: web.addSentence('%s', %f, %f);", escaped, fromSec, toSec));
    }

    private void jsFlushParagraph() {
        mWebView.loadUrl(String.format("javascript: web.flushParagraph();"));
    }

    private void jsSetCurrentSentenceIndex(int index) {
        mWebView.loadUrl(String.format("javascript: web.setCurrentSentenceIndex(%d);", index));
    }

    private void jsPopup(String text) {
        mWebView.loadUrl(String.format("javascript: web.popup('%s');", text));
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
            mWebView.loadUrl(String.format("javascript: web.setAudioSrc('%s');", encoded));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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