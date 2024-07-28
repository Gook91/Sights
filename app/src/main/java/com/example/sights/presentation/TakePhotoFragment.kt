package com.example.sights.presentation

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sights.App
import com.example.sights.R
import com.example.sights.databinding.FragmentTakePhotoBinding
import com.example.sights.entity.Sight
import com.example.sights.presentation.viewModels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

class TakePhotoFragment : Fragment() {

    private var _binding: FragmentTakePhotoBinding? = null
    private val binding get() = _binding!!

    // Получаем Вью-модель с помощью фабрики из компонента Dagger
    private val viewModel: MainViewModel by activityViewModels {
        (requireContext().applicationContext as App).appComponent.mainViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTakePhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Проверяем разрешения
        checkPermissions()

        // Назначаем кнопке команду сделать фото
        binding.takePhotoButton.setOnClickListener { takePhoto() }
    }

    // Лаунчер для запроса разрешений
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val isGrantedResult = it.values.all { entry -> entry }
        if (isGrantedResult)
        // Если разрешения выданы, то запускаем камеру
            startCamera()
        else
        // Если разрешения не выданы, то показываем layout с запросом разрешений
            binding.layoutDenied.visibility = View.VISIBLE
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
            isPermissionsGranted ->
                startCamera()
            // Если разрешения не даны, но показывались, то показываем layout с запросом разрешений
            isShowPermissionRationale ->
                binding.layoutDenied.visibility = View.VISIBLE
            // Если разрешения не даны и не показывались, то запрашиваем разрешения
            else ->
                requestPermissionLauncher.launch(REQUESTED_PERMISSIONS)
        }
    }

    // Объявляем переменные для камеры
    private val executor: Executor by lazy { ContextCompat.getMainExecutor(requireContext()) }
    private var imageCapture: ImageCapture? = null

    // Запускаем камеру и превью
    private fun startCamera() {
        // Прячем layout с запросом разрешений
        binding.layoutDenied.visibility = View.GONE
        // Получаем доступ к камере
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        // Создаём слушателя
        cameraProviderFuture.addListener({
            // Получаем камеру
            val cameraProvider = cameraProviderFuture.get()
            // Создаём превью и привязываем его ко вью
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.sightPreviewView.surfaceProvider)
            // Создаём переменную захвата изображения
            imageCapture = ImageCapture.Builder().build()
            // Отвязываем камеру
            cameraProvider.unbind()
            // Привязываем камеру
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, executor)
    }

    // Делаем фото
    private fun takePhoto() {
        // Получаем переменную захвата изображения
        val imageCapture = imageCapture ?: return
        // Получаем время создания снимка и создаём его имя
        val currentTime: Long = Date().time
        val fileName: String = FILE_NAME_FORMATTER.format(currentTime) + "_sight"
        // Обозначаем, что это за тип файл
        val contentValues = contentValuesOf(
            MediaStore.MediaColumns.DISPLAY_NAME to fileName,
            MediaStore.MediaColumns.MIME_TYPE to "image/jpeg"
        )
        // Указываем куда сохранить изображение
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            activity?.contentResolver ?: return,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()
        // Делаем фото
        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                // В случае успешного сохранения
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Создаём новый объект достопримечательность и добавляем его через ViewModel
                    val newSight = object : Sight {
                        override val uriPhoto = outputFileResults.savedUri.toString()
                        override val datePhoto = currentTime
                    }
                    viewModel.addSight(newSight)
                    // Возвращаемся на предыдущий экран
                    findNavController().navigate(R.id.action_takePhotoFragment_to_listFragment)
                }

                // В случае ошибки выводим её в лог
                override fun onError(exception: ImageCaptureException) {
                    Log.e(LOG_TAG, "Error in save photo: $exception", exception)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOG_TAG = "LoggedErrors"

        // Список запрашиваемых разрешений
        private val REQUESTED_PERMISSIONS: Array<String> = buildList {
            add(android.Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }.toTypedArray()

        // Формат имени файла
        private val FILE_NAME_FORMATTER = SimpleDateFormat("yyyy_MM_dd_hh_mm", Locale.US)
    }
}