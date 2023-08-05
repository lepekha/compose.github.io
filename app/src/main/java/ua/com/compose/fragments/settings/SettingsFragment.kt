package ua.com.compose.fragments.settings

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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import org.koin.android.ext.android.get
import org.koin.androidx.scope.requireScopeActivity
import ua.com.compose.MainActivity
import ua.com.compose.R
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.EColorType
import ua.com.compose.databinding.ModuleOtherColorPickFragmentSettingsBinding
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.extension.sp
import ua.com.compose.extension.vibrate
import ua.com.compose.mvp.data.viewBindingWithBinder


class SettingsFragment : BottomSheetDialogFragment()  {

    companion object {

        const val TAG = "SettingsFragment"
        private const val REQUEST_KEY= "REQUEST_KEY"

        fun show(fm: FragmentManager): String {
            SettingsFragment().show(fm, TAG)
            return REQUEST_KEY
        }
    }

    private val binding by viewBindingWithBinder(ModuleOtherColorPickFragmentSettingsBinding::bind)

    private val viewModule: SettingsViewModel by lazy {
        requireScopeActivity<MainActivity>().get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FragmentBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_other_color_pick_fragment_settings, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analytics.send(SimpleEvent(key = Analytics.Event.OPEN_SETTINGS))

        viewModule.colorType.observe(viewLifecycleOwner) { setting ->
            binding.chipGroup.removeAllViews()
            setting.params.forEachIndexed { index, eColorType ->
                val chip = Chip(context).apply {
                    this.text = eColorType.title()
                    this.tag = eColorType
                    if(setting.current == eColorType) {
                        this.chipBackgroundColor = ColorStateList.valueOf(view.context.getColorFromAttr(R.attr.color_6))
                    } else {
                        this.chipBackgroundColor = ColorStateList.valueOf(view.context.getColorFromAttr(R.attr.color_12))
                    }
                    this.isCheckable = true
                    this.id = index
                    this.isChipIconVisible = setting.current == eColorType
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
        }

        binding.btnDone.setOnClickListener {
            binding.chipGroup.context.vibrate(EVibrate.BUTTON)
            setFragmentResult(REQUEST_KEY, bundleOf("CHANGE_SETTINGS" to true))
            dismiss()
        }

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            binding.chipGroup.context.vibrate(EVibrate.BUTTON)
            viewModule.changeColorType((group.get(checkedId) as Chip).tag as EColorType)
        }
    }
}