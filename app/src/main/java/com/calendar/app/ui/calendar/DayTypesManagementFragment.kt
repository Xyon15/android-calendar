package com.calendar.app.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.MainActivity
import com.calendar.app.R
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.model.EventType
import com.calendar.app.data.repository.CalendarRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DayTypesManagementFragment : Fragment() {

    private lateinit var repository: CalendarRepository
    private lateinit var dayTypesAdapter: DayTypesAdapter
    
    private lateinit var rvDayTypes: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_day_types_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Masquer le menu hamburger sur cette page
        (activity as? MainActivity)?.hideMenuButton()
        
        val database = CalendarDatabase.getDatabase(requireContext())
        repository = CalendarRepository(database.eventDao(), database.eventTypeDao())
        
        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadDayTypes()
    }
    
    override fun onResume() {
        super.onResume()
        // Recharger les données quand on revient au fragment
        loadDayTypes()
    }

    private fun initializeViews(view: View) {
        rvDayTypes = view.findViewById(R.id.rvDayTypes)
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage)
        fabAdd = view.findViewById(R.id.fabAdd)
    }

    private fun setupRecyclerView() {
        dayTypesAdapter = DayTypesAdapter(
            onEditClick = { dayType ->
                // Naviguer vers l'écran d'édition
                val bundle = Bundle().apply {
                    putLong("dayTypeId", dayType.id)
                }
                findNavController().navigate(R.id.dayTypeFormFragment, bundle)
            },
            onDeleteClick = { dayType ->
                // Supprimer le type de journée
                deleteDayType(dayType.id)
            }
        )
        
        rvDayTypes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dayTypesAdapter
        }
    }

    private fun setupClickListeners() {
        fabAdd.setOnClickListener {
            // Naviguer vers l'écran de création
            findNavController().navigate(R.id.dayTypeFormFragment)
        }
    }

    private fun loadDayTypes() {
        lifecycleScope.launch {
            try {
                val dayTypes = repository.getAllEventTypes().first()
                Log.d("DayTypesManagement", "Types de journées chargés: ${dayTypes.size} éléments")
                dayTypes.forEach { 
                    Log.d("DayTypesManagement", "- ${it.name} (ID: ${it.id})") 
                }
                
                if (dayTypes.isEmpty()) {
                    tvEmptyMessage.visibility = View.VISIBLE
                    rvDayTypes.visibility = View.GONE
                } else {
                    tvEmptyMessage.visibility = View.GONE
                    rvDayTypes.visibility = View.VISIBLE
                    dayTypesAdapter.submitList(dayTypes.toList()) // Force une nouvelle liste
                }
            } catch (e: Exception) {
                Log.e("DayTypesManagement", "Erreur lors du chargement", e)
                tvEmptyMessage.visibility = View.VISIBLE
                rvDayTypes.visibility = View.GONE
            }
        }
    }

    private fun deleteDayType(dayTypeId: Long) {
        lifecycleScope.launch {
            try {
                val dayTypes = repository.getAllEventTypes().first()
                val dayTypeToDelete = dayTypes.find { it.id == dayTypeId }
                dayTypeToDelete?.let {
                    repository.deleteEventType(it)
                    loadDayTypes() // Recharger la liste
                }
            } catch (e: Exception) {
                // Gérer l'erreur
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        
        // Réafficher le menu hamburger quand on quitte cette page
        (activity as? MainActivity)?.showMenuButton()
    }
}