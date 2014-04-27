package com.example.app;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;




public class SettingsFragment extends PreferenceFragment implements
                OnSharedPreferenceChangeListener{
    private SharedPreferences sharedPrefs;
    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }


    @Override
    public void onStart() {
        super.onStart();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("PreferenceChangedTag","Prefs changed for key: " + key);
        for(String pairKey:sharedPreferences.getAll().keySet())
            Log.d(TAG, "Current prefs:" + pairKey);
    }
}
