package com.kikotoba.android.model.dictation;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TextTokenParserTest {

    String text = "So it's fascinating, but we .\" one-two 12";

    @Test
    public void get_returnCorrectCHAR_TYPE() throws Exception {
        assertEquals(TextToken.CHAR_TYPE.ALPHABET, TextToken.CHAR_TYPE.get('a'));
        assertEquals(TextToken.CHAR_TYPE.ALPHABET, TextToken.CHAR_TYPE.get('Z'));
        assertEquals(TextToken.CHAR_TYPE.ALPHABET, TextToken.CHAR_TYPE.get('\''));
        assertEquals(TextToken.CHAR_TYPE.SIGN, TextToken.CHAR_TYPE.get(','));
        assertEquals(TextToken.CHAR_TYPE.SIGN, TextToken.CHAR_TYPE.get('.'));
        assertEquals(TextToken.CHAR_TYPE.SIGN, TextToken.CHAR_TYPE.get('1'));
        assertEquals(TextToken.CHAR_TYPE.SIGN, TextToken.CHAR_TYPE.get('"'));
        assertEquals(TextToken.CHAR_TYPE.SIGN, TextToken.CHAR_TYPE.get(' '));
        assertEquals(TextToken.CHAR_TYPE.SIGN, TextToken.CHAR_TYPE.get(':'));
        assertEquals(TextToken.CHAR_TYPE.SIGN, TextToken.CHAR_TYPE.get(';'));
        assertEquals(TextToken.CHAR_TYPE.SIGN, TextToken.CHAR_TYPE.get('-'));
        assertEquals(TextToken.CHAR_TYPE.SIGN, TextToken.CHAR_TYPE.get('_'));
    }

    @Test
    public void paser_divideTokenCorrectly() throws Exception {
        TextTokenParser parser = new TextTokenParser();
        List<TextToken> result = parser.parse(text);

        assertEquals("So",              result.get(0).text);assertEquals(TextToken.CHAR_TYPE.ALPHABET, result.get(0).charType);
        assertEquals(" ",               result.get(1).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(1).charType);
        assertEquals("it's",            result.get(2).text);assertEquals(TextToken.CHAR_TYPE.ALPHABET, result.get(2).charType);
        assertEquals(" ",               result.get(3).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(3).charType);
        assertEquals("fascinating",     result.get(4).text);assertEquals(TextToken.CHAR_TYPE.ALPHABET, result.get(4).charType);
        assertEquals(",",               result.get(5).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(5).charType);
        assertEquals(" ",               result.get(6).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(6).charType);
        assertEquals("but",             result.get(7).text);assertEquals(TextToken.CHAR_TYPE.ALPHABET, result.get(7).charType);
        assertEquals(" ",               result.get(8).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(8).charType);
        assertEquals("we",              result.get(9).text);assertEquals(TextToken.CHAR_TYPE.ALPHABET, result.get(9).charType);
        assertEquals(" ",               result.get(10).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(10).charType);
        assertEquals(".",               result.get(11).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(11).charType);
        assertEquals("\"",              result.get(12).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(12).charType);
        assertEquals(" ",               result.get(13).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(13).charType);
        assertEquals("one",             result.get(14).text);assertEquals(TextToken.CHAR_TYPE.ALPHABET, result.get(14).charType);
        assertEquals("-",               result.get(15).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(15).charType);
        assertEquals("two",             result.get(16).text);assertEquals(TextToken.CHAR_TYPE.ALPHABET, result.get(16).charType);
        assertEquals(" ",               result.get(17).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(17).charType);
        assertEquals("1",               result.get(18).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(18).charType);
        assertEquals("2",               result.get(19).text);assertEquals(TextToken.CHAR_TYPE.SIGN, result.get(19).charType);
        assertEquals("分割数", 20, result.size());
    }
}