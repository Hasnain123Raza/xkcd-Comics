package com.example.xkcdcomics.utility

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.canScrollVertically(): Boolean {
    return (canScrollVertically(1) || canScrollVertically(-1))
}

fun RecyclerView.hasScrolledToLastItem(): Boolean {
    val layoutManager = layoutManager as LinearLayoutManager
    val position = layoutManager.findLastCompletelyVisibleItemPosition() + 1
    val numberOfItems = adapter?.itemCount?:0

    return (position >= numberOfItems)
}