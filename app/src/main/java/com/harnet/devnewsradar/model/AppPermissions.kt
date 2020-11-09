package com.harnet.devnewsradar.model

import android.app.Activity
import androidx.fragment.app.Fragment
import com.harnet.devnewsradar.service.permissions.SmsPermissionService

data class AppPermissions(val activity: Activity,val fragment: Fragment){
    val smsPermissionService: SmsPermissionService = SmsPermissionService(activity, fragment)
}