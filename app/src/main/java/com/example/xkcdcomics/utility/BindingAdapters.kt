package com.example.xkcdcomics.utility

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.*
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, url: String) {
    Glide
        .with(view.context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(view)
}

@BindingAdapter("onEditorAction")
fun setOnEditorActionListener(view: EditText, editorActionListener: TextView.OnEditorActionListener) {
    view.setOnEditorActionListener(editorActionListener)
}