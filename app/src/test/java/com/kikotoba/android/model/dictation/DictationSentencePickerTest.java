package com.kikotoba.android.model.dictation;

import com.kikotoba.android.model.entity.Sentence;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

public class DictationSentencePickerTest {
    
    private static final int MAX_PICKING_COUNT = 5;

    @Test
    public void pickup_tinyCountScripts_rangeAllScripts() throws Exception {
        List<String> sentenceList = new ArrayList<>(Arrays.asList(
                "dddd",
                "ccc",
                "bb",
                "a"
        ));

        DictationSentencePicker dictationSentencePicker;
        List<String> scripts;

        // easy
        dictationSentencePicker = new DictationSentencePicker(
                sentenceList,
                Level.EASY,
                MAX_PICKING_COUNT);
        scripts= dictationSentencePicker.pickup();

        assertEquals("全て選択される。", sentenceList.size(), scripts.size());
        assertTrue("全て選択される", scripts.contains("a"));
        assertTrue(scripts.contains("bb"));
        assertTrue(scripts.contains("ccc"));
        assertTrue(scripts.contains("dddd"));

        // hard
        dictationSentencePicker = new DictationSentencePicker(
                sentenceList,
                Level.HARD,
                MAX_PICKING_COUNT);
        scripts = dictationSentencePicker.pickup();

        assertEquals("全て選択される", sentenceList.size(), scripts.size());
        assertTrue("全て選択される", scripts.contains("a"));
        assertTrue(scripts.contains("bb"));
        assertTrue(scripts.contains("ccc"));
        assertTrue(scripts.contains("dddd"));
    }

    @Test
    public void pickup_smallCountScripts_rangeDividedByPICKING_COUNT() throws Exception {
        List<String> sentenceList = new ArrayList<>(Arrays.asList(
                "gggggg",
                "fffff",
                "eeee",
                "dddd",
                "ccc",
                "bb",
                "a"
        ));

        DictationSentencePicker dictationSentencePicker;
        List<String> scripts;

        // easy
        dictationSentencePicker = new DictationSentencePicker(
                sentenceList,
                Level.EASY,
                MAX_PICKING_COUNT);
        scripts= dictationSentencePicker.pickup();

        assertEquals("所定の数だけ選ぶ", MAX_PICKING_COUNT, scripts.size());
        assertTrue("小さい方から選ぶ", scripts.contains("a"));
        assertTrue(scripts.contains("bb"));
        assertTrue(scripts.contains("ccc"));
        assertTrue(scripts.contains("dddd"));
        assertTrue(scripts.contains("eeee"));
        assertFalse(scripts.contains("fffff"));
        assertFalse(scripts.contains("gggggg"));

        // hard
        dictationSentencePicker = new DictationSentencePicker(
                sentenceList,
                Level.HARD,
                MAX_PICKING_COUNT);
        scripts = dictationSentencePicker.pickup();

        assertEquals("所定の数だけ選ぶ", MAX_PICKING_COUNT, scripts.size());
        assertTrue("多きい方から選ぶ", scripts.contains("gggggg"));
        assertTrue(scripts.contains("fffff"));
        assertTrue(scripts.contains("dddd"));
        assertTrue(scripts.contains("eeee"));
        assertTrue(scripts.contains("ccc"));
        assertFalse(scripts.contains("bb"));
        assertFalse(scripts.contains("a"));
    }

    @Test
    public void pickup_largeCountScripts_rangeDividedLevelCount() throws Exception {
        List<String> sentenceList = new ArrayList<>(Arrays.asList(
                "qqqqqqqqqqqqqqqq",
                "ppppppppppppppp", // 15
                "oooooooooooooo",
                "nnnnnnnnnnnnnn",
                "mmmmmmmmmmmm",
                "lllllllllll",
                "kkkkkkkkkk",
                "jjjjjjjjj", // 10
                "iiiiiiii",
                "hhhhhhh",
                "gggggg",
                "fffff",
                "eeee", // 5
                "dddd",
                "ccc",
                "bb",
                "a" // 1
        ));

        DictationSentencePicker dictationSentencePicker;
        List<String> scripts;
        int hitCount = 0;

        // easy
        dictationSentencePicker = new DictationSentencePicker(
                sentenceList,
                Level.EASY,
                MAX_PICKING_COUNT);
        scripts= dictationSentencePicker.pickup();

        assertEquals("所定の数だけ選ぶ", MAX_PICKING_COUNT, scripts.size());
        List<String> easyCandidateList = new ArrayList<>(Arrays.asList(
                "fffff",
                "eeee", // 5
                "dddd",
                "ccc",
                "bb",
                "a" // 1
        ));
        hitCount = 0;
        for (String script : easyCandidateList) {
            hitCount += scripts.contains(script) ? 1 : 0;
        }
        assertEquals("小さい方から選ぶ", MAX_PICKING_COUNT, hitCount);

        // hard
        dictationSentencePicker = new DictationSentencePicker(
                sentenceList,
                Level.HARD,
                MAX_PICKING_COUNT);
        scripts = dictationSentencePicker.pickup();

        assertEquals("所定の数だけ選ぶ", MAX_PICKING_COUNT, scripts.size());
        List<String> hardCandidateList = new ArrayList<>(Arrays.asList(
                "qqqqqqqqqqqqqqqq",
                "ppppppppppppppp", // 15
                "oooooooooooooo",
                "nnnnnnnnnnnnnn",
                "mmmmmmmmmmmm",
                "lllllllllll"
        ));
        hitCount = 0;
        for (String script : hardCandidateList) {
            hitCount += scripts.contains(script) ? 1 : 0;
        }
        assertEquals("大きい方から選ぶ", MAX_PICKING_COUNT, hitCount);
    }

    private Sentence newSentence(String text) {
        Sentence sentence = new Sentence();
        sentence.setText(text);
        return sentence;
    }

}