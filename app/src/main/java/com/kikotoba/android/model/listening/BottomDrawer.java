package com.kikotoba.android.model.listening;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kikotoba.android.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class BottomDrawer
        extends LinearLayout {
    private static final String TAG = "BottomDrawer";

    private static final int STATUS_CLOSE = 1;
    private static final int STATUS_INPUT = 2;
    private static final int STATUS_OPEN = 3;

    private int mStatus = STATUS_CLOSE;
    private Context mContext;
    private View mRoot;
    private ViewerWebView mSubWebView;
    private TextView mTitle;
    private View mTitleBar;
    private View mSpacer;

    public static final int TRANSCRIPT_LAYOUT_WEIGHT = 4;
    public static final int WORDSEARCH_LAYOUT_WEIGHT = 2;

    public BottomDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mContext = context;

        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate.inflate(R.layout.bottom_drawer, this);
    }

    public BottomDrawer(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            initControllerView(mRoot);
    }

    private void initControllerView(View v) {
        mSubWebView = (ViewerWebView) v.findViewById(R.id.subViewer);
        mTitle = (TextView) v.findViewById(R.id.textViewSearch);
        mTitleBar = findViewById(R.id.containerSearchHeader);
        mSpacer = findViewById(R.id.spacer);
        mTitleBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 後ろのviewにタッチを伝えない
                return true;
            }
        });
        v.findViewById(R.id.buttonOpenSubview).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubWebView.getVisibility() == View.VISIBLE) {
                    closeSubViewer();
                } else {
                    openAsWordSearch(mTitle.getText().toString());
                }
            }
        });
    }

    private OnTouchListener spacerTouchedListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            closeSubViewer();
            return false;
        }
    };

    public boolean isStatusOpen() {
        return mStatus == STATUS_OPEN;
    }

    public void startInput(String text) {
        mStatus = STATUS_INPUT;
        mTitle.setText(text);
        mTitleBar.setVisibility(View.VISIBLE);
    }

    public void setTranscript(String title, String text) {
        mTitle.setText(title);

        text = text.trim();
        if (text == null || text.isEmpty()) {
            return;
        }
        mSubWebView.loadData(
                "<html><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><body>"
                        + text
                        + "</body></html>",
                "text/html; charset=utf-8", "UTF-8");
    }

    public void closeSubViewer() {
        mStatus = STATUS_CLOSE;
        mSubWebView.setVisibility(View.GONE);
        mTitleBar.setVisibility(View.GONE);
        mSpacer.setOnTouchListener(null);
    }

    public void openAsWordSearch(String text) {
        text = text.trim();
        if (text != null || !text.isEmpty()) {
            String query = "";
            if (Pattern.compile("^[ -~]+$").matcher(text).find()) {
                // 半角のみ
                query = text + " 日本語";
            } else {
                query = text + " 英語";
            }

            try {
                String url = String.format(
                        "https://www.google.com/search?q=%s", URLEncoder.encode(query, "UTF-8"));
                Log.v(TAG, url);
                mSubWebView.loadUrl(url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        openSubViewer(WORDSEARCH_LAYOUT_WEIGHT);
    }

    public void openSubViewer(int weight) {
        Log.v(TAG, String.format("%d, %d, %f, %d",
                getHeight(), weight,
                (float) getHeight() / (float) weight,
                (int) ((float) getHeight() / (float) weight))
        );
        mStatus = STATUS_OPEN;
//        mSpacer.setOnTouchListener(spacerTouchedListener);

        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        params.weight = 0;
        params.height = (int) ((float) getHeight() / (float) weight);
        mSubWebView.setLayoutParams(params);


        mTitleBar.setVisibility(View.VISIBLE);
        mSubWebView.setVisibility(View.VISIBLE);
    }

}
