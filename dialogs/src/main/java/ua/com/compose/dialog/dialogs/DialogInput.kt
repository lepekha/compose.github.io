package ua.com.compose.dialog.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ua.com.compose.dialog.R
import ua.com.compose.extension.getColorFromAttr
import kotlinx.android.synthetic.main.dialog_input.*
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.onTextChangedListener
import ua.com.compose.extension.setVibrate


class DialogInput : BottomSheetDialogFragment() {

    companion object {

        private const val BUNDLE_KEY_HINT = "BUNDLE_KEY_HINT"
        private const val BUNDLE_KEY_TEXT = "BUNDLE_KEY_TEXT"
        const val BUNDLE_KEY_INPUT_MESSAGE = "BUNDLE_KEY_INPUT_MESSAGE"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"

        fun show(fm: FragmentManager, hint: String = "", text: String? = null): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogInput().apply {
                this.arguments = bundleOf(
                        BUNDLE_KEY_HINT to hint,
                        BUNDLE_KEY_TEXT to text,
                        BUNDLE_KEY_REQUEST_KEY to requestKey
                )
            }
            fragment.show(fm, fragment.tag)
            return requestKey
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_input, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(BUNDLE_KEY_TEXT)?.let {
            editText.setText(it)
        }
        editText.hint = arguments?.getString(BUNDLE_KEY_HINT) ?: ""

        editText.onTextChangedListener {
            btnClear.isVisible = it.isNotEmpty()
        }

        btnCancel.setVibrate(EVibrate.BUTTON)
        btnCancel.setOnClickListener {
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf())
            dismiss()
        }

        btnClear.setVibrate(EVibrate.BUTTON)
        btnClear.setOnClickListener {
            editText.text.clear()
        }

        btnDone.setVibrate(EVibrate.BUTTON)
        btnDone.setOnClickListener {
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf(BUNDLE_KEY_INPUT_MESSAGE to editText.text.toString()))
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf())
    }
}