package ua.com.compose.fragments.palette

import android.content.ClipData
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.R
import ua.com.compose.api.tooltips.ETooltipKey
import ua.com.compose.data.ColorItem
import ua.com.compose.data.ColorNames
import ua.com.compose.data.EColorType
import ua.com.compose.databinding.ModuleOtherColorPickElementColorBinding
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.prefs
import ua.com.compose.extension.showTooltip
import ua.com.compose.extension.vibrate


class ColorsRvAdapter(private val onPressCopy: (item: ColorItem) -> Unit,
                      private val onPressColorTune: (item: ColorItem) -> Unit,
                      private val onPressItem: (item: ColorItem) -> Unit,
                      private val onPressRemove: (id: Long) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val CLIP_DATA_LABEL = "CLIP_DATA_LABEL"
        const val CLIP_DATA = "CLIP_DATA"
    }

    val cards = mutableListOf<Card.CardColor>()
    private var colorType = EColorType.HEX

    fun update(newList: List<Card.CardColor>){
        this.cards.clear()
        this.cards.addAll(newList)
        notifyDataSetChanged()
    }

    fun changeColorType(colorType: EColorType) {
        this.colorType = colorType
        notifyDataSetChanged()
    }

    override fun getItemCount() = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ModuleOtherColorPickElementColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding).apply {
            this.binding.container.setOnClickListener {
                this.binding.container.context.vibrate(EVibrate.BUTTON)
                onPressItem(cards[adapterPosition].item)
            }

            this.binding.btnCopy.setOnClickListener {
                this.binding.btnCopy.context.vibrate(EVibrate.BUTTON)
                onPressCopy(cards[adapterPosition].item)
            }

            this.binding.btnSetting.setOnClickListener {
                this.binding.btnCopy.context.vibrate(EVibrate.BUTTON)
                onPressColorTune(cards[adapterPosition].item)
            }

            this.binding.btnRemove.setOnClickListener {
                this.binding.btnCopy.context.vibrate(EVibrate.BUTTON)
                cards.getOrNull(adapterPosition)?.item?.id?.let(onPressRemove)
            }

            this.binding.container.setOnLongClickListener { view ->
                val data = cards[adapterPosition]
                val clipData = ClipData.newIntent(CLIP_DATA_LABEL, Intent().apply {
                    this.putExtra(CLIP_DATA, data.item.id)
                })
                val shadowBuilder = View.DragShadowBuilder(view)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(clipData, shadowBuilder, view, 0);
                }else{
                    view.startDrag(clipData, shadowBuilder, view, 0);
                }
            }


        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(cards[position])
    }

    inner class ViewHolder(val binding: ModuleOtherColorPickElementColorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card.CardColor) {
            val hex = "#${Integer.toHexString(card.item.color).substring(2).toLowerCase()}"
            binding.txtName.text = ColorNames.getColorName(hex)
            binding.txtColor.text = colorType.convertColor(card.item.color, withSeparator = ",")
            binding.txtColor.setTextColor(Color.WHITE)
            binding.imgInfo.imageTintList = ColorStateList.valueOf(if (ColorUtils.calculateLuminance(card.item.color) < 0.5) Color.WHITE else Color.BLACK)
            binding.txtName.setTextColor(Color.WHITE)
            binding.imgColor.setCardBackgroundColor(card.item.color)

            if(ETooltipKey.PALETTE_DRAG_AND_DROP.isShow() && adapterPosition == 2) {
                ETooltipKey.PALETTE_DRAG_AND_DROP.confirm()
                binding.root.doOnLayout {
                    it.showTooltip(it.context.getString(R.string.module_other_color_pick_tooltip_drag_and_drop))
                }
            }
        }
    }
}
