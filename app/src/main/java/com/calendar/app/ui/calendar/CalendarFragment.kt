package com.calendar.app.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.repository.CalendarRepository
import com.calendar.app.databinding.FragmentCalendarBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarFragment : Fragment() {
    
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var repository: CalendarRepository
    
    private val viewModel: CalendarViewModel by viewModels {
        CalendarViewModelFactory(repository)
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
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        calendarAdapter = CalendarAdapter { date ->
            Log.d("CalendarFragment", "Date clicked: $date")
            
            // Extraire le jour du mois à partir de la date
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            
            Log.d("CalendarFragment", "Date sélectionnée: $dayOfMonth/$month/$year")
            
            // Afficher le bottom sheet au lieu de naviguer directement
            val selectedDateString = String.format("%02d/%02d/%04d", dayOfMonth, month, year)
            showDayMenuBottomSheet(selectedDateString)
        }
        
        binding.calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = calendarAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnPreviousMonth.setOnClickListener {
            Log.d("CalendarFragment", "Previous month button clicked")
            Toast.makeText(requireContext(), "Mois précédent", Toast.LENGTH_SHORT).show()
            viewModel.navigateToPreviousMonth()
        }
        
        binding.btnNextMonth.setOnClickListener {
            Log.d("CalendarFragment", "Next month button clicked")
            Toast.makeText(requireContext(), "Mois suivant", Toast.LENGTH_SHORT).show()
            viewModel.navigateToNextMonth()
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentMonth.collect { calendar ->
                Log.d("CalendarFragment", "Month changed: ${viewModel.getCurrentMonthTitle()}")
                binding.tvCurrentMonth.text = viewModel.getCurrentMonthTitle()
                val days = viewModel.getDaysInMonth()
                Log.d("CalendarFragment", "Generated ${days.size} days for calendar")
                calendarAdapter.submitList(days)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsForCurrentMonth.collect { events ->
                Log.d("CalendarFragment", "Events updated: ${events.size} events")
                calendarAdapter.updateEvents(events)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDayMenuBottomSheet(selectedDate: String) {
        try {
            val action = CalendarFragmentDirections.actionCalendarToDayMenu(selectedDate)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Log.e("CalendarFragment", "Error showing day menu: ${e.message}")
            Toast.makeText(requireContext(), "📅 Date sélectionnée: $selectedDate", Toast.LENGTH_SHORT).show()
        }
    }
}