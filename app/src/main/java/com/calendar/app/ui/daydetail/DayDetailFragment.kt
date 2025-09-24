package com.calendar.app.ui.daydetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.repository.CalendarRepository
import com.calendar.app.databinding.FragmentDayDetailBinding
import com.calendar.app.ui.event.EventViewModel
import com.calendar.app.ui.event.EventViewModelFactory
import com.calendar.app.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.*

/*
// Fragment temporairement désactivé pour compilation
class DayDetailFragment : Fragment() {
    
    private var _binding: FragmentDayDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: DayDetailFragmentArgs by navArgs()
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
        _binding = FragmentDayDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupClickListeners()
        observeViewModel()
        
        viewModel.setSelectedDate(args.selectedDate)
    }
    
    private fun setupUI() {
        // Setup day information
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = args.selectedDate
        
        binding.tvSelectedDate.text = DateUtils.formatFullDate(args.selectedDate)
        
        // Setup events RecyclerView
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        // TODO: Setup adapter when event adapter is created
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.fabAddEvent.setOnClickListener {
            val action = DayDetailFragmentDirections.actionDayDetailFragmentToAddEventFragment(args.selectedDate)
            findNavController().navigate(action)
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe events for the selected date
            // This would require additional ViewModel setup
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
*/