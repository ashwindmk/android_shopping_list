package com.ashwin.android.shoppinglist.di

import android.content.Context
import androidx.room.Room
import com.ashwin.android.shoppinglist.Constant
import com.ashwin.android.shoppinglist.R
import com.ashwin.android.shoppinglist.data.local.ShoppingDao
import com.ashwin.android.shoppinglist.data.local.ShoppingItemDatabase
import com.ashwin.android.shoppinglist.data.remote.PixabayApi
import com.ashwin.android.shoppinglist.repository.DefaultShoppingRepository
import com.ashwin.android.shoppinglist.repository.ShoppingRepository
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideShoppingItemDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, ShoppingItemDatabase::class.java, Constant.DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideShoppingDao(database: ShoppingItemDatabase) = database.shoppingDao()

    @Provides
    @Singleton
    fun providePixabayApi() = Retrofit.Builder()
        .baseUrl(Constant.PIXABAY_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PixabayApi::class.java)

    @Provides
    @Singleton
    fun provideShoppingRepository(
        shoppingDao: ShoppingDao,
        pixabayApi: PixabayApi
    ) = DefaultShoppingRepository(shoppingDao, pixabayApi) as ShoppingRepository

    @Provides
    @Singleton
    fun provideGlide(
        @ApplicationContext context: Context
    ): RequestManager = Glide
        .with(context)
        .setDefaultRequestOptions(RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
        )
}
