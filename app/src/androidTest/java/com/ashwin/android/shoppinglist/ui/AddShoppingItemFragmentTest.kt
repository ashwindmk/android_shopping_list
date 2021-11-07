package com.ashwin.android.shoppinglist.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.ashwin.android.shoppinglist.R
import com.ashwin.android.shoppinglist.data.local.ShoppingItem
import com.ashwin.android.shoppinglist.getOrAwaitValue
import com.ashwin.android.shoppinglist.launchFragmentInHiltContainer
import com.ashwin.android.shoppinglist.repository.AndroidTestShoppingRepository
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
class AddShoppingItemFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: ShoppingFragmentFactory

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun clickInsertIntoDb_shoppingItemInsertedIntoDb() {
        val androidTestShoppingViewModel = ShoppingViewModel(AndroidTestShoppingRepository())
        launchFragmentInHiltContainer<AddShoppingItemFragment>(
            fragmentFactory = fragmentFactory
        ) {
            shoppingViewModel = androidTestShoppingViewModel
        }

        Espresso.onView(ViewMatchers.withId(R.id.etShoppingItemName))
            .perform(ViewActions.replaceText("shopping item"))
        Espresso.onView(ViewMatchers.withId(R.id.etShoppingItemAmount))
            .perform(ViewActions.replaceText("2"))
        Espresso.onView(ViewMatchers.withId(R.id.etShoppingItemPrice))
            .perform(ViewActions.replaceText("5.0"))
        Espresso.onView(ViewMatchers.withId(R.id.btnAddShoppingItem))
            .perform(ViewActions.click())

        Truth.assertThat(androidTestShoppingViewModel.shoppingItems.getOrAwaitValue())
            .contains(ShoppingItem(null, "shopping item", 2, 5.0f, ""))
    }

    @Test
    fun clickShoppingImageView_navigateToImagePickFragment() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<AddShoppingItemFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(ViewMatchers.withId(R.id.ivShoppingImage)).perform(ViewActions.click())

        Mockito.verify(navController, Mockito.times(1))
            .navigate(AddShoppingItemFragmentDirections.actionAddShoppingItemFragmentToImagePickFragment())
    }

    @Test
    fun pressBackButton_popBackstack() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<AddShoppingItemFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.pressBack()

        Mockito.verify(navController, Mockito.times(1)).popBackStack()
    }
}
