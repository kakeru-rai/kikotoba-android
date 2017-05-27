/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kikotoba.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.WindowManager;

import com.kikotoba.android.model.entity.ArticlePair;

public class ListeningActivity extends BaseActivity {

    public static final String ARTICLE_ID = "article_id";
    public static final String ARTICLE_TITLE = "article_title";
    public static final String ARTICLE_PAIR = "article_pair";
    public static final String CURRENT_READING_INDEX = "current_reading_index";

    public static Intent newIntent(Context context, String articleId, String title, ArticlePair articlePair, int currentReadingIndex) {
        Intent intent = new Intent(context, ListeningActivity.class);
        intent.putExtra(ListeningActivity.ARTICLE_ID, articleId);
        intent.putExtra(ListeningActivity.ARTICLE_TITLE, title);
        intent.putExtra(ListeningActivity.ARTICLE_PAIR, articlePair.toJson());
        intent.putExtra(ListeningActivity.CURRENT_READING_INDEX, currentReadingIndex);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        final String title = intent.getStringExtra(ARTICLE_TITLE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);

        addFragment();
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ListeningFragment fragment = ListeningFragment.newInstance(
                ArticlePair.fromJson(getIntent().getStringExtra(ARTICLE_PAIR)),
                getIntent().getIntExtra(CURRENT_READING_INDEX, 0)
        );
        fragmentTransaction.add(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

}
