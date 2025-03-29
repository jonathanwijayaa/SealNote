package com.example.sealnote

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
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
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Konfigurasi ActionBar & Drawer
        setupDrawer(toolbar)
        setupNavigationDrawer()
    }

    private fun setupDrawer(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun setupNavigationDrawer() {
        val menuRes = if (isStealthMode) R.menu.stealth_menu_drawer else R.menu.notes_menu_drawer
        navView.menu.clear()
        navView.inflateMenu(menuRes)

        // Update daftar fragment yang dianggap sebagai top-level
        appBarConfiguration = AppBarConfiguration(getTopLevelDestinations(), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers() // Tutup drawer setelah memilih item
            navController.navigate(menuItem.itemId)
            true
        }

        // Kunci atau buka drawer berdasarkan mode
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateDrawerState(destination.id in getTopLevelDestinations())
        }
    }

    private fun updateDrawerState(isNotesMode: Boolean) {
        if (isNotesMode) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
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
                R.id.bookmarksFragment,
                R.id.secretNotesFragment,
                R.id.trashFragment
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setStealthMode(enabled: Boolean) {
        isStealthMode = enabled
        setupNavigationDrawer() // Update menu drawer

        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.nav_graph)

        // Tetapkan startDestination sesuai mode
        val startDestination = if (isStealthMode) R.id.stealthCalculatorFragment else R.id.homepageFragment
        navGraph.setStartDestination(startDestination)

        navController.graph = navGraph
    }
}