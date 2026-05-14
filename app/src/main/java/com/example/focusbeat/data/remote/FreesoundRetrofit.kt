package com.example.focusbeat.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FreesoundRetrofit {

    val api: FreesoundApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://freesound.org/apiv2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FreesoundApi::class.java)
    }
}