package net.snow69it.listeningworkout.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.util.Linkify;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class WidgetCreator {
    static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
    static final int FP = LinearLayout.LayoutParams.MATCH_PARENT;

    static public void formatHtml(final Context context, TextView textView){
    	textView.setAutoLinkMask(Linkify.WEB_URLS);
    	MovementMethod movementmethod = LinkMovementMethod.getInstance();
        // TextView に LinkMovementMethod を登録します
    	textView.setMovementMethod(movementmethod);
        // <a>タグを含めたテキストを用意します
        String html = textView.getText().toString();
        // URLSpan をテキストにを組み込みます
        CharSequence spanned = Html.fromHtml(html, new Html.ImageGetter(){
            public Drawable getDrawable(String source){
                // 画像のリソースIDを取得
                int id = context.getResources().getIdentifier(source, "drawable", context.getPackageName());
                
                // リソースIDから Drawable のインスタンスを取得
                Drawable d = context.getResources().getDrawable(id);
                
                // 取得した元画像のサイズを取得し、表示画像のサイズとする
                int w = d.getIntrinsicWidth();
                int h = d.getIntrinsicHeight();
                d.setBounds(0, 0, w, h);
                
                return d;
            }
        }, null);
        textView.setText(spanned);
    }
	static public TextView getTextView(Context context, CharSequence text){
		TextView tv = new TextView(context);
		tv.setText(text);
		return tv;
	}
	static public CheckBox getCheckBox(Context context, String text, boolean checked){
		CheckBox cb = new CheckBox(context);
		if( text!=null ) cb.setText(text);
		cb.setChecked(checked);
		return cb;
	}
	static public Button getButton(Context context, String text){
		Button btn = new Button(context);
		btn.setText(text);
		return btn;
	}
	static public ScrollView getScrollView(Context context){
		return new ScrollView(context);
	}
	static public HorizontalScrollView getHorizontalScrollView(Context context){
		return new HorizontalScrollView(context);
	}
	//defStr:先頭の文字列、defIndex：デフォルトの選択肢
	static public Spinner getSpinner(Context context, String[] titles, String firstStr, int defIndex){
		Spinner spn = new Spinner(context);
		String[] newItems = null;

		//先頭の文字列がある場合は配列に追加
		if( firstStr!=null ){
			newItems = new String[titles.length+1];
			newItems[0] = firstStr;
			for(int i=1; i<newItems.length; ++i){
				newItems[i] = titles[i-1];
			}
		}else newItems = titles;
		
		//newItemsをスピナにセット
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, newItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn.setAdapter(adapter);

		//初期選択状態を設定
		if(defIndex<newItems.length-1) spn.setSelection(defIndex);
		
		return spn;
	}
	static public LinearLayout getLinearLayout(Context context, int orientation){
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(orientation);
		return ll;
	}
	static public EditText getEditText(Context context){
		EditText et = new EditText(context);
		et.setInputType(InputType.TYPE_CLASS_TEXT);
		return et;
	}
	static public SeekBar getSeekBar(Context context, int max){
		SeekBar sb = new SeekBar(context);
		sb.setMax(max);
		return sb;
	}
	static public RadioGroup getRadioGroup(Context context, String[] btnTexts, int selectedIndex){
		//RadioGroupを作成
		RadioGroup group = new RadioGroup(context);

		//RadioButtonを作成
		for(int i=0; i<btnTexts.length; ++i){
			RadioButton rbtn = new RadioButton(context);
			rbtn.setText(btnTexts[i]);
			group.addView(rbtn);
		}
		if(selectedIndex<btnTexts.length){
			RadioButton rbtn = (RadioButton)group.getChildAt(selectedIndex);
			rbtn.setChecked(true);
		}else{
			RadioButton rbtn = (RadioButton)group.getChildAt(0);
			rbtn.setChecked(true);
		}
		
		return group;
	}

}
