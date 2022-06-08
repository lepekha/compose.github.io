package ua.com.compose.other_color_pick.main.camera

import android.graphics.Color
import android.graphics.PorterDuff
import android.hardware.Camera
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import ua.com.compose.extension.*
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.other_color_pick.R
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickFragmentCameraBinding
import ua.com.compose.other_color_pick.di.Scope
import ua.com.compose.other_color_pick.view.CameraColorPickerPreview
import ua.com.compose.other_color_pick.view.Cameras


class CameraFragment : BaseMvvmFragment(), CameraColorPickerPreview.OnColorSelectedListener {

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

    private val btnCopy = BottomMenu(iconResId = ua.com.compose.R.drawable.ic_copy) {
        binding?.textView?.text?.toString()?.let { color ->
            requireContext().clipboardCopy(color)
            showAlert(R.string.module_other_color_pick_color_copy)
        }
    }

    private val btnPaletteAdd = BottomMenu(iconResId = R.drawable.ic_palette_add){
        viewModule.pressPaletteAdd()
        showAlert(R.string.module_other_color_pick_color_add_to_pallete)
    }

    private val btnSwitch = BottomMenu(iconResId = R.drawable.ic_format_color){
        viewModule.changeColorType()
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnSwitch)
            this.add(btnCopy)
            this.add(btnPaletteAdd)
        }
    }

    private var binding: ModuleOtherColorPickFragmentCameraBinding? = null

    private val viewModule: CameraViewModule by lazy {
        Scope.COLOR_PICK.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ModuleOtherColorPickFragmentCameraBinding.inflate(inflater)
        mPreviewContainer = binding?.previewContainer
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(requireContext().getString(R.string.module_other_color_pick_fragment_title))
        setVisibleBack(true)

        binding?.frameLayout?.bringToFront()

        viewModule.changeColor.nonNull().observe(this) { color ->
            color?.let {
                binding?.textView?.text = color.second
                binding?.cardView?.setCardBackgroundColor(color.first)
                binding?.pointerRing?.background?.setColorFilter(color.first, PorterDuff.Mode.SRC_ATOP)
                binding?.activityMainPointer?.background?.setColorFilter(if(isDark(color.first)) Color.WHITE else Color.BLACK, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

    private fun isDark(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.5
    }

    override fun backPress(byBack: Boolean): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        binding?.container?.post {
            cameraStart()
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
            binding?.previewContainer?.removeView(mCameraPreview)
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
                cameraParameters?.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
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
                binding?.frameLayout?.isVisible = true
            }
        }

        override fun onCancelled(camera: Camera?) {
            super.onCancelled(camera)
            camera?.release()
        }
    }
}

