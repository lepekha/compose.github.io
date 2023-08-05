package ua.com.compose.dialogs

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import ua.com.compose.R
import ua.com.compose.databinding.DialogListBinding
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.extension.remove
import ua.com.compose.extension.sp
import ua.com.compose.extension.vibrate
import ua.com.compose.mvp.data.viewBindingWithBinder


class DialogChip : BottomSheetDialogFragment() {

    companion object {

        const val BUTTON_NONE = 0
        const val BUTTON_DONE = 1
        const val BUTTON_CANCEL = 2

        const val TAG = "DialogChipTag"
        private const val BUNDLE_KEY_LIST = "BUNDLE_KEY_LIST"
        private const val BUNDLE_KEY_SELECTED = "BUNDLE_KEY_SELECTED"
        const val BUNDLE_KEY_ANSWER_POSITION = "BUNDLE_KEY_ANSWER_POSITION"
        const val BUNDLE_KEY_ANSWER_NAME = "BUNDLE_KEY_ANSWER_NAME"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"
        private const val BUNDLE_KEY_BUTTON = "BUNDLE_KEY_BUTTON"

        fun show(fm: FragmentManager, list: List<String>, selected: String = "", buttonStatus: Int = BUTTON_NONE): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogChip().apply {
                this.arguments = bundleOf(
                        BUNDLE_KEY_REQUEST_KEY to requestKey,
                        BUNDLE_KEY_SELECTED to selected,
                        BUNDLE_KEY_BUTTON to buttonStatus,
                        BUNDLE_KEY_LIST to list.toTypedArray()
                )
            }
            fm.remove(TAG)
            fragment.show(fm, TAG)
            return requestKey
        }
    }

    private val binding by viewBindingWithBinder(DialogListBinding::bind)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_list, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.DialogBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            this.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selected = arguments?.getString(BUNDLE_KEY_SELECTED)
        val btnStatus = arguments?.getInt(BUNDLE_KEY_BUTTON) ?: BUTTON_NONE

        binding.btnDone.isVisible = btnStatus == BUTTON_DONE
        binding.btnCancel.isVisible = btnStatus == BUTTON_CANCEL

        binding.btnDone.setOnClickListener {
            view.context.vibrate(EVibrate.BUTTON)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            view.context.vibrate(EVibrate.BUTTON)
            dismiss()
        }

        arguments?.getStringArray(BUNDLE_KEY_LIST)?.forEachIndexed { index, it ->
            val chip = Chip(context).apply {
                this.text = it
                if(selected == it) {
                    this.chipBackgroundColor = ColorStateList.valueOf(view.context.getColorFromAttr(R.attr.color_6))
                } else {
                    this.chipBackgroundColor = ColorStateList.valueOf(view.context.getColorFromAttr(R.attr.color_12))
                }
                this.isCheckable = true
                this.id = index
                this.isChipIconVisible = selected == it
                this.isCheckedIconVisible = false
                this.isClickable = true
                this.chipIcon = ContextCompat.getDrawable(view.context, R.drawable.ic_done)
                this.chipIconTint = ColorStateList.valueOf(view.context.getColorFromAttr(R.attr.color_10))
                this.setTextColor(view.context.getColorFromAttr(R.attr.color_10))
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, 18.sp)
                this.ellipsize = TextUtils.TruncateAt.END
            }

            binding.chipGroup.addView(chip)
        }

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            binding.chipGroup.context.vibrate(EVibrate.BUTTON)
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf(
                    BUNDLE_KEY_ANSWER_POSITION to checkedId,
                    BUNDLE_KEY_ANSWER_NAME to (group.get(checkedId) as Chip).text)
            )
            dismiss()
        }
    }
}