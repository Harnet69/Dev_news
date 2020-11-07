package com.harnet.devnewsradar.service

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.harnet.devnewsradar.view.ArticleFragment
import com.harnet.devnewsradar.view.FavouriteFragment

class PermissionService(val fragment: Fragment) {
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
                        notifyFragment(fragment, false)
                    }
                    .show()
            } else {
                // if we shouldn't explain about permission asking
                requestSmsPermission()
            }
        } else {
            // notify a fragment a permission was granted
            notifyFragment(fragment, true)
        }

    }

    private fun requestSmsPermission() {
        fragment.requestPermissions(
            arrayOf(smsPermission),
            PERMISSION_SEND_SMS
        )
    }

    // when user accept a permission
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            PERMISSION_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyFragment(fragment, true)
                } else {
                    notifyFragment(fragment, false)
                }
            }

        }
    }

    private fun notifyFragment(fragment: Fragment, permissionGranted: Boolean) {
        // for precaution if user click to "Send SMS" and just after it a back button - can be a crash
        when (val activeFragment: Fragment? = fragment.childFragmentManager.primaryNavigationFragment) {
            is ArticleFragment -> {
                (activeFragment as ArticleFragment).onPermissionsResult(permissionGranted)
            }
            is FavouriteFragment -> {
                (activeFragment as FavouriteFragment).onPermissionsResult(permissionGranted)
            }
        }
    }
}