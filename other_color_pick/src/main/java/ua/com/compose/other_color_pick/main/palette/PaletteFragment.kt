package ua.com.compose.other_color_pick.main.palette

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.EColorType
import ua.com.compose.dialog.dialogs.DialogChip
import ua.com.compose.dialog.dialogs.DialogColor
import ua.com.compose.dialog.dialogs.DialogConfirmation
import ua.com.compose.dialog.dialogs.DialogInput
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.extension.dp
import ua.com.compose.extension.get
import ua.com.compose.extension.navigationBarHeight
import ua.com.compose.extension.nonNull
import ua.com.compose.extension.observe
import ua.com.compose.extension.prefs
import ua.com.compose.extension.setMarginBottom
import ua.com.compose.extension.setPaddingBottom
import ua.com.compose.extension.shareFile
import ua.com.compose.mvp.BaseMvpActivity
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.mvp.data.viewBindingWithBinder
import ua.com.compose.other_color_pick.R
import ua.com.compose.other_color_pick.data.ColorPallet
import ua.com.compose.other_color_pick.data.EPaletteExportScheme
import ua.com.compose.other_color_pick.data.SharedPreferencesKey
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickFragmentPaletteBinding
import ua.com.compose.other_color_pick.di.Scope
import ua.com.compose.other_color_pick.main.ColorPickViewModule
import ua.com.compose.other_color_pick.main.defaultPaletteName
import ua.com.compose.other_color_pick.main.info.ColorInfoFragment


class PaletteFragment : BaseMvvmFragment(R.layout.module_other_color_pick_fragment_palette) {

    companion object {
        fun newInstance(): PaletteFragment {
            return PaletteFragment()
        }
    }

    private val btnAddColor = BottomMenu(iconResId = R.drawable.ic_add_circle){
        addColor()
    }

    private fun addColor() {
        val key = DialogColor.show(fm = childFragmentManager)
        childFragmentManager.setFragmentResultListener(
                key,
                viewLifecycleOwner
        ) { _, bundle ->
            val color = bundle.getInt(DialogColor.BUNDLE_KEY_ANSWER_COLOR)
            viewModule.pressAddColor(color = color)
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>()
    }

    private val binding by viewBindingWithBinder(ModuleOtherColorPickFragmentPaletteBinding::bind)

    private val viewModule: PaletteViewModule by lazy {
        Scope.COLOR_PICK.get()
    }

    private val mainModule: ColorPickViewModule by activityViewModels()

    private fun initColors() {
        ColorsRvAdapter(
            onPressCopy = { item ->
                val color = mainModule.colorType.value?.convertColor(item.color, withSeparator = ",") ?: ""
                requireContext().clipboardCopy(color)
                showAlert(R.string.module_other_color_pick_color_copy)
            },
            onPressItem = { item ->
                ColorInfoFragment.show(childFragmentManager, color = item.color)
            },
            onPressColorTune = { item ->
                val key = DialogColor.show(fm = childFragmentManager, color = item.color)
                childFragmentManager.setFragmentResultListener(
                        key,
                        viewLifecycleOwner
                ) { _, bundle ->
                    val color = bundle.getInt(DialogColor.BUNDLE_KEY_ANSWER_COLOR)
                    viewModule.pressChangeColor(id = item.id, color = color)
                }
            },
            onPressRemove = {
                viewModule.pressRemoveColor(id = it)
            }
        ).apply {
            binding.lstColors.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.lstColors.adapter = this
            binding.lstColors.setHasFixedSize(true)
        }
    }

    private fun initPallets() {
        PalletsRvAdapter(
                onPressItem = {
                    viewModule.pressPallet(id = it.id)
                },
                onPressRemove = {
                    val request = DialogConfirmation.show(fm = this.childFragmentManager, message = requireContext().getString(R.string.module_other_color_pick_remove_pallet))
                    this.childFragmentManager.setFragmentResultListener(request, viewLifecycleOwner) { _, bundle ->
                        if(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER)){
                            viewModule.pressRemovePallet(id = it.id)
                        }
                    }
                },
                onPressAddPallet = {
                    createPallet()
                },
                onPressShare = {
                    pressPaletteShare(it)
                },
                onPressChangePallet = { colorId, palletId ->
                    viewModule.pressChangePallet(colorId, palletId)
                }
        ).apply {
            binding.lstPallets.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            binding.lstPallets.adapter = this
        }
    }

    private fun pressPaletteShare(pallet: ColorPallet) {
        val key = DialogChip.show(fm = childFragmentManager, list = EPaletteExportScheme.values().map { it.name })
        childFragmentManager.setFragmentResultListener(
                key,
                viewLifecycleOwner
        ) { _, bundle ->
            val position = bundle.getInt(DialogChip.BUNDLE_KEY_ANSWER_POSITION, -1)
            viewModule.pressExport(pallet, EPaletteExportScheme.values()[position])
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPallets()
        initColors()

        childFragmentManager.setFragmentResultListener(
                ColorInfoFragment.REQUEST_KEY,
                viewLifecycleOwner
        ) { _, bundle ->
            viewModule.pressUpdatePalette()
        }

        viewModule.colors.nonNull().observe(viewLifecycleOwner) {
            (binding.lstColors.adapter as? ColorsRvAdapter)?.update(binding.lstColors, it ?: listOf())
            binding.btnAddColor.isVisible = (it?.isEmpty() ?: true) && (binding.imgPlaceholder.isVisible == false)
            if(it?.isNotEmpty() == true) {
                binding.lstColors.setPaddingBottom(requireContext().navigationBarHeight() + 55.dp.toInt() + 8.dp.toInt())
                (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnAddColor))
            } else {
                (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf())
            }
//            binding?.lstPalette?.runLayoutAnimation(anim = R.anim.layout_animation_fall_down)
        }

        viewModule.palettes.nonNull().observe(viewLifecycleOwner) {
            val pallets = mutableListOf<Card>().apply {
                this.addAll(it ?: listOf())
            }
            binding.divider.isVisible = !pallets.isEmpty()
            binding.imgPlaceholder.isVisible = pallets.isEmpty()
            if(pallets.isNotEmpty()) {
                pallets.add(0, Card.CardButton(iconResId = R.drawable.ic_palette_add))
            }
            (binding.lstPallets.adapter as? PalletsRvAdapter)?.update(pallets)
        }

        mainModule.colorType.nonNull().observe(viewLifecycleOwner) {
            (binding.lstColors.adapter as? ColorsRvAdapter)?.changeColorType(it)
        }

        viewModule.state.nonNull().observe(viewLifecycleOwner) {
            if(it is PaletteViewModule.State.SHARE) {
                requireActivity().shareFile(it.file)
            }
        }

        binding.imgPlaceholder.setOnClickListener {
            createPallet()
        }

        binding.btnAddColor.setOnClickListener {
            addColor()
        }

        viewModule.init()
    }

    private fun createPallet() {
        val request = DialogInput.show(fm = this.childFragmentManager, title = requireContext().getString(R.string.module_other_color_pick_pallet_name), text = requireContext().defaultPaletteName(withIncrement = false))
        this.childFragmentManager.setFragmentResultListener(request, viewLifecycleOwner) { _, bundle ->
            val message = bundle.getString(DialogInput.BUNDLE_KEY_INPUT_MESSAGE)
            if(!message.isNullOrBlank() && message.isNotEmpty()){
                if(requireContext().defaultPaletteName(withIncrement = false) == message) {
                    requireContext().defaultPaletteName(withIncrement = true)
                }
                viewModule.pressNewPallet(name = message)
            }
        }
    }
}

