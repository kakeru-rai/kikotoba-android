package net.snow69it.listeningworkout.article;

import net.snow69it.listeningworkout.TestUtil.Util;
import net.snow69it.listeningworkout.datasource.ApiDataLoader;

import java.io.IOException;

/**
 * Created by raix on 2017/01/29.
 */

public class FakeApiDataLoader extends ApiDataLoader {

    public FakeApiDataLoader() {
        super(null);
    }

    @Override
    public String load(String path) {
        String rawResult = "";
        try {
            rawResult = Util.readTestFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawResult;
    }
}
