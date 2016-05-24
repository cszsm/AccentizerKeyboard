package com.zscseh93.accentizerkeyboard.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.zscseh93.accentizerkeyboard.R;
import com.zscseh93.accentizerkeyboard.dictionary.Suggestion;

public class ImeSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ime_preferences);

        Preference clearPreference = findPreference("pref_clear");
        clearPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new AlertDialog.Builder(getActivity())
                        .setMessage("Are you sure to clear the dictionary?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Suggestion.deleteAll(Suggestion.class);
                                Toast.makeText(getActivity(), "Dictionary cleared", Toast
                                        .LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();

                return true;
            }
        });
    }
}
