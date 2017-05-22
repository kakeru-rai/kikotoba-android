package com.kikotoba.android.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class IOUtil {

	final static private String TAG = IOUtil.class.getSimpleName();

	public enum Encode{
		SJIS("SJIS"),
		UTF8("UTF-8");
		public final String text;
		Encode(final String text) {
			this.text = text;
		}
	}
	
	public enum ResType {
		APP_DATA,
		CACHE,
		ASSETS,
	}
	
	public enum LineBreak {
		CRLF("\r\n"),
		CR("\r"),
		LF("\n"),
		;
		
		public final String text;
		LineBreak(String text) {
			this.text = text;
		}
	}

	/**
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static File getFileFromAppData(Context context, String path){
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		File file = new File(context.getFilesDir().getAbsolutePath() + path);
		return file;
	}
	
	public static File getFileFromCache(Context context, String path){
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		File file = new File(context.getCacheDir().getAbsolutePath() + path);
		return file;
	}

	public static File getPrivateExternalDir(Context context, String path) {
		File file = new File(context.getExternalFilesDir(null), path);
		return file;
	}

	public static IOUtil newAssetsInstance() {
		IOUtil io = new IOUtil();
		io.setResType(ResType.ASSETS);
		return io;
	}

	
	private ResType resType = ResType.APP_DATA;

	//textファイル設定
	private Encode encode = Encode.UTF8;
	private LineBreak lineBreak = LineBreak.CRLF;
	
	//bitmapファイル設定
	private CompressFormat format = CompressFormat.PNG;
	private int quality = 100;

	public IOUtil() {
	}
	
	public IOUtil(ResType resType) {
		this.resType = resType;
	}

	public ResType getResType() {
		return resType;
	}

	public void setResType(ResType resType) {
		this.resType = resType;
	}

	public Encode getEncode() {
		return encode;
	}

	public void setEncode(Encode encode) {
		this.encode = encode;
	}

	public LineBreak getLineBreak() {
		return lineBreak;
	}

	public void setLineBreak(LineBreak lineBreak) {
		this.lineBreak = lineBreak;
	}

	public CompressFormat getFormat() {
		return format;
	}

	public void setFormat(CompressFormat format) {
		this.format = format;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		if (quality > 100) {
			quality = 100;
		} else if (quality < 0) {
			quality = 0;
		}
		this.quality = quality;
	}
	
	public String debug(Context context, String path) {
		File file = getUpdateFile(context, path);
		
		StringBuilder sb = new StringBuilder();
		debug(file, sb);
		return sb.toString();
	}
	
	private void debug(File file, StringBuilder sb){
		if (file.isFile()) {
			sb.append(
					file.getAbsolutePath()
					+ getLineBreak().text
					);
		}
		
		if (!file.isDirectory()) {
			return;
		}
		
		File[] files = file.listFiles();

		if ( files!=null ){
			for (File f : files){
				sb.append(
						f.getAbsolutePath()
						+ ((f.isDirectory())? "/" : "")
						+ getLineBreak().text
						);
				if (f.isDirectory()) {
					debug(f, sb);
				}
			}
		} else {
			sb.append("無効なファイル：" + file.getAbsolutePath());
		}
	}
	
	public void remove(Context context, String path) {
		File file = getUpdateFile(context, path);
		remove(file);
	}
	
	public void mkdir(Context context, String path) {
		File file = getUpdateFile(context, path);
		file.mkdirs();
	}
	
	public boolean isExist(Context context, String path) {
		File file = null;
		switch (resType) {
		case APP_DATA:
			file = getFileFromAppData(context, path);
			return file.exists();
		case CACHE:
			file = getFileFromCache(context, path);
			return file.exists();
		case ASSETS:
			AssetManager assets = context.getAssets();
			try {
				assets.open(path);
				return true;
			} catch (IOException e) {
				return false;
			}
		default:
			break;
		}
		throw new IllegalStateException(ResType.class.getSimpleName() + "の指定が不正です");
	}
	
	private void remove(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		
		File[] files = file.listFiles();

		if ( files!=null ){
			for (File f : files){
				if (f.isFile()) {
					f.delete();
				} else if (f.isDirectory()){
					remove(f);
				}
			}
			file.delete();
		}
	}

	public String getText(Context context, String path) throws IOException, FileNotFoundException, UnsupportedEncodingException{
	    InputStream is = null;
		String str = "";

	    try {
	    	is = getStream(context, path);
	    	
			str = JVersatile.convertInputStreamToString(is);
	    } finally {
	    	if (is != null) {
	    		is.close();
	    	}
	    }
		
        return str;
	}
	
	public void putText(Context context, String path , String text) throws FileNotFoundException, UnsupportedEncodingException, IllegalStateException {
		
		File file = getUpdateFile(context, path);
		
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
	
		FileOutputStream fos = null;
		PrintWriter writer = null;
		try {
			fos = new FileOutputStream(file);
			writer = new PrintWriter(new OutputStreamWriter(fos, encode.text));
			writer.append(text);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public Bitmap getBitmap(Context context, String path) throws FileNotFoundException, IOException{
		InputStream is = null;
		
		try {
			is = getStream(context, path);
			Bitmap bm = BitmapFactory.decodeStream(is);
			return bm;
		} finally {
			if ( is!=null ){
				is.close();
			}
		}
	}

	public void putBitmap(Context context, String path, Bitmap bitmap) throws IOException {
		File file = getUpdateFile(context, path);
		
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(format, quality, fos);
			fos.flush();
		} finally {
			if ( fos!=null ){
				fos.close();
			}
		}
	}
	
	
	// ==========================================================================================
	// utility
	//

	private File getUpdateFile(Context context, String path) throws IllegalStateException {
		File file = null;
		switch (resType) {
		case APP_DATA:
			file = getFileFromAppData(context, path);
			break;
		case CACHE:
			file = getFileFromCache(context, path);
			break;
		default:
			throw new IllegalStateException("assetsでは実行できません。");
		}
		return file;
	}
	
	@SuppressWarnings("resource")
	private InputStream getStream(Context context, String path) throws IOException {
		InputStream is = null;
		switch (resType) {
		case APP_DATA:
			is = new FileInputStream(getFileFromAppData(context, path));
			break;
		case CACHE:
			is = new FileInputStream(getFileFromCache(context, path));
			break;
		case ASSETS:
			if (path.startsWith("/")) {
				path = path.substring(1, path.length());
			}
			AssetManager assets = context.getAssets();
			is = assets.open(path);
			break;
		}
		return is;
	}
	
}
