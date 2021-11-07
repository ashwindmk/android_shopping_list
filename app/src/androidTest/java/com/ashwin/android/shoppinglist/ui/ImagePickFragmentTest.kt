package com.ashwin.android.shoppinglist.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.ashwin.android.shoppinglist.R
import com.ashwin.android.shoppinglist.adapter.ImageAdapter
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
class ImagePickFragmentTest {
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
    fun clickImage_popBackstackAndSetImageUrl() {
        val imageUrl = "test"
        val androidTestShoppingViewModel = ShoppingViewModel(AndroidTestShoppingRepository())
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<ImagePickFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            imageAdapter.images = listOf(imageUrl)
            shoppingViewModel = androidTestShoppingViewModel
        }

        Espresso.onView(ViewMatchers.withId(R.id.rvImages)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(
            ViewMatchers.withId(R.id.rvImages)
        ).perform(
            RecyclerViewActions.actionOnItemAtPosition<ImageAdapter.ImageViewHolder>(
                0,
                ViewActions.click()
            )
        )

        Mockito.verify(navController).popBackStack()
        Truth.assertThat(androidTestShoppingViewModel.currentImageUrl.getOrAwaitValue()).isEqualTo(imageUrl)
    }
}
