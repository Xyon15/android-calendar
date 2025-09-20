package com.calendar.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.calendar.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
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
        
        // Setup share button
        binding.btnShare.setOnClickListener {
            // Implement share functionality
            shareSchedule()
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