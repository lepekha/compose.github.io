package ua.com.compose.other_color_pick.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickElementPaletteBinding


class PaletteRvAdapter(private val onPress: (position: Int) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    private val colors = mutableListOf<Int>()

    fun update(newList: List<Int>){
        colors.clear()
        colors.addAll(newList)
        this.notifyDataSetChanged()
    }

    override fun getItemCount() = colors.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ModuleOtherColorPickElementPaletteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding).apply {
            this.binding.btnCopy.setOnClickListener {
                onPress(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtColor.text = colors[position].toString()
        holder.binding.container.setBackgroundColor(colors[position])
    }
}

class ViewHolder(val binding: ModuleOtherColorPickElementPaletteBinding) : RecyclerView.ViewHolder(binding.root)
