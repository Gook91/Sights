package com.example.sights.presentation.sightList

import androidx.recyclerview.widget.DiffUtil
import com.example.sights.entity.Sight

// Сравнение элементов для листа
class SightDiffUtilCallback : DiffUtil.ItemCallback<Sight>() {
    override fun areItemsTheSame(oldItem: Sight, newItem: Sight): Boolean {
        return oldItem.uriPhoto == newItem.uriPhoto
    }

    override fun areContentsTheSame(oldItem: Sight, newItem: Sight): Boolean {
        return oldItem.uriPhoto == newItem.uriPhoto &&
                oldItem.datePhoto == newItem.datePhoto
    }
}