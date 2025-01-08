package iss.nus.edu.sg.fragments.workshop.ca5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlayImageAdapter(
    private val images: List<String>,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<PlayImageAdapter.ImageViewHolder>() {
    //Code by Chen Sirui
    private val flippedStates = MutableList(images.size) { false }
    private val matchedStates = MutableList(images.size) { false }
    private val errorStates = MutableList(images.size) { false }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (!flippedStates[position]) {
                    onImageClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePath = images[position]
        if (flippedStates[position]) {
            Glide.with(holder.imageView.context)
                .load(imagePath)
                .placeholder(R.drawable.placeholder)  // 加载占位图
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder)
        }
        when {
            matchedStates[position]->{
                holder.imageView.findViewById<View>(R.id.border_view).setBackgroundResource(R.drawable.green_border)
            }
            errorStates[position]->{
                holder.imageView.findViewById<View>(R.id.border_view).setBackgroundResource(R.drawable.red_border)
            }
            else ->{
                holder.imageView.findViewById<View>(R.id.border_view).setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    override fun getItemCount(): Int = images.size

    fun flipImage(position: Int) {
        flippedStates[position] = true
        notifyItemChanged(position)
    }

    fun hideImage(position: Int) {
        flippedStates[position] = false
        notifyItemChanged(position)
    }

    fun isFlipped(position: Int): Boolean = flippedStates[position]

    fun setMatched(position: Int, isMatched: Boolean) {
        matchedStates[position] = isMatched
        notifyItemChanged(position)
    }

    fun setError(position: Int, isError: Boolean) {
        errorStates[position] = isError
        notifyItemChanged(position)
    }

    fun clearBorders() {
        matchedStates.fill(false)
        errorStates.fill(false)
        notifyDataSetChanged() // 清除所有边框
    }
}