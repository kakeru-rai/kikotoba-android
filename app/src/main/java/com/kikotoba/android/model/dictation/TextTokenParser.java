package com.kikotoba.android.model.dictation;

import com.kikotoba.android.model.dictation.TextToken.CHAR_TYPE;

import java.util.ArrayList;
import java.util.List;

public class TextTokenParser {
    private List<TextToken> tokenList;
    private StringBuilder buffer;

    public List<TextToken> parse(String text) throws Exception {
        tokenList = new ArrayList<>();
        buffer = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            CHAR_TYPE charType = CHAR_TYPE.get(c);
            switch (charType) {
                case ALPHABET:
                    buffer.append(c);
                    break;
                case SIGN:
                    flushBuffer();
                    tokenList.add(new TextToken(CHAR_TYPE.SIGN, String.valueOf(c)));
                    break;
            }
        }
        flushBuffer();

        return tokenList;
    }

    private void flushBuffer() {
        String token = buffer.toString();
        if (token.length() > 0) {
            tokenList.add(new TextToken(CHAR_TYPE.ALPHABET, token));
        }
        buffer = new StringBuilder();
    }
}
