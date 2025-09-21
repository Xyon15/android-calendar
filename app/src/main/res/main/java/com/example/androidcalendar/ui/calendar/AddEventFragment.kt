package com.example.androidcalendar.ui.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidcalendar.R
import com.example.androidcalendar.data.entity.Event
import com.example.androidcalendar.data.entity.EventType
import com.example.androidcalendar.databinding.FragmentAddEventBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEventFragment : Fragment() {

    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = _binding!!

    private val args: AddEventFragmentArgs by navArgs()
    private val viewModel: CalendarViewModel by viewModels()

    private var selectedDate: Calendar = Calendar.getInstance()
    private var startTime: Calendar = Calendar.getInstance()
    private var endTime: Calendar = Calendar.getInstance()
    
    private var eventTypes: List<EventType> = emptyList()
    private var selectedEventType: EventType? = null

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupEventTypeSpinner()
        setupObservers()
        setupClickListeners()
        
        // Initialize with selected date from navigation arguments
        val selectedDateString = args.selectedDate
        parseSelectedDate(selectedDateString)
    }

    private fun parseSelectedDate(dateString: String) {
        try {
            selectedDate.time = dateFormat.parse(dateString) ?: Date()
            updateDateDisplay()
        } catch (e: Exception) {
            selectedDate.time = Date()
            updateDateDisplay()
        }
    }

    private fun setupViews() {
        // Initialize times with current time
        val now = Calendar.getInstance()
        startTime.time = now.time
        startTime.set(Calendar.MINUTE, 0) // Round to hour
        
        endTime.time = now.time
        endTime.add(Calendar.HOUR_OF_DAY, 1)
        endTime.set(Calendar.MINUTE, 0)
        
        updateTimeDisplays()
        updateDateDisplay()
        
        // Setup alert time spinner
        setupAlertTimeSpinner()
    }

    private fun setupEventTypeSpinner() {
        lifecycleScope.launch {
            eventTypes = viewModel.getAllEventTypes().first()
            
            val adapter = object : ArrayAdapter<EventType>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                eventTypes
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    return view
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    return view
                }
            }
            
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.eventTypeSpinner.adapter = adapter
            
            binding.eventTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedEventType = eventTypes[position]
                    updateEventTypeColorPreview()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedEventType = null
                    binding.eventTypeColorPreview.visibility = View.GONE
                }
            }
            
            // Select first event type by default
            if (eventTypes.isNotEmpty()) {
                binding.eventTypeSpinner.setSelection(0)
            }
        }
    }

    private fun updateEventTypeColorPreview() {
        selectedEventType?.let { eventType ->
            val colorRes = getEventTypeColorResource(eventType.name)
            val color = ContextCompat.getColor(requireContext(), colorRes)
            binding.eventTypeColorPreview.setBackgroundColor(color)
            binding.eventTypeColorPreview.text = "Couleur: ${eventType.name}"
            binding.eventTypeColorPreview.visibility = View.VISIBLE
        }
    }

    private fun getEventTypeColorResource(eventTypeName: String): Int {
        return when (eventTypeName.lowercase()) {
            "personnel" -> R.color.event_purple
            "travail" -> R.color.event_blue
            "sport" -> R.color.event_green
            "famille" -> R.color.event_orange
            "rendez-vous" -> R.color.event_red
            "anniversaire" -> R.color.event_pink
            "vacances" -> R.color.event_cyan
            "formation" -> R.color.event_indigo
            else -> R.color.event_purple
        }
    }

    private fun setupAlertTimeSpinner() {
        val alertOptions = arrayOf(
            "5 minutes avant",
            "15 minutes avant",
            "30 minutes avant",
            "1 heure avant",
            "2 heures avant",
            "1 jour avant"
        )
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            alertOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.alertTimeSpinner.adapter = adapter
    }

    private fun setupObservers() {
        // No specific observers needed for this fragment currently
    }

    private fun setupClickListeners() {
        binding.selectDateButton.setOnClickListener {
            showDatePicker()
        }

        binding.selectStartTimeButton.setOnClickListener {
            showTimePicker(true)
        }

        binding.selectEndTimeButton.setOnClickListener {
            showTimePicker(false)
        }

        binding.allDaySwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.timeSection.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        binding.alertSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.alertOptionsSection.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.saveButton.setOnClickListener {
            saveEvent()
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                updateDateDisplay()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val timeToShow = if (isStartTime) startTime else endTime
        
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                if (isStartTime) {
                    startTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    startTime.set(Calendar.MINUTE, minute)
                    
                    // Automatically adjust end time to be 1 hour after start time
                    endTime.time = startTime.time
                    endTime.add(Calendar.HOUR_OF_DAY, 1)
                } else {
                    endTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    endTime.set(Calendar.MINUTE, minute)
                    
                    // Validate that end time is after start time
                    if (endTime.before(startTime)) {
                        Toast.makeText(
                            requireContext(),
                            "L'heure de fin doit être après l'heure de début",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TimePickerDialog
                    }
                }
                updateTimeDisplays()
            },
            timeToShow.get(Calendar.HOUR_OF_DAY),
            timeToShow.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun updateDateDisplay() {
        binding.selectedDateText.text = "📅 ${dateFormat.format(selectedDate.time)}"
        binding.dateDisplay.text = dateFormat.format(selectedDate.time)
    }

    private fun updateTimeDisplays() {
        binding.startTimeDisplay.text = timeFormat.format(startTime.time)
        binding.endTimeDisplay.text = timeFormat.format(endTime.time)
    }

    private fun saveEvent() {
        val title = binding.titleEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()

        // Validation
        if (title.isEmpty()) {
            binding.titleInputLayout.error = "Le titre est obligatoire"
            return
        } else {
            binding.titleInputLayout.error = null
        }

        if (selectedEventType == null) {
            Toast.makeText(requireContext(), "Veuillez sélectionner un type d'événement", Toast.LENGTH_SHORT).show()
            return
        }

        // Create event
        val isAllDay = binding.allDaySwitch.isChecked
        val hasAlert = binding.alertSwitch.isChecked
        
        val alertType = if (hasAlert) {
            when (binding.alertTimeSpinner.selectedItemPosition) {
                0 -> "5_MINUTES"
                1 -> "15_MINUTES"
                2 -> "30_MINUTES"
                3 -> "1_HOUR"
                4 -> "2_HOURS"
                5 -> "1_DAY"
                else -> "15_MINUTES"
            }
        } else {
            "NONE"
        }

        val eventDate = selectedDate.time
        val eventStartTime = if (isAllDay) null else startTime.time
        val eventEndTime = if (isAllDay) null else endTime.time

        // Calculate work hours (if it's a work event)
        val workHours = if (selectedEventType?.name?.lowercase() == "travail" && !isAllDay) {
            val diffInMillis = endTime.timeInMillis - startTime.timeInMillis
            diffInMillis.toFloat() / (1000 * 60 * 60) // Convert to hours
        } else {
            0f
        }

        val event = Event(
            title = title,
            description = description.ifEmpty { null },
            date = eventDate,
            startTime = eventStartTime,
            endTime = eventEndTime,
            eventTypeId = selectedEventType!!.id,
            isAllDay = isAllDay,
            alertType = alertType,
            workHours = workHours
        )

        lifecycleScope.launch {
            try {
                viewModel.insertEvent(event)
                Toast.makeText(requireContext(), "✅ Événement créé avec succès", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "❌ Erreur lors de la création: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}