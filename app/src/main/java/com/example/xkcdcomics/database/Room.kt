package com.example.xkcdcomics.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ComicDao {
    @Query("SELECT * FROM databasecomic")
    fun getComics(): LiveData<List<DatabaseComic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comics: List<DatabaseComic>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comic: DatabaseComic)
}

@Database(entities = [DatabaseComic::class], version = 4)
abstract class ComicsDatabase : RoomDatabase() {
    abstract val comicDao : ComicDao
}