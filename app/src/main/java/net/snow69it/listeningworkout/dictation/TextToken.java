package net.snow69it.listeningworkout.dictation;

import android.app.Activity;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.snow69it.listeningworkout.util.WidgetCreator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 穴埋め問題で使用する単語の単位
 * テキストから分割して得られる
 */
public class TextToken {

    public enum CHAR_TYPE {
        /**
         * 穴埋め対象となりうるアルファベットなどの文字
         */
        ALPHABET,
        /**
         * 穴埋め対象とならない記号などの文字
         */
        SIGN
        ;
        public static CHAR_TYPE get(char c) throws Exception {
            if (isWordCharacter(c)) {
                return ALPHABET;
            } else {
                return SIGN;
            }
        }

        private static boolean isWordCharacter(char c) {
            Pattern p = Pattern.compile("[a-zA-Z']");
            Matcher m = p.matcher(String.valueOf(c));
            return m.matches();
        }
    }

    private static final String PLACE_HOLDER = "Util";

    /**
     * 文字種別
     */
    public CHAR_TYPE charType;

    /**
     * 対象の文字列
     */
    public String text;

    /**
     * 穴埋め対象となっているか
     */
    public boolean isBlank = false;

    public TextToken(CHAR_TYPE charType, String text) {
        this.charType = charType;
        this.text = text;
    }

    public String getPlaceHolder() {
        String placeHolder = "";
        for (int i = 0; i < text.length(); i++) {
            placeHolder += PLACE_HOLDER;
        }
        return placeHolder;
    }

    public EditText createTextField(Activity activity) {
        EditText et = WidgetCreator.getEditText(activity);

        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        et.setLines(1);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        et.setHint(getPlaceHolder());
        et.setText(getPlaceHolder());
        InputFilter[] _inputFilter = new InputFilter[1];
        _inputFilter[0] = new InputFilter.LengthFilter(text.length());
        et.setFilters(_inputFilter);

        return et;
    }
}
