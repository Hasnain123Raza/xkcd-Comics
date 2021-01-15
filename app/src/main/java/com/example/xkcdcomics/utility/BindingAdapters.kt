package com.example.xkcdcomics.utility

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
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

@BindingAdapter("showHide")
fun showHide(view: ContentLoadingProgressBar, showHide: Boolean) {
    if (showHide) {
        view.show()
    } else {
        view.hide()
    }
}