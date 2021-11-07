package com.ashwin.android.shoppinglist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ashwin.android.shoppinglist.R
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class ImageAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: ((String) -> Unit)) {
        onItemClickListener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var images: List<String>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = images[position]
        holder.itemView.apply {
            val ivShoppingImage = this.findViewById<ImageView>(R.id.shopping_image_view)
            println("glide: $glide, url: $url, iv: $ivShoppingImage")
            glide
                .load(url)
                .into(ivShoppingImage)
            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(url)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}
