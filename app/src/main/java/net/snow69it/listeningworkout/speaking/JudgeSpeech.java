package net.snow69it.listeningworkout.speaking;

/**
 * 音声読み取りと元の文章が一致しているか判定を行う
 */
public class JudgeSpeech {

    /**
     *
     * @param expected オリジナルの文章
     * @param actual 比較対象の文章
     * @return 一致と判定したらtrue、そうでなければfalse
     */
    public static boolean validate(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        expected = normalize(expected);
        actual = normalize(actual);
        return expected.equals(actual);
    }

    private static String normalize(String text) {
        return text.replaceAll("[^a-zA-Z]", "").toLowerCase();
    }
}
