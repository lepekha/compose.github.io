package ua.com.compose.other_color_pick.main.palette

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.EColorType
import ua.com.compose.dialog.dialogs.DialogColor
import ua.com.compose.dialog.dialogs.DialogConfirmation
import ua.com.compose.dialog.dialogs.DialogInput
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.extension.nonNull
import ua.com.compose.extension.observe
import ua.com.compose.mvp.BaseMvpActivity
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.other_color_pick.R
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickFragmentPaletteBinding
import ua.com.compose.other_color_pick.di.Scope
import ua.com.compose.other_color_pick.main.ColorPickViewModule
import ua.com.compose.other_color_pick.main.defaultPaletteName


class PaletteFragment : BaseMvvmFragment() {

    companion object {
        fun newInstance(): PaletteFragment {
            return PaletteFragment()
        }
    }

//    private val btnClearAll = BottomMenu(iconResId = R.drawable.ic_remove_all) {
//        if((binding?.lstPalette?.adapter?.itemCount ?: 0) > 0) {
//            val request = DialogConfirmation.show(fm = this.childFragmentManager, message = requireContext().getString(R.string.module_other_color_pick_remove_all))
//            this.childFragmentManager.setFragmentResultListener(request, viewLifecycleOwner) { _, bundle ->
//                if(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER)){
//                    viewModule.pressRemoveAll()
//                }
//            }
//        } else {
//            showAlert(R.string.module_other_color_pick_empty)
//        }
//    }

    private val btnAddColor = BottomMenu(iconResId = R.drawable.ic_add_circle){
        addColor()
    }

    private fun addColor() {
        val key = DialogColor.show(fm = childFragmentManager)
        childFragmentManager.setFragmentResultListener(
                key,
                viewLifecycleOwner
        ) { _, bundle ->
            val color = bundle.getInt(DialogColor.BUNDLE_KEY_ANSWER_COLOR) ?: return@setFragmentResultListener
            viewModule.pressAddColor(color = color)
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>()
    }

    private var binding: ModuleOtherColorPickFragmentPaletteBinding? = null

    private val viewModule: PaletteViewModule by lazy {
        Scope.COLOR_PICK.get()
    }

    private val mainModule: ColorPickViewModule by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ModuleOtherColorPickFragmentPaletteBinding.inflate(inflater)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initColors() {
        ColorsRvAdapter(
            onPressCopy = { item ->
                val color = mainModule.colorType.value?.convertColor(item.color) ?: ""
                requireContext().clipboardCopy(color)
                showAlert(R.string.module_other_color_pick_color_copy)
            },
            onPressColorTune = { item ->
                val key = DialogColor.show(fm = childFragmentManager, color = item.color)
                childFragmentManager.setFragmentResultListener(
                        key,
                        viewLifecycleOwner
                ) { _, bundle ->
                    val color = bundle.getInt(DialogColor.BUNDLE_KEY_ANSWER_COLOR) ?: return@setFragmentResultListener
                    viewModule.pressChangeColor(id = item.id, color = color)
                }
            },
            onPressRemove = {
                viewModule.pressRemoveColor(id = it)
                (binding?.lstColors?.adapter as? ColorsRvAdapter)?.removeColor(id = it)
            }
        ).apply {
            binding?.lstColors?.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding?.lstColors?.adapter = this
            binding?.lstColors?.setHasFixedSize(true)
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

                },
                onPressChangePallet = { colorId, palletId ->
                    viewModule.pressChangePallet(colorId, palletId)
                }
        ).apply {
            binding?.lstPallets?.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            binding?.lstPallets?.adapter = this
            binding?.lstPallets?.setHasFixedSize(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPallets()
        initColors()

        viewModule.colors.nonNull().observe(this) {
            (binding?.lstColors?.adapter as? ColorsRvAdapter)?.update(it ?: listOf())
            binding?.btnAddColor?.isVisible = (it?.isEmpty() ?: true) && (binding?.imgPlaceholder?.isVisible == false)
            binding?.lstColors?.adapter?.notifyDataSetChanged()
            if(it?.isNotEmpty() == true) {
                binding?.frameLayout?.isVisible = true
                (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnAddColor))
            } else {
                binding?.frameLayout?.isVisible = false
                (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf())
            }
//            binding?.lstPalette?.runLayoutAnimation(anim = R.anim.layout_animation_fall_down)
        }

        viewModule.palettes.nonNull().observe(this) {
            val pallets = mutableListOf<Card>().apply {
                this.addAll(it ?: listOf())
            }
            binding?.divider?.isVisible = !pallets.isEmpty()
            binding?.imgPlaceholder?.isVisible = pallets.isEmpty()
            if(pallets.isNotEmpty()) {
                pallets.add(0, Card.CardButton(iconResId = R.drawable.ic_palette_add))
            }
            (binding?.lstPallets?.adapter as? PalletsRvAdapter)?.update(pallets)
            binding?.lstPallets?.adapter?.notifyDataSetChanged()
        }

        mainModule.colorType.nonNull().observe(this) {
            (binding?.lstColors?.adapter as? ColorsRvAdapter)?.changeColorType(it ?: EColorType.HEX)
        }

        binding?.imgPlaceholder?.setOnClickListener {
            createPallet()
        }

        binding?.btnAddColor?.setOnClickListener {
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

