package com.calendar.app.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R
import com.calendar.app.data.database.CalendarDatabase
import com.calendar.app.data.model.EventType
import com.calendar.app.data.repository.CalendarRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DayTypeFormFragment : Fragment() {

    private lateinit var repository: CalendarRepository
    private lateinit var colorPaletteAdapter: ColorPaletteAdapter
    
    private lateinit var etName: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var colorPreview: View
    private lateinit var tvSelectedColor: TextView
    private lateinit var rvColorPalette: RecyclerView
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var btnBack: View
    private lateinit var tvTitle: TextView
    
    private var editingDayTypeId: Long? = null
    private var selectedColor: String = "#3498DB"
    
    private val availableColors = listOf(
        ColorItem("Bleu", "#3498DB"),
        ColorItem("Rouge", "#E74C3C"),
        ColorItem("Vert", "#27AE60"),
        ColorItem("Orange", "#E67E22"),
        ColorItem("Violet", "#8E44AD"),
        ColorItem("Turquoise", "#1ABC9C"),
        ColorItem("Jaune", "#F1C40F"),
        ColorItem("Rose", "#E91E63"),
        ColorItem("Indigo", "#3F51B5"),
        ColorItem("Marron", "#8D6E63"),
        ColorItem("Gris", "#95A5A6"),
        ColorItem("Noir", "#2C3E50")
    )

    data class ColorItem(val name: String, val hex: String)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_day_type_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val database = CalendarDatabase.getDatabase(requireContext())
        repository = CalendarRepository(database.eventDao(), database.eventTypeDao())
        
        // Récupérer l'ID pour l'édition
        editingDayTypeId = arguments?.getLong("dayTypeId")?.takeIf { it != 0L }
        
        initializeViews(view)
        setupColorPalette()
        setupClickListeners()
        
        if (editingDayTypeId != null) {
            loadDayTypeForEditing()
        }
    }

    private fun initializeViews(view: View) {
        etName = view.findViewById(R.id.etName)
        etDescription = view.findViewById(R.id.etDescription)
        colorPreview = view.findViewById(R.id.colorPreview)
        tvSelectedColor = view.findViewById(R.id.tvSelectedColor)
        rvColorPalette = view.findViewById(R.id.rvColorPalette)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnBack = view.findViewById(R.id.btnBack)
        tvTitle = view.findViewById(R.id.tvTitle)
        
        // Mettre à jour le titre
        tvTitle.text = if (editingDayTypeId != null) "Modifier le type" else "Nouveau type de journée"
    }

    private fun setupColorPalette() {
        colorPaletteAdapter = ColorPaletteAdapter(availableColors) { color ->
            selectedColor = color.hex
            updateColorPreview()
        }
        
        rvColorPalette.apply {
            layoutManager = GridLayoutManager(requireContext(), 6)
            adapter = colorPaletteAdapter
        }
        
        updateColorPreview()
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        
        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        
        btnSave.setOnClickListener {
            saveDayType()
        }
    }

    private fun loadDayTypeForEditing() {
        editingDayTypeId?.let { id ->
            lifecycleScope.launch {
                try {
                    val dayTypes = repository.getAllEventTypes().first()
                    val dayType = dayTypes.find { it.id == id }
                    dayType?.let {
                        etName.setText(it.name)
                        etDescription.setText(it.description)
                        selectedColor = it.color
                        updateColorPreview()
                    }
                } catch (e: Exception) {
                    // Gérer l'erreur
                }
            }
        }
    }

    private fun updateColorPreview() {
        try {
            val color = android.graphics.Color.parseColor(selectedColor)
            colorPreview.backgroundTintList = 
                android.content.res.ColorStateList.valueOf(color)
            tvSelectedColor.text = selectedColor
        } catch (e: Exception) {
            // Couleur par défaut
        }
    }

    private fun saveDayType() {
        val name = etName.text?.toString()?.trim()
        val description = etDescription.text?.toString()?.trim() ?: ""
        
        if (name.isNullOrBlank()) {
            etName.error = "Le nom est requis"
            return
        }
        
        lifecycleScope.launch {
            try {
                if (editingDayTypeId != null) {
                    // Modification
                    val dayTypes = repository.getAllEventTypes().first()
                    val existingDayType = dayTypes.find { it.id == editingDayTypeId }
                    existingDayType?.let {
                        val updatedDayType = it.copy(
                            name = name,
                            description = description,
                            color = selectedColor
                        )
                        repository.updateEventType(updatedDayType)
                        Log.d("DayTypeForm", "Type de journée modifié: $name")
                    }
                } else {
                    // Création
                    val newDayType = EventType(
                        name = name,
                        description = description,
                        color = selectedColor
                    )
                    val insertedId = repository.insertEventType(newDayType)
                    Log.d("DayTypeForm", "Type de journée créé: $name avec ID: $insertedId")
                }
                
                // Afficher un message de confirmation
                Toast.makeText(requireContext(), "Type de journée enregistré", Toast.LENGTH_SHORT).show()
                
                findNavController().popBackStack()
            } catch (e: Exception) {
                Log.e("DayTypeForm", "Erreur lors de la sauvegarde", e)
                Toast.makeText(requireContext(), "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show()
                // Gérer l'erreur
            }
        }
    }
}