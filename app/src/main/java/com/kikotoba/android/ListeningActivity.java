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

import com.kikotoba.android.model.entity.master.ArticlePair;

public class ListeningActivity extends BaseActivity {

    public static final String ARTICLE_PAIR = "article_pair";
    public static final String ARTICLE_PART_INDEX = "article_part_index";
    public static final String CURRENT_READING_INDEX = "current_reading_index";

    public static Intent newIntent(Context context,
                                   ArticlePair articlePair, int partIndex) {
//        int currentReadingIndex = articlePair._getUserLogByArticle(partIndex).getCurrentReadingIndex();

        ArticlePair partialArticlePair = articlePair.devideToPart(partIndex);

        Intent intent = new Intent(context, ListeningActivity.class);
        intent.putExtra(ListeningActivity.ARTICLE_PAIR, partialArticlePair.toJson());
        intent.putExtra(ListeningActivity.ARTICLE_PART_INDEX, partIndex);
        intent.putExtra(ListeningActivity.CURRENT_READING_INDEX, 0);
        return intent;
    }

    private ArticlePair articlePair;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        init();

        ActionBar actionBar = getSupportActionBar();
        String partTitle = articlePair.getPartIndex().size() == 1
                ? ""
                : String.format("Part%d ", getPartIndex() + 1);
        actionBar.setTitle(partTitle + articlePair._getTranslated().getTitle());
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

    private void init() {
        String json = getIntent().getStringExtra(DictationActivity.ARTICLE_PAIR);
        articlePair = ArticlePair.fromJson(json);
    }

    public int getPartIndex() {
        return getIntent().getIntExtra(ARTICLE_PART_INDEX, 0);
    }

    public ArticlePair getArticlePair() {
        return articlePair;
    }

}
