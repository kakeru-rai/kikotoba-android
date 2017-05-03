package net.snow69it.listeningworkout.util;

import android.content.Context;
import android.content.Intent;

import net.snow69it.listeningworkout.SettingsActivity;

/**
 * Created by raix on 2017/03/12.
 */

public class Navigation {
    public static void goSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }
}
