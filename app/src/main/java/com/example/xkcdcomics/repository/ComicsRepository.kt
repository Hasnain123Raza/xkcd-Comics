package com.example.xkcdcomics.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.xkcdcomics.database.ComicDatabase
import com.example.xkcdcomics.database.asDomainModel
import com.example.xkcdcomics.domain.xkcdComic
import com.example.xkcdcomics.network.ComicNetwork
import com.example.xkcdcomics.network.asDatabaseComic
import com.example.xkcdcomics.network.asDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComicsRepository(val database: ComicDatabase) {

    val comics: LiveData<List<xkcdComic>> = Transformations.map(database.comicDao.getComics()) {
        it.asDomainModel()
    }

    suspend fun loadLatestComic() : xkcdComic {
        val comic = ComicNetwork.comic.getLatestComic()
        database.comicDao.insert(comic.asDatabaseComic())
        return comic.asDomainModel()
    }

    suspend fun loadComic(number: Int) : xkcdComic {
        val comic = ComicNetwork.comic.getComic(number)
        database.comicDao.insert(comic.asDatabaseComic())
        return comic.asDomainModel()
    }
}