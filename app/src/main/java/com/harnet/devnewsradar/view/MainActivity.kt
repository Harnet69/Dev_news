package com.harnet.devnewsradar.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.service.permissions.SmsPermissionService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // permission service
    lateinit var smsPermissionService: SmsPermissionService

    // back arrow
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //for back arrow
        navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        smsPermissionService = SmsPermissionService(this,  fragment)
    }

    //for back arrow
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    // when user accept a permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        smsPermissionService.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}