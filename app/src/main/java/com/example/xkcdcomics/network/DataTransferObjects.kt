package com.example.xkcdcomics.network

import com.example.xkcdcomics.database.DatabaseComic
import com.example.xkcdcomics.domain.xkcdComic
import com.squareup.moshi.Json

data class NetworkComic(
    @Json(name = "num")
    val number: Int,

    @Json(name = "safe_title")
    val title: String,

    @Json(name = "img")
    val imageUrl: String,

    @Json(name = "day")
    val day: Int,

    @Json(name = "month")
    val month: Int,

    @Json(name = "year")
    val year: Int,
)

fun NetworkComic.asDatabaseComic(): DatabaseComic {
    return DatabaseComic(
        number = number,
        title = title,
        imageUrl = imageUrl,
        day = day,
        month = month,
        year = year
    )
}

fun NetworkComic.asDomainModel() : xkcdComic {
    return xkcdComic(
        number = number,
        title = title,
        imageUrl = imageUrl,
        day = day,
        month = month,
        year = year
    )
}