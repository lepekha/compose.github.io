package ua.com.compose.fragments.camera

import android.Manifest
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.dsl.extension.requestPermissions
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.android.get
import org.koin.androidx.scope.requireScopeActivity
import ua.com.compose.MainActivity
import ua.com.compose.R
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.databinding.ModuleOtherColorPickFragmentCameraBinding
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.extension.createReview
import ua.com.compose.extension.dp
import ua.com.compose.extension.hasPermission
import ua.com.compose.extension.navigationBarHeight
import ua.com.compose.extension.nonNull
import ua.com.compose.extension.setMarginBottom
import ua.com.compose.extension.setVibrate
import ua.com.compose.extension.showToast
import ua.com.compose.extension.throttleLatest
import ua.com.compose.fragments.ColorPickViewModule
import ua.com.compose.fragments.info.ColorInfoFragment
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.mvp.data.viewBindingWithBinder
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : BaseMvvmFragment(layoutId = R.layout.module_other_color_pick_fragment_camera) {
    companion object {
        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private val btnPaletteAdd = BottomMenu(iconResId = R.drawable.ic_add_circle){
        viewModule.pressPaletteAdd()
        requireActivity().createReview()
        requireContext().showToast(R.string.module_other_color_pick_color_add_to_pallete)
    }

    private val btnCopy = BottomMenu(iconResId = R.drawable.ic_copy){
        binding.textView.text?.toString()?.let { color ->
            analytics.send(SimpleEvent(key = Analytics.Event.COLOR_COPY_CAMERA))
            requireContext().clipboardCopy(color)
            requireContext().showToast(R.string.module_other_color_pick_color_copy)
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>()
    }

    private val binding by viewBindingWithBinder(ModuleOtherColorPickFragmentCameraBinding::bind)

    private val viewModule: CameraViewModule by lazy {
        requireScopeActivity<MainActivity>().get()
    }

    private val mainModule: ColorPickViewModule by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        viewModule.nameColor.nonNull().observe(viewLifecycleOwner) { name ->
            binding.txtName.text = name
        }

        mainModule.colorType.nonNull().observe(viewLifecycleOwner) { type ->
            viewModule.updateColor()
        }

        viewModule.changeColor.nonNull().observe(viewLifecycleOwner) { color ->
            binding.textView.text = mainModule.colorType.value?.convertColor(color, withSeparator = ",") ?: ""
            binding.textView.setTextColor( if (isDark(color)) Color.WHITE else Color.BLACK)
            binding.txtName.setTextColor( if (isDark(color)) Color.WHITE else Color.BLACK)
            binding.cardColor.setBackgroundColor(color)
            binding.imgInfo.imageTintList = ColorStateList.valueOf(if (isDark(color)) Color.WHITE else Color.BLACK)
            binding.pointerRing.background?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            binding.activityMainPointer.background?.setColorFilter(if(isDark(color)) Color.WHITE else Color.BLACK, PorterDuff.Mode.SRC_ATOP)
            binding.pointerRing2.background?.setColorFilter(if(isDark(color)) Color.WHITE else Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        }

        binding.cardView.setVibrate(EVibrate.BUTTON)
        binding.cardView.setOnClickListener {
            ColorInfoFragment.show(childFragmentManager, color = viewModule.color)
        }

        if(!requireContext().hasPermission(permission = Manifest.permission.CAMERA)) {
            binding.placeholder.isVisible = true
            binding.placeholder.setVibrate(EVibrate.BUTTON)
            binding.imgPlaceholder.setImageResource(R.drawable.module_other_text_style_fragment_text_style_ic_open)
            binding.placeholder.setOnClickListener {
                checkPermission()
            }
            checkPermission()
        } else {
            binding.container.doOnLayout { startCamera() }
        }
    }

    private fun isDark(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.5
    }

    private fun checkPermission() {
        requestPermissions(
            Manifest.permission.CAMERA
        ){
            requestCode = 4
            resultCallback = {
                when(this) {
                    is PermissionResult.PermissionGranted -> {
                        binding.container.doOnLayout { startCamera() }
                    }
                    is PermissionResult.PermissionDenied -> {
                    }
                    is PermissionResult.PermissionDeniedPermanently -> {
                    }
                    is PermissionResult.ShowRational -> {
                        //If user denied permission frequently then she/he is not clear about why you are asking this permission.
                        //This is your chance to explain them why you need permission.
                    }
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ColorAnalyzer { color ->
                        viewModule.changeColor(color = color)
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                binding.cardView.setMarginBottom(requireContext().navigationBarHeight() + 55.dp.toInt() + 8.dp.toInt())
                binding.cardView.isVisible = true
                binding.pointerRing.isVisible = true
                binding.pointerRing2.isVisible = true
                binding.previewContainer.isInvisible = false
                binding.activityMainPointer.isVisible = true
                binding.placeholder.isVisible = false
                (activity as BaseMvpView).setupBottomMenu(mutableListOf(btnCopy, btnPaletteAdd))
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private class ColorAnalyzer(private val listener: (value: Int) -> Unit) : ImageAnalysis.Analyzer {
        private var lastTotalRed = 0
        private var lastTotalGreen = 0
        private var lastTotalBlue = 0

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()
            val data = ByteArray(remaining())
            get(data)
            return data
        }

        override fun analyze(image: ImageProxy) {
            val imageWidth = image.width
            val imageHeight = image.height

            // Задати розмір області центру
            val centerSize = 2

            // Знайти початкові координати області центру
            val centerX = imageWidth / 2 - centerSize / 2
            val centerY = imageHeight / 2 - centerSize / 2

            // Отримати пікселі зображення з ImageProxy
            val buffer = image.planes[0].buffer
            val pixels = buffer.toByteArray()

            // Зберігати суми каналів кольорів для всіх пікселів у центральній області
            var totalRed = 0
            var totalGreen = 0
            var totalBlue = 0

            // Перебрати всі пікселі у центральній області і додати значення каналів кольору до сум
            for (y in centerY until centerY + centerSize) {
                for (x in centerX until centerX + centerSize) {
                    val pixelOffset = (y * imageWidth + x) * 4
                    val red = pixels[pixelOffset].toInt() and 0xFF
                    val green = pixels[pixelOffset + 1].toInt() and 0xFF
                    val blue = pixels[pixelOffset + 2].toInt() and 0xFF

                    totalRed += red
                    totalGreen += green
                    totalBlue += blue
                }
            }

            // Обчислити середнє значення кольору для червоного, зеленого та синього каналів
            val averageRed = totalRed / (centerSize * centerSize)
            val averageGreen = totalGreen / (centerSize * centerSize)
            val averageBlue = totalBlue / (centerSize * centerSize)

            if(Math.abs(averageRed - lastTotalRed) > 3 || Math.abs(averageBlue - lastTotalBlue) > 3 || Math.abs(averageGreen - lastTotalGreen) > 3) {
                lastTotalRed = averageRed
                lastTotalGreen = averageGreen
                lastTotalBlue = averageBlue
            }
            // Створити колір із середніми значеннями каналів RGB
            val averageColor = Color.rgb(lastTotalRed, lastTotalGreen, lastTotalBlue)
            listener(averageColor)
            image.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

