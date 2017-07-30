package com.kikotoba.android;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseError;
import com.kikotoba.android.model.WorkingDirectory;
import com.kikotoba.android.model.entity.user.Summary;
import com.kikotoba.android.repository.BaseRepository;
import com.kikotoba.android.repository.user.SummaryRepository;
import com.kikotoba.android.util.IOUtil;
import com.kikotoba.android.util.Versatile;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private MainActivity _this = this;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private FirebaseAuth mAuth;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WorkingDirectory.getInstance().createWorkingDirectory(this);
//        wd.deleteAudio(this);

        init();

        // デバッグ
//        D.pref(this);

    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                WorkingDirectory.getInstance().deleteAudio(MainActivity.this);
            }
        });

        logUserEvent();

    }

    private void logUserEvent() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        final SummaryRepository summaryRepository = new SummaryRepository();
        summaryRepository.get(user.getUid(), new BaseRepository.EntityEventListener<Summary>() {
            @Override
            public void onSuccess(Summary entity) {
                if (entity == null) {
                    entity = new Summary();
                }
                if (StringUtils.isEmpty(entity.getStartAndroidAppVersionName())) {
                    entity.setStartAndroidAppVersionName(Versatile.getVersionName(_this));
                }
                summaryRepository.update(user.getUid(), entity, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            return;
                        }
                        FirebaseCrash.report(task.getException());
                    }
                });
            }
            @Override
            public void onError(DatabaseError error) {
                FirebaseCrash.report(error.toException());
            }
        });
    }

    private FirebaseUser getUser() {
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "user :" + user.getUid());
            // User is signed in
        } else {
            Log.d(TAG, "user :No user is signed in");
            // No user is signed in
        }

        return user;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final String[] tabNames;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            tabNames = getResources().getStringArray(R.array.main_tab_names);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            switch (position) {
                case 0:
                    fragment = new ArticleListFragment();
                    break;
                case 1:
                    fragment = new SettingsFragment();
//                    fragment = ProfileFragment.newInstance();
                    break;
                case 2:
                    File sd = IOUtil.getPrivateExternalDir(MainActivity.this, "");
                    fragment = WebViewFragment.newInstance("file://" + sd.getPath() + "/html/description.html");
                    break;
                default:
                    fragment = new ArticleListFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames[position];
        }
    }
}
