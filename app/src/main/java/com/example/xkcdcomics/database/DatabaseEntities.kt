package com.example.xkcdcomics.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.xkcdcomics.domain.xkcdComic

@Entity
data class DatabaseComic(
    @ColumnInfo(name = "number")
    @PrimaryKey
    val number: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    @ColumnInfo(name = "day")
    val day: Int,

    @ColumnInfo(name = "month")
    val month: Int,

    @ColumnInfo(name = "year")
    val year: Int
)

fun List<DatabaseComic>.asDomainModel(): List<xkcdComic> {
    return map {
        xkcdComic(
            number = it.number,
            title = it.title,
            imageUrl = it.imageUrl,
            day = it.day,
            month = it.month,
            year = it.year
        )
    }
}