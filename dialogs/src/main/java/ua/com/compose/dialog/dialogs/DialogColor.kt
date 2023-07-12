package ua.com.compose.dialog.dialogs

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
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import ua.com.compose.ColorNames
import ua.com.compose.EColorType
import ua.com.compose.colorName
import ua.com.compose.dialog.R
import ua.com.compose.dialog.databinding.DialogColorBinding
import ua.com.compose.extension.*
import ua.com.compose.mvp.data.viewBindingWithBinder
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

    private val binding by viewBindingWithBinder(DialogColorBinding::bind)

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
        binding.edColor.filters = arrayOf<InputFilter>(alphaNumericFilter, InputFilter.LengthFilter(6))

        val watcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                // not used in this extension
            }

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                // not used in this extension
            }

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                var hex = p0.toString()
                repeat(6 - hex.count()) {
                    hex = "0$hex"
                }
                val color = Color.parseColor("#$hex")
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

        binding.colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                binding.imgExample.tag = color
                binding.imgExample.backgroundTintList = ColorStateList.valueOf(color)
                if(ColorUtils.calculateLuminance(color) < 0.5) {
                    binding.txtColor.setTextColor(Color.WHITE)
                } else {
                    binding.txtColor.setTextColor(Color.BLACK)
                }
                binding.txtColor.text = color.colorName()
                binding.edColor.removeTextChangedListener(watcher)
                binding.edColor.setText(Integer.toHexString(color).substring(2).uppercase(Locale.getDefault()))
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

        binding.btnCancel.setVibrate(EVibrate.BUTTON)
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setColor(color: Int, colorType: EColorType){
        binding.colorPicker.setColor(color)
        binding.edColor.setText(Integer.toHexString(color).substring(2).uppercase(Locale.getDefault()))
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