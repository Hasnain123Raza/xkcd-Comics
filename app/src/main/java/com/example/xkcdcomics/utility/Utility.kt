package com.example.xkcdcomics.utility

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.canScrollVertically(): Boolean {
    return (canScrollVertically(1) || canScrollVertically(-1))
}

