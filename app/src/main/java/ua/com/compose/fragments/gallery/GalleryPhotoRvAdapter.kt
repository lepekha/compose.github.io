package ua.com.compose.fragments.gallery

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ua.com.compose.R
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate


class GalleryPhotoRvAdapter(val context: Context, var images: List<Uri> = listOf(), val selectedImage: MutableList<Uri>, val onPress: (value: Uri, isLongPress: Boolean) -> Unit, val onUpdateBadge: () -> Unit) : RecyclerView.Adapter<GalleryPhotoRvAdapter.ViewHolder>() {

    companion object {
        const val CHANGE_ITEM = 0
        const val CHANGE_BADGE = 1
        const val CHANGE_CLEAR_SELECT = 2
    }


    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_gallery_element_gallery_images, parent, false)).apply {
            this.imgView.setOnClickListener {
                this.imgView.context.vibrate(type = EVibrate.BUTTON)
                onPress(images[adapterPosition], false)
                animateScale(this, selectedImage.contains(images[adapterPosition]))
            }

            this.imgView.setOnLongClickListener {
                this.imgView.context.vibrate(type = EVibrate.BUTTON_LONG)
                onPress(images[adapterPosition], true)
                animateScale(this, selectedImage.contains(images[adapterPosition]))
                true
            }
        }
    }

    private fun animateScale(holder: ViewHolder, isSelected: Boolean){
        val scale = if(isSelected){
            holder.badge.text = (selectedImage.indexOf(images[holder.adapterPosition]) + 1).toString()
            scaleAnimation(holder, 1f)
            0.8f
        }else{
            scaleAnimation(holder, 0f)
            1f
        }

        val scaleDownX = ObjectAnimator.ofFloat(holder.imgView, "scaleX", scale)
        val scaleDownY = ObjectAnimator.ofFloat(holder.imgView, "scaleY", scale)
        scaleDownX.duration = 200
        scaleDownY.duration = 200

        val scaleDown = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDown.start()
    }

    private fun scaleAnimation(holder: ViewHolder, scale: Float){
        val scaleDownX = ObjectAnimator.ofFloat(holder.badge, "scaleX", scale)
        val scaleDownY = ObjectAnimator.ofFloat(holder.badge, "scaleY", scale)
        scaleDownX.duration = 200
        scaleDownY.duration = 200

        val scaleDown = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDown.doOnEnd {
            onUpdateBadge()
        }
        scaleDown.start()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imgView.context).load(images[position]).centerInside().thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imgView)
        holder.badge.text = (selectedImage.indexOf(images[holder.adapterPosition]) + 1).toString()
        if(selectedImage.contains(images[position])){
            holder.imgView.scaleX = 0.7f
            holder.imgView.scaleY = 0.7f
            holder.badge.scaleX = 1f
            holder.badge.scaleY = 1f
        }else{
            holder.imgView.scaleX = 1f
            holder.imgView.scaleY = 1f
            holder.badge.scaleX = 0f
            holder.badge.scaleY = 0f
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                CHANGE_BADGE -> {
                    holder.badge.text = (selectedImage.indexOf(images[holder.adapterPosition]) + 1).toString()
                }
                CHANGE_CLEAR_SELECT -> {
                    animateScale(holder, selectedImage.contains(images[position]))
                }
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.findViewById(R.id.imgView)
        val badge: TextView = view.findViewById(R.id.badge)
    }
}
