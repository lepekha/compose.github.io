package com.inhelp.text_style.main

import android.R.attr.label
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.inhelp.extension.clipboardCopy
import com.inhelp.text_style.R
import kotlinx.android.synthetic.main.element_text_style.view.*


class MenuRvAdapter(private val list: MutableList<String>) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_text_style, parent, false)).apply {
            this.btnCopy.setOnClickListener {
                parent.context.clipboardCopy(text = txtTextStyle.text.toString())
                Toast.makeText(parent.context, parent.context.getString(R.string.fragment_text_copied),Toast.LENGTH_SHORT).show();
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
