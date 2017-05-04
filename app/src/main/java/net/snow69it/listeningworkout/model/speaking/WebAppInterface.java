package net.snow69it.listeningworkout.model.speaking;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import net.snow69it.listeningworkout.model.entity.Article;
import net.snow69it.listeningworkout.util.IOUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * dictationのwebインターフェース
 */
public abstract class WebAppInterface {
    public static String INTERFACE_NAME = "Android";

    private WebView mWebView;
    private Article mTargetArticle;
    private int mSentenceIndex;

    public WebAppInterface(WebView webView, Article targetArticle, int sentenceIndex) {
        mWebView = webView;
        mTargetArticle = targetArticle;
        mSentenceIndex = sentenceIndex;
        mWebView.addJavascriptInterface(this, INTERFACE_NAME);
    }

    @JavascriptInterface
    abstract public void onReady();

    public void load(Context context) {
        File sd = IOUtil.getPrivateExternalDir(context, "");
        String url = "file://" + sd.getPath() + "/html/speaking/index.html";
        mWebView.loadUrl(url);
    }

    public void play(float fromSec, float toSec) {
        mWebView.loadUrl(String.format("javascript: web.play(%f, %f);", fromSec, toSec));
    }

    public void pause() {
        mWebView.loadUrl("javascript: web.pause();");
    }

//    public void setAudioSrc(String articleId, int trackIndex) {
//        mWebView.loadUrl(String.format("javascript: web.setAudioSrc('%s', %d);", articleId, trackIndex));
//    }

    public void setAudioSrc(String audioUrl) {
        jsSetAudioSrc(audioUrl);
    }

    private void jsSetAudioSrc(String src) {
        try {
            String encoded = URLEncoder.encode(src, "UTF-8");
            mWebView.loadUrl(String.format("javascript: web.setAudioSrc('%s');", encoded));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void setText(String text) {
        String escaped = text.replace("'", "\\'");
        mWebView.loadUrl(String.format("javascript: web.setText('%s');", escaped));
    }
}