package com.harnet.devnewsradar.service

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.harnet.devnewsradar.view.ArticleFragment
import com.harnet.devnewsradar.view.FavouriteFragment
import java.util.regex.Matcher
import java.util.regex.Pattern

class PermissionService(private val activity: Activity, val fragment: Fragment) {
    private val PERMISSION_SEND_SMS = 123
    private val smsPermission = Manifest.permission.SEND_SMS

    // check SMS permission
    fun checkSmsPermission() {
        // check if we have granted permission already
        if (fragment.context?.checkSelfPermission(smsPermission) != PackageManager.PERMISSION_GRANTED) {
            //check if we should explain to user why we ask for permission(for the first time we haven't)
            if (fragment.shouldShowRequestPermissionRationale(smsPermission)
            ) {
                // explanation the cause of request a permission
                AlertDialog.Builder(fragment.context)
                    .setTitle("SMS sending permission")
                    .setMessage("The app requires access to SMS sending")
                    .setPositiveButton("Ask me") { dialog, which ->
                        requestSmsPermission()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        //permission haven't been received
                        notifyFragment(fragment, smsPermission, false)
                    }
                    .show()
            } else {
                // if we shouldn't explain about permission asking
                requestSmsPermission()
            }
        } else {
            // notify a fragment a permission was granted
            notifyFragment(fragment, smsPermission, true)
        }

    }

    private fun requestSmsPermission() {
        //!!! IT CRUCIAL TO CALL IN ON ACTIVITY, NOT ON FRAGMENT!!!
        activity.requestPermissions(
            arrayOf(smsPermission),
            PERMISSION_SEND_SMS
        )
    }

    // when user react to a permission
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyFragment(fragment, permissions[0], true)
                    // show Toast of a permission granted
                    showPermissionToast(activity.applicationContext, permissions[0],true)
                } else {
                    notifyFragment(fragment, permissions[0], false)
                    // show Toast of a permission refused
                    showPermissionToast(activity.applicationContext, permissions[0],false)
                }
            }

        }
    }

    // here you notify Fragment about permission giving
    private fun notifyFragment(
        fragment: Fragment,
        permissionName: String,
        permissionGranted: Boolean
    ) {
        // for precaution if user click to "Send SMS" and just after it a back button - can be a crash
        when (val activeFragment: Fragment? =
            fragment.childFragmentManager.primaryNavigationFragment) {
            is ArticleFragment -> {
                (activeFragment as ArticleFragment).onPermissionsResult(
                    permissionGranted,
                    permissionName
                )
            }
            is FavouriteFragment -> {
                (activeFragment as FavouriteFragment).onPermissionsResult(
                    permissionGranted,
                    permissionName
                )
            }
        }
    }

    //trim permission name by regex and return a permission message
    fun showPermissionToast(context: Context, inputStr: String, permissionGranted: Boolean) {
        var toastMsg = ""
        var s: String? = ""
        val p: Pattern = Pattern.compile("android.permission.(.*)")
        val m: Matcher = p.matcher(inputStr)
        if (m.find()) {
            s = m.group(1)
        }

        if (permissionGranted) {
            toastMsg = "Permission $s was granted"
        } else {
            toastMsg = "Permission $s wasn't granted"
        }

        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
    }
}