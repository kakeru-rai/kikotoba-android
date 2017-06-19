package com.kikotoba.android.model.dictation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BlankTokenPicker {

    private static final int SEED = 1;

    /**
     * トークンリストの中で穴埋め対象となるインデックスと文字列のマップ
     */
    private HashMap<Integer, String> alphabetIndexList;

    /**
     * 穴埋め対象と決定したインデックスセット
     */
    private Set<Integer> blankIndexSet;

    private DictationSentencePicker.Level mLevel;

    public BlankTokenPicker(DictationSentencePicker.Level level) {
        mLevel = level;
    }

    /**
     * tokenListから穴埋め対象のTextTokenを決定する
     * @param tokenList
     * @throws Exception
     */
    public void pick(List<TextToken> tokenList) throws Exception {
        alphabetIndexList = new HashMap<Integer, String>();
        blankIndexSet = new HashSet();

        scanAlphabetIndex(tokenList);

        selectSmallWord();
        selectRandom();

        setBlank(tokenList);
    }

    /**
     * 穴埋め対象のみ抽出する
     * @param tokenList
     */
    private void scanAlphabetIndex(List<TextToken> tokenList) {
        for (int i = 0; i < tokenList.size(); i++) {
            TextToken token = tokenList.get(i);
            if (token.charType == TextToken.CHAR_TYPE.ALPHABET) {
                alphabetIndexList.put(i, token.text);
            }
        }
    }

    /**
     * 少ない文字数の単語を選択する
     */
    private void selectSmallWord() {
    }

    private void selectRandom() {
        double blankRatio = 1.0 / 3.0;
        switch (mLevel) {
            case EASY:
                blankRatio = 1.0 / 3.0;
                break;
            case HARD:
                blankRatio = 3.0 / 4.0;
                break;
        }
        Random r = new Random(SEED);
        for (Integer index : alphabetIndexList.keySet()) {
            double d = r.nextDouble();
            if (d < blankRatio) {
                blankIndexSet.add(index);
            }
        }
    }

    /**
     * 穴埋め対象としたトークンにblankフラグを立てていく
     * @param tokenList
     */
    private void setBlank(List<TextToken> tokenList) {
        for (Integer i : blankIndexSet) {
            tokenList.get(i).isBlank = true;
        }
    }

}
