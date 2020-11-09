package com.harnet.devnewsradar.service.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.harnet.devnewsradar.view.ArticleFragment
import com.harnet.devnewsradar.view.FavouriteFragment
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class PermissionService(private val activity: Activity, val fragment: Fragment) {
    protected open val permissionCode: Int = 0
    protected open val permissionType = ""

    abstract fun checkPermission()

    protected fun requestPermission() {
        //!!! IT CRUCIAL TO CALL IN ON ACTIVITY, NOT ON FRAGMENT!!!
        activity.requestPermissions(
            arrayOf(permissionType),
            permissionCode
        )
    }

    // when user react to a permission
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyFragment(fragment, true)
                    // show Toast of a permission granted
                    showPermissionToast(activity.applicationContext, permissions[0],true)
                } else {
                    notifyFragment(fragment, false)
                    // show Toast of a permission refused
                    showPermissionToast(activity.applicationContext, permissions[0],false)
                }
            }

        }
    }

    // here you notify Fragment about permission giving
    protected fun notifyFragment(
        fragment: Fragment,
        permissionGranted: Boolean
    ) {
        // for precaution if user click to "Send SMS" and just after it a back button - can be a crash
        when (val activeFragment: Fragment? =
            fragment.childFragmentManager.primaryNavigationFragment) {
            is ArticleFragment -> {
                (activeFragment as ArticleFragment).onPermissionsResult(
                    permissionGranted
                )
            }
            is FavouriteFragment -> {
                (activeFragment as FavouriteFragment).onPermissionsResult(
                    permissionGranted
                )
            }
        }
    }

    //trim permission name by regex and return a permission message
    private fun showPermissionToast(context: Context, inputStr: String, permissionGranted: Boolean) {
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