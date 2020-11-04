package com.harnet.devnewsradar.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.util.SharedPreferencesHelper

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // change page background
        setSettingsBackground(listView)
//        listView.setBackgroundResource(R.drawable.bgnd)
    }

    private fun setSettingsBackground(view: RecyclerView){
        when(SharedPreferencesHelper.invoke(view.context).getBackgroundColor()){
            "1" -> {
                view.setBackgroundColor(Color.rgb(255, 255, 255))
            }
            "2" -> {
                view.setBackgroundResource(R.drawable.bgnd)
            }
        }
    }
}