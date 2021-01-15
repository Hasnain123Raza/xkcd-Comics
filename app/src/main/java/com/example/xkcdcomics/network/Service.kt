package com.example.xkcdcomics.network

import retrofit2.http.GET
import retrofit2.http.Path


interface ComicsService {
    @GET("{number}/info.0.json")
    suspend fun getComic(@Path("number") number: Int): NetworkComic

    @GET("info.0.json")
    suspend fun getLatestComic(): NetworkComic
}