package com.kikotoba.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * DebugのD Logのラッパー
 * 
 * @author raix
 * 
 */
public class D {
	public enum BuildMode {
		DEVELOPEMENT,
		PRODUCTION,
	}

	public enum LogLevel {
		NONE(0),
		ERROR(1),
		WARN(2),
		INFO(3),
		DEBUG(4),
		VORBOSE(5);
		public final int intValue;

		LogLevel(int intValue) {
			this.intValue = intValue;
		}
	}

	private LogLevel logLevel = LogLevel.VORBOSE;

	public static final D d = new D(BuildMode.DEVELOPEMENT);

	public D(BuildMode buildMode) {
		if (buildMode == BuildMode.PRODUCTION) {
			this.logLevel = LogLevel.WARN;
		} else {
			this.logLevel = LogLevel.VORBOSE;
		}
	}

	public D(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public void v(String format, Object... args) {
		if (LogLevel.VORBOSE.intValue > logLevel.intValue) {
			return;
		}
		String tag = tag();
		if (args.length == 0) {
			Log.v(tag, format);
		} else {
			Log.v(tag, String.format(format, args));
		}
	}

	public void d(String format, Object... args) {
		if (LogLevel.DEBUG.intValue > logLevel.intValue) {
			return;
		}
		String tag = tag();
		if (args.length == 0) {
			Log.d(tag, format);
		} else {
			Log.d(tag, String.format(format, args));
		}
	}

	public void i(String format, Object... args) {
		if (LogLevel.INFO.intValue > logLevel.intValue) {
			return;
		}
		String tag = tag();
		if (args.length == 0) {
			Log.i(tag, format);
		} else {
			Log.i(tag, String.format(format, args));
		}
	}

	public void w(String format, Object... args) {
		if (LogLevel.WARN.intValue > logLevel.intValue) {
			return;
		}
		String tag = tag();
		if (args.length == 0) {
			Log.w(tag, format);
		} else {
			Log.w(tag, String.format(format, args));
		}
	}

	public void e(String format, Object... args) {
		if (LogLevel.ERROR.intValue > logLevel.intValue) {
			return;
		}

		String tag = tag();
		if (args.length == 0) {
			Log.e(tag, format);
		} else {
			Log.e(tag, String.format(format, args));
		}
	}

	public static String str(Calendar calendar) {
		try {
			return calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/"
					+ calendar.get(Calendar.DAY_OF_MONTH) + " "
					+ calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE)
					+ ":" + calendar.get(Calendar.SECOND);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void pref(Context con) {
		try {
			Set<String> sorted = new TreeSet<String>();
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
			Map<String, ?> map = sp.getAll();
			D.d.d(String.format("preference has %d entries.", map.size()));
			for (String key : map.keySet()) {
				sorted.add(key);
			}
			for (String key : sorted) {
				D.d.d(String.format("key:%s(%s), val:%s", key, (map.get(key) == null) ? "" : map
								.get(key).getClass().getSimpleName(), String.valueOf(map.get(key))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static private String TAB = "    ";

	public static void setTab(String tab) {
		TAB = tab;
	}

	// JSONObjectをログに表示する
	// depth : 階層の深さ。初めに呼び出すときは0を指定。
	// tab : インデントに使う余白を指定。depth*tabだけインデントされる。
	public static void jsonObj(JSONObject obj) {
		jsonObj(obj, 0);
	}

	static private void jsonObj(JSONObject obj, int depth) {

		// インデントを計算
		String indent = "";
		for (int i = 0; i < depth; ++i) {
			indent += TAB;
		}

		// 内容を出力
		Iterator<String> ite = obj.keys();
		while (ite.hasNext()) {
			String key = ite.next();
			try {
				if (obj.get(key) instanceof JSONObject) {
					D.d.d(indent + key + " ( JSONObject ) {");
					jsonObj(obj.getJSONObject(key), depth + 1);
					D.d.d(indent + "}");
				} else if (obj.get(key) instanceof JSONArray) {
					D.d.d(indent + key + " ( JSONArray ) [");
					jsonArray(obj.getJSONArray(key), depth + 1);
					D.d.d(indent + "]");
				} else {
					D.d.d(indent + key + " ( " + obj.get(key).getClass().getSimpleName()
							+ " ) " + " : " + String.valueOf(obj.get(key)));
				}
			} catch (JSONException e) {
				D.d.d(e.toString());
			}
		}

		// 深度をデクリメントして終了
		depth -= 1;
		indent = "";
		for (int i = 0; i < depth; ++i) {
			indent += TAB;
		}
	}

	// JSONArrayをログに表示する
	// depth : 階層の深さ。初めに呼び出すときは0を指定。
	// tab : インデントに使う余白を指定。depth*tabだけインデントされる。
	public static void jsonArray(JSONArray array) {
		jsonArray(array, 0);
	}

	static private void jsonArray(JSONArray array, int depth) {

		// インデントを計算
		String indent = "";
		for (int i = 0; i < depth; ++i) {
			indent += TAB;
		}

		// 内容の出力
		for (int i = 0; i < array.length(); ++i) {
			try {
				if (array.get(i) instanceof JSONObject) {
					D.d.d(indent + "[" + String.valueOf(i) + "] ( JSONObject ) {");
					jsonObj(array.getJSONObject(i), depth + 1);
					D.d.d(indent + "}");
				} else if (array.get(i) instanceof JSONArray) {
					D.d.d(indent + "[" + String.valueOf(i) + "] ( JSONArray ) [");
					jsonArray(array.getJSONArray(i), depth + 1);
					D.d.d(indent + "]");
				} else {
					D.d.d(
							indent + "(" + String.valueOf(i) + ") "
									+ array.get(i).getClass().getSimpleName() + " ) " + " : "
									+ String.valueOf(array.get(i)));
				}
			} catch (JSONException e) {
				D.d.d(e.toString());
			}
		}

		// 深度をデクリメントして終了
		depth -= 1;
		indent = "";
		for (int i = 0; i < depth; ++i) {
			indent += TAB;
		}
	}

	// ファイルのデバッグ
	// path:
	public static void dir(String path) {
		File file = new File(path);
		dir(file);
	}
	
	public static void assets(Context context, String path) {
		AssetManager assetManager = context.getAssets();
		try {
			String[] files = assetManager.list(path);
			for (String filename : files) {
				D.d.d(" assets/%s", filename);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 指定したFileとその配下のFileをリストする。 ディレクトリに対しては再帰的に呼ばれる。
	 * 
	 * @param file
	 */
	public static void dir(File file) {
		if (!file.exists()) {
			D.d.d("target dir is not exist");
			return;
		}

		if (!file.isDirectory()) {
			D.d.d( " showDir %s", "F " + file.getAbsolutePath());
			return;
		} else {
			D.d.d( " showDir %s", "D " + file.getAbsolutePath());
		}

		// ディレクトリ内
		File[] files = file.listFiles();
		if (files == null) {
			return;
		}
		for (int i = 0, len = files.length; i < len; ++i) {
			dir(files[i]);
		}
	}

	public static String str(String[] array) {
		StringBuffer log = new StringBuffer();
		log.append("debugArray() array.length=" + String.valueOf(array.length) + "\r\n");
		for (int i = 0; i < array.length; ++i) {
			log.append(String.format("  %5d  : %s", i, array[i]) + "\r\n");
		}
		return log.toString();
	}

	public static String str(int[] array) {
		StringBuffer log = new StringBuffer();
		log.append("debugArray() array.length=" + String.valueOf(array.length) + "\r\n");
		for (int i = 0; i < array.length; ++i) {
			log.append(String.format("  %5d  : %s", i, String.valueOf(array[i])) + "\r\n");
		}
		return log.toString();
	}

	public static String str(double[] array) {
		StringBuffer log = new StringBuffer();

		log.append("debugArray() array.length=" + String.valueOf(array.length) + "\r\n");
		for (int i = 0; i < array.length; ++i) {
			log.append(String.format("  %5d  : %s", i, String.valueOf(array[i])) + "\r\n");
		}
		return log.toString();
	}

	public static String str(float[] array) {
		StringBuffer log = new StringBuffer();
		log.append("debugArray() array.length=" + String.valueOf(array.length) + "\r\n");
		for (int i = 0; i < array.length; ++i) {
			log.append(String.format("  %5d  : %s", i, String.valueOf(array[i])) + "\r\n");
		}
		return log.toString();
	}

	public static String str(Set<String> set) {
		StringBuffer log = new StringBuffer();
		if (set == null) {
			log.append("the set is null");
		} else {
			Object[] keys = set.toArray();
			log.append(String.format(" size : %d", set.size()) + "\r\n");
			for (int i = 0; i < keys.length; ++i) {
				log.append(String.format("  : %s", keys[i].toString()) + "\r\n");
			}
			/*
			 * Iterator<String> ite = set.iterator(); Log.d(TAG + " " +
			 * getMethodName(), String.format(" size : %d", set.size()) );
			 * while( ite.hasNext() ){ Log.d(TAG, String.format("  : %s", ite)
			 * ); ite.next(); }
			 */
		}
		return log.toString();
	}

	public static String str(Map<?, ?> map) {
		StringBuffer log = new StringBuffer();
		if (map == null) {
			log.append("the set is null" + "\r\n");
		} else {
			log.append(String.format(" size : %d", map.size()) + "\r\n");
			Object[] keyArray = map.keySet().toArray();
			for (int i = 0; i < keyArray.length; ++i) {
				String keyClass = keyArray[i].getClass().getSimpleName();
				String valueClass = map.get(keyArray[i]) == null ? "null" : map.get(keyArray[i])
						.getClass().getSimpleName();
				String value = map.get(keyArray[i]) == null ? "" : map.get(keyArray[i]).toString();
				log.append(String.format("(%s)%s : (%s)%s", keyClass, keyArray[i], valueClass,
						value) + "\r\n");
			}
		}
		return log.toString();
	}

	public static String str(ArrayList<String> list) {
		StringBuffer log = new StringBuffer();
		if (list == null) {
			log.append("the set is null");
		} else {
			for (int i = 0, len = list.size(); i < len; ++i) {
				log.append(String.format(" %5d  : %s", i, String.valueOf(list.get(i))) + "\r\n");
			}
		}
		return log.toString();
	}

	// メソッド名を返す
	public static String methodName() {
		try {
			int depth = 3; // 呼び出し元からの深さ。メソッド呼び出し元のメソッド名がほしいので1ではなく2。
			String methodName = " ";

			int line = Thread.currentThread().getStackTrace()[depth].getLineNumber();
			// String clazz =
			// Thread.currentThread().getStackTrace()[depth].getClassName();
			// String[] pkg = clazz.split("\\.");
			// clazz = pkg[pkg.length-1];
			String method = Thread.currentThread().getStackTrace()[depth].getMethodName();
			methodName = String.format(" %s [%d]", method, line);

			methodName += " ";

			return methodName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "null";
	}
	

	// ログメソッド内で使用
	static private String tag() {
		try {
			int depth = 4; // 呼び出し元からの深さ。
			String methodName = " ";

			int line = Thread.currentThread().getStackTrace()[depth].getLineNumber();
			// String clazz =
			// Thread.currentThread().getStackTrace()[depth].getClassName();
			// String[] pkg = clazz.split("\\.");
			// clazz = pkg[pkg.length-1];
			String method = Thread.currentThread().getStackTrace()[depth].getMethodName();
			String[] pkgs = Thread.currentThread().getStackTrace()[depth].getClassName().split(".");
			String clazz = (pkgs.length > 0)? pkgs[pkgs.length - 1] : Thread.currentThread().getStackTrace()[depth].getClassName();
			methodName = String.format("%s.%s (%d)", clazz, method, line);

			methodName += " ";
			
			return methodName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "null";
	}

	public static String str(Cursor c) {
		StringBuffer log = new StringBuffer();
		String s = "";

		String[] names = c.getColumnNames();
		int[] types = new int[names.length];
		for (int i = 0; i < names.length; ++i) {
			String name = names[i];
			s += name + ",    ";

			int index = c.getColumnIndex(name);
			D.d.d(name);
			D.d.d("" + index);
			types[i] = c.getType(index);
		}
		log.append(s + "\r\n");

		s = "";
		for (int i = 0; i < types.length; ++i) {
			int type = types[i];
			switch (type) {
			case Cursor.FIELD_TYPE_FLOAT:
				s += String.valueOf(c.getFloat(i)) + ",    ";
				break;
			case Cursor.FIELD_TYPE_INTEGER:
				s += String.valueOf(c.getInt(i)) + ",    ";
				break;
			case Cursor.FIELD_TYPE_NULL:
				s += "NULL,\t";
				break;
			case Cursor.FIELD_TYPE_STRING:
				s += String.valueOf(c.getString(i)) + ",    ";
				break;
			default:
				break;
			}
		}
		log.append(s + "\r\n");

		return log.toString();
	}

	/**
	 * Cursorをスキャンするのでイテレーションがリセットされることに注意
	 * 
	 * @param c
	 * @return
	 */
	public static String str_(Cursor c) {
		StringBuffer log = new StringBuffer();
		String s = "";
		boolean exists = c.moveToFirst();

		String[] names = c.getColumnNames();
		int[] types = new int[names.length];
		for (int i = 0; i < names.length; ++i) {
			String name = names[i];
			s += name + ",    ";

			int index = c.getColumnIndex(name);
			types[i] = c.getType(index);
		}
		log.append(s + "\r\n");

		do {
			s = "";
			for (int i = 0; i < types.length; ++i) {
				int type = types[i];
				switch (type) {
				case Cursor.FIELD_TYPE_FLOAT:
					s += String.valueOf(c.getFloat(i)) + ",    ";
					break;
				case Cursor.FIELD_TYPE_INTEGER:
					s += String.valueOf(c.getInt(i)) + ",    ";
					break;
				case Cursor.FIELD_TYPE_NULL:
					s += "NULL,\t";
					break;
				case Cursor.FIELD_TYPE_STRING:
					s += String.valueOf(c.getString(i)) + ",    ";
					break;
				default:
					break;
				}
			}
			log.append(s + "\r\n");
		} while (exists && c.moveToNext());

		return log.toString();
	}

}
