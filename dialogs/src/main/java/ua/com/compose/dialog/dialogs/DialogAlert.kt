package ua.com.compose.dialog.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_alert.*
import ua.com.compose.dialog.R
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.setVibrate


class DialogAlert : BottomSheetDialogFragment() {

    companion object {

        private const val BUNDLE_KEY_MESSAGE = "BUNDLE_KEY_MESSAGE"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"

        fun show(fm: FragmentManager, message: String): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogAlert().apply {
                this.arguments = bundleOf(
                        BUNDLE_KEY_MESSAGE to message,
                        BUNDLE_KEY_REQUEST_KEY to requestKey
                )
            }
            fragment.show(fm, fragment.tag)
            return requestKey
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_alert, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            this.setCancelable(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtMessage.text = arguments?.getString(BUNDLE_KEY_MESSAGE) ?: ""

        btnDone.setVibrate(EVibrate.BUTTON)
        btnDone.setOnClickListener {
            dismiss()
        }
    }
}