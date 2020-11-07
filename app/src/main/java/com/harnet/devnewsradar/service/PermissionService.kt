package com.harnet.devnewsradar.service

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import com.harnet.devnewsradar.view.ArticleFragment

class PermissionService(val fragment: Fragment) {
    private val PERMISSION_SEND_SMS = 123

    // check SMS permission
    fun checkSmsPermission() {
        val smsPermission = Manifest.permission.SEND_SMS
        // check if we have granted permission already
        if(fragment.context?.checkSelfPermission(smsPermission) != PackageManager.PERMISSION_GRANTED){
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
                        notifyArticleFragment(fragment, false)
                    }
                    .show()
            } else {
                // if we shouldn't explain about permission asking
                requestSmsPermission()
            }
        } else {
            // notify ArticleFragment a permission was granted
            notifyArticleFragment(fragment, true)
        }

    }

    private fun requestSmsPermission() {
        fragment.requestPermissions(
            arrayOf(Manifest.permission.SEND_SMS),
            PERMISSION_SEND_SMS
        )
    }

    // when user accept a permission
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            PERMISSION_SEND_SMS ->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    notifyArticleFragment(fragment, true)
                }else{
                    notifyArticleFragment(fragment, false)
                }
            }

        }
    }

    private fun notifyArticleFragment(fragment: Fragment, permissionGranted: Boolean){
        val activeFragment: Fragment? = fragment.childFragmentManager.primaryNavigationFragment
        // for precaution if user click to "Send SMS" and just after it a back button - can be a crash
        //TODO here implement the permission functionality for other fragments
        if(activeFragment is ArticleFragment){
            (activeFragment as ArticleFragment).onPermissionsResult(permissionGranted)
        }
    }
}