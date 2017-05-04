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

package net.snow69it.listeningworkout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import net.snow69it.listeningworkout.model.entity.ArticlePair;
import net.snow69it.listeningworkout.util.Navigation;

public class ListeningActivity extends AppCompatActivity {

    public static final String ARTICLE_ID = "article_id";
    public static final String ARTICLE_TITLE = "article_title";
    public static final String ARTICLE_PAIR = "article_pair";

    public static Intent newIntent(Context context, String articleId, String title, ArticlePair articlePair) {
        Intent intent = new Intent(context, ListeningActivity.class);
        intent.putExtra(ListeningActivity.ARTICLE_ID, articleId);
        intent.putExtra(ListeningActivity.ARTICLE_TITLE, title);
        intent.putExtra(ListeningActivity.ARTICLE_PAIR, articlePair.toJson());
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
        ListeningFragment fragment = ListeningFragment.newInstance(ArticlePair.fromJson(getIntent().getStringExtra(ARTICLE_PAIR)));
        fragmentTransaction.add(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        getMenuInflater().inflate(R.menu.sample_actions, menu);
//        MenuItem item = menu.findItem(R.id.action_shadowing);
//        SubMenu subMenu = item.getSubMenu();
//        subMenu.getItem(2).setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Navigation.goSettings(this);
                return true;
            case R.id.action_shadowing:
                return true;
            case R.id.action_shadowing_none:
            case R.id.action_shadowing_short:
            case R.id.action_shadowing_mid:
            case R.id.action_shadowing_long:
                item.setChecked(!item.isChecked());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
