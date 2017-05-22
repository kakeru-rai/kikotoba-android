package com.kikotoba.android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class WebViewDefault
		extends WebView {

	private static final String HTML5_WEB_SQL_DATABASE_NAME = "HTML5_WEB_SQL_DATABASE";

	private OnScrollChangedListener onScrollChanged;

	public WebViewDefault(Context context) {
		super(context);
		if (!isInEditMode()) {
			init();
		}
	}

	public WebViewDefault(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (!isInEditMode()) {
			init();
		}
	}

	public WebViewDefault(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			init();
		}
	}

	@Override
	public void onScrollChanged(int currentH, int currentV, int oldH, int oldV) {
		if (onScrollChanged != null) {
			onScrollChanged.onScrollChanged(currentH, currentV, oldH, oldV);
		}
	}

	// onScrollChangedのリスナを提供
	public interface OnScrollChangedListener {
		public void onScrollChanged(int currentH, int currentV, int oldH, int oldV);
	}

	public void setOnScrollChangedListener(OnScrollChangedListener listener) {
		this.onScrollChanged = listener;
	}

	private void init() {
		setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			}
		});

		setOverScrollMode(OVER_SCROLL_ALWAYS);

		WebSettings webSettings = getSettings();
		// javascript有効化
		webSettings.setJavaScriptEnabled(true);

		// スクロールバーをページ内にオーバーレイ表示する
		setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);

		// プラグインを有効化
		try {
			// Plugins will not be supported in future, and should not be
			// used.
			if (Versatile.getSDKVersion() < 8) {
				JVersatile.reflectMethodInstance(webSettings, "setPluginsEnabled",
						new Class[] { Boolean.class }, new Object[] { true });
				JVersatile.reflectMethodInstance(webSettings, "setPluginState",
						new Class[] { WebSettings.PluginState.class },
						new Object[] { WebSettings.PluginState.ON });
			} else if (Versatile.getSDKVersion() < 18) {
				JVersatile.reflectMethodInstance(webSettings, "setPluginState",
						new Class[] { WebSettings.PluginState.class },
						new Object[] { WebSettings.PluginState.ON_DEMAND });
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}

		// html5 Web SQL Database
		try {
			if (Versatile.getSDKVersion() < 18) {
				webSettings.setDatabaseEnabled(true);
				String databasePath = getContext().getApplicationContext()
						.getDir(HTML5_WEB_SQL_DATABASE_NAME, Context.MODE_PRIVATE).getPath();
				JVersatile.reflectMethodInstance(webSettings, "setDatabasePath",
						new Class[] { String.class }, new Object[] { databasePath });
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}

		// ピンチ
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(false);
		webSettings.setSupportZoom(true);
		if (Versatile.getSDKVersion() < 18) {
			try {
				// マルチタッチを有効にしたまま、zoom controlを消す
				Field nameField = webSettings.getClass().getDeclaredField("mBuiltInZoomControls");
				nameField.setAccessible(true);
				nameField.set(webSettings, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// PC用サイトの100%表示
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);

		// html5 web storage
		webSettings.setDomStorageEnabled(true);



	}

	public void setBlockNetworkImage(boolean isImageBlocked) {
		getSettings().setBlockNetworkImage(isImageBlocked);
	}

	public void setUserAgentString(String agent) {
		getSettings().setUserAgentString(agent);
	}

	public void setDefaultWebChromeClient() {
		setWebChromeClient(new WebChromeClientDefault(this) {});
	}

	@Override
	public int computeHorizontalScrollExtent() {
		return super.computeHorizontalScrollExtent();
	}

	@Override
	public int computeHorizontalScrollOffset() {
		return super.computeHorizontalScrollOffset();
	}

	@Override
	public int computeHorizontalScrollRange() {
		return super.computeHorizontalScrollRange();
	}

	@Override
	public int computeVerticalScrollExtent() {
		return super.computeVerticalScrollExtent();
	}

	@Override
	public int computeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	@Override
	public int computeVerticalScrollRange() {
		return super.computeVerticalScrollRange();
	}
	
	public boolean isRightEdge() {
		return computeHorizontalScrollOffset() + getWidth() == computeHorizontalScrollRange();
	}
	
	public boolean isLeftEdge() {
		return computeHorizontalScrollOffset() == 0;
	}
	
	public boolean isTopEdge() {
		return computeVerticalScrollOffset() == 0;
	}
	
	public boolean isBottomEdge() {
		return computeVerticalScrollOffset() + getHeight() == computeVerticalScrollRange();
	}

}
