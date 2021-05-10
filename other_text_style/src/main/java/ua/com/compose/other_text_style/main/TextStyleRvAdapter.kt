package ua.com.compose.other_text_style.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.module_other_text_style_element_text_style.view.*
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.other_text_style.R


class MenuRvAdapter(private val list: MutableList<String>) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_other_text_style_element_text_style, parent, false)).apply {
            this.btnCopy.setOnClickListener {
                parent.context.clipboardCopy(text = txtTextStyle.text.toString())
                Toast.makeText(parent.context, parent.context.getString(R.string.module_other_text_style_fragment_text_copied),Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTextStyle.text = list[position]
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val txtTextStyle: TextView = view.txtTextStyle
    val btnCopy: ImageButton = view.btnCopy
}
