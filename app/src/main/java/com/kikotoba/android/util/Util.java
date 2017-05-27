package com.kikotoba.android.util;

import android.content.Context;

import java.io.File;

/**
 * Created by raix on 2017/03/12.
 */

public class Util {

    public static String getSdPath(Context context, String path) {
        File sd = IOUtil.getPrivateExternalDir(context, "");
        return "file://" + sd.getPath() + path;
    }

    /**
     * urlからjavascriptに文字列を渡す際に必要なエスケープ処理
     * String.format("javascript: alert('%s')", text)
     * @param text
     * @return エスケープされた文字
     */
    public static String escapeJsArgumentFromUrl(String text) {
        return text
                .replace("'", "\\'") // 'をエスケープ
                .replace("\"", "\\\""); // "をエスケープ
    }

}
