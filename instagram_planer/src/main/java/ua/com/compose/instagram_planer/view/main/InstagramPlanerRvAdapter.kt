package ua.com.compose.instagram_planer.view.main

import android.content.ClipData
import android.os.Build
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.module_instagram_planer_element_instagram_planer_image.view.*
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.animateScale
import ua.com.compose.extension.setVibrate
import ua.com.compose.instagram_planer.R
import ua.com.compose.instagram_planer.data.Image


class InstagramPlanerRvAdapter(
    val onPress: (position: Int) -> Unit,
    val onChange: (oldPosition: Int, newPosition: Int) -> Unit,
    val onStartDrag: () -> Unit = {},
    val onEndDrag: () -> Unit = {},
) : RecyclerView.Adapter<InstagramPlanerRvAdapter.ViewHolder>() {

    companion object {
        private const val SCALE_DRAG_ENTERED = 0.75f
        private const val SCALE_DRAG_EXITED = 1f

        const val CHANGE_ITEM_POSITION = 0
    }

    private var images: List<Image> = mutableListOf()

    fun updateImages(images: List<Image>){
        this.images = images
        this.notifyDataSetChanged()
    }

    fun addImages(images: List<Image>){
        this.images = images
        this.notifyDataSetChanged()
    }

    fun changeImages(images: List<Image>){
        this.images = images
    }

    fun removeImage(image: Image?){
        this.images.indexOfFirst { it.id == image?.id }.takeIf { it >= 0 }?.let { position ->
            this.images = this.images.toMutableList().apply {
                removeAt(position)
            }
            this.notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_instagram_planer_element_instagram_planer_image, parent, false)).apply {
            this.imgView.setVibrate(EVibrate.BUTTON)
            this.imgView.setOnClickListener {
                onPress(adapterPosition)
            }

            this.imgView.setOnDragListener { view, dragEvent ->
                when (dragEvent.action) {
                    DragEvent.ACTION_DRAG_ENDED -> {
                        this.imgView.animateScale(toScale = SCALE_DRAG_EXITED)
                        onEndDrag()
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        this.imgView.animateScale(toScale = SCALE_DRAG_ENTERED)
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        this.imgView.animateScale(toScale = SCALE_DRAG_EXITED)
                    }
                    DragEvent.ACTION_DROP -> {
                        val oldPosition = dragEvent.clipData.getItemAt(0).text.toString().toInt()
                        val newPosition = adapterPosition
                        if(oldPosition == newPosition){
                            this.imgView.animateScale(toScale = SCALE_DRAG_EXITED)
                        }else{
                            onChange(oldPosition, newPosition)
                        }
                        onEndDrag()
                    }
                }
                true
            }

            this.imgView.setOnLongClickListener {
                val data = ClipData.newPlainText("NAME", adapterPosition.toString())
                onStartDrag()
                this.imgView.animateScale(toScale = SCALE_DRAG_ENTERED)
                val shadowBuilder = View.DragShadowBuilder(it)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    it.startDragAndDrop(data, shadowBuilder, it, 0)
                } else {
                    it.startDrag(data, shadowBuilder, it, 0)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imgView.context).load(images[position].uri).centerInside().thumbnail(0.1f).into(holder.imgView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                CHANGE_ITEM_POSITION -> {
                    holder.imgView.animateScale(
                            toScale = 0f,
                            onEnd = {
                                Glide.with(holder.imgView.context).load(images[position].uri).centerInside().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).thumbnail(0.1f).into(holder.imgView)
                                holder.imgView.animateScale(toScale = 1f)
                            })
                }
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.imgView
    }
}
