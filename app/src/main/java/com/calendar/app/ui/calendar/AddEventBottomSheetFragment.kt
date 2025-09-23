package com.calendar.app.ui.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.calendar.app.MainActivity
import com.calendar.app.data.model.Event
import com.calendar.app.data.model.EventType
import com.calendar.app.data.repository.CalendarRepository
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.databinding.BottomSheetAddEventBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEventBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddEventBinding? = null
    private val binding get() = _binding!!
    
    private var selectedDate: String = ""
    private var eventToEdit: Event? = null
    private lateinit var repository: CalendarRepository
    
    private var selectedCalendar: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedDate = arguments?.getString("selectedDate") ?: ""
        eventToEdit = arguments?.getParcelable("eventToEdit")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Configurer le comportement du bottom sheet
        setupBottomSheetBehavior()
        
        // Cacher le menu hamburger lors de l'affichage
        (activity as? MainActivity)?.hideMenuButton()
        
        // Initialiser le repository
        val database = CalendarDatabase.getDatabase(requireContext())
        repository = CalendarRepository(database.eventDao(), database.eventTypeDao())
        
        // Initialiser la date sélectionnée
        initializeSelectedDate()
        
        // Pré-remplir les champs si on édite un événement existant
        if (eventToEdit != null) {
            populateFieldsForEditing()
        }
        
        // Configurer les listeners
        setupListeners()
        
        // Initialiser l'affichage de la date
        updateDateDisplay()
        updateTimeDisplay()
    }

    private fun setupBottomSheetBehavior() {
        // S'assurer que le bottom sheet reste ouvert et a une taille appropriée
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as com.google.android.material.bottomsheet.BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<android.widget.FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let {
                val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)
                behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = true
                behavior.isHideable = true
                
                // Définir une hauteur minimale
                val layoutParams = it.layoutParams
                layoutParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                it.layoutParams = layoutParams
            }
        }
    }

    private fun populateFieldsForEditing() {
        eventToEdit?.let { event ->
            binding.etTitle.setText(event.title)
            binding.etDescription.setText(event.description)
            
            // Configuration de l'heure si elle existe
            if (event.startTime != null) {
                binding.switchTime.isChecked = true
                binding.layoutTime.visibility = View.VISIBLE
                
                // Parser l'heure depuis le format HH:mm
                try {
                    val timeParts = event.startTime.split(":")
                    if (timeParts.size == 2) {
                        selectedTime.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                        selectedTime.set(Calendar.MINUTE, timeParts[1].toInt())
                    }
                } catch (e: Exception) {
                    // Garder l'heure par défaut en cas d'erreur
                }
            }
        }
    }

    private fun initializeSelectedDate() {
        try {
            // Parser la date sélectionnée (format: "dd/MM/yyyy")
            val parts = selectedDate.split("/")
            if (parts.size == 3) {
                selectedCalendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
                selectedCalendar.set(Calendar.MONTH, parts[1].toInt() - 1) // Month is 0-based
                selectedCalendar.set(Calendar.YEAR, parts[2].toInt())
            }
        } catch (e: Exception) {
            // En cas d'erreur, utiliser la date actuelle
            selectedCalendar = Calendar.getInstance()
        }
        
        // Initialiser l'heure par défaut
        selectedTime.set(Calendar.HOUR_OF_DAY, 14)
        selectedTime.set(Calendar.MINUTE, 0)
    }

    private fun setupListeners() {
        // Switch pour l'heure
        binding.switchTime.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutTime.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        // Click sur la date
        binding.tvSelectedDate.setOnClickListener {
            showDatePicker()
        }
        
        // Click sur l'heure
        binding.tvSelectedTime.setOnClickListener {
            showTimePicker()
        }
        
        // Bouton annuler
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        // Bouton enregistrer
        binding.btnSave.setOnClickListener {
            saveEvent()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateDisplay()
            },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                updateTimeDisplay()
            },
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            true // Format 24h
        ).show()
    }

    private fun updateDateDisplay() {
        binding.tvSelectedDate.text = dateFormat.format(selectedCalendar.time)
    }

    private fun updateTimeDisplay() {
        binding.tvSelectedTime.text = timeFormat.format(selectedTime.time)
    }

    private fun saveEvent() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val hasTime = binding.switchTime.isChecked

        // Validation
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Le titre est obligatoire", Toast.LENGTH_SHORT).show()
            return
        }

        // Créer la date finale
        val finalDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedCalendar.get(Calendar.YEAR))
            set(Calendar.MONTH, selectedCalendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, selectedCalendar.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0)  // Toujours sauvegarder la date à 00:00
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        lifecycleScope.launch {
            try {
                if (eventToEdit != null) {
                    // Mode édition : mettre à jour l'événement existant
                    val updatedEvent = eventToEdit!!.copy(
                        title = title,
                        description = if (description.isNotEmpty()) description else "",
                        date = finalDate.timeInMillis,
                        startTime = if (hasTime) String.format("%02d:%02d", selectedTime.get(Calendar.HOUR_OF_DAY), selectedTime.get(Calendar.MINUTE)) else null,
                        endTime = null,
                        eventTypeId = null, // Pas de type pour les rendez-vous simples
                        updatedAt = System.currentTimeMillis()
                    )
                    
                    repository.updateEvent(updatedEvent)
                    Toast.makeText(requireContext(), "Rendez-vous modifié avec succès", Toast.LENGTH_SHORT).show()
                } else {
                    // Mode création : créer un nouvel événement sans type
                    val event = Event(
                        title = title,
                        description = if (description.isNotEmpty()) description else "",
                        date = finalDate.timeInMillis,
                        startTime = if (hasTime) String.format("%02d:%02d", selectedTime.get(Calendar.HOUR_OF_DAY), selectedTime.get(Calendar.MINUTE)) else null,
                        endTime = null,
                        eventTypeId = null // Pas de type pour les rendez-vous simples
                    )
                    
                    repository.insertEvent(event)
                    Toast.makeText(requireContext(), "Rendez-vous créé avec succès", Toast.LENGTH_SHORT).show()
                }
                
                dismiss()
                
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur lors de la sauvegarde: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Rétablir le menu hamburger
        (activity as? MainActivity)?.showMenuButton()
        _binding = null
    }

    companion object {
        fun newInstance(selectedDate: String): AddEventBottomSheetFragment {
            val fragment = AddEventBottomSheetFragment()
            val args = Bundle()
            args.putString("selectedDate", selectedDate)
            fragment.arguments = args
            return fragment
        }
        
        fun newInstance(selectedDate: String, eventToEdit: Event): AddEventBottomSheetFragment {
            val fragment = AddEventBottomSheetFragment()
            val args = Bundle()
            args.putString("selectedDate", selectedDate)
            args.putParcelable("eventToEdit", eventToEdit)
            fragment.arguments = args
            return fragment
        }
    }
}