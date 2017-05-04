package net.snow69it.listeningworkout;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * webviewを表示する汎用Activity
 */
public class WebViewActivity extends AppCompatActivity {

    private static final String ARG_URL = "url";
    private static final String ARG_TITLE = "title";

    public static void move(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(ARG_URL, url);
        intent.putExtra(ARG_TITLE, title);
        context.startActivity(intent);
    }

    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(ARG_URL);
        mTitle = getIntent().getStringExtra(ARG_TITLE);
        if (url == null) {
            url = "";
        }
        setupActionBar();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, WebViewFragment.newInstance(url))
                .commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(true);
        if (mTitle != null && !mTitle.isEmpty()) {
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
