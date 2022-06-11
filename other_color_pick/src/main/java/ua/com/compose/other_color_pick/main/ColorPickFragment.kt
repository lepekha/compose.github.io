package ua.com.compose.other_color_pick.main

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.dsl.extension.requestPermissions
import io.fotoapparat.Fotoapparat
import org.koin.androidx.viewmodel.ext.android.viewModel
import ua.com.compose.extension.*
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.navigator.replace
import ua.com.compose.other_color_pick.R
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickFragmentMainBinding
import ua.com.compose.other_color_pick.di.Scope
import ua.com.compose.other_color_pick.main.camera.CameraFragment
import ua.com.compose.other_color_pick.main.image.ImageFragment
import ua.com.compose.other_color_pick.main.palette.PaletteFragment
import ua.com.compose.other_color_pick.view.CameraColorPickerPreview
import ua.com.compose.other_color_pick.view.Cameras
import ua.com.compose.views.SliderBox
import ua.com.compose.views.SliderBoxElement
import java.lang.ref.WeakReference


class ColorPickFragment : BaseMvvmFragment() {

    companion object {

        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): ColorPickFragment {
            return ColorPickFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    private var binding: ModuleOtherColorPickFragmentMainBinding? = null

    private val viewModule: ImageInfoViewModule by lazy {
        Scope.COLOR_PICK.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ModuleOtherColorPickFragmentMainBinding.inflate(inflater)
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

        binding?.btnCamera?.setVibrate(EVibrate.BUTTON)
        binding?.btnCamera?.setOnClickListener {
            selectScreen(binding?.tabCamera)
        }

        binding?.btnImage?.setVibrate(EVibrate.BUTTON)
        binding?.btnImage?.setOnClickListener {
            selectScreen(binding?.tabImage)
        }

        binding?.btnPalette?.setVibrate(EVibrate.BUTTON)
        binding?.btnPalette?.setOnClickListener {
            selectScreen(binding?.tabPalette)
        }

        requestPermissions(
            Manifest.permission.CAMERA
        ){
            requestCode = 4
            resultCallback = {
                when(this) {
                    is PermissionResult.PermissionGranted -> {
                        if((arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri) != null){
                            selectScreen(binding?.tabImage)
                        }else{
                            selectScreen(binding?.tabCamera)
                        }
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

    private var prevTab: WeakReference<View>? = null
    private fun selectScreen(tabView: View?) {
        prevTab?.get()?.toggle()
        prevTab = WeakReference(tabView)
        tabView?.toggle()

        when(tabView?.id) {
            binding?.tabCamera?.id -> {
                childFragmentManager.replace(CameraFragment.newInstance(), binding?.content?.id ?: -1, addToBackStack = false)
            }
            binding?.tabImage?.id  -> {
                childFragmentManager.replace(ImageFragment.newInstance(arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri), binding?.content?.id ?: -1, addToBackStack = false)
            }
            binding?.tabPalette?.id  -> {
                childFragmentManager.replace(PaletteFragment.newInstance(), binding?.content?.id ?: -1, addToBackStack = false)
            }
        }
    }

    override fun backPress(byBack: Boolean): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.COLOR_PICK.close()
    }
}

