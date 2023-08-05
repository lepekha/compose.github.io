package ua.com.compose.fragments.palette

import android.graphics.Color
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.R
import ua.com.compose.data.ColorPallet
import ua.com.compose.databinding.ModuleOtherColorPickElementButtonBinding
import ua.com.compose.databinding.ModuleOtherColorPickElementPalletBinding
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.colorToMonochrome
import ua.com.compose.extension.dp
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.extension.vibrate


class PalletsRvAdapter(private val onPressItem: (item: ColorPallet) -> Unit,
                       private val onPressAddPallet: () -> Unit,
                       private val onPressChangePallet: (colorId: Long, palletId: Long?) -> Unit,
                       private val onPressRemove: (item: ColorPallet) -> Unit,
                       private val onPressShare: (item: ColorPallet) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_BUTTON = 0
        const val VIEW_TYPE_PALLET = 1
    }

    val cards = mutableListOf<Card>()

    fun update(newList: List<Card>){
        this.cards.clear()
        this.cards.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when(cards[position]) {
            is Card.CardPallet -> VIEW_TYPE_PALLET
            else -> VIEW_TYPE_BUTTON
        }
    }

    override fun getItemCount() = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            VIEW_TYPE_PALLET -> {
                val binding = ModuleOtherColorPickElementPalletBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolderPallet(binding).apply {
                    binding.root.setOnDragListener { _, dragEvent ->
                        when (dragEvent.action) {
                            DragEvent.ACTION_DRAG_ENTERED -> {
                                this.binding.root.context.vibrate(EVibrate.BUTTON)
                                binding.rootCard.strokeColor = binding.rootCard.context.getColorFromAttr(R.attr.color_5)
                            }
                            DragEvent.ACTION_DRAG_EXITED -> {
                                binding.rootCard.strokeColor = Color.TRANSPARENT
                            }
                            DragEvent.ACTION_DRAG_STARTED -> {
                                binding.colorGrid.isVisible = false
                                binding.container.isVisible = false
                                binding.placeholder.isVisible = false
                                binding.rootCard.strokeColor = Color.TRANSPARENT
                                binding.placeholderAdd.isVisible = true
                            }
                            DragEvent.ACTION_DROP -> {
                                val intent = dragEvent.clipData.getItemAt(0)?.intent ?: return@setOnDragListener false
                                onPressChangePallet.invoke(intent.getLongExtra(ColorsRvAdapter.CLIP_DATA, -1), (cards[adapterPosition] as Card.CardPallet).item.id)
                            }
                            DragEvent.ACTION_DRAG_ENDED -> {
                                binding.colorGrid.isVisible = true
                                binding.container.isVisible = true
                                binding.placeholder.isVisible = true
                                binding.placeholderAdd.isVisible = false
                                notifyDataSetChanged()
                            }
                        }
                        true
                    }


                    binding.root.setOnClickListener {
                        this.binding.root.context.vibrate(EVibrate.BUTTON)
                        onPressItem.invoke((cards[adapterPosition] as Card.CardPallet).item)
                    }
                    binding.btnRemove.setOnClickListener {
                        this.binding.btnRemove.context.vibrate(EVibrate.BUTTON)
                        onPressRemove.invoke((cards[adapterPosition] as Card.CardPallet).item)
                    }
                    binding.btnShare.setOnClickListener {
                        this.binding.btnShare.context.vibrate(EVibrate.BUTTON)
                        onPressShare.invoke((cards[adapterPosition] as Card.CardPallet).item)
                    }
                }
            }
            else -> {
                val binding = ModuleOtherColorPickElementButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolderButton(binding).apply {
                    binding.root.setOnDragListener { _, dragEvent ->
                        when (dragEvent.action) {
                            DragEvent.ACTION_DRAG_ENTERED -> {
                                this.binding.root.context.vibrate(EVibrate.BUTTON)
                                binding.btnAddPallet.strokeWidth = 1.dp.toInt()
                                binding.btnAddPallet.strokeColor = binding.btnAddPallet.context.getColorFromAttr(R.attr.color_5)
                            }
                            DragEvent.ACTION_DRAG_EXITED -> {
                                binding.btnAddPallet.strokeColor = Color.TRANSPARENT
                            }
                            DragEvent.ACTION_DRAG_STARTED -> {
                                binding.icon.setImageResource(R.drawable.ic_add_fill)
                                binding.btnAddPallet.strokeColor = Color.TRANSPARENT
                            }
                            DragEvent.ACTION_DROP -> {
                                val intent = dragEvent.clipData.getItemAt(0)?.intent ?: return@setOnDragListener false
                                onPressChangePallet.invoke(intent.getLongExtra(ColorsRvAdapter.CLIP_DATA, -1), null)
                            }
                            DragEvent.ACTION_DRAG_ENDED -> {
                                binding.btnAddPallet.strokeWidth = 0
                                binding.icon.setImageResource(R.drawable.ic_palette_add)
                                notifyDataSetChanged()
                            }
                        }
                        true
                    }

                    binding.root.setOnClickListener {
                        this.binding.root.context.vibrate(EVibrate.BUTTON)
                        onPressAddPallet.invoke()
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = cards[position]
        when(getItemViewType(position)) {
            VIEW_TYPE_PALLET -> {
                (holder as ViewHolderPallet).bind(item as Card.CardPallet)
            }
            else -> {
                (holder as ViewHolderButton).bind(item as Card.CardButton)
            }
        }

    }

    inner class ViewHolderPallet(val binding: ModuleOtherColorPickElementPalletBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card.CardPallet) {
            if(card.isCurrent) {
                binding.txtTitle.setTextColor(binding.rootCard.context.getColorFromAttr(R.attr.color_6))
                binding.rootCard.strokeColor = binding.rootCard.context.getColorFromAttr(R.attr.color_6)
            } else {
                binding.txtTitle.setTextColor(binding.rootCard.context.getColorFromAttr(R.attr.color_9))
                binding.rootCard.strokeColor = Color.TRANSPARENT
            }
            binding.txtTitle.text = card.item.name
            binding.colorGrid.setColors(card.colors)
            binding.btnShare.isVisible = card.isCurrent && card.colors.isNotEmpty()
            binding.btnRemove.isVisible = card.isCurrent
        }
    }

    inner class ViewHolderButton(val binding: ModuleOtherColorPickElementButtonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card.CardButton) {
            binding.icon.setImageResource(card.iconResId)
        }
    }
}
