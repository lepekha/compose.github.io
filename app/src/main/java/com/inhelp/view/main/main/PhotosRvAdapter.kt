package com.inhelp.view.main.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inhelp.R
import com.inhelp.core.models.data.Menu
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element_photo.view.*


class PhotosRvAdapter(private val photoList: MutableList<String>, val clickListener: (menuItem: Menu) -> Unit = {}) : RecyclerView.Adapter<PhotosViewHolder>() {

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        return PhotosViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_photo, parent, false))
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        Picasso.get().load(photoList[position]).into(holder.imgPhoto)
    }
}

class PhotosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imgPhoto = view.imgPhoto
}
