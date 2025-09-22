package com.calendar.app.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.calendar.app.MainActivity
import com.calendar.app.databinding.FragmentYearMonthPickerBinding
import java.util.*

class YearMonthPickerFragment : Fragment() {
    
    private var _binding: FragmentYearMonthPickerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var monthsAdapter: MonthsAdapter
    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private val currentYearActual = Calendar.getInstance().get(Calendar.YEAR)
    
    private val monthNames = arrayOf(
        "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYearMonthPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Masquer le menu hamburger
        (activity as? MainActivity)?.hideMenuButton()
        
        setupRecyclerView()
        setupClickListeners()
        updateYearDisplay()
        generateMonthsList()
    }

    private fun setupRecyclerView() {
        monthsAdapter = MonthsAdapter { monthNumber ->
            // Retourner le mois sélectionné au CalendarFragment
            returnSelectedMonth(currentYear, monthNumber)
        }
        
        binding.monthsRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3) // 3 colonnes pour 4 lignes
            adapter = monthsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.btnPreviousYear.setOnClickListener {
            currentYear--
            updateYearDisplay()
            generateMonthsList()
        }
        
        binding.btnNextYear.setOnClickListener {
            currentYear++
            updateYearDisplay()
            generateMonthsList()
        }
    }

    private fun updateYearDisplay() {
        binding.tvCurrentYear.text = currentYear.toString()
    }

    private fun generateMonthsList() {
        val months = mutableListOf<MonthItem>()
        
        for (i in 1..12) {
            val isCurrentMonth = (currentYear == currentYearActual && i == currentMonth)
            months.add(
                MonthItem(
                    monthNumber = i,
                    monthName = monthNames[i - 1],
                    isCurrentMonth = isCurrentMonth
                )
            )
        }
        
        monthsAdapter.submitList(months)
    }

    private fun returnSelectedMonth(year: Int, month: Int) {
        // Créer un bundle avec les données à retourner
        val result = Bundle().apply {
            putInt("selected_year", year)
            putInt("selected_month", month)
        }
        
        // Utiliser le Fragment Result API pour retourner les données
        parentFragmentManager.setFragmentResult("month_selection", result)
        
        // Retourner au calendrier
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        
        // Réafficher le menu hamburger
        (activity as? MainActivity)?.showMenuButton()
        
        _binding = null
    }
}