package com.kikotoba.android.model.dictation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BlankTokenPicker {

    /**
     * トークンリストの中で穴埋め対象となるインデックスと文字列のマップ
     */
    private HashMap<Integer, String> alphabetIndexList;

    /**
     * 穴埋め対象と決定したインデックスセット
     */
    private Set<Integer> blankIndexSet;

    private Level mLevel;

    public BlankTokenPicker(Level level) {
        mLevel = level;
    }

    /**
     * tokenListから穴埋め対象のTextTokenを決定する
     * @param tokenList
     * @throws Exception
     */
    public void pick(List<TextToken> tokenList) throws Exception {
        blankIndexSet = new HashSet();

        alphabetIndexList = scanAlphabetIndex(tokenList);

        selectSmallWord();
        selectRandom();

        setBlank(tokenList);
    }

    /**
     * 穴埋め対象のみ抽出する
     * @param tokenList
     */
    private HashMap<Integer, String> scanAlphabetIndex(List<TextToken> tokenList) {
        HashMap<Integer, String> alphabetIndexList = new HashMap<Integer, String>();
        for (int i = 0; i < tokenList.size(); i++) {
            TextToken token = tokenList.get(i);
            if (token.charType == TextToken.CHAR_TYPE.ALPHABET) {
                alphabetIndexList.put(i, token.text);
            }
        }
        return alphabetIndexList;
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
                blankRatio = 1.0 / 4.0;
                break;
            case NORMAL:
                blankRatio = 1.0 / 2.0;
                break;
            case HARD:
                blankRatio = 4.0 / 5.0;
                break;
            default:
                throw new RuntimeException("予期せぬDictationLevelが指定されました");
        }
        Random r = new Random((int)(Math.random() * 100 / 10));
        List<Integer> indexList = new ArrayList();
        for (Integer index : alphabetIndexList.keySet()) {
            indexList.add(index);
        }

        Collections.shuffle(indexList);
        int selectCount = (int) (blankRatio * indexList.size());
        if (selectCount == 0) {
            ++selectCount;
        }
        for (int i = 0; i < selectCount; ++i) {
            blankIndexSet.add(indexList.get(i));
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
