package com.calendar.app.ui.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventType
import com.calendar.app.data.repository.CalendarRepository
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.databinding.FragmentAddEventBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEventFragment : Fragment() {

    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = _binding!!
    
    private val args: AddEventFragmentArgs by navArgs()
    private lateinit var repository: CalendarRepository
    
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)

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
        
        // Initialiser le repository
        val database = CalendarDatabase.getDatabase(requireContext())
        repository = CalendarRepository(database.eventDao(), database.eventTypeDao())
        
        setupUI()
        setupClickListeners()
        initializeDateFromArgs()
    }

    private fun setupUI() {
        // Initialiser l'heure par défaut à 14:00
        selectedTime.set(Calendar.HOUR_OF_DAY, 14)
        selectedTime.set(Calendar.MINUTE, 0)
        
        // Afficher la date et l'heure
        updateDateDisplay()
        updateTimeDisplay()
        
        // Le switch contrôle la visibilité de la section heure
        binding.switchTime.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutTime.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        // Sélection de date
        binding.tvSelectedDate.setOnClickListener {
            showDatePicker()
        }
        
        // Sélection d'heure
        binding.tvSelectedTime.setOnClickListener {
            showTimePicker()
        }
        
        // Bouton annuler
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Bouton enregistrer
        binding.btnSave.setOnClickListener {
            saveAppointment()
        }
    }

    private fun initializeDateFromArgs() {
        // Utiliser la date passée en argument si disponible
        if (args.selectedDate.isNotEmpty()) {
            try {
                val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
                val date = inputFormat.parse(args.selectedDate)
                date?.let {
                    selectedDate.time = it
                    updateDateDisplay()
                }
            } catch (e: Exception) {
                // Garder la date actuelle si parsing échoue
            }
        }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateDisplay()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePicker() {
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                updateTimeDisplay()
            },
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            true // Format 24h
        )
        timePicker.show()
    }

    private fun updateDateDisplay() {
        binding.tvSelectedDate.text = dateFormat.format(selectedDate.time)
    }

    private fun updateTimeDisplay() {
        binding.tvSelectedTime.text = timeFormat.format(selectedTime.time)
    }

    private fun saveAppointment() {
        val title = binding.etTitle.text.toString().trim()
        
        // Validation
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Veuillez saisir un titre", Toast.LENGTH_SHORT).show()
            binding.etTitle.requestFocus()
            return
        }
        
        val description = binding.etDescription.text.toString().trim()
        val hasTime = binding.switchTime.isChecked
        
        lifecycleScope.launch {
            try {
                // Créer un type d'événement "Rendez-vous" s'il n'existe pas
                val appointmentEventType = getOrCreateAppointmentEventType()
                
                // Préparer la date finale
                val finalDate = Calendar.getInstance()
                finalDate.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
                finalDate.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
                finalDate.set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
                
                if (hasTime) {
                    finalDate.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
                    finalDate.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))
                } else {
                    finalDate.set(Calendar.HOUR_OF_DAY, 0)
                    finalDate.set(Calendar.MINUTE, 0)
                }
                finalDate.set(Calendar.SECOND, 0)
                finalDate.set(Calendar.MILLISECOND, 0)
                
                // Créer l'événement
                val event = Event(
                    title = title,
                    description = if (description.isNotEmpty()) description else "",
                    date = finalDate.timeInMillis,
                    startTime = if (hasTime) String.format("%02d:%02d", finalDate.get(Calendar.HOUR_OF_DAY), finalDate.get(Calendar.MINUTE)) else null,
                    endTime = null, // Pas de fin pour les rendez-vous simples
                    eventTypeId = appointmentEventType.id
                )
                
                repository.insertEvent(event)
                
                Toast.makeText(
                    requireContext(), 
                    "Rendez-vous créé avec succès", 
                    Toast.LENGTH_SHORT
                ).show()
                
                findNavController().navigateUp()
                
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(), 
                    "Erreur lors de la création du rendez-vous", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun getOrCreateAppointmentEventType(): EventType {
        // Chercher un type "Rendez-vous" existant
        val existingTypes = repository.getAllEventTypes().first()
        val appointmentType = existingTypes.find { it.name == "Rendez-vous" }
        
        return appointmentType ?: run {
            // Créer un nouveau type "Rendez-vous"
            val newType = EventType(
                name = "Rendez-vous",
                color = "#3498DB" // Bleu
            )
            val id = repository.insertEventType(newType)
            newType.copy(id = id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}