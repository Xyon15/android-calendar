package com.calendar.app.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventType
import com.calendar.app.data.repository.CalendarRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DayTypeBottomSheetFragment : BottomSheetDialogFragment() {

    private var selectedDate: String = ""
    private lateinit var repository: CalendarRepository
    private lateinit var userDayTypesAdapter: UserDayTypesAdapter
    
    private lateinit var rvUserDayTypes: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var btnBack: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedDate = arguments?.getString("selectedDate") ?: ""
        Log.d("DayTypeBottomSheet", "DayTypeBottomSheetFragment created with selectedDate: $selectedDate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_day_types, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DayTypeBottomSheet", "onViewCreated called")
        
        val database = CalendarDatabase.getDatabase(requireContext())
        repository = CalendarRepository(database.eventDao(), database.eventTypeDao())
        
        try {
            initializeViews(view)
            setupRecyclerView()
            setupClickListeners()
            loadUserDayTypes()
            Log.d("DayTypeBottomSheet", "Setup completed successfully")
        } catch (e: Exception) {
            Log.e("DayTypeBottomSheet", "Error in onViewCreated", e)
        }
    }

    private fun initializeViews(view: View) {
        Log.d("DayTypeBottomSheet", "Initializing views")
        rvUserDayTypes = view.findViewById(R.id.rvUserDayTypes)
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage)
        btnBack = view.findViewById(R.id.btnBack)
        
        Log.d("DayTypeBottomSheet", "Views found - RecyclerView: ${rvUserDayTypes != null}, EmptyMessage: ${tvEmptyMessage != null}, BackButton: ${btnBack != null}")
    }

    override fun onStart() {
        super.onStart()
        Log.d("DayTypeBottomSheet", "onStart called")
        
        // Forcer l'affichage et l'état expandé du BottomSheet
        val dialog = dialog as? com.google.android.material.bottomsheet.BottomSheetDialog
        dialog?.let { bottomSheetDialog ->
            // S'assurer que le dialog est visible
            bottomSheetDialog.window?.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
            
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(sheet)
                behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isHideable = false
                Log.d("DayTypeBottomSheet", "BottomSheet state set to EXPANDED with enhanced settings")
            }
        }
    }

    private fun setupRecyclerView() {
        userDayTypesAdapter = UserDayTypesAdapter { dayType ->
            createDayTypeEvent(dayType)
        }
        
        rvUserDayTypes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userDayTypesAdapter
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            dismiss()
        }
    }

    private fun loadUserDayTypes() {
        Log.d("DayTypeBottomSheet", "loadUserDayTypes called")
        lifecycleScope.launch {
            try {
                val dayTypes = repository.getAllEventTypes().first()
                Log.d("DayTypeBottomSheet", "Loaded ${dayTypes.size} day types")
                
                if (dayTypes.isEmpty()) {
                    Log.d("DayTypeBottomSheet", "No day types found, showing empty message")
                    tvEmptyMessage.visibility = View.VISIBLE
                    rvUserDayTypes.visibility = View.GONE
                } else {
                    Log.d("DayTypeBottomSheet", "Showing ${dayTypes.size} day types in RecyclerView")
                    tvEmptyMessage.visibility = View.GONE
                    rvUserDayTypes.visibility = View.VISIBLE
                    userDayTypesAdapter.submitList(dayTypes)
                }
            } catch (e: Exception) {
                Log.e("DayTypeBottomSheet", "Error loading day types", e)
                tvEmptyMessage.visibility = View.VISIBLE
                rvUserDayTypes.visibility = View.GONE
            }
        }
    }

    private fun navigateToAddEvent(dayTypeName: String) {
        // Navigation simple vers AddEventFragment
        val navController = findNavController()
        val bundle = Bundle().apply {
            putString("selectedDate", selectedDate)
            putString("eventType", "day_type_$dayTypeName")
        }
        navController.navigate(R.id.addEventFragment, bundle)
        dismiss()
    }
    
    private fun createDayTypeEvent(dayType: EventType) {
        lifecycleScope.launch {
            try {
                Log.d("DayTypeBottomSheet", "Selected date received: $selectedDate")
                
                // Convertir la date string en timestamp
                val dateTimestamp = convertDateStringToTimestamp(selectedDate)
                Log.d("DayTypeBottomSheet", "Converted to timestamp: $dateTimestamp")
                
                // Vérifier s'il y a déjà un événement de type journée pour cette date
                val existingEvents = repository.getEventsWithTypeByDate(dateTimestamp).first()
                val existingDayTypeEvent = existingEvents.find { 
                    it.event.eventTypeId == dayType.id && it.event.startTime == null 
                }
                
                if (existingDayTypeEvent != null) {
                    // Supprimer l'événement existant si c'est le même type
                    repository.deleteEvent(existingDayTypeEvent.event)
                    Log.d("DayTypeBottomSheet", "Removed existing day type event")
                } else {
                    // Supprimer tout autre événement de type journée pour cette date
                    existingEvents.forEach { eventWithType ->
                        if (eventWithType.event.startTime == null) { // C'est un type de journée
                            repository.deleteEvent(eventWithType.event)
                        }
                    }
                    
                    // Créer le nouvel événement de type journée
                    val newEvent = Event(
                        title = dayType.name,
                        description = "Type de journée: ${dayType.name}",
                        date = dateTimestamp,
                        eventTypeId = dayType.id,
                        startTime = null, // Pas d'heure = type de journée
                        endTime = null
                    )
                    
                    repository.insertEvent(newEvent)
                    Log.d("DayTypeBottomSheet", "Created new day type event for timestamp: $dateTimestamp")
                    
                    Toast.makeText(
                        requireContext(), 
                        "Type de journée '${dayType.name}' appliqué", 
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                dismiss()
            } catch (e: Exception) {
                Log.e("DayTypeBottomSheet", "Error applying day type", e)
                Toast.makeText(
                    requireContext(), 
                    "Erreur lors de l'application du type", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun convertDateStringToTimestamp(dateString: String): Long {
        return try {
            Log.d("DayTypeBottomSheet", "Converting date string: $dateString")
            
            // Essayer le format dd/MM/yyyy d'abord
            if (dateString.contains("/")) {
                val parts = dateString.split("/")
                if (parts.size == 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt() - 1 // Calendar uses 0-based months
                    val year = parts[2].toInt()
                    
                    val calendar = java.util.Calendar.getInstance()
                    calendar.set(year, month, day, 0, 0, 0)
                    calendar.set(java.util.Calendar.MILLISECOND, 0)
                    Log.d("DayTypeBottomSheet", "Converted dd/MM/yyyy to timestamp: ${calendar.timeInMillis}")
                    return calendar.timeInMillis
                }
            }
            
            // Sinon essayer le format yyyy-MM-dd
            if (dateString.contains("-")) {
                val parts = dateString.split("-")
                if (parts.size == 3) {
                    val year = parts[0].toInt()
                    val month = parts[1].toInt() - 1 // Calendar uses 0-based months
                    val day = parts[2].toInt()
                    
                    val calendar = java.util.Calendar.getInstance()
                    calendar.set(year, month, day, 0, 0, 0)
                    calendar.set(java.util.Calendar.MILLISECOND, 0)
                    Log.d("DayTypeBottomSheet", "Converted yyyy-MM-dd to timestamp: ${calendar.timeInMillis}")
                    return calendar.timeInMillis
                }
            }
            
            Log.e("DayTypeBottomSheet", "Unknown date format: $dateString, falling back to current time")
            System.currentTimeMillis() // Fallback to current time
        } catch (e: Exception) {
            Log.e("DayTypeBottomSheet", "Error parsing date: $dateString", e)
            System.currentTimeMillis() // Fallback to current time
        }
    }
}