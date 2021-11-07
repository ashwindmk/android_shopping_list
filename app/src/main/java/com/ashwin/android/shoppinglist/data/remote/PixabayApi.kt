package com.ashwin.android.shoppinglist.data.remote

import com.ashwin.android.shoppinglist.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {
    @GET("api/")
    suspend fun searchImage(
        @Query("q") query: String,
        @Query("key") apiKey: String = BuildConfig.PIXABAY_API_KEY
    ): Response<ImageResponse>
}
