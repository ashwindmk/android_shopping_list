package com.ashwin.android.shoppinglist.repository

import androidx.lifecycle.LiveData
import com.ashwin.android.shoppinglist.data.local.ShoppingDao
import com.ashwin.android.shoppinglist.data.local.ShoppingItem
import com.ashwin.android.shoppinglist.data.remote.ImageResponse
import com.ashwin.android.shoppinglist.data.remote.PixabayApi
import com.ashwin.android.shoppinglist.util.Resource
import javax.inject.Inject

class DefaultShoppingRepository @Inject constructor(
    private val shoppingDao: ShoppingDao,
    private val pixabayApi: PixabayApi
) : ShoppingRepository {
    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.insertShoppingItem(shoppingItem)
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.deleteShoppingItem(shoppingItem)
    }

    override fun observeAllShoppingItems(): LiveData<List<ShoppingItem>> {
        return shoppingDao.observeAllShoppingItems()
    }

    override fun observeTotalPrice(): LiveData<Float> {
        return shoppingDao.observeTotalPrice()
    }

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return try {
            val response = pixabayApi.searchImage(imageQuery)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.success(it)
                } ?: Resource.error("An unknown error occurred", null)
            } else {
                Resource.error("An unknown error occurred", null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't reach the server", null)
        }
    }
}
