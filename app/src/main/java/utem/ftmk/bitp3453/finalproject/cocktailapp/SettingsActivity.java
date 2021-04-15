package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity
        implements Preference.OnPreferenceChangeListener {

    public static final String PREFERENCE_NAME = "my_preference";
    public static final String KEY_THEME = "theme";
    public static final int MODE_DEFAULT = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment(this))
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        int theme = Integer.parseInt(newValue.toString());
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_THEME, theme);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(theme);
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private static Preference.OnPreferenceChangeListener listener;

        public SettingsFragment() {

        }

        public SettingsFragment(Preference.OnPreferenceChangeListener listener) {
            SettingsFragment.listener = listener;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ListPreference listPreference = findPreference(KEY_THEME);
            if (listPreference != null) {
                listPreference.setOnPreferenceChangeListener(listener);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}