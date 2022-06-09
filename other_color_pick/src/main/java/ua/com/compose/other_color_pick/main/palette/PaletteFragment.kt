package ua.com.compose.other_color_pick.main.palette

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.dialog.dialogs.DialogConfirmation
import ua.com.compose.extension.*
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.other_color_pick.R
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickFragmentPaletteBinding
import ua.com.compose.other_color_pick.di.Scope
import ua.com.compose.other_color_pick.main.EColorType

class PaletteFragment : BaseMvvmFragment() {

    companion object {
        fun newInstance(): PaletteFragment {
            return PaletteFragment()
        }
    }

    private val btnSwitch = BottomMenu(iconResId = R.drawable.ic_format_color){
        viewModule.changeColorType()
    }

    private val btnClearAll = BottomMenu(iconResId = R.drawable.ic_remove_all) {
        if((binding?.lstPalette?.adapter?.itemCount ?: 0) > 0) {
            val request = DialogConfirmation.show(fm = this.childFragmentManager, message = requireContext().getString(R.string.module_other_color_pick_remove_all))
            this.childFragmentManager.setFragmentResultListener(request, viewLifecycleOwner) { _, bundle ->
                if(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER)){
                    viewModule.pressRemoveAll()
                }
            }
        } else {
            showAlert(R.string.module_other_color_pick_empty)
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnSwitch)
            this.add(btnClearAll)
        }
    }

    private var binding: ModuleOtherColorPickFragmentPaletteBinding? = null

    private val viewModule: PaletteViewModule by lazy {
        Scope.COLOR_PICK.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ModuleOtherColorPickFragmentPaletteBinding.inflate(inflater)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initPallete() {
        PaletteRvAdapter(
            onPressCopy = { color ->
                requireContext().clipboardCopy(color)
                showAlert(R.string.module_other_color_pick_color_copy)
            },
            onPressRemove = {
                viewModule.pressColorRemove(id = it)
                (binding?.lstPalette?.adapter as? PaletteRvAdapter)?.removeColor(id = it)
            }
        ).apply {
            binding?.lstPalette?.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding?.lstPalette?.adapter = this
            binding?.lstPalette?.setHasFixedSize(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPallete()

        viewModule.paletteColors.nonNull().observe(this) {
            (binding?.lstPalette?.adapter as? PaletteRvAdapter)?.update(it ?: listOf())
            binding?.lstPalette?.runLayoutAnimation(anim = R.anim.layout_animation_fall_down)
        }

        viewModule.placeholderState.nonNull().observe(this) {
            binding?.imgPlaceholder?.isVisible = it
        }

        viewModule.colorType.nonNull().observe(this) {
            (binding?.lstPalette?.adapter as? PaletteRvAdapter)?.changeColorType(it ?: EColorType.HEX)
        }

        viewModule.onCreate()
    }
}

