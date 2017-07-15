package com.kikotoba.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.kikotoba.android.model.audio.AudioWebInterface;
import com.kikotoba.android.model.entity.master.Article;
import com.kikotoba.android.model.entity.master.ArticlePair;
import com.kikotoba.android.model.entity.master.Sentence;
import com.kikotoba.android.util.WebViewDefault;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpeakingActivity extends BaseActivity {
    private static final String TAG = "SpeakingActivity";

    public static final String ARTICLE_ID = "article_id";
    public static final String ARTICLE_TITLE = "article_title";
    public static final String ARTICLE_PAIR = "article_pair";

    public static Intent newIntent(Context context, String articleId, String title, ArticlePair articlePair) {
        Intent intent = new Intent(context, SpeakingActivity.class);
        intent.putExtra(SpeakingActivity.ARTICLE_ID, articleId);
        intent.putExtra(SpeakingActivity.ARTICLE_TITLE, title);
        intent.putExtra(SpeakingActivity.ARTICLE_PAIR, articlePair.toJson());
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

    private static final int REQUEST_CODE = 1;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private boolean isSpeechWorking = false;

    private SpeechRecognizer mRecognizer;
    @BindView(R.id.audioWebview) WebViewDefault webView;
    private AudioWebInterface mAudioWebInterface;
    private Article mArticle;
    private Handler mHandler = new Handler();

    private RecognitionListener mRecognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.d("TAG", "onReadyForSpeech");
            getCurrentFragment().onReadyForSpeech();
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d("TAG", "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float v) {
            Log.d("TAG", "onRmsChanged");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.d("TAG", "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d("TAG", "onEndOfSpeech");
        }

        @Override
        public void onError(int i) {
            Log.d("TAG", "onError " + String.valueOf(i));
            getCurrentFragment().onSpeechError(i);
            isSpeechWorking = false;
            mRecognizer.destroy();
            mRecognizer = null;
        }

        @Override
        public void onResults(Bundle results) {
            List<String> resultstr = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
            getCurrentFragment().onSpeechResult(resultstr.get(0));
            isSpeechWorking = false;
            mRecognizer.destroy();
            mRecognizer = null;
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            Log.d("TAG", "onPartialResults");
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            Log.d("TAG", "onEvent");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        //        final String title = getIntent().getStringExtra(ARTICLE_TITLE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getArticleTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);

        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAudioWebInterface.pause();
    }

    public void play(int index) {
        Sentence sentence = mArticle.getSentences().get(index);
        mAudioWebInterface.play(sentence.getFromSec(), sentence.getToSec());
    }

    private void init() {
        String json = getIntent().getStringExtra(SpeakingActivity.ARTICLE_PAIR);
        ArticlePair entity = ArticlePair.fromJson(json);
        mArticle = entity._getTarget();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mArticle.getSentences());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(mViewPager);

        mAudioWebInterface = createDictationWebInterface(webView);
    }

    private AudioWebInterface createDictationWebInterface(WebView webView){
        return new AudioWebInterface(webView) {
            @JavascriptInterface
            @Override
            public void onReady() {
                mHandler.post(new Runnable() {
                    public void run() {
//                        mAudioWebInterface.setAudioSrc();
                    }
                });
            }
        };
    }

    private String getArticleId() {
        return getIntent().getStringExtra(ARTICLE_ID);
    }

    private String getArticleTitle() {
        return getIntent().getStringExtra(ARTICLE_TITLE);
    }

    private SpeakingFragment getCurrentFragment() {
        return (SpeakingFragment) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
    }

    // アクティビティ終了時に呼び出される
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 自分が投げたインテントであれば応答する
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String resultsString = "";

            // 結果文字列リスト
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            for (int i = 0; i< results.size(); i++) {
                // ここでは、文字列が複数あった場合に結合しています
                resultsString += results.get(i);
            }

            String[] items = results.toArray(new String[0]);
            new AlertDialog.Builder(this)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startSpeech() {
        if (isSpeechWorking) {
            return;
        }
        isSpeechWorking = true;

        final Context context = SpeakingActivity.this;

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, getString(R.string.target_language));
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mRecognizer.setRecognitionListener(mRecognitionListener);
        mRecognizer.startListening(intent);
        getCurrentFragment().onSpeechPreparing();
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a SpeakingFragment (defined as a static inner class below).
//            return SpeakingFragment.newInstance(position + 1);


            return SpeakingFragment.newInstance(getArticleId(), position);
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
}
