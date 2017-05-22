package com.kikotoba.android.util;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.util.AndroidRuntimeException;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 *
 * @version 2013/03/07
 */
public class Versatile {
	static private final String TAG = Versatile.class.getSimpleName();

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static void setSoftInputAdgust(Activity activity, int adgustMode) {
		activity.getWindow().setSoftInputMode(adgustMode | WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);
	}
	public static void setSoftInputState(Activity activity, int stateMode) {
		activity.getWindow().setSoftInputMode(stateMode | WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
	}

	public static boolean hasCamera(Context context){
		PackageManager pm = context.getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)? true:false;
	}
	public static boolean chkRequestCodeInActivityFromFragment(int requestCode, int expectedCode){
		return (requestCode & 0xffff) == expectedCode;
	}
	public static void finishForOk(Activity activity, Intent intent){
		activity.setResult(Activity.RESULT_OK, intent);
		activity.finish();
	}

	public static void showIME(Context context, View v) {
		v.requestFocus();
		v.requestFocusFromTouch();
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
	}

	public static void dismissIME(Context context, View v){
		//IME閉じる
		InputMethodManager inputMethodManager =  (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public static void setSize(View view, int widthPx, int heightPx){
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		lp.width = heightPx;
		lp.width = widthPx;
		view.setLayoutParams(lp);
	}

	public static void setHeight(View view, int heightPx){
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		lp.height = heightPx;
		view.setLayoutParams(lp);
	}
	public static void setWidth(View view, int widthPx){
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		lp.width = widthPx;
		view.setLayoutParams(lp);
	}
	/**
	 * リソースからレイアウトを取得する
	 *
	 * @param context
	 * @param resLayout
	 *            レイアウトのリソースID
	 * @return
	 */
	public static View inflateLayout(Context context, int resLayout) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(resLayout, null);
	}

	public static Locale getLocale(Context context) {
		return context.getResources().getConfiguration().locale;
	}


	public static long getCurrentMilSec(){
		return Calendar.getInstance().getTimeInMillis();
	}
	public static Calendar getCurrentCalendar(){
		return Calendar.getInstance();
	}

	public static Calendar getCalenderByMillisec(long msec){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(msec);
		return c;
	}

	public static boolean isPassedDay(Calendar from, Calendar to, int passedDay){
		from.add(Calendar.DAY_OF_MONTH, passedDay);
		if ( from.getTimeInMillis() < to.getTimeInMillis() ){
			return true;
		}else{
			return false;
		}
	}

	public static void animateToRelativeParent(View view, int x, int y) {
		view.animate()
        .x(x)
        .y(y);
	}

	public static void animateToRelativeParentForHide(final View view, int direction, final long durationMs) {
		final int x_org = view.getLeft();
		final int y_org = view.getTop();
		int x = view.getLeft();
		int y = view.getTop();
		switch (direction) {
		case 0: //top
			y -= view.getHeight();
			break;
		case 1: //right
			x += view.getWidth();
			break;
		case 2: //bottom
			y += view.getHeight();
			break;
		case 3: //left
			x -= view.getWidth();
			break;
		}
		final int xf = x;
		final int yf = y;
		view.animate()
        .x(x_org)
        .y(y_org)
        .setDuration(0)
        .setListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				view.setVisibility(View.VISIBLE);
				view.animate()
		        .x(xf)
		        .y(yf)
		        .setDuration(durationMs)
		        .setListener(new AnimatorListener() {

					@Override
					public void onAnimationStart(Animator animation) {
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						view.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationCancel(Animator animation) {
					}
				});
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
	}

	public static void animateToRelativeParentForShow(final View view, int direction, final long durationMs) {
		view.setVisibility(View.GONE);
		final int x_org = view.getLeft();
		final int y_org = view.getTop();
		int x = view.getLeft();
		int y = view.getTop();
		switch (direction) {
		case 0: //top
			y += view.getHeight();
			break;
		case 1: //right
			x -= view.getWidth();
			break;
		case 2: //bottom
			y -= view.getHeight();
			break;
		case 3: //left
			x += view.getWidth();
			break;
		}
		view.animate()
        .x(x)
        .y(y)
        .setDuration(0)
        .setListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				view.setVisibility(View.VISIBLE);
				view.animate()
		        .x(x_org)
		        .y(y_org)
		        .setDuration(durationMs)
		        ;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
	}

	public static void animateDelta(View view, int x, int y) {
		view.animate()
        .xBy(x)
        .yBy(y);
	}

	public static void animateRotate(View view, int degree) {
		view.animate()
        .rotation(degree);
	}

	/**
	 * viewを現在地からrelX、relYへ移動させる
	 *
	 * @param view
	 * @param x
	 * @param y
	 */
	public static void moveRelativeParent(View view, int x, int y) {
		view.layout(x, y, x + view.getWidth(), y + view.getHeight());
	}

	// 通知を消去
	public static void removeNotification(Context context, int id) {
		NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.cancel(id);
	}

	// 通知を消去
	public static void removeAllNotification(Context context) {
		NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.cancelAll();
	}

	// SDKバージョン（8とか16とか）を返す
	public static int getSDKVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	public static String getDeviceName() {
		return android.os.Build.MODEL;
	}

	public static int getVersionCode(Context con) {
		int versionCode = -1;

		PackageManager packageManager = con.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(con.getPackageName(), PackageManager.GET_ACTIVITIES);
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionCode;
	}

	public static String getVersionName(Context con) {
		String versionName = "0.0.0";

		PackageManager packageManager = con.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(con.getPackageName(), PackageManager.GET_ACTIVITIES);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionName;
	}

	public static String getDirFromFullpath(String path) {
		// 最後の"/"も含む
		return path.substring(0, path.lastIndexOf("/") + 1);
	}

	public static String getExtension(String file) {
		// "."も含む
		return file.substring(file.lastIndexOf("."));
	}

	public static String getFileNameNoExt(String file) {
		return file.substring(file.lastIndexOf("/") + 1, file.lastIndexOf("."));
	}

	public static void selectFromContactList(Activity act, int request_code) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setData(ContactsContract.Contacts.CONTENT_URI);
		act.startActivityForResult(intent, request_code);
	}

	public static void selectFromGallery(Activity act, int request_code) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		act.startActivityForResult(intent, request_code);
	}

	// リストから配列を作成
	public static String[] getArray(ArrayList<String> sList) {
		String[] strs = new String[sList.size()];
		for (int i = 0; i < sList.size(); ++i) {
			strs[i] = sList.get(i);
		}
		return strs;
	}

	// 画面サイズを取得
	@SuppressLint("NewApi")
	public static int getPxDisplayWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();

		int width = 0;
		if (getSDKVersion() >= 13) {
			Point p = new Point();
			disp.getSize(p);
			width = p.x;
		} else {
			width = disp.getWidth();
		}
		return width;
	}

	@SuppressLint("NewApi")
	public static int getPxDisplayHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		int height = 0;
		if (getSDKVersion() >= 13) {
			Point p = new Point();
			disp.getSize(p);
			height = p.y;
		} else {
			height = disp.getWidth();
		}
		return height;
	}

	@SuppressLint("NewApi")
	public static int getDisplayInch(Activity activity) {
		// ディスプレイ情報を取得する
		final DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// ピクセル数（width, height）を取得する
		final int widthPx = metrics.widthPixels;
		final int heightPx = metrics.heightPixels;

		// dpi (xdpi, ydpi) を取得する
		final float xdpi = metrics.xdpi;
		final float ydpi = metrics.ydpi;

		// インチ（width, height) を計算する
		final float widthIn = widthPx / xdpi;
		final float heightIn = heightPx / ydpi;

		// 画面サイズ（インチ）を計算する
		final double inchi = Math.sqrt(widthIn * widthIn + heightIn * heightIn);
		return (int) Math.round(inchi);
	}

	// dipをpxに変換
	public static int getPxFromDip(int dip, Activity act) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = act.getWindowManager();
		Display display = wm.getDefaultDisplay();
		display.getMetrics(metrics);
		return (int) ((float) dip * metrics.scaledDensity);
	}

	// pxをdipに変換
	public static int getDipFromPx(int px, Activity act) {
		DisplayMetrics metrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return (int) ((float) px / metrics.scaledDensity);
	}

	// RAWから準備済みのMediaPlayerを返す
	public static MediaPlayer createMediaPlayer(Context con, int rawResourceId) {// storeType=DataSourceType.RAW,DataSourceType.FILE_OR_URL
		MediaPlayer mp = MediaPlayer.create(con, rawResourceId);
		return mp;
	}

	// パスかURLから準備済みのMediaPlayerを返す
	public static MediaPlayer createMediaPlayer(String pathToFileOrUrl) {// storeType=DataSourceType.RAW,DataSourceType.FILE_OR_URL
		MediaPlayer mp = null;
		try {
			mp = new MediaPlayer();
			mp.setDataSource(pathToFileOrUrl);
			mp.prepare();
			return mp;
		} catch (IllegalArgumentException ex) {
			throw new AndroidRuntimeException(ex);
		} catch (IllegalStateException ex) {
			throw new AndroidRuntimeException(ex);
		} catch (IOException ex) {
			throw new AndroidRuntimeException(ex);
		}
	}

	// 2点の緯度、経度から距離と方位角、xy座標を求める
	public static HashMap<String, String> getDistanceDegree(double srcLat, double srcLon, double distLat, double distLon) {
		HashMap<String, String> hm = new HashMap<String, String>();
		// 現在地の緯度から1度あたりの距離を計算する　→　縮尺
		double a = 6378137;// 赤道半径
		double tf = 298.2572221;// 扁平率の逆数
		double f = 1 / tf;// 扁平率
		double e2 = 2 * f - f * f;// 離心率の二乗
		double rad = (srcLat / 180) * Math.PI;
		double ladsec = (Math.PI / 648000) * a * (1 - e2) / Math.pow((1 - Math.pow(e2 * (Math.sin(rad)), 2)), 1.5);// 緯度1秒あたり
		double lonsec = Math.PI / 180 / 3600 * a * Math.cos(f) / Math.sqrt(1 - e2 * Math.sin(rad));
		double ladkm = ladsec * 3600 / 1000;// 緯度1度あたりのkm
		double lonkm = lonsec * 3600 / 1000;// 経度1度あたりのkm

		// 平面座標として計算
		double dx = 0;// 目的地までの東西距離km
		double dy = 0;// 目的地までの南北距離km
		double dist = 0;// 目的までの直線距離km
		double targetDeg = 0;// 目的の方向

		dy = (distLat - srcLat) * ladkm;
		dx = (distLon - srcLon) * lonkm;
		dist = Math.sqrt(dy * dy + dx * dx);

		// targetDegは±180
		int sign = 1;
		if (dx < 0)
			sign = -1;
		targetDeg = Math.acos(dy / dist) * 180 / Math.PI * sign;

		hm.put("distance", String.valueOf(dist));
		hm.put("degree", String.valueOf(targetDeg));
		hm.put("dx", String.valueOf(dx));
		hm.put("dy", String.valueOf(dy));
		return hm;
	}

	// GPSが有効かCheck
	// 有効になっていなければ、設定画面の表示確認ダイアログ
	public static boolean chkGpsService(final Activity act, String chkType) {// chkTypeは単純な確認(chk)かダイアログを表示する(dia)かの選択
		boolean gpsOn = false;
		LocationManager locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);

		// GPSセンサーが利用可能か確認
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {// 利用不可
			if (chkType.equals("dia")) {// ダイアログでGPSの有効化をすすめる
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
				alertDialogBuilder.setMessage("GPSが有効になっていません。\n有効化しますか？").setCancelable(false)

				// GPS設定画面起動用ボタンとイベントの定義
				.setPositiveButton("GPS設定起動", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						act.startActivity(callGPSSettingIntent);
						dialog.cancel();
					}
				});
				// キャンセルボタン処理
				alertDialogBuilder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = alertDialogBuilder.create();
				// 設定画面へ移動するかの問い合わせダイアログを表示
				alert.show();
			} else
				gpsOn = true;
		} else
			gpsOn = true;
		return gpsOn;
	}

	public static String listToStr(ArrayList<String> list, String separator) {
		String str = "";
		for (int i = 0; i < list.size(); ++i) {
			str += list.get(i);
			if (i < list.size() - 1)
				str += separator;
		}
		return str;
	}

	public static String arrayToStr(String[] array, String separator) {
		String str = "";
		for (int i = 0; i < array.length; ++i) {
			str += array[i];
			if (i < array.length - 1)
				str += separator;
		}
		return str;
	}

	public static String[] listToArray(ArrayList<String> list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); ++i) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static ArrayList<String> ArrayToList(String[] array) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; ++i) {
			list.add(array[i]);
		}
		return list;
	}

	/**
	 * http://ja.wikipedia.org/wiki/HSV%E8%89%B2%E7%A9%BA%E9%96%93
	 * @param hue 色相 (0 - 360) * n
	 * @param saturation 彩度 0.0 - 1.0
	 * @param value 明度 0.0 - 1.0
	 */
	public static int[] getRgbByHsv(int hue, float saturation, float value) {
	    int hi;
	    float f;
	    int p;
	    int q;
	    int t;
	    int v;

	    while (hue < 0) {
	        hue += 360;
	    }
	    hue = hue % 360;

	    saturation = saturation < 0 ? 0
	        : saturation > 1 ? 1
	        : saturation;

	    value = value < 0 ? 0
	        : value > 1 ? 1
	        : value;

	    value = value * 255.0f;
        hi = (int) ((hue / 60.0f) % 6.0f);
        f = hue / 60.0f - hi;

        p = Math.round(value * (1.0f - saturation));
        q = Math.round(value * (1.0f - f  * saturation));
        t = Math.round(value * (1.0f - (1.0f - f) * saturation));

        //0.0 - 1.0 -> 16進2桁
	    v = (int) value;

	    switch ((int) hi) {
	        case 0:
	            return new int[]{v, t, p};
	        case 1:
	            return  new int[]{q, v, p};
	        case 2:
	            return  new int[]{p, v, t};
	        case 3:
	            return  new int[]{p, q, v};
	        case 4:
	            return  new int[]{t, p, v};
	        case 5:
	            return  new int[]{v, p, q};
	    }

	    throw new Error("invalid hue");
	}

	public static Bitmap convertToBitmap(Context context, int resIdDrawable) {
		return BitmapFactory.decodeResource(context.getResources(), resIdDrawable);
	}

	public static Bitmap convertToBitmap(Drawable drawable) {
		return ((BitmapDrawable)drawable).getBitmap();
	}

	public static Drawable convertToDrawable(Context context, Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT)
		.show();
	}

	public static void showToast(Context context, int resString) {
		String msg = context.getString(resString);
		Toast.makeText(context, msg, Toast.LENGTH_SHORT)
		.show();
	}

	public static void showToastLong(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG)
		.show();
	}

	public static void showToastLong(Context context, int resString) {
		String msg = context.getString(resString);
		Toast.makeText(context, msg, Toast.LENGTH_LONG)
		.show();
	}

	public void loadWebArchive(WebView webView, String webArchive) {
		webView.loadData(webArchive, "application/x-webarchive-xml", "utf-8");
	}

	public static void copyToClipBoard(Context context, String text) {
		//クリップボードに格納するItemを作成
		ClipData.Item item = new ClipData.Item(text);
		//MIMETYPEの作成
		String[] mimeType = new String[1];
		mimeType[0] = ClipDescription.MIMETYPE_TEXT_PLAIN;
		//クリップボードに格納するClipDataオブジェクトの作成
		ClipData cd = new ClipData(new ClipDescription("text_data", mimeType), item);
		//クリップボードにデータを格納
		ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cm.setPrimaryClip(cd);
	}
	
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		}
		return ni.isConnectedOrConnecting();
	}


}
