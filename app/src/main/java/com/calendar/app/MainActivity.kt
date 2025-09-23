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
import java.util.*

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
        
        // Setup today button
        setupTodayButton()
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
                    showMenuButton()
                    binding.btnToday.visibility = android.view.View.VISIBLE
                }
                R.id.dayTypeFormFragment -> {
                    supportActionBar?.title = "Type de journée"
                    binding.toolbar.title = "Type de journée"
                    hideMenuButton()
                    binding.btnToday.visibility = android.view.View.GONE
                }
                R.id.addEventFragment -> {
                    supportActionBar?.title = "Nouvel événement"
                    binding.toolbar.title = "Nouvel événement"
                    hideMenuButton()
                    binding.btnToday.visibility = android.view.View.GONE
                }
                else -> {
                    supportActionBar?.title = ""
                    binding.toolbar.title = ""
                    showMenuButton()
                    binding.btnToday.visibility = android.view.View.VISIBLE
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
    
    private fun setupTodayButton() {
        // Afficher le numéro du jour actuel
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        binding.btnToday.text = today.toString()
        
        // Action pour retourner au jour actuel
        binding.btnToday.setOnClickListener {
            navigateToToday()
        }
    }
    
    private fun navigateToToday() {
        // Obtenir une référence au CalendarFragment et naviguer vers aujourd'hui
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull()
        
        if (currentFragment is com.calendar.app.ui.calendar.CalendarFragment) {
            // Si on est déjà sur le CalendarFragment, utiliser son ViewModel pour naviguer vers aujourd'hui
            currentFragment.navigateToToday()
        } else {
            // Sinon, naviguer vers le CalendarFragment (qui affichera le mois actuel par défaut)
            navHostFragment?.navController?.navigate(R.id.calendarFragment)
        }
    }
    
    // Méthodes pour contrôler la visibilité du menu hamburger
    fun hideMenuButton() {
        binding.btnMenu.visibility = android.view.View.GONE
    }
    
    fun showMenuButton() {
        binding.btnMenu.visibility = android.view.View.VISIBLE
    }
}