package com.example.xkcdcomics.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.xkcdcomics.database.ComicsDatabase
import com.example.xkcdcomics.database.asDomainModel
import com.example.xkcdcomics.domain.XKCDComic
import com.example.xkcdcomics.network.ComicsService
import com.example.xkcdcomics.network.asDatabaseComic
import com.example.xkcdcomics.network.asDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComicsRepository @Inject constructor(
    database: ComicsDatabase,
    comicsService: ComicsService
) {

    // Shortcuts
    private val comicDao = database.comicDao
    private val comicsService = comicsService

    // All Comics
    val comics: LiveData<List<XKCDComic>> = Transformations
        .map(comicDao.getComics()) {
        it.asDomainModel()
    }

    // Last Comic
    private val _latestComic = MutableLiveData<XKCDComic>()
    val latestComic: LiveData<XKCDComic>
        get() = _latestComic

    init {
        CoroutineScope(Dispatchers.IO).launch {
            var latestComic = loadLatestComic()
            while (latestComic == null) {
                delay(5000)
                latestComic = loadLatestComic()
            }

            _latestComic.postValue(latestComic)
        }
    }

    // Load the latest comic
    private suspend fun loadLatestComic(): XKCDComic? {
        return try {
            val comic = comicsService.getLatestComic()
            comicDao.insert(comic.asDatabaseComic())
            comic.asDomainModel()
        } catch(e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Load a comic identified by its num
    suspend fun loadComic(number: Int): XKCDComic? {
        val last = latestComic.value?.number ?:1
        val range = (1..last)
        if (!range.contains(number)) { return null }

        return comics.value?.firstOrNull { it.number == number }
            ?: try {
                val networkComic = comicsService.getComic(number)
                comicDao.insert(networkComic.asDatabaseComic())
                networkComic.asDomainModel()
            } catch(e: Exception) {
                e.printStackTrace()
                null
            }
    }

    // Load comics by selection
    suspend fun loadComics(range: IntRange): List<XKCDComic>? {
        if (comics.value == null) return null

        for (counter in range) {
            if (comics.value!!.all { it.number != counter }) {
                loadComic(counter)
            }
        }

        return comics.value?.filter { range.contains(it.number) }
    }
}