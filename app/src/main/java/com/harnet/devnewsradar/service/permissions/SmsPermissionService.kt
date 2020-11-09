package com.harnet.devnewsradar.service.permissions

import android.Manifest
import android.app.Activity
import androidx.fragment.app.Fragment

class SmsPermissionService(activity: Activity, fragment: Fragment) : PermissionService(activity, fragment) {
    override val permissionCode: Int = 123
    override val permissionType = Manifest.permission.SEND_SMS
    override val rationaleTitle = "SMS sending permission"
    override val rationaleMessage = "The app requires access to SMS sending"
}