package net.snow69it.listeningworkout;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import net.snow69it.listeningworkout.util.Navigation;
import net.snow69it.listeningworkout.util.Util;
import net.snow69it.listeningworkout.util.Versatile;

public class SettingsFragment extends PreferenceFragmentCompat {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

//            } else if (preference instanceof RingtonePreference) {
//                // For ringtone preferences, look up the correct display value
//                // using RingtoneManager.
//                if (TextUtils.isEmpty(stringValue)) {
//                    // Empty values correspond to 'silent' (no ringtone).
//                    preference.setSummary(R.string.pref_ringtone_silent);
//
//                } else {
//                    Ringtone ringtone = RingtoneManager.getRingtone(
//                            preference.getContext(), Uri.parse(stringValue));
//
//                    if (ringtone == null) {
//                        // Clear the summary if there was a lookup error.
//                        preference.setSummary(null);
//                    } else {
//                        // Set the summary to reflect the new ringtone display
//                        // name.
//                        String name = ringtone.getTitle(preference.getContext());
//                        preference.setSummary(name);
//                    }
//                }
//
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.

        // 読み上げスピードとギャップ
//            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_general_speech_speed_key)));
//            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_shadowing_gap_key)));

        // バグレポート
        findPreference("bugReport").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                String subject = getString(
                        R.string.settings_bug_report_email_subject,
                        getString(R.string.app_name),
                        Versatile.getVersionName(getActivity()));
                String body = getString(
                        R.string.settings_bug_report_email_body,
                        Versatile.getSDKVersion(),
                        Versatile.getDeviceName());
                Navigation.goMail(
                        getActivity(),
                        getString(R.string.settings_email),
                        subject,
                        body);
                return true;
            }
        });

        // ご意見
        findPreference("opinion").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                String subject = getString(
                        R.string.settings_opinion_email_subject,
                        getString(R.string.app_name),
                        Versatile.getVersionName(getActivity()));
                String body = "";
                Navigation.goMail(
                        getActivity(),
                        getString(R.string.settings_email),
                        subject,
                        body);
                return true;
            }
        });

//        // 評価
//        findPreference("rating").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference preference) {
//
//                return true;
//            }
//        });

        // ライセンス
        findPreference("license").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                WebViewActivity.move(getActivity(),
                        Util.getSdPath(getActivity(), "/html/licences.html"),
                        getString(R.string.tmpl_license));
                return true;
            }
        });

        // バージョン
        Preference version = findPreference("version");
        version.setSummary(Versatile.getVersionName(getActivity()));
        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                mLogoutClickCount += 1;
                if (mLogoutClickCount == 10) {
                    FirebaseAuth.getInstance().signOut();
                    getActivity().finish();
                    getActivity().startActivity(new Intent(getActivity(), SplashActivity.class));
                }
                return true;
            }
        });
    }

    private int mLogoutClickCount = 0;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
