package com.ashwin.android.shoppinglist.repository

import androidx.lifecycle.LiveData
import com.ashwin.android.shoppinglist.data.local.ShoppingItem
import com.ashwin.android.shoppinglist.data.remote.ImageResponse
import com.ashwin.android.shoppinglist.util.Resource

interface ShoppingRepository {
    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    fun observeAllShoppingItems(): LiveData<List<ShoppingItem>>

    fun observeTotalPrice(): LiveData<Float>

    suspend fun searchForImage(imageQuery: String): Resource<ImageResponse>
}
