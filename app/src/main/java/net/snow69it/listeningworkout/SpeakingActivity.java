package net.snow69it.listeningworkout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.snow69it.listeningworkout.model.entity.Article;
import net.snow69it.listeningworkout.model.entity.ArticlePair;
import net.snow69it.listeningworkout.model.entity.Sentence;

import java.util.ArrayList;
import java.util.List;

public class SpeakingActivity extends AppCompatActivity {
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        //        final String title = getIntent().getStringExtra(ARTICLE_TITLE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getArticleTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);

        String json = getIntent().getStringExtra(SpeakingActivity.ARTICLE_PAIR);
        init(ArticlePair.fromJson(json));

    }

    private void init(ArticlePair entity) {
        Article mTargetArticle = entity.getTarget();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mTargetArticle.getSentences());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(mViewPager);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
