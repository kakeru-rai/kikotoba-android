package net.snow69it.listeningworkout.util;

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
}
