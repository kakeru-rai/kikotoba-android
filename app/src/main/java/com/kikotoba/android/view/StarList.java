package com.kikotoba.android.view;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kikotoba.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * カードのレベルスター
 */
public class StarList extends LinearLayout {

    private final Context context;
    @BindView(R.id.viewCardStar1) View start1;
    @BindView(R.id.viewCardStar2) View start2;
    @BindView(R.id.viewCardStar3) View start3;

    public StarList(Context context) {
        this(context, null);
    }

    public StarList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.view_card_starts, this);
        ButterKnife.bind(rootView);
    }

    public void setStarCount(int starCount) {
        start1.setVisibility(starCount >= 1 ? View.VISIBLE : View.GONE);
        start2.setVisibility(starCount >= 2 ? View.VISIBLE : View.GONE);
        start3.setVisibility(starCount >= 3 ? View.VISIBLE : View.GONE);
    }

}
