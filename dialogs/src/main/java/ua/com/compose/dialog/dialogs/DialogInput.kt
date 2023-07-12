package ua.com.compose.dialog.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ua.com.compose.dialog.R
import ua.com.compose.dialog.databinding.DialogInputBinding
import ua.com.compose.extension.*
import ua.com.compose.mvp.data.viewBindingWithBinder
import ua.com.compose.navigator.remove


class DialogInput : BottomSheetDialogFragment() {

    companion object {

        const val TAG = "DialogInputTag"
        private const val BUNDLE_KEY_SINGLE_LINE = "BUNDLE_KEY_SINGLE_LINE"
        private const val BUNDLE_KEY_HINT = "BUNDLE_KEY_HINT"
        private const val BUNDLE_KEY_TEXT = "BUNDLE_KEY_TEXT"
        private const val BUNDLE_KEY_TITLE = "BUNDLE_KEY_TITLE"
        const val BUNDLE_KEY_INPUT_MESSAGE = "BUNDLE_KEY_INPUT_MESSAGE"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"

        fun show(fm: FragmentManager, title: String, hint: String = "", text: String? = null, singleLine: Boolean = false): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogInput().apply {
                this.arguments = bundleOf(
                        BUNDLE_KEY_SINGLE_LINE to singleLine,
                        BUNDLE_KEY_HINT to hint,
                        BUNDLE_KEY_TEXT to text,
                        BUNDLE_KEY_TITLE to title,
                        BUNDLE_KEY_REQUEST_KEY to requestKey
                )
            }
            fm.remove(TAG)
            fragment.show(fm, TAG)
            return requestKey
        }
    }

    private val binding by viewBindingWithBinder(DialogInputBinding::bind)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_input, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            this.setCancelable(false)
            this.setCanceledOnTouchOutside(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(BUNDLE_KEY_TITLE)?.let {
            binding.txtMessage.text = it
        }
        binding.editText.hint = arguments?.getString(BUNDLE_KEY_HINT) ?: ""
        binding.editText.isSingleLine = arguments?.getBoolean(BUNDLE_KEY_SINGLE_LINE, false) ?: false

        binding.editText.onTextChangedListener {
            binding.btnCopy.isVisible = it.isNotEmpty()
            binding.btnDone.isVisible = it.isNotEmpty() || it.isNotBlank()
        }

        binding.btnCancel.setVibrate(EVibrate.BUTTON)
        binding.btnCancel.setOnClickListener {
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf())
            dismiss()
        }

        binding.btnCopy.setVibrate(EVibrate.BUTTON)
        binding.btnCopy.setOnClickListener {
            binding.editText.text.clear()
        }

        binding.btnDone.setVibrate(EVibrate.BUTTON)
        binding.btnDone.setOnClickListener {
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf(BUNDLE_KEY_INPUT_MESSAGE to binding.editText.text.toString()))
            dismiss()
        }

        binding.btnDone.isVisible = false
        arguments?.getString(BUNDLE_KEY_TEXT)?.let {
            binding.editText.setText(it)
            binding.editText.setSelection(binding.editText.length())
            binding.btnDone.isVisible = it.isNotEmpty() || it.isNotBlank()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf())
    }
}