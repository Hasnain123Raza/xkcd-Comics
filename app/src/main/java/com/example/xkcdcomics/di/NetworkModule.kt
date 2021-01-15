package com.example.xkcdcomics.di

import com.example.xkcdcomics.network.ComicsService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideComicsRetrofitService() : ComicsService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://xkcd.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ComicsService::class.java)
    }
}