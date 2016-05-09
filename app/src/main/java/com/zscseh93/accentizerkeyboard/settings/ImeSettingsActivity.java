package com.zscseh93.accentizerkeyboard.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zscseh93.accentizerkeyboard.R;

public class ImeSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ime_settings);

        ImeSettingsFragment settingsFragment = new ImeSettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragmentSettings, settingsFragment)
                .commit();
    }
}
