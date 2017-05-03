package net.snow69it.listeningworkout.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.snow69it.listeningworkout.R;

public abstract class WebChromeClientDefault extends WebChromeClient{
	private static final String TAG = WebChromeClientDefault.class.getSimpleName();
	
	private Context context;
	private CustomViewCallback costomViewCallback;
	private View customView;
	private WebView webView;


	public WebChromeClientDefault(WebView webView) {
		super();
		this.context = webView.getContext();
		this.webView = webView;
		
		WebIconDatabase.getInstance().open(context.getDir("WEBVIEW_FAVICON", Context.MODE_PRIVATE).getPath());
	}

	@Override
	public void onReceivedIcon(WebView view, Bitmap favicon_) {
	}

	@Override
	public void onReceivedTitle(WebView view, String title_) {
	}
	
	@Override
	public void onConsoleMessage(String message, int lineNumber, String sourceID) {
//		D.d.d(String.format("%s(%d):%s", sourceID, lineNumber, message));
	}
	
	@Override
	public boolean onConsoleMessage (ConsoleMessage consoleMessage) {
//		D.d.d(String.format("%s %s(%d):%s", consoleMessage.messageLevel().name(), consoleMessage.sourceId(), consoleMessage.lineNumber(), consoleMessage.message()));
		String[] sourcePathList = consoleMessage.sourceId().split("/");
		Log.v("js", String.format("%s:%s (%d) %s",
				sourcePathList.length > 0 ? sourcePathList[sourcePathList.length - 1] : "",
				consoleMessage.messageLevel().name(),
				consoleMessage.lineNumber(),
				consoleMessage.message()));
		return true;
	}
	
	@Override
	public boolean onJsAlert(WebView view, String url, final String message, final JsResult result) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(null);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		});
		builder.setCancelable(false);
		builder.setMessage(message).create().show();
		return true;
	}

	@Override
	public boolean onJsConfirm(WebView view, String url, final String message, final JsResult result) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(null);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				result.cancel();
			}
		});
		builder.setCancelable(false);
		builder.setMessage(message).create().show();
		return true;
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
		final LinearLayout ll = WidgetCreator.getLinearLayout(context, LinearLayout.VERTICAL);
		final TextView tv = WidgetCreator.getTextView(context, message);
		final EditText et = WidgetCreator.getEditText(context);
		
		ll.addView(tv);
		ll.addView(et);
		
		new AlertDialog.Builder(context)
		.setView(ll)
		.setPositiveButton(R.string.tmpl_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.confirm(et.getText().toString());
			}
		})
		.setNegativeButton(R.string.tmpl_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.cancel();
			}
		})
		.show();
		
		return true;
	}

	@Override
	public boolean onJsBeforeUnload(WebView view, String url, String message, final JsResult result) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(null);
		builder.setPositiveButton(R.string.tmpl_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		});
		builder.setNegativeButton(R.string.tmpl_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				result.cancel();
			}
		});
		builder.setCancelable(false);
		builder.setMessage(message).create().show();
		return true;
	}

	@Override
	public boolean onJsTimeout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(null);
		builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setCancelable(false);
		builder.setMessage("javascript Time out").create().show();
		return true;
	}
	
	/**
	 * 2.x系のビデオ再生用
	 */
	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		webView.setVisibility(View.GONE);

		if (customView != null) {
			callback.onCustomViewHidden();
			return;
		}

		costomViewCallback = callback;
		customView = view;

		ViewGroup parent = (ViewGroup) webView.getParent();

		parent.addView(customView);
	}

	@Override
	public void onHideCustomView() {
		if (customView == null) {
			return;
		}

		ViewGroup parent = (ViewGroup) webView.getParent();
		parent.removeView(customView);
		customView = null;
		costomViewCallback.onCustomViewHidden();

		webView.setVisibility(View.VISIBLE);
	}

//	@Override 隠しメソッドを無効化
	public void onSelectionStart(WebView view) {
	}

	public boolean inCustomView() {
		return customView != null;
	}

}
