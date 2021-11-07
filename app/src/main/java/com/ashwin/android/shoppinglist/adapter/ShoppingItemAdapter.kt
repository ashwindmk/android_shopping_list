package com.ashwin.android.shoppinglist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ashwin.android.shoppinglist.data.local.ShoppingItem
import com.ashwin.android.shoppinglist.databinding.ItemShoppingBinding
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class ShoppingItemAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<ShoppingItemAdapter.ShoppingItemViewHolder>() {
    inner class ShoppingItemViewHolder(val binding: ItemShoppingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(shoppingItem: ShoppingItem) {
            binding.apply {
                glide
                    .load(shoppingItem.imageUrl)
                    .into(ivShoppingImage)
                tvName.text = shoppingItem.name
                tvShoppingItemAmount.text = "x ${shoppingItem.amount}"
                tvShoppingItemPrice.text = "${shoppingItem.price}€"
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ShoppingItem>() {
        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var shoppingItems: List<ShoppingItem>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingItemViewHolder {
        val binding = ItemShoppingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return shoppingItems.size
    }

    override fun onBindViewHolder(holder: ShoppingItemViewHolder, position: Int) {
        val shoppingItem = shoppingItems[position]
        holder.bind(shoppingItem)
    }
}
