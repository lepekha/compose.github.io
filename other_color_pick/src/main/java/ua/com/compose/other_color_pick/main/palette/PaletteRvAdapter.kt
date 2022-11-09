package ua.com.compose.other_color_pick.main.palette

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.setVibrate
import ua.com.compose.extension.vibrate
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickElementPaletteBinding
import ua.com.compose.other_color_pick.main.EColorType


class PaletteRvAdapter(private val onPressCopy: (color: String) -> Unit,
                       private val onPressRemove: (id: Long) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val cards = mutableListOf<Card.CardColor>()
    private var colorType = EColorType.HEX

    fun update(newList: List<Card.CardColor>){
        cards.clear()
        cards.addAll(newList)
    }

    fun changeColorType(colorType: EColorType) {
        this.colorType = colorType
        this.notifyDataSetChanged()
    }

    fun removeColor(id: Long) {
        cards.indexOfFirst { it.item.id == id }.let { index ->
            cards.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount() = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ModuleOtherColorPickElementPaletteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding).apply {
            this.binding.container.setOnClickListener {
                this.binding.btnCopy.context.vibrate(EVibrate.BUTTON)
                onPressCopy(this.binding.txtColor.text.toString())
            }
            this.binding.btnCopy.setOnClickListener {
                this.binding.btnCopy.context.vibrate(EVibrate.BUTTON)
                onPressCopy(this.binding.txtColor.text.toString())
            }

            this.binding.btnRemove.setOnClickListener {
                this.binding.btnCopy.context.vibrate(EVibrate.BUTTON)
                cards.getOrNull(adapterPosition)?.item?.id?.let(onPressRemove)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(cards[position])
    }

    inner class ViewHolder(val binding: ModuleOtherColorPickElementPaletteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card.CardColor) {
            binding.txtName.text = card.item.name
            binding.txtColor.text = colorType.convertColor(card.item.color)
            binding.txtColor.setTextColor(getTextColor(card.item.color))
            binding.txtName.setTextColor(getTextColor(card.item.color))
            binding.container.setBackgroundColor(card.item.color)
        }

        private fun getTextColor(color: Int): Int {
            return if(ColorUtils.calculateLuminance(color) < 0.5) Color.WHITE else Color.BLACK
        }
    }
}
