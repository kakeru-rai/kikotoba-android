package net.snow69it.listeningworkout.datasource;

import android.content.Context;

import net.snow69it.listeningworkout.util.IOUtil;

import java.io.IOException;

/**
 * Created by raix on 2017/01/29.
 */

public class ApiDataLoader {

    private final Context context;

    public ApiDataLoader(Context context) {
        this.context = context;
    }

    public String load(String path) {
        IOUtil io = new IOUtil();
        io.setResType(IOUtil.ResType.ASSETS);

        String rawResult = "";
        try {
            rawResult = io.getText(context, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawResult;
    }
}
