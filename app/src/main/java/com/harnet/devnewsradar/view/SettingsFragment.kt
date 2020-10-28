package com.harnet.devnewsradar.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.harnet.devnewsradar.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // change page background
        listView.setBackgroundResource(R.drawable.bgnd)
    }
}