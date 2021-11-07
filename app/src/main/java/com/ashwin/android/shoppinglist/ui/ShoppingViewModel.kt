package com.ashwin.android.shoppinglist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashwin.android.shoppinglist.Constant
import com.ashwin.android.shoppinglist.data.local.ShoppingItem
import com.ashwin.android.shoppinglist.data.remote.ImageResponse
import com.ashwin.android.shoppinglist.repository.ShoppingRepository
import com.ashwin.android.shoppinglist.util.Event
import com.ashwin.android.shoppinglist.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {
    val shoppingItems = shoppingRepository.observeAllShoppingItems()
    val totalPrice = shoppingRepository.observeTotalPrice()

    private val _images = MutableLiveData<Event<Resource<ImageResponse>>>()
    val images: LiveData<Event<Resource<ImageResponse>>> = _images

    private val _currentImageUrl = MutableLiveData<String>()
    val currentImageUrl: LiveData<String> = _currentImageUrl

    private val _insertShoppingItemStatus = MutableLiveData<Event<Resource<ShoppingItem>>>()
    val insertShoppingItemStatus: LiveData<Event<Resource<ShoppingItem>>> = _insertShoppingItemStatus

    fun setCurrentImageUrl(url: String) {
        _currentImageUrl.postValue(url)
    }

    fun insertShoppingItem(inputName: String, inputAmount: String, inputPrice: String) {
        if (inputName.isEmpty() || inputAmount.isEmpty() || inputPrice.isEmpty()) {
            _insertShoppingItemStatus.postValue(Event(Resource.error("Fields cannot be empty.", null)))
            return
        }
        if (inputName.length > Constant.MAX_NAME_INPUT_LENGTH) {
            _insertShoppingItemStatus.postValue(Event(Resource.error("Name must not exceed ${Constant.MAX_NAME_INPUT_LENGTH} characters.", null)))
            return
        }
        if (inputPrice.length > Constant.MAX_PRICE_INPUT_LENGTH) {
            _insertShoppingItemStatus.postValue(Event(Resource.error("Price must not exceed ${Constant.MAX_PRICE_INPUT_LENGTH} characters.", null)))
            return
        }
        val amount = try {
            inputAmount.toInt()
        } catch (e: Exception) {
            _insertShoppingItemStatus.postValue(Event(Resource.error("Invalid amount", null)))
            return
        }

        val shoppingItem = ShoppingItem(null, inputName, amount, inputPrice.toFloat(), _currentImageUrl.value ?: "")
        insertShoppingItemInDb(shoppingItem)

        setCurrentImageUrl("")  // Since this ViewModel is being reused in ShoppingFragment on return.

        _insertShoppingItemStatus.postValue(Event(Resource.success(shoppingItem)))
    }

    fun searchForImage(inputQuery: String) {
        if (inputQuery.isEmpty()) {
            return
        }

        _images.value = Event(Resource.loading(null))

        viewModelScope.launch {
            val response = shoppingRepository.searchForImage(inputQuery)
            _images.value = Event(response)
        }
    }

    fun insertShoppingItemInDb(shoppingItem: ShoppingItem) = viewModelScope.launch {
        shoppingRepository.insertShoppingItem(shoppingItem)
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItem) = viewModelScope.launch {
        shoppingRepository.deleteShoppingItem(shoppingItem)
    }
}
