package com.example.sights.presentation

import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.example.sights.App
import com.example.sights.R
import com.example.sights.databinding.FragmentListBinding
import com.example.sights.presentation.sightList.HeaderSightAdapter
import com.example.sights.presentation.sightList.SightListAdapter
import com.example.sights.presentation.viewModels.MainViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    // Получаем Вью-модель с помощью фабрики из компонента Dagger
    private val viewModel: MainViewModel by viewModels {
        (requireContext().applicationContext as App).appComponent.mainViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Создаём адаптер заголовка списка
        val headerSightAdapter = HeaderSightAdapter {
            // Исключение для проверки Crashlytics
            // throw Exception("Test crashlytics exception")
            findNavController().navigate(R.id.action_listFragment_to_takePhotoFragment)
        }
        // Создаём адаптер списка
        val mainSightAdapter = SightListAdapter(DateFormat.getDateFormat(requireContext()))

        // Получаем данные из потока и отправляем их в главный адаптер списка
        viewModel.allSightsFlow.onEach {
            mainSightAdapter.submitList(it)
        }.launchIn(lifecycleScope)

        // Объединяем адаптеры и назначаем их во вью списка
        binding.sightList.adapter = ConcatAdapter(headerSightAdapter, mainSightAdapter)

        // При нажатии на FAB переходим к карте
        binding.goToMapButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_mapFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}