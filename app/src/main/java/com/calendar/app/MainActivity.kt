package com.calendar.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.repository.CalendarRepository
import com.calendar.app.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        drawerLayout = binding.drawerLayout
        
        setSupportActionBar(binding.toolbar)
        
        // Setup navigation drawer
        setupNavigationDrawer()
        
        // Setup navigation with post to ensure fragment is ready
        binding.root.post {
            try {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                navHostFragment?.let { 
                    val navController = it.navController
                    setupActionBarWithNavController(navController)
                }
            } catch (e: Exception) {
                // Navigation will work without ActionBar setup
            }
        }
        
        // Setup menu button
        binding.btnMenu.setOnClickListener {
            drawerLayout.open()
        }
        
        // Setup share button
        binding.btnShare.setOnClickListener {
            // Implement share functionality
            shareSchedule()
        }
    }
    
    private fun setupNavigationDrawer() {
        val navigationView = binding.navView
        
        // Setup navigation destination listener pour mettre à jour le titre
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        navHostFragment?.navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dayTypesManagementFragment -> {
                    supportActionBar?.title = "Types de journées"
                    binding.toolbar.title = "Types de journées"
                }
                R.id.dayTypeFormFragment -> {
                    supportActionBar?.title = "Type de journée"
                    binding.toolbar.title = "Type de journée"
                }
                else -> {
                    supportActionBar?.title = ""
                    binding.toolbar.title = ""
                }
            }
        }
        
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_day_types -> {
                    // Naviguer vers la gestion des types de journées
                    navHostFragment?.navController?.navigate(R.id.dayTypesManagementFragment)
                    drawerLayout.close()
                    true
                }
                else -> false
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return try {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            navHostFragment?.navController?.navigateUp() ?: false
        } catch (e: Exception) {
            super.onSupportNavigateUp()
        }
    }
    
    
    private fun shareSchedule() {
        // Implementation for sharing schedule
        // This could export to calendar app or share as text/image
    }
}