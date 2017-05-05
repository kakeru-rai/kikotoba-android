package net.snow69it.listeningworkout.model.speaking;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import net.snow69it.listeningworkout.util.IOUtil;

import java.io.File;

/**
 * dictationのwebインターフェース
 */
public abstract class WebAppInterface {
    public static String INTERFACE_NAME = "Android";

    @JavascriptInterface
    abstract public void onReady();


    private WebView mWebView;

    public WebAppInterface(WebView webView) {
        mWebView = webView;
        mWebView.addJavascriptInterface(this, INTERFACE_NAME);
        load(mWebView.getContext());
    }

    private void load(Context context) {
        File sd = IOUtil.getPrivateExternalDir(context, "");
        String url = "file://" + sd.getPath() + "/html/speaking/index.html";
        mWebView.loadUrl(url);
    }

    public void play(float fromSec, float toSec) {
        mWebView.loadUrl(String.format("javascript: web.play(%f, %f);", fromSec, toSec));
    }

    public void setText(String text) {
        String escaped = text.replace("'", "\\'");
        mWebView.loadUrl(String.format("javascript: web.setText('%s');", escaped));
    }
}