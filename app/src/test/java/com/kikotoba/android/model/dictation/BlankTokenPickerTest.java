package com.kikotoba.android.model.dictation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class BlankTokenPickerTest {

    @Test
    public void pick_easy_blankCountRatio() throws Exception {
        List<TextToken> list = createTextTokenList(4, 2);

        BlankTokenPicker picker = new BlankTokenPicker(Level.EASY);
        picker.pick(list);

        int blankCount = 0;
        for (TextToken t : list) {
            blankCount += t.isBlank ? 1 : 0;
        }
        assertEquals("easyの比率で虫食いを作る", 1, blankCount);
    }

    @Test
    public void pick_normal_blankCountRatio() throws Exception {
        List<TextToken> list = createTextTokenList(4, 2);

        BlankTokenPicker picker = new BlankTokenPicker(Level.NORMAL);
        picker.pick(list);

        int blankCount = 0;
        for (TextToken t : list) {
            blankCount += t.isBlank ? 1 : 0;
        }
        assertEquals("normalの比率で虫食いを作る", 2, blankCount);
    }

    @Test
    public void pick_hard_blankCountRatio() throws Exception {
        List<TextToken> list = createTextTokenList(5, 2);

        BlankTokenPicker picker = new BlankTokenPicker(Level.HARD);
        picker.pick(list);

        int blankCount = 0;
        for (TextToken t : list) {
            blankCount += t.isBlank ? 1 : 0;
        }
        assertEquals("normalの比率で虫食いを作る", 4, blankCount);
    }

    @Test
    public void pick_tooShortSentence_atLeastSelectOneWord() throws Exception {
        List<TextToken> list = createTextTokenList(2, 2);
        BlankTokenPicker picker = new BlankTokenPicker(Level.EASY);
        picker.pick(list);

        int blankCount = 0;
        for (TextToken t : list) {
            blankCount += t.isBlank ? 1 : 0;
        }
        assertEquals("1つは選択する", 1, blankCount);
    }

    private List<TextToken> createTextTokenList(int alphabetCount, int signCount) {
        List<TextToken> list = new ArrayList();
        for (int i = 0; i < alphabetCount; ++i) {
            list.add(new TextToken(TextToken.CHAR_TYPE.ALPHABET, "a"));
        }
        for (int i = 0; i < signCount; ++i) {
            list.add(new TextToken(TextToken.CHAR_TYPE.SIGN, ";"));
        }
        return list;
    }

}