package com.kikotoba.android.model.dictation;

import com.kikotoba.android.util.JVersatile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 文章のリストからディクテーションに使用する文章を抜き出す
 */
public class DictationSentencePicker {

    private Comparator<String> mScriptLengthCompAsc = new Comparator<String>() {
        public int compare(String s1, String s2) {
            return Math.abs(s1.length()) - Math.abs(s2.length());
        }
    };

    private final List<String> mSentenceList;
    private final Level mLevel;
    private final int maxPickingCount;

    
    private List<Integer> resultIndexList = new ArrayList<>();
    private List<String> resultScriptList = new ArrayList<>();

    public List<String> getResultScriptList() {
        return resultScriptList;
    }

    public List<Integer> getResultIndexList() {
        return resultIndexList;
    }

    public DictationSentencePicker(List<String> sentenceList, Level level, int maxPickingCount) {
        mSentenceList = sentenceList;
        mLevel = level;
        this.maxPickingCount = maxPickingCount;
    }

    public List<String> pickup() {
        // 文章の文字列リストを短い順で作成
        List<String> sortedScripts = new ArrayList<>(mSentenceList);
        Collections.sort(sortedScripts, mScriptLengthCompAsc);

        // 抽出範囲
        int fromIndex;
        int toIndex;
        int levelDividedRange = (sortedScripts.size() / (Level.values().length + 1));
        switch (mLevel) {
            case EASY:
                fromIndex = 0;
                if (sortedScripts.size() < maxPickingCount) {
                    toIndex = sortedScripts.size() - 1;
                } else if (levelDividedRange < maxPickingCount) {
                    toIndex = maxPickingCount - 1;
                } else {
                    toIndex = levelDividedRange;
                }
                break;
            case HARD:
                if (sortedScripts.size() < maxPickingCount) {
                    fromIndex = 0;
                } else if (levelDividedRange < maxPickingCount) {
                    fromIndex = sortedScripts.size() - maxPickingCount;
                } else {
                    fromIndex = sortedScripts.size() - 1 - levelDividedRange;
                }
                toIndex = sortedScripts.size() - 1;
                break;
            default:
                throw new RuntimeException("予期せぬDictationLevelが指定されました");
        }
        List<Integer> candidateIndexList = JVersatile.range(fromIndex, toIndex);

        // 候補の中からランダム抽出
        resultScriptList = new ArrayList<>();
        resultIndexList = new ArrayList<>();
        int pickingCount = this.maxPickingCount > sortedScripts.size() ? sortedScripts.size() : this.maxPickingCount;
        for (int i = 0; i < pickingCount; ++i) {
            int candidateIndex = JVersatile.getRandomInt(0, candidateIndexList.size() - 1);
            int scriptIndex = candidateIndexList.remove(candidateIndex);
            resultIndexList.add(scriptIndex);
            resultScriptList.add(sortedScripts.get(scriptIndex));
        }

        return resultScriptList;
    }
}
