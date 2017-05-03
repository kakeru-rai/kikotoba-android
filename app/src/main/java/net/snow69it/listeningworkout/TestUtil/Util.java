package net.snow69it.listeningworkout.TestUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by raix on 2017/01/29.
 */

public class Util {

    /**
     * テストフォルダ内からテストデータをロード
     * @param path
     * @return
     * @throws IOException
     */
    public static String readTestFile(String path) throws IOException {
        File file = new File(getTestRootDirPath() + path);
        return FileUtils.readFileToString(file, "UTF-8");
    }

    private static String getTestRootDirPath() {
        String pjRoot = (new File("")).getAbsolutePath();
        String testRoot = "/src/test/java/net/snow69it/listeningworkout/";
        return pjRoot + testRoot;
    }
}
