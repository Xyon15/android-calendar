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
            Toast.makeText(requireContext(), "Date sélectionnée", Toast.LENGTH_SHORT).show()
            viewModel.selectDate(date)
            // Navigate to day detail
            try {
                val action = CalendarFragmentDirections.actionCalendarFragmentToDayDetailFragment(date)
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.e("CalendarFragment", "Navigation error", e)
                Toast.makeText(requireContext(), "Erreur de navigation", Toast.LENGTH_SHORT).show()
            }
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
}