package com.harnet.devnews.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.harnet.devnews.R

class MainActivity : AppCompatActivity() {
    // create application menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // interacting with menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        var isSelected = false
        when (item.itemId) {
            R.id.favourites -> {
                isSelected = true
                //TODO here start a new fragment from menu
                Log.i("FavouritesArt", "onOptionsItemSelected: FAVOURITES")
                Toast.makeText(this, "Favourites", Toast.LENGTH_SHORT).show()
//                startFragment(ProfileFragment(), Fragments.PROFILE.toString())
            }
            R.id.newsRadar -> {
                isSelected = true
                //TODO here start a new fragment from menu
                Log.i("FavouritesArt", "onOptionsItemSelected: NEWS RADAR")
                Toast.makeText(this, "News radar", Toast.LENGTH_SHORT).show()
//                startFragment(ProfileFragment(), Fragments.PROFILE.toString())
            }
        }
        return isSelected
    }

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
}