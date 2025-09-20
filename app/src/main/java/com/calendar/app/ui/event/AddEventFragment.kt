package com.calendar.app.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.repository.CalendarRepository
import com.calendar.app.databinding.FragmentAddEventBinding
import com.calendar.app.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.*

class AddEventFragment : Fragment() {
    
    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = _binding!!
    
    private val args: AddEventFragmentArgs by navArgs()
    private lateinit var repository: CalendarRepository
    
    private val viewModel: EventViewModel by viewModels {
        EventViewModelFactory(repository)
    }
    
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
        _binding = FragmentAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupClickListeners()
        observeViewModel()
        
        // Load event if editing
        if (args.eventId != -1L) {
            viewModel.loadEvent(args.eventId)
            binding.tvHeaderTitle.text = "Modifier l'événement"
        } else {
            viewModel.setSelectedDate(args.selectedDate)
        }
    }
    
    private fun setupUI() {
        // Setup date display
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = args.selectedDate
        
        binding.tvDayOfWeek.text = DateUtils.getDayOfWeekName(args.selectedDate)
        binding.tvDayNumber.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        binding.tvMonth.text = DateUtils.getMonthName(calendar.get(Calendar.MONTH))
        
        binding.etEventDate.setText(viewModel.getFormattedDate())
        
        // Setup number pickers
        binding.npHour.minValue = 0
        binding.npHour.maxValue = 23
        binding.npHour.value = 12
        
        binding.npMinute.minValue = 0
        binding.npMinute.maxValue = 59
        binding.npMinute.value = 0
        
        // Setup number picker listeners
        binding.npHour.setOnValueChangedListener { _, _, newVal ->
            viewModel.setSelectedHour(newVal)
        }
        
        binding.npMinute.setOnValueChangedListener { _, _, newVal ->
            viewModel.setSelectedMinute(newVal)
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.etEventType.setOnClickListener {
            val action = AddEventFragmentDirections.actionAddEventFragmentToSelectEventTypeFragment()
            findNavController().navigate(action)
        }
        
        binding.fabAddHours.setOnClickListener {
            // Add work hours logic
            val currentHours = viewModel.workHours.value
            viewModel.setWorkHours(currentHours + 1.0f)
        }
        
        binding.fabSave.setOnClickListener {
            saveEvent()
        }
        
        binding.fabCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventTitle.collect { title ->
                if (binding.etEventTitle.text.toString() != title) {
                    binding.etEventTitle.setText(title)
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedEventType.collect { eventType ->
                binding.etEventType.setText(eventType?.name ?: "Sélectionner un type")
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.workHours.collect { hours ->
                binding.workHoursLayout.findViewById<android.widget.TextView>(android.R.id.text1)?.text = 
                    String.format("%.1f heures", hours)
            }
        }
    }
    
    private fun saveEvent() {
        val title = binding.etEventTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Veuillez saisir un titre", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewModel.setEventTitle(title)
        
        if (viewModel.saveEvent()) {
            Toast.makeText(requireContext(), "Événement sauvegardé", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}