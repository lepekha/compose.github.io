package ua.com.compose.other_color_pick.main

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.hardware.Camera
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fotoapparat.Fotoapparat
import org.koin.androidx.viewmodel.ext.android.viewModel
import ua.com.compose.extension.*
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.other_color_pick.R
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickFragmentMainBinding
import ua.com.compose.other_color_pick.view.CameraColorPickerPreview
import ua.com.compose.other_color_pick.view.Cameras
import ua.com.compose.views.SliderBox
import ua.com.compose.views.SliderBoxElement


class ColorPickFragment : BaseMvvmFragment(), CameraColorPickerPreview.OnColorSelectedListener {

    companion object {

        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"
        private const val REQUEST_KEY = "REQUEST_KEY"

        private fun getCameraInstance(): Camera? {
            var c: Camera? = null
            try {
                c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
            } catch (e: Exception) {
            }
            return c
        }

        fun newInstance(uri: Uri?): ColorPickFragment {
            return ColorPickFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
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

    private val btnGallery = BottomMenu(iconResId = ua.com.compose.R.drawable.ic_gallery){
    }

    private val btnPaletteAdd = BottomMenu(iconResId = R.drawable.ic_palette_add){
        viewModule.pressPaletteAdd()
    }

    private val btnSwitch = BottomMenu(iconResId = ua.com.compose.R.drawable.ic_switch){
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnSwitch)
            this.add(btnCopy)
            this.add(btnPaletteAdd)
        }
    }

    private var binding: ModuleOtherColorPickFragmentMainBinding? = null

    private val viewModule: ImageInfoViewModule by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ModuleOtherColorPickFragmentMainBinding.inflate(inflater)
        mPreviewContainer = binding?.previewContainer
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initPallete() {
        PaletteRvAdapter {
//            requireContext().clipboardCopy(text = it)
//            showAlert(R.string.module_other_text_style_fragment_text_copied)
        }.apply {
            binding?.lstPalette?.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding?.lstPalette?.adapter = this
            binding?.lstPalette?.setHasFixedSize(true)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(requireContext().getString(R.string.module_other_color_pick_fragment_title))
        setVisibleBack(true)
        initPallete()

        val slider = mutableListOf(
            SliderBoxElement(0, ContextCompat.getDrawable(requireContext(), R.drawable.ic_camera)!!),
            SliderBoxElement(1, ContextCompat.getDrawable(requireContext(), R.drawable.ic_image)!!),
            SliderBoxElement(2, ContextCompat.getDrawable(requireContext(), R.drawable.ic_palette)!!)
        )

        binding?.slider?.setItems(slider)
        binding?.slider?.setSelected(0)
        binding?.contentCamera?.isVisible = true

        binding?.slider?.setOnSelectListener(object : SliderBox.OnSelectListener {
            override fun onSelect(position: Int) {
                binding?.contentCamera?.isVisible = false
                binding?.contentImage?.isVisible = false
                binding?.contentPalette?.isVisible = false

                when(position) {
                    0 -> {
                        binding?.contentCamera?.isVisible = true
                        cameraStart()
                    }
                    1 -> {
                        binding?.contentImage?.isVisible = true
                        cameraStop()
                    }
                    2 -> {
                        binding?.contentPalette?.isVisible = true
                        viewModule.pressPalette()
                        cameraStop()
                    }
                }
            }
        })

        viewModule.mainImage.nonNull().observe(this) { bm ->
            binding?.imgPreview?.let {
                it.setImageBitmap(bm)
            }
        }

        viewModule.paletteColors.nonNull().observe(this) {
            (binding?.lstPalette?.adapter as? PaletteRvAdapter)?.update(it ?: listOf())
        }

        viewModule.changeColor.nonNull().observe(this) { color ->
            color?.let {
                val colorHex = Integer.toHexString(color).substring(2).toUpperCase()
                binding?.textView?.text = "#$colorHex"
                binding?.frameLayout?.setBackgroundColor(color)
                binding?.pointerRing?.background?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }

        viewModule.alert.observe(this) {
            it?.let {
                showAlert(it)
            }
        }

        viewModule.visible.observe(this){
        }

//        requestPermissions(
//            Manifest.permission.CAMERA
//        ){
//            requestCode = 4
//            resultCallback = {
//                when(this) {
//                    is PermissionResult.PermissionGranted -> {
////                        presenter.getAllShownImagesPath(getCurrentActivity())
////                        initPhotos()
//                    }
//                    is PermissionResult.PermissionDenied -> {
//                        dismiss()
//                    }
//                    is PermissionResult.PermissionDeniedPermanently -> {
//                        dismiss()
//                    }
//                    is PermissionResult.ShowRational -> {
//                        //If user denied permission frequently then she/he is not clear about why you are asking this permission.
//                        //This is your chance to explain them why you need permission.
//                    }
//                }
//            }
//        }


        val inputUri = arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri
        viewModule.onCreate(uri = inputUri)
    }

    override fun backPress(byBack: Boolean): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        cameraStart()
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
                mCameraPreview?.setOnColorSelectedListener(this@ColorPickFragment)
//                mCameraPreview?.setOnClickListener(this@ColorPickFragment)

                //add camera preview
                mPreviewContainer?.addView(mCameraPreview, 0, mPreviewParams)
            }
        }

        override fun onCancelled(camera: Camera?) {
            super.onCancelled(camera)
            camera?.release()
        }
    }
}

