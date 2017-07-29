package com.kikotoba.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.kikotoba.android.model.LanguagePair;
import com.kikotoba.android.model.audio.AudioWebInterface;
import com.kikotoba.android.model.dictation.DictationScore;
import com.kikotoba.android.model.dictation.DictationSentencePicker;
import com.kikotoba.android.model.dictation.Level;
import com.kikotoba.android.model.entity.master.Article;
import com.kikotoba.android.model.entity.master.ArticlePair;
import com.kikotoba.android.model.entity.master.Sentence;
import com.kikotoba.android.model.entity.user.UserLogByArticle;
import com.kikotoba.android.repository.BaseRepository;
import com.kikotoba.android.repository.UserLogRepository;
import com.kikotoba.android.util.WebViewDefault;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DictationActivity extends BaseActivity {
    private static final String TAG = "DictationActivity";

    public static final String ARTICLE_PAIR = "article_pair";
    public static final String ARTICLE_PART_INDEX = "article_part_index";
    public static final String ARTICLE_LEVEL = "level";

    public static final int QUESTION_COUNT = 5;

    public static Intent newIntent(Context context,
                                   ArticlePair articlePair,
                                   int partIndex,
                                   Level level) {
        ArticlePair partialArticlePair = articlePair.devideToPart(partIndex);

        Intent intent = new Intent(context, DictationActivity.class);
        intent.putExtra(DictationActivity.ARTICLE_PAIR, partialArticlePair.toJson());
        intent.putExtra(DictationActivity.ARTICLE_PART_INDEX, partIndex);
        intent.putExtra(DictationActivity.ARTICLE_LEVEL, level.name());
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
    private AudioWebInterface mAudioWebInterface;
    private ArticlePair articlePair;
    private Article mArticle;
    private Article mTranslationArticle;
    private UserLogByArticle userLog;
    private DictationActivity mThis;

    // 状態変数
    private DictationScore dictationScore = new DictationScore(QUESTION_COUNT);

    @BindView(R.id.audioWebview) WebViewDefault webView;
    @BindView(R.id.dictationProgressLayout) View dictationProgressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation);
        ButterKnife.bind(this);
        mThis = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dictationProgressLayout.setVisibility(View.VISIBLE);
        init();

        ActionBar actionBar = getSupportActionBar();

        String partTitle = articlePair.getPartIndex().size() == 1
                ? ""
                : String.format("Part%d ", getPartIndex() + 1);
        actionBar.setTitle(partTitle + getTranslationArticle().getTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        String json = getIntent().getStringExtra(DictationActivity.ARTICLE_PAIR);
        articlePair = ArticlePair.fromJson(json);
        mArticle = articlePair._getTarget();
        mTranslationArticle = articlePair._getTranslated();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), pickupSentence(mArticle));
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.canScrollHorizontally(-1);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        initUserLog();
    }

    private void initUserLog() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final UserLogRepository repo = new UserLogRepository();
        repo.getUserLogByArticle(user.getUid(), articlePair._getId(), new BaseRepository.EntityEventListener<UserLogByArticle>() {
            @Override
            public void onSuccess(UserLogByArticle entity) {
                if (entity == null) {
                    entity = new UserLogByArticle();
                }
                userLog = entity;

                initAudioWebInterface(webView);
            }

            @Override
            public void onError(DatabaseError error) {
                new AlertDialog.Builder(mThis)
                        .setTitle(R.string.msg_error_common)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                mThis.finish();
                            }
                        })
                        .setNeutralButton(R.string.tmpl_close, null)
                        .show();
            }
        });
    }

    private void initAudioWebInterface(final WebView webView){
        final Handler handler = new Handler();
        mAudioWebInterface = new AudioWebInterface(webView) {
            @JavascriptInterface
            @Override
            public void onReady() {
                runOnUiThread(new Runnable() {
//                mHandler.post(new Runnable() {
                    public void run() {
                        mAudioWebInterface.setAudioSrc(articlePair, LanguagePair.getInstance().getTarget());
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    // 一問目の音声が再生されないことがある
                                    // 少し待つと解消されることが多いためスリープする
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                handler.post(new Runnable() {
                                    public void run() {
                                        dictationProgressLayout.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        };
    }

    private List<Integer> pickupSentence(Article article) {
        DictationSentencePicker picker = new DictationSentencePicker(
                article.makeSenteceStringList(), getLevel(), QUESTION_COUNT);
        picker.pickup();

        List<Integer> list = new ArrayList<>();
        list.addAll(picker.getResultIndexList());
        return list;
    }

    public int getPartIndex() {
        return getIntent().getIntExtra(ARTICLE_PART_INDEX, 0);
    }
    public Level getLevel() {
        return Level.valueOf(getIntent().getStringExtra(ARTICLE_LEVEL));
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

        private List<Integer> sentenceIndex;

        public SectionsPagerAdapter(FragmentManager fm, List<Integer> sentenceIndex) {
            super(fm);
            this.sentenceIndex = sentenceIndex;
        }

        @Override
        public DictationFragment getItem(int position) {
            return DictationFragment.newInstance(sentenceIndex.get(position), position);
        }

        @Override
        public int getCount() {
            return sentenceIndex.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.format("#%d", position);
        }
    }

    public void updateDictationScore() {
        int newScore = dictationScore.calcScoreRank().typeValue;
        if (userLog._getPart(getPartIndex())._getScore(getLevel()) >= newScore) {
            return;
        }
        // 記録を更新したらDBに反映
        userLog._getPart(getPartIndex())._setScore(getLevel(), newScore);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final UserLogRepository repo = new UserLogRepository();

        Task task = repo.setDictationScore(
                user.getUid(),
                articlePair._getId(),
                getLevel(),
                getPartIndex(),
                newScore);

        task.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.getException() != null) {
                    task.getException().printStackTrace();
                }
            }
        });
    }

    public Article getArticle() {
        return mArticle;
    }

    public Article getTranslationArticle() {
        return mTranslationArticle;
    }

    public void play(int index) {
        Sentence sentence = mArticle.getSentences().get(index);
        mAudioWebInterface.play(sentence.getFromSec(), sentence.getToSec());
    }

    public void next() {
        int nextPosition = mViewPager.getCurrentItem() + 1;
        mViewPager.setCurrentItem(nextPosition);
        ((DictationFragment) mSectionsPagerAdapter.instantiateItem(mViewPager, nextPosition)).onPageSelected();
    }

    public boolean isLastQuestion() {
        return mSectionsPagerAdapter.getCount() - 1 <= mViewPager.getCurrentItem();
    }

    public DictationScore getDictationScore() {
        return dictationScore;
    }
}
