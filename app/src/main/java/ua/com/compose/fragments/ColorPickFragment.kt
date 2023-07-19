package ua.com.compose.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.Event
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.ColorNames
import ua.com.compose.data.EColorType
import ua.com.compose.data.SharedPreferencesKey
import ua.com.compose.databinding.ModuleOtherColorPickFragmentMainBinding
import ua.com.compose.dialogs.DialogChip
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.get
import ua.com.compose.extension.prefs
import ua.com.compose.extension.put
import ua.com.compose.extension.replace
import ua.com.compose.extension.vibrate
import ua.com.compose.fragments.camera.CameraFragment
import ua.com.compose.fragments.image.ImageFragment
import ua.com.compose.fragments.palette.PaletteFragment
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.viewBindingWithBinder
import java.lang.ref.WeakReference


class ColorPickFragment : BaseMvvmFragment(R.layout.module_other_color_pick_fragment_main) {

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

    private val binding by viewBindingWithBinder(ModuleOtherColorPickFragmentMainBinding::bind)

    private val viewModule: ColorPickViewModule by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ColorNames.init(requireContext())
        analytics.send(Event(key = Analytics.Event.COLOR_TYPE, params = arrayOf("color_type" to Settings.colorType.title())))
        binding.btnColorType.setOnClickListener {
            analytics.send(SimpleEvent(key = Analytics.Event.OPEN_SETTINGS))
            val key = DialogChip.show(fm = childFragmentManager, list = EColorType.visibleValues().map { it.title() }, selected = Settings.colorType.title())
            childFragmentManager.setFragmentResultListener(
                    key,
                    viewLifecycleOwner
            ) { _, bundle ->
                val position = bundle.getInt(DialogChip.BUNDLE_KEY_ANSWER_POSITION, -1)
                viewModule.changeColorType(colorType = EColorType.visibleValues()[position])
            }
        }

        binding.btnCamera.isVisible = EPanel.CAMERA.isVisible()
        binding.btnCamera.setOnClickListener {
            requireContext().vibrate(EVibrate.BUTTON)
            selectScreen(binding.tabCamera)
        }

        binding.btnImage.isVisible = EPanel.IMAGE.isVisible()
        binding.btnImage.setOnClickListener {
            requireContext().vibrate(EVibrate.BUTTON)
            selectScreen(binding.tabImage)
        }

        binding.btnPalette.isVisible = EPanel.PALLETS.isVisible()
        binding.btnPalette.setOnClickListener {
            requireContext().vibrate(EVibrate.BUTTON)
            selectScreen(binding.tabPalette)
        }
        if((arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri) != null && EPanel.IMAGE.isVisible()){
            selectScreen(binding.tabImage)
        }else{
            val available = listOf(EPanel.IMAGE, EPanel.CAMERA, EPanel.PALLETS).firstOrNull { it.isVisible() } ?: return
            val panel = EPanel.valueOfKey(prefs.get(key = SharedPreferencesKey.KEY_PANEL_ID, defaultValue = available.id)).takeIf { it.isVisible() } ?: available
            val view = when(panel) {
                EPanel.CAMERA -> binding.tabCamera
                EPanel.IMAGE -> binding.tabImage
                EPanel.PALLETS -> binding.tabPalette
            }
            selectScreen(tabView = view)
        }
    }

    private var prevTab: WeakReference<View>? = null
    private fun selectScreen(tabView: View? = null) {
        prevTab?.get()?.let { it.isVisible = !it.isVisible  }
        prevTab = WeakReference(tabView)
        tabView?.let { it.isVisible = !it.isVisible }
        binding.imgCamera.setImageResource(R.drawable.ic_camera)
        binding.imgImage.setImageResource(R.drawable.ic_image)
        binding.imgPalette.setImageResource(R.drawable.ic_palette)
        when {
            tabView?.id == binding.tabCamera.id -> {
                binding.imgCamera.setImageResource(R.drawable.ic_camera_fill)
                prefs.put(key = SharedPreferencesKey.KEY_PANEL_ID, value = EPanel.CAMERA.id)
                childFragmentManager.replace(CameraFragment.newInstance(), binding.content.id , addToBackStack = false)
            }
            tabView?.id == binding.tabImage.id-> {
                binding.imgImage.setImageResource(R.drawable.ic_image_fill)
                prefs.put(key = SharedPreferencesKey.KEY_PANEL_ID, value = EPanel.IMAGE.id)
                childFragmentManager.replace(ImageFragment.newInstance(arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri), binding?.content?.id ?: -1, addToBackStack = false)
            }
            tabView?.id == binding.tabPalette.id  -> {
                binding.imgPalette.setImageResource(R.drawable.ic_palette_fill)
                prefs.put(key = SharedPreferencesKey.KEY_PANEL_ID, value = EPanel.PALLETS.id)
                childFragmentManager.replace(PaletteFragment.newInstance(), binding.content.id, addToBackStack = false)
            }
        }
    }
}

