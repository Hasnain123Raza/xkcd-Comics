package com.example.xkcdcomics.utility

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, url: String) {
    Glide.with(view.context).load(url).into(view)
}