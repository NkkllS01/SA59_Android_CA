package iss.nus.edu.sg.fragments.workshop.ca5

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(
    private var imageList : MutableList<Any>,
    private val onSelectionChanged: (Int) -> Unit   // Callback to update selectedCountText
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    // Store selected images
    private val selectedImages = mutableSetOf<String>()    // Track selected image paths
    private var isSelectionEnabled = false

    fun getSelectedImages(): Set<String> = selectedImages

    // Enable selection when all images are downloaded
    fun enableSelection() {
        isSelectionEnabled = true
        notifyDataSetChanged()
    }

    class ImageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val imageView : ImageView = itemView.findViewById(R.id.image_view)
        val overlay : View = itemView.findViewById(R.id.selection_overlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = imageList[position]

        // Display image or placeholder
        if (item is Int) {              // Placeholder
            holder.imageView.setImageResource(item)
        } else if (item is String) {    // Actual image
            Glide.with(holder.imageView.context).load(item).into(holder.imageView)
        }

        // Determine if the image is selected
        val imagePath = if (item is String) item else ""
        val isSelected = selectedImages.contains(imagePath)

        // Log the state to debug
        Log.d(
            "ImageAdapter",
            "onBindViewHolder: Image at position $position, Selected: $isSelected"
        )

        // Set overlay visibility based on selection
        holder.overlay.visibility = if (isSelected) {
            Log.d(
                "ImageAdapter",
                "Overlay visible for position $position"
            )
            View.VISIBLE
        } else {
            Log.d(
                "ImageAdapter",
                "Overlay hidden for position $position"
            )
            View.GONE
        }

        // Handle click to toggle selection
        holder.itemView.setOnClickListener {
            if (!isSelectionEnabled) {
                Toast.makeText(
                    holder.itemView.context,
                    "Please wait until all images are downloaded.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (imagePath.isNotEmpty()) {
                // Toggle selection
                if (isSelected) {
                    selectedImages.remove(imagePath)
                } else if (selectedImages.size < 6) {
                    // Select the image
                    selectedImages.add(imagePath)
                } else {
                    // Show toast for exceeding selection limit
                    Toast.makeText(
                        holder.itemView.context,
                        "You can only select up to 6 images.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                // Update selection count and notify item state change
                onSelectionChanged(selectedImages.size)

                Log.d(
                    "ImageAdapter",
                    "Selected Images: $selectedImages"
                )

                // Notify only changed position to refresh its visibility state
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = imageList.size

    fun updateImageAtPosition(position: Int, imagePath: String) {
        if (position in 0 until imageList.size) {
            imageList[position] = imagePath
            notifyItemChanged(position)
        }
    }

    fun updateImages(newImages: List<Any>) {
        imageList.clear()
        imageList.addAll(newImages)
        notifyDataSetChanged()
    }
}