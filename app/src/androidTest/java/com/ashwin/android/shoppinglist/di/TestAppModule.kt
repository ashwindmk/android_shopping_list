package com.ashwin.android.shoppinglist.di

import android.content.Context
import androidx.room.Room
import com.ashwin.android.shoppinglist.data.local.ShoppingItemDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {
    // This provides method should not be singleton since we want a new instance for every test.
    @Provides
    @Named("test_db")  // Else Hilt will be confused between AppModule and TestAppModule
    fun provideInMemoryDb(
        @ApplicationContext context: Context
    ) = Room.inMemoryDatabaseBuilder(context, ShoppingItemDatabase::class.java)
        .allowMainThreadQueries()
        .build()
}
