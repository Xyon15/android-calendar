package com.calendar.app.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.repository.CalendarRepository
import com.calendar.app.databinding.FragmentSelectEventTypeBinding
import kotlinx.coroutines.launch

class SelectEventTypeFragment : Fragment() {
    
    private var _binding: FragmentSelectEventTypeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var repository: CalendarRepository
    private lateinit var eventTypeAdapter: EventTypeAdapter
    
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
        _binding = FragmentSelectEventTypeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        eventTypeAdapter = EventTypeAdapter(
            onTypeClick = { eventType ->
                viewModel.setSelectedEventType(eventType)
                findNavController().navigateUp()
            },
            onMoreOptionsClick = { eventType ->
                // Show more options (edit, delete)
            }
        )
        
        binding.rvEventTypes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventTypeAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.fabAddType.setOnClickListener {
            // Navigate to create new event type
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventTypes.collect { eventTypes ->
                eventTypeAdapter.submitList(eventTypes)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedEventType.collect { selectedType ->
                eventTypeAdapter.setSelectedType(selectedType?.id)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}