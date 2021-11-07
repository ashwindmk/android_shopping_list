package com.ashwin.android.shoppinglist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_item")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var name: String,
    var amount: Int,
    var price: Float,
    var imageUrl: String
)
