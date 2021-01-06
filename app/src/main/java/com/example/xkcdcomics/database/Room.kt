package com.example.xkcdcomics.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ComicDao {
    @Query("SELECT * FROM databasecomic")
    fun getComics(): LiveData<List<DatabaseComic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(comics: List<DatabaseComic>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(comic: DatabaseComic)
}

@Database(entities = [DatabaseComic::class], version = 2)
abstract class ComicDatabase : RoomDatabase() {
    abstract val comicDao : ComicDao
}

private lateinit var INSTANCE: ComicDatabase

fun getComicDatabase(context: Context): ComicDatabase {
    synchronized(ComicDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                ComicDatabase::class.java,
                "comics")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}