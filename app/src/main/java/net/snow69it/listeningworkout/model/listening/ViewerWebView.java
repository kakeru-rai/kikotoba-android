package net.snow69it.listeningworkout.model.listening;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.snow69it.listeningworkout.util.WebChromeClientDefault;
import net.snow69it.listeningworkout.util.WebViewDefault;

import static android.content.ContentValues.TAG;

public class ViewerWebView
		extends WebViewDefault {
    private String SP_USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36";

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView webview, String url) {
            super.onLoadResource(webview, url);
        }

        @Override
        public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed() ;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    };

    public ViewerWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    public ViewerWebView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    private void init() {
        setWebChromeClient(new WebChromeClientDefault(this) {});
        setWebViewClient(webViewClient);
        setUserAgentString(SP_USER_AGENT);
        WebSettings webSettings = getSettings();
//        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);

        //. (1) 読み込み時にページ横幅を画面幅に無理やり合わせる
        webSettings.setLoadWithOverviewMode(true);

        //. (2) ワイドビューポートへの対応
        webSettings.setUseWideViewPort(true);
    }

    public void setWebAppInterface(WebAppInterface webAppInterface) {
        addJavascriptInterface(webAppInterface, WebAppInterface.INTERFACE_NAME);
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        ActionMode actionMode = super.startActionMode(callback);
        Log.v(TAG, "ActionMode start !!");
        return actionMode;
    }

}
