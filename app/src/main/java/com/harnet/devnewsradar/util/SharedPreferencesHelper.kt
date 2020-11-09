package com.harnet.devnewsradar.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class SharedPreferencesHelper {
    companion object {
        private const val UPD_TIME = "Update time"
        private const val IS_ABOUT_SHOWED = "Is about showed"
        private var prefs: SharedPreferences? = null

        @Volatile
        private var instance: SharedPreferencesHelper? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): SharedPreferencesHelper = instance ?: synchronized(
            LOCK
        ) {
            instance ?: buildHelper(context).also {
                instance = it
            }
        }

        private fun buildHelper(context: Context): SharedPreferencesHelper {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)

            return SharedPreferencesHelper()
        }
    }

    // save current time to SharedPreferences
    fun saveTimeOfUpd(time: Long) {
        /** requires to add to the app gradle file
         * kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
         */
        prefs?.edit(commit = true) {
            putLong(UPD_TIME, time)
        }
    }

    //getLastUpdateTime
    fun getLastUpdateTime(): Long? {
        return prefs?.getLong(UPD_TIME, 0)
    }

    // set is About app was showed
    fun setIsAboutShowed(isShowed: Boolean){
        prefs?.edit(commit = true) {
            putBoolean(IS_ABOUT_SHOWED, isShowed)
        }
    }

    // get is About app was showed
    fun getIsAboutShowed() = prefs?.getBoolean(IS_ABOUT_SHOWED, false)

    // get a value of paid_version settings
    fun getIsPaidVersion() = prefs?.getString("paid_version", "")

    // get a value of api_parse_delaying settings
    fun getApiParseDelaying() = prefs?.getString("api_parse_delaying", "")

    // get a value of articles quantity in ArticleList settings
    fun getArticleQtt() = prefs?.getString("articles_qtt", "")

    // get a value of quantity day of history storing settings
    fun getHistoryKeepingDays() = prefs?.getString("storing_history_days", "")

    // get is new articles notification On/Off
    fun getIsNewArticleNotification() = prefs?.getBoolean("new_art_notification_preference", true)

    // get is article preview image parsing On/Off
    fun getIsPreviewImageParsing() = prefs?.getBoolean("article_preview_image", true)

    // get background color
    fun getBackgroundColor() = prefs?.getString("screen_background", "1")

    // get is SMS sending On/Off
    fun getIsSmsSendingAllowed() = prefs?.getBoolean("sms_sending", false)
}