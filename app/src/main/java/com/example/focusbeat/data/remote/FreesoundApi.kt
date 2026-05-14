package com.example.focusbeat.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FreesoundApi {

    @GET("search/text/")
    suspend fun searchSounds(
        @Query("query") query: String,
        @Query("token") token: String,
        @Query("fields") fields: String = "id,name,username,previews,duration",
        @Query("page_size") pageSize: Int = 25
    ): FreesoundResponse
}