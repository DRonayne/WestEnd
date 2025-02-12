package com.darach.westend.data.remote.api

import com.darach.westend.data.remote.dto.ShowDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ShowApi {
    @GET("macros/echo")
    suspend fun getShows(
        @Query("user_content_key") key: String,
        @Query("lib") lib: String
    ): List<ShowDto>
}