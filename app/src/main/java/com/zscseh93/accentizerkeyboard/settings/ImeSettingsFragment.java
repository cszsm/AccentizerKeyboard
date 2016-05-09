package com.zscseh93.accentizerkeyboard.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.zscseh93.accentizerkeyboard.R;

/**
 * Created by zscse on 2016. 05. 09..
 */
public class ImeSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ime_preferences);
    }
}
