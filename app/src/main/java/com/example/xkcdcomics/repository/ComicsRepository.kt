package com.example.xkcdcomics.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.xkcdcomics.database.ComicDatabase
import com.example.xkcdcomics.database.asDomainModel
import com.example.xkcdcomics.domain.XKCDComic
import com.example.xkcdcomics.domain.xkcdComicDefault
import com.example.xkcdcomics.network.ComicNetwork
import com.example.xkcdcomics.network.asDatabaseComic
import com.example.xkcdcomics.network.asDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComicsRepository(private val database: ComicDatabase) {

    // All Comics
    val comics: LiveData<List<XKCDComic>> = Transformations.map(database.comicDao.getComics()) {
        it.asDomainModel()
    }

    // Last Comic
    private val _lastComic = MutableLiveData(xkcdComicDefault)
    val lastComic: LiveData<XKCDComic>
        get() = _lastComic

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _lastComic.postValue(loadLatestComic())
        }
    }

    // Load the latest comic
    private suspend fun loadLatestComic(): XKCDComic? {
        return try {
            val comic = ComicNetwork.comic.getLatestComic()
            database.comicDao.insert(comic.asDatabaseComic())
            comic.asDomainModel()
        } catch(e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Load a comic identified by its num
    suspend fun loadComic(number: Int): XKCDComic? {
        val last = lastComic.value?.number ?:1
        val range = (1..last)
        if (!range.contains(number)) { return null }

        return comics.value?.firstOrNull { it.number == number }
            ?: try {
                val networkComic = ComicNetwork.comic.getComic(number)
                database.comicDao.insert(networkComic.asDatabaseComic())
                networkComic.asDomainModel()
            } catch(e: Exception) {
                e.printStackTrace()
                null
            }
    }

    // Go through the comic numbers and load the first few missing comics
    suspend fun loadComics(amountToLoad: Int) {
        if (comics.value == null) return
        if (lastComic.value == null) return

        var comicsLoaded = 0

        for (counter in (1..lastComic.value!!.number)) {
            if (comicsLoaded == amountToLoad) return
            if (comics.value!!.all { it.number != counter }) {
                comicsLoaded++
                loadComic(counter)
            }
        }
    }
}