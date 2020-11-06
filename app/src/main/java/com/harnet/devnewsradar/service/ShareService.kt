package com.harnet.devnewsradar.service

import android.app.Activity
import android.content.Intent

class ShareService{

    fun shareUrlWith(activity: Activity, url: String){
        // ACTION_SEND generic flag for sending
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Interesting article")
        intent.putExtra(Intent.EXTRA_TEXT, url)
        intent.putExtra(Intent.EXTRA_STREAM,  url)
        // give user the possibility to chose the application for getting this data
        activity.startActivity(Intent.createChooser(intent, "Share with:"))
    }
}