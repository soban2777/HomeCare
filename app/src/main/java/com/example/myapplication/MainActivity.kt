package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

      private lateinit var navController: NavController
   //   private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
     //  setSupportActionBar(findViewById(R.id.toolbar))

        val navHomeFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
          navController = navHomeFragment.navController

       //for back button on toolbar
     //   appBarConfiguration = AppBarConfiguration(
      //      setOf(R.id.homeFragment, R.id.ordersFragment, R.id.settingsFragment, R.id.chooseFragment, R.id.splashFragment), findViewById(R.id.drawer_layout)
      //  )

              //   setupActionBarWithNavController(navController)



//        val nav_drawer = findViewById<NavigationView>(R.id.nav_drawer)
//        nav_drawer.setupWithNavController(navController)
    }

      override fun onSupportNavigateUp(): Boolean {
      return navController.navigateUp() || super.onSupportNavigateUp()
    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        var itemId = item.itemId
//        if (itemId==R.id.chooseFragment){
//            navController.navigate(R.id.chooseFragment)
//        }
//        else{
//            return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
//
//        }
//        return  super.onOptionsItemSelected(item)
//    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}