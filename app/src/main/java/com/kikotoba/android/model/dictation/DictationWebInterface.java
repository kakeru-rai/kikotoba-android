package com.kikotoba.android.model.dictation;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.kikotoba.android.util.IOUtil;
import com.kikotoba.android.util.Util;

import java.io.File;

/**
 * dictationのwebインターフェース
 */
public abstract class DictationWebInterface {
    public static String INTERFACE_NAME = "Android";

    private WebView mWebView;

    public DictationWebInterface(WebView webView) {
        mWebView = webView;
        mWebView.addJavascriptInterface(this, INTERFACE_NAME);
        load(mWebView.getContext());
    }

    /**
     * dictation画面をロードする
     */
    private void load(Context context) {
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

    public void addText(String text) {
        mWebView.loadUrl(String.format("javascript: web.addText('%s');", Util.escapeJsArgumentFromUrl(text)));
    }

    public void addInput(String text) {
        mWebView.loadUrl(String.format("javascript: web.addInput('%s');", Util.escapeJsArgumentFromUrl(text)));
    }
}