package com.example.xkcdcomics.domain

data class XKCDComic(
    val number: Int,
    val title: String,
    val imageUrl: String,
    val day: Int,
    val month: Int,
    val year: Int
)

val xkcdComicDefault = XKCDComic(
    number = 1,
    title = "Barrel - Part 1",
    imageUrl = "https://imgs.xkcd.com/comics/barrel_cropped_(1).jpg",
    day = 1,
    month = 1,
    year = 2006
)