package com.example.sights.presentation.sightList

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.example.sights.databinding.ItemSightBinding
import com.example.sights.entity.Sight

// Адаптер для списка достопримечательностей
class SightListAdapter(
    private val formatter: java.text.DateFormat
) : ListAdapter<Sight, SightViewHolder>(SightDiffUtilCallback()) {

    // Создаём элемент списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SightViewHolder {
        val binding = ItemSightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SightViewHolder(binding)
    }

    // Заполняем элемент списка
    override fun onBindViewHolder(holder: SightViewHolder, position: Int) {
        val sight: Sight = getItem(position) ?: return

        with(holder.binding) {
            datePhoto.text = formatter.format(sight.datePhoto)
            Log.d("MyErrors", "Load from ${Uri.parse(sight.uriPhoto)}")
            Glide.with(holder.itemView.context)
                .load(Uri.parse(sight.uriPhoto))
                .into(sightPicture)
        }
    }
}