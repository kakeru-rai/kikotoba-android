package com.kikotoba.android.view;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kikotoba.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * カードのレベルスター
 */
public class StarList extends LinearLayout {

    private final Context context;
    @BindView(R.id.viewCardStar1) ImageView start1;
    @BindView(R.id.viewCardStar2) ImageView start2;
    @BindView(R.id.viewCardStar3) ImageView start3;

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
        View rootView = inflater.inflate(R.layout.view_card_stars, this);
        ButterKnife.bind(rootView);
    }

    public void setStarCount(int starCount) {
        setupStar(start1, starCount >= 1);
        setupStar(start2, starCount >= 2);
        setupStar(start3, starCount >= 3);
    }

    private void setupStar(ImageView view, boolean on) {
        if (on) {
            view.setImageResource(R.drawable.ic_stars_white_24dp);
            view.setBackgroundResource(R.drawable.ic_circle_accent);
            view.setAlpha(0.9f);
        } else {
            view.setImageResource(R.drawable.ic_star_border_black_48dp);
            view.setBackgroundResource(0);
            view.setAlpha(0.1f);
        }
    }
}
