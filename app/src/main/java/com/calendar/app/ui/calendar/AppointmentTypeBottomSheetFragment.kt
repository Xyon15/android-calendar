package com.calendar.app.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.calendar.app.MainActivity
import com.calendar.app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AppointmentTypeBottomSheetFragment : BottomSheetDialogFragment() {

    private var selectedDate: String = ""
    
    private lateinit var layoutMedical: View
    private lateinit var layoutProfessional: View
    private lateinit var layoutPersonal: View
    private lateinit var layoutFamily: View
    private lateinit var layoutOther: View
    private lateinit var btnBack: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedDate = arguments?.getString("selectedDate") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_appointment_types, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        layoutMedical = view.findViewById(R.id.layoutMedical)
        layoutProfessional = view.findViewById(R.id.layoutProfessional)
        layoutPersonal = view.findViewById(R.id.layoutPersonal)
        layoutFamily = view.findViewById(R.id.layoutFamily)
        layoutOther = view.findViewById(R.id.layoutOther)
        btnBack = view.findViewById(R.id.btnBack)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            dismiss()
        }
        
        layoutMedical.setOnClickListener {
            navigateToAddEvent("Médical")
        }
        
        layoutProfessional.setOnClickListener {
            navigateToAddEvent("Professionnel")
        }
        
        layoutPersonal.setOnClickListener {
            navigateToAddEvent("Personnel")
        }
        
        layoutFamily.setOnClickListener {
            navigateToAddEvent("Famille")
        }
        
        layoutOther.setOnClickListener {
            navigateToAddEvent("Autre")
        }
    }

    private fun navigateToAddEvent(appointmentType: String) {
        // Cette méthode est obsolète - ne pas naviguer vers l'ancien AddEventFragment
        // qui créait des EventTypes parasites
        Log.d("AppointmentTypeBottomSheet", "navigateToAddEvent called but disabled to prevent parasite EventTypes")
        dismiss()
    }
}