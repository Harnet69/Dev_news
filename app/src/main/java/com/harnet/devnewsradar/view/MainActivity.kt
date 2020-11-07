package com.harnet.devnewsradar.view

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.util.PERMISSION_SEND_SMS
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // back arrow
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //for back arrow
        navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    //for back arrow
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    // check SMS permission
    fun checkSmsPermission() {
        // check if we have granted permission already
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //check if we should explain to user why we ask for permission(for the first time we haven't)
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.SEND_SMS
                )
            ) {
                // explanation the cause of request a permission
                AlertDialog.Builder(this)
                    .setTitle("SMS sending permission")
                    .setMessage("The app requires access to SMS sending")
                    .setPositiveButton("Ask me") { dialog, which ->
                        requestSmsPermission()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        //permission haven't been received
                        notifyArticleFragment(false)
                    }
            } else {
                // if we shouldn't explain about permission asking
                requestSmsPermission()
            }
        } else {
            // notify ArticleFragment a permission was granted
            notifyArticleFragment(true)
        }

    }

    private fun requestSmsPermission() {
//        requestPermissions(arrayOf(Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SEND_SMS),
            PERMISSION_SEND_SMS)
    }

    // when user accept a permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSION_SEND_SMS ->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    notifyArticleFragment(true)
                }else{
                    notifyArticleFragment(false)
                }
            }

        }
    }

    private fun notifyArticleFragment(permissionGranted: Boolean){
        val activeFragment: Fragment? = fragment.childFragmentManager.primaryNavigationFragment
        // for precaution if user click to "Send SMS" and just after it a back button - can be a crash
        if(activeFragment is ArticleFragment){
            (activeFragment as ArticleFragment).onPermissionsResult(permissionGranted)
        }
    }
}