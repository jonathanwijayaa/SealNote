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
import androidx.navigation.ui.setupActionBarWithNavController
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
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment // Replace with your NavHostFragment ID
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(getTopLevelDestinations(), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {
        val menuRes = if (isStealthMode) R.menu.stealth_menu_drawer else R.menu.notes_menu_drawer
        navView.menu.clear()
        navView.inflateMenu(menuRes)

        // Update daftar fragment yang dianggap sebagai top-level
        appBarConfiguration = AppBarConfiguration(getTopLevelDestinations(), drawerLayout)

        // Perbarui navigasi drawer
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers() // Tutup drawer setelah memilih item
            navController.navigate(menuItem.itemId)
            true
        }
    }

    private fun getTopLevelDestinations(): Set<Int> {
        return if (isStealthMode) {
            setOf(
                R.id.stealthCalculatorFragment,
                R.id.stealthHistoryFragment,
                R.id.stealthScientificFragment
            )
        } else {
            setOf(
                R.id.homepageFragment,
                R.id.profileFragment,
                R.id.bookmarksFragment,
                R.id.secretNotesFragment,
                R.id.trashFragment,
                R.id.settingsFragment
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    //  If you have a way to change modes at runtime (e.g., from a settings screen),
    //  you'll need a function like this to update the UI:
    fun setStealthMode(enabled: Boolean) {
        isStealthMode = enabled
        setupNavigationDrawer() // Update menu drawer

        // Buat ulang graf navigasi untuk memastikan navigasi berfungsi
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.nav_graph)

        // Tetapkan startDestination sesuai mode
        val startDestination = if (isStealthMode) R.id.stealthCalculatorFragment else R.id.homepageFragment
        navGraph.setStartDestination(startDestination)

        navController.graph = navGraph // Terapkan graf navigasi yang diperbarui
    }
}