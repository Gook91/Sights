package com.example.sights.presentation.sightList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sights.databinding.ItemHeaderBinding

// Адаптер заголовка списка для перехода на фрагмент добавления достопримечательности
class HeaderSightAdapter(
    private val onAddClick: View.OnClickListener
) : RecyclerView.Adapter<HeaderSightViewHolder>() {

    // Возвращаем элемент заголовка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderSightViewHolder {
        val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderSightViewHolder(binding)
    }

    // Присваиваем слушатель щелчка для выполнения перехода
    override fun onBindViewHolder(holder: HeaderSightViewHolder, position: Int) {
        holder.binding.root.setOnClickListener(onAddClick)
    }

    override fun getItemCount(): Int = 1
}