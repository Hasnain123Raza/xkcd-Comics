package com.example.xkcdcomics.di

import android.app.Application
import androidx.room.Room
import com.example.xkcdcomics.database.ComicsDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideComicsDatabase(application: Application) : ComicsDatabase {
        return Room.databaseBuilder(application,
        ComicsDatabase::class.java, "comics")
            .fallbackToDestructiveMigration()
            .build()
    }

}