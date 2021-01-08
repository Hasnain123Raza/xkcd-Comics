package com.example.xkcdcomics.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.xkcdcomics.database.ComicDatabase
import com.example.xkcdcomics.database.asDomainModel
import com.example.xkcdcomics.domain.xkcdComic
import com.example.xkcdcomics.domain.xkcdComicDefault
import com.example.xkcdcomics.network.ComicNetwork
import com.example.xkcdcomics.network.asDatabaseComic
import com.example.xkcdcomics.network.asDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ComicsRepository(private val database: ComicDatabase) {

    private val comics: LiveData<List<xkcdComic>> = Transformations.map(database.comicDao.getComics()) {
        it.asDomainModel()
    }

    private val _lastComic = MutableLiveData(xkcdComicDefault)
    val lastComic: LiveData<xkcdComic>
        get() = _lastComic

    init {
        CoroutineScope(Dispatchers.Default).launch {
            _lastComic.postValue(loadLatestComic())
        }
    }

    suspend fun loadLatestComic() : xkcdComic {
        val comic = ComicNetwork.comic.getLatestComic()
        database.comicDao.insert(comic.asDatabaseComic())
        return comic.asDomainModel()
    }

    suspend fun loadComic(number: Int) : xkcdComic {
        val comic = comics.value?.first { it.number == number }
        return if (comic == null) {
            val networkComic = ComicNetwork.comic.getComic(number)
            database.comicDao.insert(networkComic.asDatabaseComic())
            networkComic.asDomainModel()
        } else {
            comic
        }
    }
}