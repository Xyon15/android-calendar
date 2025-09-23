package com.calendar.app.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.calendar.app.R
import com.calendar.app.data.model.Event

class AppointmentsAdapter(
    private val onItemClick: (Event) -> Unit,
    private val onEditClick: (Event) -> Unit
) : ListAdapter<Event, AppointmentsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tvAppointmentTime)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvAppointmentTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvAppointmentDescription)
        private val btnEdit: View = itemView.findViewById(R.id.btnMoreOptions)

        fun bind(event: Event) {
            // Gérer l'affichage de l'heure
            if (event.startTime != null && event.startTime.isNotEmpty()) {
                tvTime.text = event.startTime
                tvTime.visibility = View.VISIBLE
                tvTime.parent?.let { parent ->
                    (parent as View).visibility = View.VISIBLE
                }
            } else {
                tvTime.text = "Toute la journée"
                tvTime.visibility = View.VISIBLE
                tvTime.parent?.let { parent ->
                    (parent as View).visibility = View.VISIBLE
                }
            }
            
            tvTitle.text = event.title
            
            if (event.description.isNotEmpty()) {
                tvDescription.text = event.description
                tvDescription.visibility = View.VISIBLE
            } else {
                tvDescription.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onItemClick(event)
            }
            
            btnEdit.setOnClickListener {
                onEditClick(event)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}