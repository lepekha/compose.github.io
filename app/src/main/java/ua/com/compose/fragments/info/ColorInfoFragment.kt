package ua.com.compose.fragments.info

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.get
import org.koin.androidx.scope.requireScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import ua.com.compose.MainActivity
import ua.com.compose.R
import ua.com.compose.databinding.ModuleOtherColorPickFragmentColorInfoBinding
import ua.com.compose.mvp.data.viewBindingWithBinder


class ColorInfoFragment : BottomSheetDialogFragment()  {

    companion object {

        const val TAG = "FragmentColorInfo"
        const val REQUEST_KEY = "REQUEST_KEY"
        const val PREF_KEY_COLOR = "PREF_KEY_COLOR"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"
        private const val BUNDLE_KEY_UPDATE_PALETTE = "BUNDLE_KEY_UPDATE_PALETTE"

        fun show(fm: FragmentManager, color: Int): String {
            val requestKey = System.currentTimeMillis().toString()
            ColorInfoFragment().apply {
                this.arguments = bundleOf(PREF_KEY_COLOR to color)
            }.show(fm, TAG)
            return requestKey
        }
    }

    private val binding by viewBindingWithBinder(ModuleOtherColorPickFragmentColorInfoBinding::bind)

    private val viewModule: ColorInfoViewModel by lazy {
        requireScopeActivity<MainActivity>().get()
    }

    private val adapter by lazy {
        ColorInfoRvAdapter(
                pressAddToPalette = {
                    viewModule.pressPaletteAdd(it)
                    setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_UPDATE_PALETTE to true))
                    Toast.makeText(requireContext(), requireContext().getString(R.string.module_other_color_pick_color_add_to_pallete), Toast.LENGTH_SHORT).show()
                }
        ).apply {
            binding.lstInfo.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.lstInfo.adapter = this
            binding.lstInfo.setHasFixedSize(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_other_color_pick_fragment_color_info, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModule.items.observe(viewLifecycleOwner) {
            adapter.update(it)
        }

        val color = arguments?.getInt(PREF_KEY_COLOR) ?: return dismiss()

        viewModule.create(color)
    }
}