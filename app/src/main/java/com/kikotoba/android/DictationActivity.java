package com.kikotoba.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.kikotoba.android.model.audio.AudioWebInterface;
import com.kikotoba.android.model.entity.Article;
import com.kikotoba.android.model.entity.ArticlePair;
import com.kikotoba.android.model.entity.Sentence;
import com.kikotoba.android.util.WebViewDefault;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DictationActivity extends BaseActivity {
    private static final String TAG = "SpeakingActivity";

    public static final String ARTICLE_ID = "article_id";
    public static final String ARTICLE_TITLE = "article_title";
    public static final String ARTICLE_PAIR = "article_pair";

    public static Intent newIntent(Context context, String articleId, String title, ArticlePair articlePair) {
        Intent intent = new Intent(context, DictationActivity.class);
        intent.putExtra(DictationActivity.ARTICLE_ID, articleId);
        intent.putExtra(DictationActivity.ARTICLE_TITLE, title);
        intent.putExtra(DictationActivity.ARTICLE_PAIR, articlePair.toJson());
        return intent;
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Handler mHandler = new Handler();
    private AudioWebInterface mAudioWebInterface;
    private Article mArticle;
    @BindView(R.id.audioWebview) WebViewDefault webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getArticleTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        String json = getIntent().getStringExtra(DictationActivity.ARTICLE_PAIR);
        ArticlePair entity = ArticlePair.fromJson(json);
        mArticle = entity.getTarget();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mArticle.getSentences());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(mViewPager);

        mAudioWebInterface = createDictationWebInterface(webView);
    }

    private String getArticleId() {
        return getIntent().getStringExtra(ARTICLE_ID);
    }

    private String getArticleTitle() {
        return getIntent().getStringExtra(ARTICLE_TITLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAudioWebInterface.pause();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Sentence> sentences;

        public SectionsPagerAdapter(FragmentManager fm, List<Sentence> sentences) {
            super(fm);
            this.sentences = sentences;
        }

        @Override
        public Fragment getItem(int position) {
            return DictationFragment.newInstance(getArticleId(), position);
        }

        @Override
        public int getCount() {
            return sentences.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.format("#%d", position);
        }
    }

    public void play(int index) {
        Sentence sentence = mArticle.getSentences().get(index);
        mAudioWebInterface.play(sentence.getFromSec(), sentence.getToSec());
    }

    private AudioWebInterface createDictationWebInterface(WebView webView){
        return new AudioWebInterface(webView) {
            @JavascriptInterface
            @Override
            public void onReady() {
                mHandler.post(new Runnable() {
                    public void run() {
                        mAudioWebInterface.setAudioSrc(mArticle.getAudio());
                    }
                });
            }
        };
    }
}
