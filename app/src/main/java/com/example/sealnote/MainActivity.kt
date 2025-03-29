package com.example.sealnote

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private var isStealthMode = true // Replace with your actual mode-switching logic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {
        // Set the appropriate menu based on the mode
        val menuRes = if (isStealthMode) R.menu.stealth_menu_drawer else R.menu.notes_menu_drawer
        navView.menu.clear() // Clear the existing menu
        navView.inflateMenu(menuRes)

        //  AppBarConfiguration needs to be recreated after menu change
        appBarConfiguration = AppBarConfiguration(
            getTopLevelDestinations(), // Use a helper function
            drawerLayout
        )

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)
    }

    private fun getTopLevelDestinations(): Set<Int> {
        // Return the IDs of the fragments where the drawer icon should appear.
        // This depends on your desired navigation flow.  For example, if you
        // want the drawer on all top-level screens in Note Mode, you'd return
        // the IDs of Homepage, Bookmarks, Profile, Trash, Settings, and SecretNotes.
        // In Stealth Mode, it might be just StealthCalculator.
        return if (isStealthMode) {
            setOf(R.id.stealthCalculatorFragment) // Adjust as needed
        } else {
            setOf(R.id.homepageFragment) // Adjust as needed
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    //  If you have a way to change modes at runtime (e.g., from a settings screen),
    //  you'll need a function like this to update the UI:
    fun setStealthMode(enabled: Boolean) {
        isStealthMode = enabled
        setupNavigationDrawer() // Reconfigure the drawer
        //  You might also want to navigate to the appropriate start destination
        //  based on the new mode.  For example:
        val startDestination = if (isStealthMode) R.id.stealthCalculatorFragment else R.id.homepageFragment
        navController.graph = navController.graph.apply {
            setStartDestination(startDestination)
        }
        navController.navigate(startDestination)
    }
}