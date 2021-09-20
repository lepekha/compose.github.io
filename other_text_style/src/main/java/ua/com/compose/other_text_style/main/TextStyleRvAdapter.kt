package ua.com.compose.other_text_style.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.other_text_style.databinding.ModuleOtherTextStyleElementTextStyleBinding


class MenuRvAdapter(private val onPress: (text: String) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    private val list = mutableListOf<String>()

    fun update(newList: List<String>){
        list.clear()
        list.addAll(newList)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ModuleOtherTextStyleElementTextStyleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding).apply {
            this.binding.btnCopy.setOnClickListener {
                onPress(list[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtTextStyle.text = list[position]
    }
}

class ViewHolder(val binding: ModuleOtherTextStyleElementTextStyleBinding) : RecyclerView.ViewHolder(binding.root)
