package com.kikotoba.android.model.audio;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.kikotoba.android.model.WorkingDirectory;
import com.kikotoba.android.model.entity.Article;
import com.kikotoba.android.util.IOUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * audio.htmlのwebインターフェース
 */
public abstract class AudioWebInterface {
    public static String INTERFACE_NAME = "Android";

    protected WebView mWebView;

    public AudioWebInterface(WebView webView) {
        mWebView = webView;
        mWebView.addJavascriptInterface(this, INTERFACE_NAME);
        load(webView.getContext());
    }

    @JavascriptInterface
    abstract public void onReady();

    private void load(Context context) {
        File sd = IOUtil.getPrivateExternalDir(context, "");
        String url = "file://" + sd.getPath() + "/html/common/audio.html";
        mWebView.loadUrl(url);
    }

    public void play(float fromSec, float toSec) {
        mWebView.loadUrl(String.format("javascript: audioPlayer.playDuration(%f, %f);", fromSec, toSec));
    }

    public void pause() {
        mWebView.loadUrl("javascript: audioPlayer.pause();");
    }

    public void setAudioSrc(Article article) {
        try {
            String audioUrl = WorkingDirectory.getAudioPath(article, "../../");
            String encoded = URLEncoder.encode(audioUrl, "UTF-8");
            mWebView.loadUrl(String.format("javascript: audioPlayer.setAudioSrc('%s');", encoded));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}