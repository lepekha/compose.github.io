package ua.com.compose.dialog.dialogs

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import kotlinx.android.synthetic.main.dialog_color.*
import ua.com.compose.ColorNames
import ua.com.compose.EColorType
import ua.com.compose.dialog.R
import ua.com.compose.extension.*
import ua.com.compose.navigator.remove
import java.util.Locale


class DialogColor : BottomSheetDialogFragment()  {

    companion object {

        const val TAG = "DialogColorTag"
        const val PREF_KEY_COLOR = "DIALOG_COLOR_LAST"
        const val BUNDLE_KEY_ANSWER_COLOR = "BUNDLE_KEY_ANSWER_COLOR"
        private const val BUNDLE_KEY_INPUT_COLOR = "BUNDLE_KEY_INPUT_COLOR"
        private const val BUNDLE_KEY_COLOR_TYPE = "BUNDLE_KEY_COLOR_TYPE"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"

        fun show(fm: FragmentManager, color: Int? = null, colorType: EColorType = EColorType.HEX): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogColor().apply {
                this.arguments = bundleOf(
                        BUNDLE_KEY_REQUEST_KEY to requestKey
                ).apply {
                    color?.let { this.putInt(BUNDLE_KEY_INPUT_COLOR, it) }
                    this.putSerializable(BUNDLE_KEY_COLOR_TYPE, colorType)
                }
            }
            fm.remove(TAG)
            fragment.show(fm, TAG)
            return requestKey
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_color, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colorType = (arguments?.getSerializable(BUNDLE_KEY_COLOR_TYPE) as? EColorType) ?: EColorType.HEX

        (arguments?.getInt(BUNDLE_KEY_INPUT_COLOR, prefs.get(PREF_KEY_COLOR, Color.GREEN)))?.let { color ->
            setColor(color, colorType)
        }

        val allowedChar = "0123456789abcdefABCDEF".toSet()
        val alphaNumericFilter = InputFilter { arg0, arg1, arg2, arg3, arg4, arg5 ->
            for (k in arg1 until arg2) {
                if (!allowedChar.contains(arg0[k])) {
                    return@InputFilter ""
                }
            }
            null
        }
        edColor.filters = arrayOf<InputFilter>(alphaNumericFilter, InputFilter.LengthFilter(6))
        edColor.onTextChangedListener {
            var hex = it
            repeat(6 - it.count()) {
                hex = "0$hex"
            }
            val color = Color.parseColor("#$hex")
            colorPicker.setColor(color)
            if(ColorUtils.calculateLuminance(color) < 0.5) {
                imgExample.setTextColor(Color.WHITE)
            } else {
                imgExample.setTextColor(Color.BLACK)
            }
            imgExample.text = "≈${ColorNames.getColorName("#" + Integer.toHexString(color).substring(2).lowercase(Locale.getDefault()))}"
            imgExample.backgroundTintList = ColorStateList.valueOf(color)
            imgExample.tag = color
        }

        colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                imgExample.tag = color
                imgExample.backgroundTintList = ColorStateList.valueOf(color)
                if(ColorUtils.calculateLuminance(color) < 0.5) {
                    imgExample.setTextColor(Color.WHITE)
                } else {
                    imgExample.setTextColor(Color.BLACK)
                }
                imgExample.text = "≈${ColorNames.getColorName("#" + Integer.toHexString(color).substring(2).lowercase(Locale.getDefault()))}"
                edColor.setText(Integer.toHexString(color).substring(2).uppercase(Locale.getDefault()))
            }
        })

        btnColor0.setVibrate(EVibrate.BUTTON)
        btnColor0.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        btnColor1.setVibrate(EVibrate.BUTTON)
        btnColor1.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        btnColor2.setVibrate(EVibrate.BUTTON)
        btnColor2.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        btnColor3.setVibrate(EVibrate.BUTTON)
        btnColor3.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        btnColor4.setVibrate(EVibrate.BUTTON)
        btnColor4.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        btnColor5.setVibrate(EVibrate.BUTTON)
        btnColor5.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        btnColor6.setVibrate(EVibrate.BUTTON)
        btnColor6.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
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

        btnCancel.setVibrate(EVibrate.BUTTON)
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setColor(color: Int, colorType: EColorType){
        colorPicker.setColor(color)
        edColor.setText(Integer.toHexString(color).substring(2).uppercase(Locale.getDefault()))
        imgExample.text = "≈${ColorNames.getColorName("#" + Integer.toHexString(color).substring(2).lowercase(Locale.getDefault()))}"
        imgExample.backgroundTintList = ColorStateList.valueOf(color)
        imgExample.tag = color
        if(ColorUtils.calculateLuminance(color) < 0.5) {
            imgExample.setTextColor(Color.WHITE)
        } else {
            imgExample.setTextColor(Color.BLACK)
        }
    }
}