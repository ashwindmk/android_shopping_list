package com.ashwin.android.shoppinglist.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ashwin.android.shoppinglist.Constant
import com.ashwin.android.shoppinglist.MainDispatcherRule
import com.ashwin.android.shoppinglist.getOrAwaitValueTest
import com.ashwin.android.shoppinglist.repository.TestShoppingRepository
import com.ashwin.android.shoppinglist.util.Status
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShoppingViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    @ExperimentalCoroutinesApi
    var mainCoroutineRule = MainDispatcherRule()

    private lateinit var shoppingViewModel: ShoppingViewModel

    @Before
    fun setUp() {
        shoppingViewModel = ShoppingViewModel(TestShoppingRepository())
    }

    @Test
    fun `insert shopping item with empty amount, return error`() {
        shoppingViewModel.insertShoppingItem("name", "", "5.0")

        val value = shoppingViewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long name, return error`() {
        val longName = buildString {
            for (i in 1..(Constant.MAX_NAME_INPUT_LENGTH + 1)) {
                append(1)
            }
        }
        shoppingViewModel.insertShoppingItem(longName, "1", "5.0")

        val value = shoppingViewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long price, return error`() {
        val longPrice = buildString {
            for (i in 1..(Constant.MAX_PRICE_INPUT_LENGTH + 1)) {
                append(1)
            }
        }
        shoppingViewModel.insertShoppingItem("name", "2", longPrice)

        val value = shoppingViewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too high amount, return error`() {
        shoppingViewModel.insertShoppingItem("name", "999999999999999999999", "5.0")

        val value = shoppingViewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too high amount, return success`() {
        shoppingViewModel.insertShoppingItem("name", "2", "5.0")

        val value = shoppingViewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @After
    fun tearDown() {
    }
}