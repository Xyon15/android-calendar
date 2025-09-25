package com.calendar.app.ui.calendar

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R
import com.calendar.app.data.model.EventType

class DayTypeSelectionAdapter(
    private var dayTypes: List<EventType>,
    private val onTypeSelected: (EventType) -> Unit
) : RecyclerView.Adapter<DayTypeSelectionAdapter.DayTypeViewHolder>() {

    private var selectedTypeId: Long? = null

    class DayTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorIndicator: View = itemView.findViewById(R.id.colorIndicator)
        val tvTypeName: TextView = itemView.findViewById(R.id.tvTypeName)
        val tvTypeDescription: TextView = itemView.findViewById(R.id.tvTypeDescription)
        val selectionIndicator: ImageView = itemView.findViewById(R.id.selectionIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayTypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_type_selection, parent, false)
        return DayTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayTypeViewHolder, position: Int) {
        val dayType = dayTypes[position]
        
        holder.tvTypeName.text = dayType.name
        holder.tvTypeDescription.text = dayType.description
        
        // Définir la couleur de l'indicateur
        try {
            val color = Color.parseColor(dayType.color)
            val drawable = holder.colorIndicator.background as? GradientDrawable
            drawable?.setColor(color)
        } catch (e: Exception) {
            // Couleur par défaut si parsing échoue
            val drawable = holder.colorIndicator.background as? GradientDrawable
            drawable?.setColor(Color.parseColor("#3498DB"))
        }
        
        // État de l'indicateur de sélection
        holder.selectionIndicator.isSelected = selectedTypeId == dayType.id
        
        holder.itemView.setOnClickListener {
            val previousSelectedId = selectedTypeId
            selectedTypeId = dayType.id
            
            // Rafraîchir les items concernés
            if (previousSelectedId != null) {
                val previousIndex = dayTypes.indexOfFirst { it.id == previousSelectedId }
                if (previousIndex != -1) {
                    notifyItemChanged(previousIndex)
                }
            }
            notifyItemChanged(position)
            
            onTypeSelected(dayType)
        }
    }

    override fun getItemCount() = dayTypes.size

    fun updateDayTypes(newDayTypes: List<EventType>) {
        dayTypes = newDayTypes
        notifyDataSetChanged()
    }

    fun getSelectedDayType(): EventType? {
        return dayTypes.find { it.id == selectedTypeId }
    }

    fun clearSelection() {
        selectedTypeId = null
        notifyDataSetChanged()
    }
}