package ua.com.compose.dialog.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ua.com.compose.dialog.R
import ua.com.compose.dialog.databinding.DialogConfirmationBinding
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.setVibrate
import ua.com.compose.mvp.data.viewBindingWithBinder
import ua.com.compose.navigator.remove


class DialogConfirmation : BottomSheetDialogFragment() {

    companion object {

        const val TAG = "DialogConfirmationTag"
        const val BUNDLE_KEY_ANSWER = "BUNDLE_KEY_ANSWER"
        private const val BUNDLE_KEY_MESSAGE = "BUNDLE_KEY_MESSAGE"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"

        fun show(fm: FragmentManager, message: String): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogConfirmation().apply {
                this.arguments = bundleOf(
                        BUNDLE_KEY_MESSAGE to message,
                        BUNDLE_KEY_REQUEST_KEY to requestKey
                )
            }
            fm.remove(TAG)
            fragment.show(fm, TAG)
            return requestKey
        }
    }

    private val binding by viewBindingWithBinder(DialogConfirmationBinding::bind)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            this.setCancelable(false)
            this.setCanceledOnTouchOutside(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_confirmation, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtMessage.text = arguments?.getString(BUNDLE_KEY_MESSAGE) ?: ""

        binding.btnCancel.setVibrate(EVibrate.BUTTON)
        binding.btnCancel.setOnClickListener {
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY)
                    ?: BUNDLE_KEY_REQUEST_KEY, bundleOf(BUNDLE_KEY_ANSWER to false))
            dismiss()
        }

        binding.btnDone.setVibrate(EVibrate.BUTTON)
        binding.btnDone.setOnClickListener {
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY)
                    ?: BUNDLE_KEY_REQUEST_KEY, bundleOf(BUNDLE_KEY_ANSWER to true))
            dismiss()
        }
    }
}