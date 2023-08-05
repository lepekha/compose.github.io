package ua.com.compose.fragments.info

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.R
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.api.tooltips.ETooltipKey
import ua.com.compose.databinding.ModuleOtherColorPickElementColorInfoColorBinding
import ua.com.compose.databinding.ModuleOtherColorPickElementColorInfoColorsBinding
import ua.com.compose.databinding.ModuleOtherColorPickElementColorInfoKeyTextBinding
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.extension.dp
import ua.com.compose.extension.setMarginLeft
import ua.com.compose.extension.showTooltip
import ua.com.compose.extension.vibrate


class ColorInfoRvAdapter(
        private val pressAddToPalette: (value: Int) -> Unit,
        private val pressCopy: (value: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_KEY_TEXT = 0
        const val VIEW_TYPE_COLORS = 1
        const val VIEW_TYPE_COLOR = 2
    }

    private val items = mutableListOf<ColorInfo>()

    fun update(newList: List<ColorInfo>){
        this.items.clear()
        this.items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is ColorInfo.TitleText -> VIEW_TYPE_KEY_TEXT
            is ColorInfo.Colors -> VIEW_TYPE_COLORS
            is ColorInfo.Color -> VIEW_TYPE_COLOR
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return when(viewType) {
           VIEW_TYPE_KEY_TEXT -> ViewHolderKeyText.createViewHolder(parent)
           VIEW_TYPE_COLORS -> ViewHolderColors.createViewHolder(parent)
           else -> ViewHolderColor.createViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = items[position]) {
            is ColorInfo.TitleText -> (holder as ViewHolderKeyText).bind(item)
            is ColorInfo.Colors -> (holder as ViewHolderColors).bind(item, pressAddToPalette, pressCopy)
            is ColorInfo.Color -> (holder as ViewHolderColor).bind(item, pressAddToPalette, pressCopy)
        }
    }


    class ViewHolderKeyText(val binding: ModuleOtherColorPickElementColorInfoKeyTextBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
                val binding = ModuleOtherColorPickElementColorInfoKeyTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolderKeyText(binding).apply {
                    binding.container.setOnClickListener {
                        analytics.send(SimpleEvent(key = Analytics.Event.COLOR_COPY_INFO))
                        binding.container.context.clipboardCopy(binding.txtValue.text.toString())
                        Toast.makeText(binding.container.context, binding.container.context.getString(R.string.module_other_color_pick_color_copy), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fun bind(item: ColorInfo.TitleText) {
            binding.txtTitle.text = item.title
            binding.txtValue.text = item.text
        }
    }

    class ViewHolderColors(val binding: ModuleOtherColorPickElementColorInfoColorsBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
                val binding = ModuleOtherColorPickElementColorInfoColorsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolderColors(binding)
            }
        }

        fun bind(item: ColorInfo.Colors, pressAddToPalette: (value: Int) -> Unit, pressCopy: (value: Int) -> Unit) {
            binding.txtTitle.text = item.title
            binding.lstColors.removeAllViews()
            item.colors.mapIndexed { index, color ->
                val view = CardView(binding.root.context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1.0f
                    )
                    if(index > 0) {
                        setMarginLeft(3.dp.toInt())
                    }
                    radius = 6.dp
                    setCardBackgroundColor(color)
                }

                view.setOnClickListener {
                    view.context.vibrate(EVibrate.BUTTON)
                    pressAddToPalette.invoke(color)
                }
                view.setOnLongClickListener {
                    view.context.vibrate(EVibrate.BUTTON)
                    pressCopy.invoke(color)
                    true
                }
                binding.lstColors.addView(view)
            }
        }
    }

    class ViewHolderColor(val binding: ModuleOtherColorPickElementColorInfoColorBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
                val binding = ModuleOtherColorPickElementColorInfoColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolderColor(binding)
            }
        }

        fun bind(item: ColorInfo.Color, pressAddToPalette: (value: Int) -> Unit, pressCopy: (value: Int) -> Unit) {
            binding.imgExample.text = item.title
            binding.imgExample.backgroundTintList = ColorStateList.valueOf(item.color)
            if(ColorUtils.calculateLuminance(item.color) < 0.5) {
                binding.imgExample.setTextColor(Color.WHITE)
            } else {
                binding.imgExample.setTextColor(Color.BLACK)
            }

            binding.imgExample.setOnClickListener {
                it.context.vibrate(EVibrate.BUTTON)
                pressAddToPalette.invoke(item.color)
            }

            binding.imgExample.setOnLongClickListener {
                it.context.vibrate(EVibrate.BUTTON)
                pressCopy.invoke(item.color)
                true
            }

            binding.imgExample.doOnLayout {
                if(ETooltipKey.COLOR_INFO_LONG_PRESS.isShow()) {
                    ETooltipKey.COLOR_INFO_LONG_PRESS.confirm()
                    it.showTooltip(it.context.getString(R.string.module_other_color_pick_long_press_copy))
                }
            }
        }
    }

}
