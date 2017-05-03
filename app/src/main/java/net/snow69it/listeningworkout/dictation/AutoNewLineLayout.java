package net.snow69it.listeningworkout.dictation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import net.snow69it.listeningworkout.util.WidgetCreator;

public class AutoNewLineLayout extends LinearLayout
{
    private static final String TAG = "AutoNewLineLayout";

    private Context mContext;
    private View mRoot;

    private LinearLayout currentLine;

    public AutoNewLineLayout(Context context) {
        this(context, null);
    }

    public AutoNewLineLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoNewLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRoot = this;
        mContext = context;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        currentLine = WidgetCreator.getLinearLayout(mContext, HORIZONTAL);
        addView(currentLine);
    }

    public void addViewNewLine(View view) {

        currentLine.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        Log.v(TAG, String.format("%d, %d, %d", currentLine.getMeasuredWidth(), getMeasuredWidth(), view.getMeasuredWidth()));


        if (currentLine.getMeasuredWidth() >= getMeasuredWidth()) {
            currentLine = WidgetCreator.getLinearLayout(mContext, HORIZONTAL);
            addView(currentLine);
        }
        currentLine.addView(view);
    }

}
