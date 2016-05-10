package com.zscseh93.accentizerkeyboard.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zscseh93.accentizerkeyboard.R;
import com.zscseh93.accentizerkeyboard.dictionary.Suggestion;

public class ImeSettingsActivity extends AppCompatActivity {

    private static final String PREF_CLEAN = "pref_clean";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ime_settings);

        ImeSettingsFragment settingsFragment = new ImeSettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragmentSettings, settingsFragment)
                .commit();
    }
}
