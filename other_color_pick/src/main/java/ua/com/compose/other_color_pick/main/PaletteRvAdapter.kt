package ua.com.compose.other_color_pick.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickElementPaletteBinding


class PaletteRvAdapter(private val onPressCopy: (color: String) -> Unit,
                       private val onPressRemove: (position: Int) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    private val colors = mutableListOf<Int>()
    private var colorType = EColorType.HEX

    fun update(newList: List<Int>, colorType: EColorType){
        colors.clear()
        colors.addAll(newList)
        this.colorType = colorType
        this.notifyDataSetChanged()
    }

    override fun getItemCount() = colors.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ModuleOtherColorPickElementPaletteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding).apply {
            this.binding.btnCopy.setOnClickListener {
                onPressCopy(this.binding.txtColor.text.toString())
            }

            this.binding.btnRemove.setOnClickListener {
                onPressRemove(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtColor.text = colorType.convertColor(colors[position])
        holder.binding.container.setBackgroundColor(colors[position])
    }
}

class ViewHolder(val binding: ModuleOtherColorPickElementPaletteBinding) : RecyclerView.ViewHolder(binding.root)
