package com.ashwin.android.shoppinglist.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.ashwin.android.shoppinglist.R
import com.ashwin.android.shoppinglist.adapter.ShoppingItemAdapter
import com.ashwin.android.shoppinglist.data.local.ShoppingItem
import com.ashwin.android.shoppinglist.getOrAwaitValue
import com.ashwin.android.shoppinglist.launchFragmentInHiltContainer
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class ShoppingFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var androidTestFragmentFactory: AndroidTestShoppingFragmentFactory

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun swipeShoppingItem_deleteItemInDb() {
        val shoppingItem = ShoppingItem(1, "test", 3, 5.0f, "test")
        var viewModel: ShoppingViewModel? = null

        launchFragmentInHiltContainer<ShoppingFragment>(fragmentFactory = androidTestFragmentFactory) {
            viewModel = shoppingViewModel
            shoppingViewModel?.insertShoppingItemInDb(shoppingItem)  // Insert item directly
        }

        Espresso.onView(ViewMatchers.withId(R.id.rvShoppingItems))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ShoppingItemAdapter.ShoppingItemViewHolder>(
                    0,
                    ViewActions.swipeLeft()
                )
            )

        Truth.assertThat(viewModel?.shoppingItems?.getOrAwaitValue()).isEmpty()
    }

    @Test
    fun clickAddShoppingItemButton_navigateToAddShoppingItemFragment() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<ShoppingFragment>(fragmentFactory = androidTestFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(ViewMatchers.withId(R.id.fabAddShoppingItem)).perform(ViewActions.click())

        Mockito.verify(
            navController,
            Mockito.times(1)
        ).navigate(
            ShoppingFragmentDirections.actionShoppingFragmentToAddShoppingItemFragment()
        )
    }
}
