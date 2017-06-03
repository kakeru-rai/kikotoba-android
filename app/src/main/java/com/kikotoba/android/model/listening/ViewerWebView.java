package com.kikotoba.android.model.listening;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.kikotoba.android.util.WebViewDefault;

import static android.content.ContentValues.TAG;

public class ViewerWebView
		extends WebViewDefault {
    private String SP_USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36";

    private WebViewClient webViewClient = new WebViewClient() {};

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
