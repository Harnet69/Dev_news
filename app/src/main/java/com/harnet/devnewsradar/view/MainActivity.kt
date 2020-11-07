package com.harnet.devnewsradar.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.service.PermissionService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // permission service
    lateinit var permissionService: PermissionService

    // back arrow
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //for back arrow
        navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        permissionService = PermissionService(fragment)
    }

    //for back arrow
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    // when user accept a permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionService.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO specify what permission was granted
        Toast.makeText(this, "Permission was granted", Toast.LENGTH_SHORT).show()
    }
}