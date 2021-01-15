package com.example.xkcdcomics.screens.list

import androidx.core.math.MathUtils
import com.example.xkcdcomics.domain.XKCDComic

class SearchFilter {

    companion object {
        fun getAscendingSelection(amountToSelect: Int, totalAmount: Int): IntRange {
            return (1..MathUtils.clamp(amountToSelect, 1, totalAmount))
        }

        fun getDescendingSelection(amountToSelect: Int, totalAmount: Int): IntRange {
            return (MathUtils.clamp(totalAmount - amountToSelect, 1, totalAmount)..totalAmount)
        }
    }

    enum class Setting {
        ASC, DESC
    }

    var searchString = ""
    var searchSetting = Setting.ASC

    fun applyFilter(amountOfComics: Int, comics: List<XKCDComic>): List<XKCDComic>? {
        // Get ascending or descending selection
        val selected: List<XKCDComic> = when (searchSetting) {
            Setting.ASC -> {
                val selection = getAscendingSelection(amountOfComics, comics.last().number)
                comics.filter { selection.contains(it.number) }
            }
            Setting.DESC -> {
                val selection = getDescendingSelection(amountOfComics, comics.last().number)
                comics.filter { selection.contains(it.number) }
            }
        }

        // Get ascending or descending order
        val ordered = when(searchSetting) {
            Setting.ASC -> selected
                .sortedBy { it.number }
            Setting.DESC -> selected
                .sortedByDescending { it.number }
        }

        // Get filtered list
        val filtered = if (searchString.isNotEmpty()) {
            ordered.filter {
                it.title.contains(searchString, true)
            }
        } else {
            ordered
        }

        return filtered
    }

}