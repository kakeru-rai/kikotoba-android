package net.snow69it.listeningworkout.speaking;

import org.junit.Test;

import static org.junit.Assert.*;

public class JudgeSpeechTest {

    String expectedText = "Over the years, \"scientists\" ; the human brain.";

    String[] correctSpeech = {
            "Over the years, \"scientists\" ; the human brain.",
            "OVER THE YEARS SCIENTISTS THE HUMAN BRAIN",
            "!\"#$%&'\\()=~|Over the years scientists the human brain  ",
    };

    String[] inCorrectSpeech = {
            "Over the years, \"scientists\" ; the human braina.",
            "Over",
            "",
            null
    };

    @Test
    public void validate_correct() throws Exception {
        for (String actual : correctSpeech) {
            assertTrue(actual, JudgeSpeech.validate(expectedText, actual));
        }
    }

    @Test
    public void validate_incorrect() throws Exception {
        for (String actual : inCorrectSpeech) {
            assertFalse(JudgeSpeech.validate(expectedText, actual));
        }
    }

}