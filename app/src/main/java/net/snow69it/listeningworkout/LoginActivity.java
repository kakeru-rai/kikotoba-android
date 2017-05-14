package net.snow69it.listeningworkout;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import net.snow69it.listeningworkout.model.WorkingDirectory;

/**
 *
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        WorkingDirectory wd = new WorkingDirectory();
        wd.createWorkingDirectory(this);

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                new LoginFragment()).commit();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}

