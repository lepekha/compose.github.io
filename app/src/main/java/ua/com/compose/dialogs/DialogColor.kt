package ua.com.compose.dialogs

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.api.tooltips.ETooltipKey
import ua.com.compose.data.EColorType
import ua.com.compose.data.colorName
import ua.com.compose.databinding.DialogColorBinding
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.get
import ua.com.compose.extension.hideKeyboard
import ua.com.compose.extension.prefs
import ua.com.compose.extension.put
import ua.com.compose.extension.remove
import ua.com.compose.extension.setVibrate
import ua.com.compose.extension.showTooltip
import ua.com.compose.extension.vibrate
import ua.com.compose.fragments.info.ColorInfoFragment
import ua.com.compose.mvp.data.viewBindingWithBinder
import java.util.Locale
import kotlin.math.max
import kotlin.math.min


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

    private val binding by viewBindingWithBinder(DialogColorBinding::bind)

    private val allowedHEXChar = "0123456789abcdefABCDEF#".toSet()
    private val hexNumericFilter = InputFilter { arg0, arg1, arg2, arg3, arg4, arg5 ->
        var newValue = arg3.toString().substring(0, arg4) + arg3.toString().substring(arg5, arg3.toString().length)
        newValue = newValue.substring(0, arg4) + arg0.toString() + newValue.substring(arg4, newValue.length)
        for (k in arg1 until arg2) {
            if (!allowedHEXChar.contains(arg0[k])) {
                return@InputFilter ""
            }
        }
        if(newValue.count() > 7) return@InputFilter ""
        if(newValue.count { it == '#' } > 1) return@InputFilter ""
        if(newValue.indexOf('#') > 0) return@InputFilter ""
        if(newValue.replace("#", "").count() > 6) return@InputFilter ""
        null
    }

    private val allowedRGBChar = "0123456789,.".toSet()
    private val rgbNumericFilter = InputFilter { arg0, arg1, arg2, arg3, arg4, arg5 ->
        var newValue = arg3.toString().substring(0, arg4) + arg3.toString().substring(arg5, arg3.toString().length)
        newValue = newValue.substring(0, arg4) + arg0.toString() + newValue.substring(arg4, newValue.length)
        for (k in arg1 until arg2) {
            if (!allowedRGBChar.contains(arg0[k])) {
                return@InputFilter ""
            }
        }
        if(newValue.count() > 11) return@InputFilter ""
        if(newValue.count { it == ',' || it == '.' } > 2) return@InputFilter ""
        null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_color, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val colorType = (arguments?.getSerializable(BUNDLE_KEY_COLOR_TYPE) as? ua.com.compose.data.EColorType) ?: ua.com.compose.data.EColorType.HEX

        (arguments?.getInt(BUNDLE_KEY_INPUT_COLOR, prefs.get(PREF_KEY_COLOR, Color.GREEN)))?.let { color ->
            setColor(color, colorType)
        }

        val watcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                // not used in this extension
            }

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                // not used in this extension
            }

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                val color = if(Settings.dialogColorInputType == EColorType.HEX) {
                    var hex = p0.toString().replace("#", "")
                    repeat(6 - hex.count()) {
                        hex = "0$hex"
                    }
                    Color.parseColor("#$hex")
                } else {
                    val args = p0.toString().split(",", ".")
                    val r = min((args.getOrNull(0) ?: "0").toIntOrNull() ?: 0, 255)
                    val g = min((args.getOrNull(1) ?: "0").toIntOrNull() ?: 0, 255)
                    val b = min((args.getOrNull(2) ?: "0").toIntOrNull() ?: 0, 255)
                    Color.rgb(r,g,b)
                }


                binding.colorPicker.setColor(color)
                if(ColorUtils.calculateLuminance(color) < 0.5) {
                    binding.txtColor.setTextColor(Color.WHITE)
                } else {
                    binding.txtColor.setTextColor(Color.BLACK)
                }
                binding.txtColor.text = color.colorName()
                binding.imgExample.backgroundTintList = ColorStateList.valueOf(color)
                binding.imgExample.tag = color
            }
        }

        binding.edColor.addTextChangedListener(watcher)

        binding.imgExample.setVibrate(EVibrate.BUTTON)
        binding.imgExample.setOnClickListener {
            ColorInfoFragment.show(parentFragmentManager, color = it.tag as Int)
        }

        binding.colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                binding.edColor.hideKeyboard()
                binding.edColor.clearFocus()
                binding.imgExample.tag = color
                binding.imgExample.backgroundTintList = ColorStateList.valueOf(color)
                if(ColorUtils.calculateLuminance(color) < 0.5) {
                    binding.txtColor.setTextColor(Color.WHITE)
                } else {
                    binding.txtColor.setTextColor(Color.BLACK)
                }
                binding.txtColor.text = color.colorName()
                binding.edColor.removeTextChangedListener(watcher)
                binding.edColor.setText(getCurrentInputColor(color))
                binding.edColor.setSelection(binding.edColor.length())
                binding.edColor.addTextChangedListener(watcher)
            }
        })

        binding.btnColor0.setVibrate(EVibrate.BUTTON)
        binding.btnColor0.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        binding.btnColor1.setVibrate(EVibrate.BUTTON)
        binding.btnColor1.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        binding.btnColor2.setVibrate(EVibrate.BUTTON)
        binding.btnColor2.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        binding.btnColor3.setVibrate(EVibrate.BUTTON)
        binding.btnColor3.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        binding.btnColor4.setVibrate(EVibrate.BUTTON)
        binding.btnColor4.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        binding.btnColor5.setVibrate(EVibrate.BUTTON)
        binding.btnColor5.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        binding.btnColor6.setVibrate(EVibrate.BUTTON)
        binding.btnColor6.setOnClickListener {
            setColor(Color.parseColor(it.contentDescription.toString()), colorType)
        }

        binding.btnDone.setVibrate(EVibrate.BUTTON)
        binding.btnDone.setOnClickListener {
            val color = binding.imgExample.tag as Int
            if(color != Color.BLACK && color != Color.WHITE){
                prefs.put(PREF_KEY_COLOR, color)
            }
            setFragmentResult(arguments?.getString(BUNDLE_KEY_REQUEST_KEY) ?: BUNDLE_KEY_REQUEST_KEY, bundleOf(BUNDLE_KEY_ANSWER_COLOR to color))
            dismiss()
        }

        if(Settings.dialogColorInputType == EColorType.HEX) {
            binding.btnHEX.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.color_night_6))
            binding.btnRGB.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.color_main_header))
        } else {
            binding.btnHEX.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.color_main_header))
            binding.btnRGB.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.color_night_6))
        }

        updateInputType(type = Settings.dialogColorInputType)

        binding.btnRGB.setVibrate(EVibrate.BUTTON)
        binding.btnRGB.setOnClickListener {
            Settings.dialogColorInputType = EColorType.RGB_DECIMAL
            updateInputType(EColorType.RGB_DECIMAL)
        }

        binding.btnHEX.setVibrate(EVibrate.BUTTON)
        binding.btnHEX.setOnClickListener {
            Settings.dialogColorInputType = EColorType.HEX
            updateInputType(EColorType.HEX)
        }

        binding.btnCancel.setVibrate(EVibrate.BUTTON)
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.imgExample.doOnLayout {
            if(ETooltipKey.COLOR_INFO_SINGLE_PRESS.isShow()) {
                ETooltipKey.COLOR_INFO_SINGLE_PRESS.confirm()
                it.showTooltip(it.context.getString(R.string.module_other_color_pick_press_open_info))
            }
        }
    }

    private fun getCurrentInputColor(color: Int): String {
        return if(Settings.dialogColorInputType == EColorType.HEX) {
            EColorType.HEX.convertColor(color).replace(" ", "")
        } else {
            EColorType.RGB_DECIMAL.convertColor(color, withSeparator = ",").replace(" ", "")
        }
    }

    private fun updateInputType(type: EColorType) {
        val color = binding.imgExample.tag as Int
        if(type == EColorType.HEX) {
            binding.edColor.filters = arrayOf(hexNumericFilter, InputFilter.LengthFilter(7))
            binding.btnHEX.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.color_night_6))
            binding.btnHEX.setTextColor(requireContext().getColor(R.color.color_night_10))
            binding.btnRGB.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.color_main_header))
            binding.btnRGB.setTextColor(requireContext().getColor(R.color.color_night_9))
        } else {
            binding.edColor.filters = arrayOf(rgbNumericFilter, InputFilter.LengthFilter(11))
            binding.btnHEX.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.color_main_header))
            binding.btnRGB.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.color_night_6))
            binding.btnRGB.setTextColor(requireContext().getColor(R.color.color_night_10))
            binding.btnHEX.setTextColor(requireContext().getColor(R.color.color_night_9))
        }
        binding.edColor.setText(getCurrentInputColor(color))
        binding.edColor.setSelection(binding.edColor.length())
    }

    private fun setColor(color: Int, colorType: EColorType){
        binding.colorPicker.setColor(color)
        binding.edColor.setText(getCurrentInputColor(color))
        binding.txtColor.text = color.colorName()
        binding.imgExample.backgroundTintList = ColorStateList.valueOf(color)
        binding.imgExample.tag = color
        if(ColorUtils.calculateLuminance(color) < 0.5) {
            binding.txtColor.setTextColor(Color.WHITE)
        } else {
            binding.txtColor.setTextColor(Color.BLACK)
        }
    }
}