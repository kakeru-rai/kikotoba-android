package net.snow69it.listeningworkout.common;

/**
 * Created by raix on 2017/03/25.
 */

public class LanguagePair {

    private static final LanguagePair instance = new LanguagePair();

    public static LanguagePair getInstance() {
        return instance;
    }

    private String target = "en";
    private String mother = "ja";

    public String getTarget() {
        return target;
    }

    public String getMother() {
        return mother;
    }
}
