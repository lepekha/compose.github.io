package ua.com.compose.dialog.dialogs

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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import ua.com.compose.dialog.R
import ua.com.compose.extension.dp
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.extension.sp
import kotlinx.android.synthetic.main.dialog_list.*
import java.util.ArrayList


class DialogChip : BottomSheetDialogFragment() {

    companion object {

        private const val BUNDLE_KEY_LIST = "BUNDLE_KEY_LIST"
        private const val BUNDLE_KEY_SELECTED = "BUNDLE_KEY_SELECTED"
        const val BUNDLE_KEY_ANSWER_POSITION = "BUNDLE_KEY_ANSWER_POSITION"
        const val BUNDLE_KEY_ANSWER_NAME = "BUNDLE_KEY_ANSWER_NAME"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"

        fun show(fm: FragmentManager, list: List<String>, selected: String = ""): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogChip().apply {
                this.arguments = bundleOf(
                        BUNDLE_KEY_REQUEST_KEY to requestKey,
                        BUNDLE_KEY_SELECTED to selected,
                        BUNDLE_KEY_LIST to list.toTypedArray()
                )
            }
            fragment.show(fm, fragment.tag)
            return requestKey
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_list, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selected = arguments?.getString(BUNDLE_KEY_SELECTED)

        arguments?.getStringArray(BUNDLE_KEY_LIST)?.forEachIndexed { index, it ->
            val chip = Chip(context).apply {
                this.text = it
                this.chipBackgroundColor = ColorStateList.valueOf(view.context.getColorFromAttr(R.attr.color_12))
                this.isCheckable = true
                this.id = index
                this.isChipIconVisible = selected == it
                this.isCheckedIconVisible = false
                this.isClickable = true
                this.chipIcon = ContextCompat.getDrawable(view.context, R.drawable.ic_done)
                this.chipIconTint = ColorStateList.valueOf(view.context.getColorFromAttr(R.attr.color_10))
                this.setTextColor(view.context.getColorFromAttr(R.attr.color_10))
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, 18.sp)
            }

            chipGroup.addView(chip)
        }

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf(
                    BUNDLE_KEY_ANSWER_POSITION to checkedId,
                    BUNDLE_KEY_ANSWER_NAME to (group.get(checkedId) as Chip).text)
            )
            dismiss()
        }
    }
}