package com.dali.instagram.planer.view.main

import android.content.res.ColorStateList
import android.net.Uri
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dali.instagram.planer.R
import com.inhelp.extension.getColorFromAttr
import kotlinx.android.synthetic.main.element_instagram_planer_image.view.*


class InstagramPlanerRvAdapter(val images: MutableList<Uri>, val onPress: (position: Int) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_instagram_planer_image, parent, false)).apply {
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imgView.context).load(images[position]).centerInside().thumbnail(0.1f).into(holder.imgView)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imgView: ImageView = view.imgView
}
