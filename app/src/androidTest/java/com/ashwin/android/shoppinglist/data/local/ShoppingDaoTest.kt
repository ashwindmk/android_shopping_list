package com.ashwin.android.shoppinglist.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.ashwin.android.shoppinglist.getOrAwaitValue
import com.ashwin.android.shoppinglist.launchFragmentInHiltContainer
import com.ashwin.android.shoppinglist.ui.ShoppingFragment
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

//@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@SmallTest
class ShoppingDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: ShoppingItemDatabase

    private lateinit var dao: ShoppingDao

    @Before
    fun setUp() {
//        database = Room.inMemoryDatabaseBuilder(
//                ApplicationProvider.getApplicationContext(),
//                ShoppingItemDatabase::class.java
//            )
//            .allowMainThreadQueries()
//            .build()
        hiltRule.inject()

        dao = database.shoppingDao()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun insertShoppingItemTest() = runBlockingTest {
        val shoppingItem = ShoppingItem(1, "name", 1, 1f, "url")
        dao.insertShoppingItem(shoppingItem)

        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        Truth.assertThat(allShoppingItems).contains(shoppingItem)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun deleteShoppingItemTest() = runBlockingTest {
        val shoppingItem = ShoppingItem(1, "name", 1, 1f, "url")
        dao.insertShoppingItem(shoppingItem)

        dao.deleteShoppingItem(shoppingItem)

        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        Truth.assertThat(allShoppingItems).doesNotContain(shoppingItem)
        Truth.assertThat(allShoppingItems).isEmpty()
    }

    @Test
    @ExperimentalCoroutinesApi
    fun observeTotalPriceTest() = runBlockingTest {
        val shoppingItem1 = ShoppingItem(1, "name-1", 1, 5f, "url1")
        val shoppingItem2 = ShoppingItem(2, "name-2", 2, 10f, "url2")
        val shoppingItem3 = ShoppingItem(3, "name-3", 1, 15f, "url3")

        dao.insertShoppingItem(shoppingItem1)
        dao.insertShoppingItem(shoppingItem2)
        dao.insertShoppingItem(shoppingItem3)

        val totalPriceSum = dao.observeTotalPrice().getOrAwaitValue()

        Truth.assertThat(totalPriceSum).isEqualTo((1 * 5f) + (2 * 10f) + (1 * 15f))
    }

    @After
    fun tearDown() {
        database.close()
    }
}
