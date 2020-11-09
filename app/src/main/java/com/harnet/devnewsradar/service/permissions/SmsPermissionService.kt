package com.harnet.devnewsradar.service.permissions

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

class SmsPermissionService(activity: Activity, fragment: Fragment) : PermissionService(activity, fragment) {
    override val permissionCode: Int = 123
    override val permissionType = Manifest.permission.SEND_SMS

    override fun checkPermission() {
        // check if we have granted permission already
        if (fragment.context?.checkSelfPermission(permissionType) != PackageManager.PERMISSION_GRANTED) {
            //check if we should explain to user why we ask for permission(for the first time we haven't)
            if (fragment.shouldShowRequestPermissionRationale(permissionType)
            ) {
                // explanation the cause of request a permission
                AlertDialog.Builder(fragment.context)
                    .setTitle("SMS sending permission")
                    .setMessage("The app requires access to SMS sending")
                    .setPositiveButton("Ask me") { dialog, which ->
                        requestPermission()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        //permission haven't been received
                        notifyFragment(fragment, permissionType, false)
                    }
                    .show()
            } else {
                // if we shouldn't explain about permission asking
                super.requestPermission()
            }
        } else {
            // notify a fragment a permission was granted
            super.notifyFragment(fragment, permissionType, true)
        }
    }
}