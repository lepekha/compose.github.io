package ua.com.compose.fragments.camera

import android.Manifest
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.hardware.Camera
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.dsl.extension.requestPermissions
import org.koin.android.ext.android.get
import org.koin.androidx.scope.requireScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import ua.com.compose.MainActivity
import ua.com.compose.R
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.customView.CameraColorPickerPreview
import ua.com.compose.customView.Cameras
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
import ua.com.compose.fragments.ColorPickViewModule
import ua.com.compose.fragments.info.ColorInfoFragment
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.mvp.data.viewBindingWithBinder


class CameraFragment : BaseMvvmFragment(layoutId = R.layout.module_other_color_pick_fragment_camera), CameraColorPickerPreview.OnColorSelectedListener {

    companion object {
        private fun getCameraInstance(): Camera? {
            var c: Camera? = null
            try {
                c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
            } catch (e: Exception) {
            }
            return c
        }

        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }

    private var mCamera: Camera? = null
    private var mCameraAsyncTask: CameraAsyncTask? = null
    private var mCameraPreview: CameraColorPickerPreview? = null
    var mPreviewContainer: FrameLayout? = null

    private val btnPaletteAdd = BottomMenu(iconResId = R.drawable.ic_add_circle){
        viewModule.pressPaletteAdd()
        requireActivity().createReview()
        requireContext().showToast(R.string.module_other_color_pick_color_add_to_pallete)
    }

    private val btnCopy = BottomMenu(iconResId = R.drawable.ic_copy){
        binding.textView.text?.toString()?.let { color ->
            analytics.send(SimpleEvent(key = Analytics.Event.COLOR_COPY))
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
        mPreviewContainer = binding.previewContainer

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
                        binding.container.doOnLayout { cameraStart() }
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

    override fun onResume() {
        super.onResume()
        if(requireContext().hasPermission(permission = Manifest.permission.CAMERA)) {
            binding.container.doOnLayout { cameraStart() }
        }
    }

    override fun onPause() {
        super.onPause()
        cameraStop()
    }

    private fun cameraStart() {
        mCameraAsyncTask = CameraAsyncTask()
        mCameraAsyncTask?.execute()
    }

    private fun cameraStop() {
        mCameraAsyncTask?.cancel(true)

        if (mCamera != null) {
            mCamera?.stopPreview()
            mCamera?.setPreviewCallback(null)
            mCamera?.release()
            mCamera = null
        }

        if (mCameraPreview != null) {
            binding.previewContainer.removeView(mCameraPreview)
        }
    }


    override fun onColorSelected(color: Int) {
        viewModule.changeColor(color)
    }

    // class CameraAsyncTask
    inner class CameraAsyncTask : AsyncTask<Void?, Void?, Camera?>() {
        /**
         * The [ViewGroup.LayoutParams] used for adding the preview to its container.
         */
         var mPreviewParams: FrameLayout.LayoutParams? = null
         override fun doInBackground(vararg params: Void?): Camera? {
            val camera: Camera? = getCameraInstance()


                //configure Camera parameters
                val cameraParameters = camera?.parameters

                //get optimal camera preview size according to the layout used to display it
                val bestSize: Camera.Size = Cameras.getBestPreviewSize(
                    cameraParameters?.supportedPreviewSizes,
                    mPreviewContainer?.getWidth() ?: 0,
                    mPreviewContainer?.getHeight()?: 0,
                    true
                )
                //set optimal camera preview
             val focusModes: List<String> = cameraParameters?.getSupportedFocusModes() ?: listOf()
             if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
                 cameraParameters?.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
             } else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
                     cameraParameters?.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                 }

                cameraParameters?.setPreviewSize(bestSize.width, bestSize.height)
                camera?.parameters = cameraParameters

                //set camera orientation to match with current device orientation
                Cameras.setCameraDisplayOrientation(requireContext(), camera)

                //get proportional dimension for the layout used to display preview according to the preview size used
                val adaptedDimension: IntArray = Cameras.getProportionalDimension(
                    bestSize,
                    mPreviewContainer?.getWidth() ?: 0,
                    mPreviewContainer?.getHeight() ?: 0,
                    true
                )

                //set up params for the layout used to display the preview
                mPreviewParams = FrameLayout.LayoutParams(adaptedDimension[0], adaptedDimension[1])
                mPreviewParams?.gravity = Gravity.CENTER
            return camera
        }

        override fun onPostExecute(result: Camera?) {
            super.onPostExecute(result)

            if (!isCancelled()) {
                mCamera = result
                //set up camera preview
                mCameraPreview = CameraColorPickerPreview(requireContext(), mCamera)
                mCameraPreview?.setOnColorSelectedListener(this@CameraFragment)

                //add camera preview
                mPreviewContainer?.addView(mCameraPreview, 0, mPreviewParams)
                binding.cardView.setMarginBottom(requireContext().navigationBarHeight() + 55.dp.toInt() + 8.dp.toInt())
                binding.cardView.isVisible = true
                binding.pointerRing.isVisible = true
                binding.pointerRing2.isVisible = true
                binding.previewContainer.isInvisible = false
                binding.activityMainPointer.isVisible = true
                binding.placeholder.isVisible = false
                (activity as BaseMvpView).setupBottomMenu(mutableListOf(btnCopy, btnPaletteAdd))
            }
        }

        override fun onCancelled(camera: Camera?) {
            super.onCancelled(camera)
            camera?.release()
        }
    }
}

