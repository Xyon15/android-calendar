package com.calendar.app.ui.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventType
import com.calendar.app.data.repository.CalendarRepository
import com.calendar.app.data.database.CalendarDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEventFragment : Fragment() {

    private val args: AddEventFragmentArgs by navArgs()
    private lateinit var repository: CalendarRepository
    
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var eventTypeRadioGroup: RadioGroup
    private lateinit var eventTypeRadioButton: RadioButton
    private lateinit var appointmentRadioButton: RadioButton
    private lateinit var eventTypeSpinner: Spinner
    private lateinit var dateDisplay: TextView
    private lateinit var selectDateButton: Button
    private lateinit var timeSwitch: Switch
    private lateinit var timeSection: LinearLayout
    private lateinit var timeDisplay: TextView
    private lateinit var selectTimeButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    
    private var eventTypes: List<EventType> = emptyList()
    private var selectedEventType: EventType? = null
    private var selectedDate: Date = Date()
    private var selectedTime: Calendar? = null

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = CalendarDatabase.getDatabase(requireContext())
        repository = CalendarRepository(database.eventDao(), database.eventTypeDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Créer l'interface de manière programmatique
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        // Titre
        val titleView = TextView(requireContext()).apply {
            text = "Nouveau rendez-vous"
            textSize = 24f
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 72)
        }
        layout.addView(titleView)

        // Input titre
        titleEditText = EditText(requireContext()).apply {
            hint = "Titre"
            setPadding(0, 0, 0, 48)
        }
        layout.addView(titleEditText)

        // Input description
        descriptionEditText = EditText(requireContext()).apply {
            hint = "Description"
            minLines = 3
            setPadding(0, 0, 0, 48)
        }
        layout.addView(descriptionEditText)

        // Radio group pour choisir entre Type de journée et Rendez-vous
        val radioGroupTitle = TextView(requireContext()).apply {
            text = "Type d'événement"
            textSize = 18f
            setPadding(0, 0, 0, 24)
        }
        layout.addView(radioGroupTitle)

        eventTypeRadioGroup = RadioGroup(requireContext()).apply {
            orientation = RadioGroup.HORIZONTAL
            setPadding(0, 0, 0, 48)
        }

        eventTypeRadioButton = RadioButton(requireContext()).apply {
            text = "Type de journée"
            id = View.generateViewId()
        }
        eventTypeRadioGroup.addView(eventTypeRadioButton)

        appointmentRadioButton = RadioButton(requireContext()).apply {
            text = "Rendez-vous"
            id = View.generateViewId()
            isChecked = true // Par défaut
        }
        eventTypeRadioGroup.addView(appointmentRadioButton)

        layout.addView(eventTypeRadioGroup)

        // Spinner type d'événement (masqué par défaut)
        eventTypeSpinner = Spinner(requireContext()).apply {
            setPadding(0, 0, 0, 48)
            visibility = View.GONE
        }
        layout.addView(eventTypeSpinner)

        // Affichage date
        dateDisplay = TextView(requireContext()).apply {
            text = "Date sélectionnée"
            textSize = 16f
            setPadding(0, 0, 0, 24)
        }
        layout.addView(dateDisplay)

        // Bouton sélection date
        selectDateButton = Button(requireContext()).apply {
            text = "Choisir la date"
            setPadding(0, 0, 0, 48)
        }
        layout.addView(selectDateButton)

        // Switch pour l'heure (visible seulement pour les rendez-vous)
        timeSwitch = Switch(requireContext()).apply {
            text = "Définir une heure"
            setPadding(0, 0, 0, 24)
            visibility = View.VISIBLE // Visible par défaut car "Rendez-vous" est sélectionné
        }
        layout.addView(timeSwitch)

        // Section heure
        timeSection = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            visibility = View.GONE
            setPadding(0, 0, 0, 48)
        }

        timeDisplay = TextView(requireContext()).apply {
            text = "Heure sélectionnée"
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        timeSection.addView(timeDisplay)

        selectTimeButton = Button(requireContext()).apply {
            text = "Choisir l'heure"
        }
        timeSection.addView(selectTimeButton)

        layout.addView(timeSection)

        // Boutons
        val buttonsLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.END
        }

        cancelButton = Button(requireContext()).apply {
            text = "Annuler"
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                marginEnd = 24
            }
        }
        buttonsLayout.addView(cancelButton)

        saveButton = Button(requireContext()).apply {
            text = "Enregistrer"
        }
        buttonsLayout.addView(saveButton)

        layout.addView(buttonsLayout)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupEventTypeSpinner()
        setupClickListeners()
        
        // Initialize with selected date and event type from navigation arguments
        val selectedDateString = args.selectedDate
        val eventTypeParam = args.eventType
        parseSelectedDate(selectedDateString)
        handleEventTypeParam(eventTypeParam)
    }

    private fun parseSelectedDate(dateString: String) {
        try {
            selectedDate = dateFormat.parse(dateString) ?: Date()
            updateDateDisplay()
        } catch (e: Exception) {
            selectedDate = Date()
            updateDateDisplay()
        }
    }

    private fun handleEventTypeParam(eventType: String) {
        when (eventType) {
            "show_day_types_menu" -> {
                // Afficher le sous-menu des types de journée
                showDayTypesBottomSheet()
                return
            }
            "show_appointment_types_menu" -> {
                // Afficher le sous-menu des types de rendez-vous
                showAppointmentTypesBottomSheet()
                return
            }
            "day_type" -> {
                eventTypeRadioButton.isChecked = true
                appointmentRadioButton.isChecked = false
                eventTypeSpinner.visibility = View.VISIBLE
                timeSwitch.visibility = View.GONE
                timeSection.visibility = View.GONE
            }
            "appointment" -> {
                appointmentRadioButton.isChecked = true
                eventTypeRadioButton.isChecked = false
                eventTypeSpinner.visibility = View.GONE
                timeSwitch.visibility = View.VISIBLE
            }
            else -> {
                // Garder la sélection par défaut (rendez-vous)
                appointmentRadioButton.isChecked = true
            }
        }
    }
    
    private fun showDayTypesBottomSheet() {
        val dayTypesFragment = DayTypeBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("selectedDate", args.selectedDate)
        dayTypesFragment.arguments = bundle
        dayTypesFragment.show(parentFragmentManager, "day_types_menu")
        // Fermer ce fragment
        findNavController().popBackStack()
    }
    
    private fun showAppointmentTypesBottomSheet() {
        val appointmentTypesFragment = AppointmentTypeBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("selectedDate", args.selectedDate)
        appointmentTypesFragment.arguments = bundle
        appointmentTypesFragment.show(parentFragmentManager, "appointment_types_menu")
        // Fermer ce fragment
        findNavController().popBackStack()
    }

    private fun updateDateDisplay() {
        dateDisplay.text = "Date: ${dateFormat.format(selectedDate)}"
    }

    private fun updateTimeDisplay() {
        if (selectedTime != null) {
            timeDisplay.text = "Heure: ${timeFormat.format(selectedTime!!.time)}"
        } else {
            timeDisplay.text = "Heure sélectionnée"
        }
    }

    private fun setupEventTypeSpinner() {
        lifecycleScope.launch {
            eventTypes = repository.getAllEventTypes().first()
            
            val eventTypeNames = eventTypes.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                eventTypeNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            eventTypeSpinner.adapter = adapter
            
            // Select first event type by default
            if (eventTypes.isNotEmpty()) {
                selectedEventType = eventTypes[0]
                eventTypeSpinner.setSelection(0)
            }
        }
    }

    private fun setupClickListeners() {
        // Radio group pour choisir le type d'événement
        eventTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                1 -> { // Type de journée
                    eventTypeSpinner.visibility = View.VISIBLE
                    timeSwitch.visibility = View.GONE
                    timeSection.visibility = View.GONE
                    setupEventTypeSpinner()
                }
                2 -> { // Rendez-vous
                    eventTypeSpinner.visibility = View.GONE
                    timeSwitch.visibility = View.VISIBLE
                    selectedEventType = null
                }
            }
        }

        // Switch pour l'heure
        timeSwitch.setOnCheckedChangeListener { _, isChecked ->
            timeSection.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                selectedTime = null
                updateTimeDisplay()
            }
        }

        // Bouton de sélection de date
        selectDateButton.setOnClickListener {
            showDatePicker()
        }

        // Bouton de sélection d'heure
        selectTimeButton.setOnClickListener {
            showTimePicker()
        }

        // Spinner pour les types d'événements
        eventTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position < eventTypes.size) {
                    selectedEventType = eventTypes[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        saveButton.setOnClickListener {
            saveEvent()
        }

        cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, day)
                selectedDate = newCalendar.time
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = selectedTime ?: Calendar.getInstance()
        
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                if (selectedTime == null) {
                    selectedTime = Calendar.getInstance()
                }
                selectedTime!!.set(Calendar.HOUR_OF_DAY, hour)
                selectedTime!!.set(Calendar.MINUTE, minute)
                updateTimeDisplay()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun saveEvent() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        // Validation
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Le titre est obligatoire", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRadioId = eventTypeRadioGroup.checkedRadioButtonId
        
        when (selectedRadioId) {
            1 -> { // Type de journée
                if (selectedEventType == null) {
                    Toast.makeText(requireContext(), "Veuillez sélectionner un type de journée", Toast.LENGTH_SHORT).show()
                    return
                }
                saveEventType(title, description)
            }
            2 -> { // Rendez-vous
                saveAppointment(title, description)
            }
            else -> {
                Toast.makeText(requireContext(), "Veuillez sélectionner un type d'événement", Toast.LENGTH_SHORT).show()
                return
            }
        }
    }

    private fun saveEventType(title: String, description: String) {
        // Pour un type de journée, on sauvegarde avec l'EventType sélectionné
        val event = Event(
            title = title,
            description = description,
            date = selectedDate.time,
            startTime = null, // Pas d'heure pour les types de journées
            endTime = null,
            eventTypeId = selectedEventType!!.id,
            workHours = 0f,
            alertType = "Aucun"
        )

        lifecycleScope.launch {
            try {
                repository.insertEvent(event)
                Toast.makeText(requireContext(), "✅ Type de journée créé avec succès", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "❌ Erreur lors de la création: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveAppointment(title: String, description: String) {
        // Pour un rendez-vous, utiliser les types existants ou demander à l'utilisateur d'en créer
        lifecycleScope.launch {
            try {
                // Si aucun type n'existe, rediriger vers la gestion des types
                if (eventTypes.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Veuillez d'abord créer des types de journées dans le menu",
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }

                // Utiliser le premier type disponible ou permettre à l'utilisateur de choisir
                val appointmentEventType = eventTypes.firstOrNull() ?: return@launch

                // Préparer les heures si le switch est activé
                var startTimeStr: String? = null
                var endTimeStr: String? = null
                
                if (timeSwitch.isChecked && selectedTime != null) {
                    // Format de l'heure en HH:mm
                    startTimeStr = timeFormat.format(selectedTime!!.time)
                    
                    // Par défaut, l'événement dure 1 heure
                    val endCalendar = Calendar.getInstance()
                    endCalendar.time = selectedTime!!.time
                    endCalendar.add(Calendar.HOUR_OF_DAY, 1)
                    endTimeStr = timeFormat.format(endCalendar.time)
                }

                // Créer l'événement
                val event = Event(
                    title = title,
                    description = description,
                    date = selectedDate.time,
                    startTime = startTimeStr,
                    endTime = endTimeStr,
                    eventTypeId = appointmentEventType.id,
                    workHours = 0f,
                    alertType = "Aucun"
                )

                repository.insertEvent(event)
                Toast.makeText(requireContext(), "✅ Rendez-vous créé avec succès", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "❌ Erreur lors de la création: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}