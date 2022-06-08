package ua.com.compose.other_color_pick.main.palette

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
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
        val index = cards.indexOfFirst { it.item.id == id }
        cards.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun getItemCount() = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ModuleOtherColorPickElementPaletteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding).apply {
            this.binding.btnCopy.setOnClickListener {
                onPressCopy(this.binding.txtColor.text.toString())
            }

            this.binding.btnRemove.setOnClickListener {
                onPressRemove(cards[adapterPosition].item.id)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(cards[position])
    }

    inner class ViewHolder(val binding: ModuleOtherColorPickElementPaletteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card.CardColor) {
            binding.txtColor.text = colorType.convertColor(getTextColor(card.item.color))
            binding.container.setBackgroundColor(card.item.color)
        }

        private fun getTextColor(color: Int): Int {
            return if(ColorUtils.calculateLuminance(color) < 0.5) Color.WHITE else Color.BLACK
        }
    }
}
