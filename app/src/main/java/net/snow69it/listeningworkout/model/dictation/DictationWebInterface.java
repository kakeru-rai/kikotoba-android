package net.snow69it.listeningworkout.model.dictation;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import net.snow69it.listeningworkout.util.IOUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * dictationのwebインターフェース
 */
public abstract class DictationWebInterface {
    public static String INTERFACE_NAME = "Android";

    private WebView mWebView;

    public DictationWebInterface(WebView webView) {
        mWebView = webView;
        mWebView.addJavascriptInterface(this, INTERFACE_NAME);
    }

    /**
     * dictation画面をロードする
     */
//    public void load() {
//        mWebView.loadUrl("file:///android_asset/html/");
//    }
    public void load(Context context) {
        File sd = IOUtil.getPrivateExternalDir(context, "");
        String url = "file://" + sd.getPath() + "/html/dictation/index.html";
        mWebView.loadUrl(url);
    }

    @JavascriptInterface
    abstract public void submitCallback(boolean isCorrect);

    @JavascriptInterface
    abstract public void onReady();

    /**
     * 回答を取得する。返り値はreturnAnswerをcallbackとして利用する。
     */
    public void submit() {
        mWebView.loadUrl("javascript: web.submit();");
    }

    /**
     *
     */
//    public void play() {
//        mWebView.loadUrl("javascript: web.play();");
//    }
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

    public void addText(String text) {
        mWebView.loadUrl(String.format("javascript: web.addText('%s');", text));
    }
    public void addInput(String text) {
        mWebView.loadUrl(String.format("javascript: web.addInput('%s');", text));
    }
}