package com.harnet.devnewsradar.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.model.AppPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // permission service
    lateinit var appPermissions: AppPermissions

    // back arrow
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //for back arrow
        navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        appPermissions = AppPermissions(this,  fragment)
    }

    //for back arrow
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    // when user was asked for a permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //here is the switcher of different kinds of permissions
        when(permissions[0]){
            android.Manifest.permission.SEND_SMS -> {
                appPermissions.smsPermissionService.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}