package com.ashwin.android.shoppinglist.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ashwin.android.shoppinglist.data.local.ShoppingItem
import com.ashwin.android.shoppinglist.data.remote.ImageResponse
import com.ashwin.android.shoppinglist.util.Resource

class TestShoppingRepository : ShoppingRepository {
    private val shoppingItems = mutableListOf<ShoppingItem>()

    private val observableShoppingItems = MutableLiveData<List<ShoppingItem>>(shoppingItems)
    private val observableTotalPrice = MutableLiveData<Float>()

    private var shouldReturnNetworkError = false

    fun shouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun getTotalPrice(): Float {
        return shoppingItems.sumOf { it.price.toDouble() }.toFloat()
    }

    private fun refreshLiveData() {
        observableShoppingItems.postValue(shoppingItems)
        observableTotalPrice.postValue(getTotalPrice())
    }

    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.add(shoppingItem)
        refreshLiveData()
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.remove(shoppingItem)
        refreshLiveData()
    }

    override fun observeAllShoppingItems(): LiveData<List<ShoppingItem>> {
        return observableShoppingItems
    }

    override fun observeTotalPrice(): LiveData<Float> {
        return observableTotalPrice
    }

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return if (shouldReturnNetworkError) {
            Resource.error("Fake error", null)
        } else {
            Resource.success(ImageResponse(listOf(), 0, 0))
        }
    }
}
