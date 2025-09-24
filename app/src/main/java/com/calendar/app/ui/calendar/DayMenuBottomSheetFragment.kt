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
import com.calendar.app.data.model.Event
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

    override fun onResume() {
        super.onResume()
        // Recharger les données quand on revient à cette vue
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
        appointmentsAdapter = AppointmentsAdapter(
            onItemClick = { appointment ->
                // Action pour voir les détails d'un rendez-vous
                // TODO: Implémenter l'affichage des détails
            },
            onEditClick = { appointment ->
                // Action pour éditer un rendez-vous
                editAppointment(appointment)
            }
        )
        
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
            // Naviguer directement vers le formulaire de création de rendez-vous
            navigateToAddAppointment()
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

    private fun navigateToAddAppointment() {
        // Ouvrir le bottom sheet pour créer un rendez-vous
        val addEventBottomSheet = AddEventBottomSheetFragment.newInstance(args.selectedDate)
        addEventBottomSheet.show(parentFragmentManager, "AddEventBottomSheet")
        
        // Attendre un peu avant de fermer pour éviter les conflits
        view?.postDelayed({
            dismiss()
        }, 100)
    }

    private fun editAppointment(appointment: Event) {
        // Ouvrir le bottom sheet d'édition avec les données de l'événement
        val editEventBottomSheet = AddEventBottomSheetFragment.newInstance(args.selectedDate, appointment)
        editEventBottomSheet.show(parentFragmentManager, "EditEventBottomSheet")
        
        // Attendre un peu avant de fermer pour éviter les conflits
        view?.postDelayed({
            dismiss()
        }, 100)
    }

    private fun navigateToAddEvent(eventType: String) {
        // Cette méthode est obsolète - ne pas naviguer vers l'ancien AddEventFragment
        // qui créait des EventTypes parasites
        Log.d("DayMenuBottomSheet", "navigateToAddEvent called but disabled to prevent parasite EventTypes")
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
                
                // Chercher un événement de type journée (startTime = null ET eventTypeId != null)
                // Les rendez-vous sans heure ont eventTypeId = null, donc on les exclut
                val currentDayTypeEvent = existingEvents.find { 
                    it.event.startTime == null && it.event.eventTypeId != null
                }
                
                if (currentDayTypeEvent != null) {
                    // Il y a déjà un type de journée, afficher son nom dans le titre
                    val eventTypeName = currentDayTypeEvent.eventType?.name ?: "Type de journée"
                    tvDayTypeTitle.text = eventTypeName
                    Log.d("DayMenuBottomSheet", "Current day type found: $eventTypeName")
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
                
                // Normaliser à 00:00:00 pour la comparaison - EXACTEMENT comme dans AddEventBottomSheetFragment
                val calendar = Calendar.getInstance()
                calendar.time = date ?: Date()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val dateTimestamp = calendar.timeInMillis
                
                Log.d("DayMenuBottomSheet", "Searching for date: ${args.selectedDate}")
                Log.d("DayMenuBottomSheet", "Normalized timestamp: $dateTimestamp")
                Log.d("DayMenuBottomSheet", "Calendar date: ${calendar.time}")
                
                // Charger tous les événements pour cette date (rendez-vous = événements sans eventTypeId)
                val events = repository.getEventsByDate(dateTimestamp).first()
                val appointments = events.filter { it.eventTypeId == null } // Les rendez-vous n'ont pas de type
                
                Log.d("DayMenuBottomSheet", "Found ${events.size} total events for date $dateTimestamp")
                Log.d("DayMenuBottomSheet", "Found ${appointments.size} appointments (no eventTypeId)")
                
                // Debug: Show all events regardless of eventTypeId
                events.forEachIndexed { index, event ->
                    Log.d("DayMenuBottomSheet", "Event $index: '${event.title}' eventTypeId=${event.eventTypeId} date=${event.date} startTime=${event.startTime}")
                }
                
                appointmentsAdapter.submitList(appointments)
            } catch (e: Exception) {
                Log.e("DayMenuBottomSheet", "Error loading data: ${e.message}", e)
                // En cas d'erreur, afficher une liste vide
                appointmentsAdapter.submitList(emptyList())
            }
        }
    }
}