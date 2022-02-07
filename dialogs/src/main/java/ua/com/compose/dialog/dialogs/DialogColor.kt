package ua.com.compose.dialog.dialogs

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ua.com.compose.dialog.R
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import kotlinx.android.synthetic.main.dialog_color.*
import ua.com.compose.extension.*
import ua.com.compose.navigator.remove


class DialogColor : BottomSheetDialogFragment()  {

    companion object {

        const val TAG = "DialogColorTag"
        const val PREF_KEY_COLOR = "DIALOG_COLOR_LAST"
        const val BUNDLE_KEY_ANSWER_COLOR = "BUNDLE_KEY_ANSWER_COLOR"
        private const val BUNDLE_KEY_INPUT_COLOR = "BUNDLE_KEY_INPUT_COLOR"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"

        fun show(fm: FragmentManager, color: Int? = null): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogColor().apply {
                this.arguments = bundleOf(
                        BUNDLE_KEY_REQUEST_KEY to requestKey
                ).apply {
                    color?.let { this.putInt(BUNDLE_KEY_INPUT_COLOR, it) }
                }
            }
            fm.remove(TAG)
            fragment.show(fm, TAG)
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

        (arguments?.getInt(BUNDLE_KEY_INPUT_COLOR, prefs.get(PREF_KEY_COLOR, Color.GREEN)))?.let { color ->
            colorPicker.setColor(color)
            imgExample.imageTintList = ColorStateList.valueOf(color)
            imgExample.tag = color
        }

        colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                imgExample.tag = color
                imgExample.imageTintList = ColorStateList.valueOf(color)
            }
        })

        btnColor0.setVibrate(EVibrate.BUTTON)
        btnColor0.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()))
        }

        btnColor1.setVibrate(EVibrate.BUTTON)
        btnColor1.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()))
        }

        btnColor2.setVibrate(EVibrate.BUTTON)
        btnColor2.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()))
        }

        btnColor3.setVibrate(EVibrate.BUTTON)
        btnColor3.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()))
        }

        btnColor4.setVibrate(EVibrate.BUTTON)
        btnColor4.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()))
        }

        btnColor5.setVibrate(EVibrate.BUTTON)
        btnColor5.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()))
        }

        btnColor6.setVibrate(EVibrate.BUTTON)
        btnColor6.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()))
        }

        btnDone.setVibrate(EVibrate.BUTTON)
        btnDone.setOnClickListener {
            val color = imgExample.tag as Int
            if(color != Color.BLACK && color != Color.WHITE){
                prefs.put(PREF_KEY_COLOR, color)
            }
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf(BUNDLE_KEY_ANSWER_COLOR to color))
            dismiss()
        }
    }

    private fun setColor(color: Int){
        colorPicker.setColor(color)
        imgExample.imageTintList = ColorStateList.valueOf(color)
        imgExample.tag = color
    }
}