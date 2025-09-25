package com.calendar.app.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.MainActivity
import com.calendar.app.R
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventType
import com.calendar.app.data.repository.CalendarRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MultiDaySelectionFragment : Fragment() {

    private val args: MultiDaySelectionFragmentArgs by navArgs()
    
    private lateinit var btnBack: ImageView
    private lateinit var multiSelectCalendar: RecyclerView
    private lateinit var bottomSection: LinearLayout
    private lateinit var dayTypesRecyclerView: RecyclerView
    private lateinit var btnApplyDayType: Button

    private lateinit var weekCalendarAdapter: MultiSelectWeekCalendarAdapter
    private lateinit var dayTypeAdapter: DayTypeSelectionAdapter
    private lateinit var repository: CalendarRepository
    
    private var loadDayTypesJob: Job? = null

    private val currentCalendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_multi_day_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRepository()
        setupRecyclerViews()
        setupClickListeners()
        setupDisplayMonth()
        loadCalendarDays()
        loadDayTypes()
        
        // Masquer le menu hamburger
        (activity as? MainActivity)?.hideMenuButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Annuler toutes les coroutines en cours pour éviter les fuites mémoire
        loadDayTypesJob?.cancel()
        loadDayTypesJob = null
        // Afficher le menu hamburger quand on quitte
        (activity as? MainActivity)?.showMenuButton()
    }

    private fun initializeViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        multiSelectCalendar = view.findViewById(R.id.multiSelectCalendar)
        bottomSection = view.findViewById(R.id.bottomSection)
        dayTypesRecyclerView = view.findViewById(R.id.dayTypesRecyclerView)
        btnApplyDayType = view.findViewById(R.id.btnApplyDayType)
    }

    private fun setupRepository() {
        val database = CalendarDatabase.getDatabase(requireContext())
        repository = CalendarRepository(database.eventDao(), database.eventTypeDao())
    }
    
    private fun setupDisplayMonth() {
        // Récupérer les arguments passés via le Bundle
        val year = arguments?.getInt("displayYear", 0) ?: 0
        val month = arguments?.getInt("displayMonth", 0) ?: 0
        
        // Utiliser le mois passé en paramètre, ou le mois actuel par défaut
        if (year > 0 && month > 0) {
            currentCalendar.set(Calendar.YEAR, year)
            currentCalendar.set(Calendar.MONTH, month - 1) // Calendar months are 0-based
            Log.d("MultiDaySelection", "Using specified month: $month/$year")
        } else {
            Log.d("MultiDaySelection", "Using current month")
        }
    }

    private fun setupRecyclerViews() {
        // Calendrier de sélection multiple par semaines
        weekCalendarAdapter = MultiSelectWeekCalendarAdapter(emptyList()) { day ->
            if (weekCalendarAdapter.getSelectedDays().isNotEmpty()) {
                bottomSection.visibility = View.VISIBLE
            } else {
                bottomSection.visibility = View.GONE
                dayTypeAdapter.clearSelection()
                updateApplyButton()
            }
        }
        
        multiSelectCalendar.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = weekCalendarAdapter
        }
        
        // Liste des types de jour
        dayTypeAdapter = DayTypeSelectionAdapter(emptyList()) { dayType ->
            updateApplyButton()
        }
        
        dayTypesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dayTypeAdapter
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }



        btnApplyDayType.setOnClickListener {
            applyDayTypeToSelectedDays()
        }
    }

    private fun loadCalendarDays() {
        val weeks = mutableListOf<CalendarWeek>()
        
        // Clone du calendrier pour manipulation
        val cal = currentCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        
        // Trouver le premier jour de la semaine à afficher (commencer par lundi)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysToSubtract = (firstDayOfWeek - Calendar.MONDAY + 7) % 7
        cal.add(Calendar.DAY_OF_MONTH, -daysToSubtract)
        
        // Générer les semaines (6 semaines maximum)
        for (week in 0 until 6) {
            val weekDays = mutableListOf<SelectableDay>()
            
            // 7 jours par semaine (Lun-Dim)
            for (dayOfWeek in 0 until 7) {
                val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH)
                val isCurrentMonth = month == currentCalendar.get(Calendar.MONTH)
                val dateStr = dateFormat.format(cal.time)
                
                weekDays.add(SelectableDay(
                    dayOfMonth = if (isCurrentMonth) dayOfMonth else 0,
                    isCurrentMonth = isCurrentMonth,
                    date = dateStr
                ))
                
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
            
            weeks.add(CalendarWeek(weekDays))
        }
        
        weekCalendarAdapter.updateWeeks(weeks)
    }

    private fun loadDayTypes() {
        loadDayTypesJob?.cancel() // Annuler le job précédent s'il existe
        loadDayTypesJob = lifecycleScope.launch {
            try {
                repository.getAllEventTypes().collect { dayTypes ->
                    // Vérifier que le fragment est encore attaché
                    if (isAdded && context != null) {
                        dayTypeAdapter.updateDayTypes(dayTypes)
                    }
                }
            } catch (e: Exception) {
                Log.e("MultiDaySelection", "Erreur lors du chargement des types de jour", e)
                // Vérifier que le fragment est encore attaché avant d'afficher le Toast
                if (isAdded && context != null) {
                    Toast.makeText(requireContext(), "Erreur lors du chargement des types de jour", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun applyDayTypeToSelectedDays() {
        // Vérifier que le fragment est encore attaché
        if (!isAdded || context == null) {
            return
        }
        
        val selectedDays = weekCalendarAdapter.getSelectedDays()
        val selectedDayType = dayTypeAdapter.getSelectedDayType()
        
        if (selectedDays.isEmpty() || selectedDayType == null) {
            Toast.makeText(requireContext(), "Veuillez sélectionner des jours et un type de journée", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                selectedDays.forEach { dateStr ->
                    // Convertir la date string en timestamp
                    val dateTimestamp = try {
                        dateFormat.parse(dateStr)?.time ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        System.currentTimeMillis()
                    }
                    
                    // Créer un événement pour toute la journée
                    val event = Event(
                        title = selectedDayType.name,
                        description = selectedDayType.description,
                        date = dateTimestamp,
                        startTime = null, // Pas d'heure = toute la journée
                        eventTypeId = selectedDayType.id
                    )
                    
                    repository.insertEvent(event)
                }
                
                // Vérifier que le fragment est encore attaché avant d'afficher le Toast et de naviguer
                if (isAdded && context != null) {
                    Toast.makeText(
                        requireContext(),
                        "Type de journée appliqué à ${selectedDays.size} jour(s)",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Retourner au calendrier principal
                    findNavController().navigateUp()
                }
                
            } catch (e: Exception) {
                Log.e("MultiDaySelection", "Erreur lors de l'application du type de jour", e)
                // Vérifier que le fragment est encore attaché avant d'afficher le Toast
                if (isAdded && context != null) {
                    Toast.makeText(requireContext(), "Erreur lors de l'application du type de jour", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateApplyButton() {
        val hasSelectedDays = weekCalendarAdapter.getSelectedDays().isNotEmpty()
        val hasSelectedType = dayTypeAdapter.getSelectedDayType() != null
        btnApplyDayType.isEnabled = hasSelectedDays && hasSelectedType
    }
}