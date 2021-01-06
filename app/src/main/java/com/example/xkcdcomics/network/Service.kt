package com.example.xkcdcomics.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


interface ComicService {
    @GET("{number}/info.0.json")
    suspend fun getComic(@Path("number") number: Int): NetworkComic

    @GET("info.0.json")
    suspend fun getLatestComic(): NetworkComic
}

object ComicNetwork {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://xkcd.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val comic = retrofit.create(ComicService::class.java)
}