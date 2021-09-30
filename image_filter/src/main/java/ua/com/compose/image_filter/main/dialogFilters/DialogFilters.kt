package ua.com.compose.image_filter.main.dialogFilters

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import ua.com.compose.extension.dp
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.extension.sp
import kotlinx.android.synthetic.main.module_image_filter_fragment_filter.*
import ua.com.compose.dialog.dialogs.DialogChip
import ua.com.compose.image_filter.R
import ua.com.compose.image_filter.data.EImageFilter
import ua.com.compose.image_filter.main.ImageFilterMenuRvAdapter
import ua.com.compose.mvp.BaseMvpActivity
import java.util.ArrayList


class DialogFilters : BottomSheetDialogFragment() {

    companion object {

        const val BUNDLE_KEY_FILTER_ID = "BUNDLE_KEY_FILTER_ID"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"

        fun show(fm: FragmentManager): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogFilters().apply {
                this.arguments = bundleOf(
                        BUNDLE_KEY_REQUEST_KEY to requestKey
                )
            }
            fragment.show(fm, fragment.tag)
            return requestKey
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_image_filter_dialog_filters, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    }

    private val filters = EImageFilter.values().map { it.createFilter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list?.post {
            list.layoutManager = GridLayoutManager(context, 4)
            list.adapter = ImageFilterMenuRvAdapter(filters = filters) {
                setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf(
                    BUNDLE_KEY_FILTER_ID to it.id
                ))
                dismiss()
            }
        }
    }
}