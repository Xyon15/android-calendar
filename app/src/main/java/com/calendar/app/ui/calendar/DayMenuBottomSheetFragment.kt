package com.calendar.app.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.MainActivity
import com.calendar.app.R
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.repository.CalendarRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DayMenuBottomSheetFragment : BottomSheetDialogFragment() {

    private val args: DayMenuBottomSheetFragmentArgs by navArgs()
    private lateinit var repository: CalendarRepository
    private lateinit var appointmentsAdapter: AppointmentsAdapter
    
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvDayTypeTitle: TextView
    private lateinit var rvAppointments: RecyclerView
    private lateinit var layoutDayType: View
    private lateinit var layoutAddAppointment: View
    private lateinit var btnBack: View

    private val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.FRENCH)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(com.calendar.app.R.layout.bottom_sheet_day_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val database = CalendarDatabase.getDatabase(requireContext())
        repository = CalendarRepository(database.eventDao(), database.eventTypeDao())
        
        initializeViews(view)
        checkCurrentDayType()
        setupRecyclerView()
        setupClickListeners()
        loadData()
    }

    private fun initializeViews(view: View) {
        tvSelectedDate = view.findViewById(com.calendar.app.R.id.tvSelectedDate)
        tvDayTypeTitle = view.findViewById(com.calendar.app.R.id.tvDayTypeTitle)
        rvAppointments = view.findViewById(com.calendar.app.R.id.rvAppointments)
        layoutDayType = view.findViewById(com.calendar.app.R.id.layoutDayType)
        layoutAddAppointment = view.findViewById(com.calendar.app.R.id.layoutAddAppointment)
        btnBack = view.findViewById(com.calendar.app.R.id.btnBack)
        
        // Afficher la date sélectionnée
        try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
            val date = inputFormat.parse(args.selectedDate)
            val formattedDate = dateFormat.format(date ?: Date())
            // Capitaliser la première lettre du mois
            val parts = formattedDate.split(" ")
            if (parts.size >= 2) {
                val day = parts[0]
                val month = parts[1].replaceFirstChar { it.uppercase() }
                val year = if (parts.size > 2) parts[2] else ""
                tvSelectedDate.text = "$day $month $year".trim()
            } else {
                tvSelectedDate.text = formattedDate
            }
        } catch (e: Exception) {
            tvSelectedDate.text = args.selectedDate
        }
    }

    private fun setupRecyclerView() {
        appointmentsAdapter = AppointmentsAdapter { appointment ->
            // Action pour éditer un rendez-vous
            // TODO: Implémenter l'édition
        }
        
        rvAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appointmentsAdapter
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            dismiss()
        }
        
        layoutDayType.setOnClickListener {
            // Naviguer vers le sous-menu des types de journée
            navigateToDayTypes()
        }
        
        layoutAddAppointment.setOnClickListener {
            // Naviguer vers le sous-menu des types de rendez-vous
            navigateToAppointmentTypes()
        }
    }

    private fun navigateToDayTypes() {
        Log.d("DayMenuBottomSheet", "navigateToDayTypes called with selectedDate: ${args.selectedDate}")
        try {
            // Navigation directe vers DayTypeBottomSheetFragment avec Bundle
            val bundle = Bundle().apply {
                putString("selectedDate", args.selectedDate)
            }
            Log.d("DayMenuBottomSheet", "About to navigate to DayTypeBottomSheetFragment with bundle")
            findNavController().navigate(R.id.action_day_menu_to_day_types, bundle)
            // Ne pas appeler dismiss() - laisser la navigation gérer la fermeture
        } catch (e: Exception) {
            Log.e("DayMenuBottomSheet", "Navigation failed", e)
        }
    }
    
    private fun navigateToAppointmentTypes() {
        // Navigation vers AddEventFragment avec un type spécial pour identifier qu'on veut le sous-menu des rendez-vous
        val action = DayMenuBottomSheetFragmentDirections
            .actionDayMenuToAddEvent(args.selectedDate, "show_appointment_types_menu")
        findNavController().navigate(action)
        dismiss()
    }

    private fun navigateToAddEvent(eventType: String) {
        val action = DayMenuBottomSheetFragmentDirections
            .actionDayMenuToAddEvent(args.selectedDate, eventType)
        findNavController().navigate(action)
        dismiss()
    }

    private fun checkCurrentDayType() {
        lifecycleScope.launch {
            try {
                // Convertir la date pour chercher les événements
                val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = inputFormat.parse(args.selectedDate)
                val dateTimestamp = date?.time ?: 0L

                val existingEvents = repository.getEventsWithTypeByDate(dateTimestamp).first()
                
                // Chercher un événement de type journée (startTime = null)
                val currentDayTypeEvent = existingEvents.find { 
                    it.event.startTime == null 
                }
                
                if (currentDayTypeEvent != null) {
                    // Il y a déjà un type de journée, afficher son nom dans le titre
                    tvDayTypeTitle.text = currentDayTypeEvent.eventType.name
                    Log.d("DayMenuBottomSheet", "Current day type found: ${currentDayTypeEvent.eventType.name}")
                } else {
                    // Pas de type de journée, garder le titre par défaut
                    tvDayTypeTitle.text = "Définir le type de cette journée"
                    Log.d("DayMenuBottomSheet", "No current day type found")
                }
            } catch (e: Exception) {
                Log.e("DayMenuBottomSheet", "Error checking current day type", e)
                tvDayTypeTitle.text = "Définir le type de cette journée"
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                // Convertir la date pour chercher les événements
                val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = inputFormat.parse(args.selectedDate)
                val dateTimestamp = date?.time ?: System.currentTimeMillis()
                
                // Charger les rendez-vous pour cette date
                val events = repository.getEventsByDate(dateTimestamp).first()
                val appointments = events.filter { it.startTime != null } // Seulement les rendez-vous avec heure
                
                appointmentsAdapter.submitList(appointments)
            } catch (e: Exception) {
                // En cas d'erreur, afficher une liste vide
                appointmentsAdapter.submitList(emptyList())
            }
        }
    }
}