package net.snow69it.listeningworkout;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.snow69it.listeningworkout.util.Navigation;

abstract public class BaseActivity extends AppCompatActivity {

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                Navigation.goSettings(this);
                return true;
            case R.id.action_shadowing:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
