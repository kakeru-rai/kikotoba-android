package net.snow69it.listeningworkout.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * DebugのD Logのラッパー
 * 
 * @author raix
 * 
 */
public class FileUtil {

	public static File createTmpFile(String name, String extension) throws IOException {
		return File.createTempFile(name, extension);
	}

	public static File createPrivateExternalDir(Context context, String path) {
		return new File(context.getExternalFilesDir(null), path);
	}

	public static void copyFile(File source, File toFile) throws IOException {
		FileUtils.copyFile(source, toFile);
	}

	public static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

	private AssetManager assetManager;

	public FileUtil(Context context) {
		assetManager = context.getAssets();
	}

	private boolean isDirectory(final String path) {
		boolean isDirectory = false;
		try {
			if (assetManager.list(path).length > 0){ //子が含まれる場合はディレクトリ
				isDirectory = true;
			} else {
				// オープン可能かチェック
				assetManager.open(path);
			}
		} catch (FileNotFoundException fnfe) {
			isDirectory = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isDirectory;
	}

	public void copyFiles(final String parentPath, final String filename, final File toExternalStorageDir) throws IOException {

		String assetpath = (parentPath == null || parentPath.isEmpty() ? filename : parentPath + File.separator + filename);
		Log.v("######### 0", String.format("", parentPath + "/" + filename + "   " + assetpath));
		if (isDirectory(assetpath)) { //ディレクトリ判定
			Log.v("######### 1", assetpath);
			if (!toExternalStorageDir.exists()) {
				//出力先のディレクトリ作成
				boolean result = toExternalStorageDir.mkdirs();
				Log.v("######### 2", String.valueOf(result));
			}
			for (String child : assetManager.list(assetpath)) {
				Log.v("######### 3", child);
				//再帰呼出
				copyFiles(assetpath, child, new File(toExternalStorageDir, child));
			}
		} else {
			//バイナリコピー
			Log.v("######### 4", assetpath);
			FileUtil.copyFile(assetManager.open(assetpath),
					new FileOutputStream(new File(toExternalStorageDir.getParentFile(), filename)));
		}
		Log.v("######### 5", assetpath);
	}

}
