package net.snow69it.listeningworkout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.snow69it.listeningworkout.article.Article;
import net.snow69it.listeningworkout.article.ArticlePair;
import net.snow69it.listeningworkout.article.Sentence;

import java.util.List;

public class DictationActivity extends AppCompatActivity {
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

    private static final int REQUEST_CODE = 1;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getArticleTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);

        String json = getIntent().getStringExtra(DictationActivity.ARTICLE_PAIR);
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
}
