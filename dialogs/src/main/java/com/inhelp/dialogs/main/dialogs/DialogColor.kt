package com.inhelp.dialogs.main.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.inhelp.dialogs.R
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import kotlinx.android.synthetic.main.dialog_color.*


class DialogColor : BottomSheetDialogFragment()  {

    companion object {

        const val BUNDLE_KEY_ANSWER_COLOR = "BUNDLE_KEY_ANSWER_COLOR"
        private const val BUNDLE_KEY_INPUT_COLOR = "BUNDLE_KEY_INPUT_COLOR"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"

        fun show(fm: FragmentManager, color: Int): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogColor().apply {
                this.arguments = bundleOf(
                        BUNDLE_KEY_REQUEST_KEY to requestKey,
                        BUNDLE_KEY_INPUT_COLOR to color
                )
            }
            fragment.show(fm, fragment.tag)
            return requestKey
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_color, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getInt(BUNDLE_KEY_INPUT_COLOR)?.let { color ->
            colorPicker.setColor(color)
            imgExample.setCardBackgroundColor(color)
            imgExample.tag = color
        }

        colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                imgExample.tag = color
                imgExample.setCardBackgroundColor(color)
            }
        })

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnDone.setOnClickListener {
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf(BUNDLE_KEY_ANSWER_COLOR to imgExample.tag as Int))
            dismiss()
        }
    }
}