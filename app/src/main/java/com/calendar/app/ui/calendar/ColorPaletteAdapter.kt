package com.calendar.app.ui.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R

class ColorPaletteAdapter(
    private val colors: List<DayTypeFormFragment.ColorItem>,
    private val onColorSelected: (DayTypeFormFragment.ColorItem) -> Unit
) : RecyclerView.Adapter<ColorPaletteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color_palette, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount(): Int = colors.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorCircle: View = itemView.findViewById(R.id.colorCircle)
        private val tvColorName: TextView = itemView.findViewById(R.id.tvColorName)

        fun bind(colorItem: DayTypeFormFragment.ColorItem) {
            tvColorName.text = colorItem.name
            
            try {
                val color = Color.parseColor(colorItem.hex)
                colorCircle.backgroundTintList = 
                    android.content.res.ColorStateList.valueOf(color)
            } catch (e: Exception) {
                // Couleur par défaut
            }

            itemView.setOnClickListener {
                onColorSelected(colorItem)
            }
        }
    }
}