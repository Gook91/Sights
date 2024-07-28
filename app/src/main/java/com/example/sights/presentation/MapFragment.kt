package com.example.sights.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.sights.App
import com.example.sights.R
import com.example.sights.databinding.FragmentMapBinding
import com.example.sights.presentation.viewModels.MapViewModel
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // Получаем Вью-модель с помощью фабрики из компонента Dagger
    private val viewModel: MapViewModel by viewModels {
        (requireContext().applicationContext as App).appComponent.mapViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Прячем layout с запросом разрешений по кнопке пропустить
        binding.missPermissionButton.setOnClickListener {
            binding.layoutGpsDenied.visibility = View.GONE
        }

        // Указываем для карт место хранения кэша
        requireContext().run {
            Configuration.getInstance().load(
                this,
                getSharedPreferences(CACHE_OF_MAP, MODE_PRIVATE)
            )
        }
        // Назначаем источник для карт
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        // Отключаем встроенные кнопки масштаба, чтобы использовать свои
        binding.mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        // Включаем масштабирование щипком
        binding.mapView.setMultiTouchControls(true)
        // Устанавливаем масштаб и местоположение по умолчанию
        binding.mapView.controller.setZoom(DEFAULT_ZOOM_VALUE)
        binding.mapView.controller.setCenter(GeoPoint(DEFAULT_LAT_VALUE, DEFAULT_LON_VALUE))

        // Назначаем свои кнопки масштаба
        binding.buttonZoomIn.setOnClickListener { binding.mapView.controller.zoomIn() }
        binding.buttonZoomOut.setOnClickListener { binding.mapView.controller.zoomOut() }

        // Слушатель для карты, следящий за изменением положения - используется отложенный слушатель,
        // который начинает выполнение спустя определённое время после последнего изменения
        binding.mapView.addMapListener(
            DelayedMapListener(
                object : MapListener {
                    override fun onScroll(event: ScrollEvent?): Boolean = updateSightsOnMap()

                    override fun onZoom(event: ZoomEvent?): Boolean = updateSightsOnMap()

                    // Обновляем список достопримечательностей по текущим координатам карты
                    private fun updateSightsOnMap(): Boolean {
                        viewModel.getSightByCoordinates(
                            binding.mapView.boundingBox.lonWest,
                            binding.mapView.boundingBox.lonEast,
                            binding.mapView.boundingBox.latSouth,
                            binding.mapView.boundingBox.latNorth
                        )
                        // Прячем название достопримечательности, если оно показывалось
                        binding.sightName.visibility = View.GONE
                        return true
                    }
                },
                LISTENER_DELAY_TIME
            )
        )

        // Объявляем слой с достопримечательностями для карты
        var sightsOverlay: SimpleFastPointOverlay? = null

        // Получаем данные на карту из потока
        viewModel.sightsOnMapFlow.onEach { list ->

            // Если слой с достопримечательностями ранее был создан, то удаляем его с карты
            sightsOverlay?.let {
                binding.mapView.overlays.remove(it)
            }
            // Преобразуем данные достопримечательностей в список GeoPoint для отображения на карте
            val points: List<IGeoPoint> = list.map {
                LabelledGeoPoint(
                    it.point.lat,
                    it.point.lon,
                    it.name
                )
            }
            // Добавляем маркеры в тему
            val pointTheme = SimplePointTheme(points, false)

            // Настраиваем отображение маркеров
            val overlayPointOptions = SimpleFastPointOverlayOptions.getDefaultStyle()
                // Назначаем алгоритм отрисовки - максимальная оптимизация
                .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                // В качестве символов используем круг и устанавливаем его свойства
                .setSymbol(SimpleFastPointOverlayOptions.Shape.CIRCLE)
                .setRadius(24f)
                .setPointStyle(
                    Paint().also { it.color = getColor(resources, R.color.purple_500, null) }
                )
                .setIsClickable(true)

            // Создаём новый слой с достопримечательностями из темы и настроек
            sightsOverlay = SimpleFastPointOverlay(pointTheme, overlayPointOptions)

            // Показываем название объекта
            sightsOverlay?.setOnClickListener { innerPoints, selectedPoint ->
                val marker = innerPoints.get(selectedPoint) as LabelledGeoPoint
                binding.sightName.apply {
                    text = marker.label
                    visibility = View.VISIBLE
                }
            }

            // Подключаем слой с достопримечательностями
            binding.mapView.overlays.add(sightsOverlay)

        }.launchIn(lifecycleScope)

        // Показываем индикатор при загрузке
        viewModel.isLoading.onEach {
            binding.loadProgressBar.visibility = if (it) View.VISIBLE else View.GONE
        }.launchIn(lifecycleScope)

        // Проверяем разрешения
        checkPermissions()

    }

    // Лаунчер для запроса разрешений
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val isGrantedResult = it.values.all { entry -> entry }
        if (isGrantedResult)
            locationGranted()
        else
            binding.layoutGpsDenied.visibility = View.VISIBLE
    }

    // Проверяем разрешения
    private fun checkPermissions() {
        // Выданы ли уже разрешения?
        val isPermissionsGranted: Boolean = REQUESTED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        // Выводились ли уже пользователю запросы разрешений
        val isShowPermissionRationale = REQUESTED_PERMISSIONS.all { permission ->
            shouldShowRequestPermissionRationale(permission)
        }
        when {
            // Если разрешения даны запускаем камеру
            isPermissionsGranted -> locationGranted()
            // Если разрешения не даны, но показывались, то показываем layout с запросом разрешений
            isShowPermissionRationale -> binding.layoutGpsDenied.visibility = View.VISIBLE
            // Если разрешения не даны и не показывались, то запрашиваем разрешения
            else ->
                requestPermissionLauncher.launch(REQUESTED_PERMISSIONS)
        }
    }

    // Fused клиент для получения местоположения
    private val fusedClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val locationCancellationToken = CancellationTokenSource()

    // Если разрешения даны, то выполняем следующие действия
    @SuppressLint("MissingPermission")
    private fun locationGranted() {
        // Прячем layout с запросом разрешений
        binding.layoutGpsDenied.visibility = View.GONE

        // Показываем кнопку текущего местоположения и назначаем ей действие
        binding.buttonCurrentLocation.visibility = View.VISIBLE
        binding.buttonCurrentLocation.setOnClickListener {
            // Получаем текущее местоположение и центрируем по нему карту
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                locationCancellationToken.token
            ).addOnCompleteListener {
                binding.mapView.controller.setCenter(GeoPoint(it.result))
            }
        }

        // Подключаем слой с отображением текущего местоположения
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), binding.mapView)
        locationOverlay.enableMyLocation()

        // Подключаем слой к карте и переходим к текущей позиции
        binding.mapView.overlays.add(locationOverlay)
        locationOverlay.enableFollowLocation()
    }

    // Запускаем встроенное отслеживание местоположения в соответствии с документацией Osmdroid
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    // Приостанавливаем встроенное отслеживание местоположения в соответствии с документацией Osmdroid
    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        // Отменяем токен получения местоположения
        locationCancellationToken.cancel()
    }

    companion object {
        // Ключ для хранящихся карт
        private const val CACHE_OF_MAP = "CacheOfMap"

        // Параметры карты по умолчанию
        private const val DEFAULT_ZOOM_VALUE = 16.5
        private const val DEFAULT_LAT_VALUE = 55.75347
        private const val DEFAULT_LON_VALUE = 37.62209

        // Список разрешений для местоположения
        private val REQUESTED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        // Время после последнего обновления карты, когда срабатывает слушатель карты
        private const val LISTENER_DELAY_TIME = 500L
    }
}