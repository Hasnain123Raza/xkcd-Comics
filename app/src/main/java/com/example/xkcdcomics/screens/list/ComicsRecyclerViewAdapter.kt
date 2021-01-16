package com.example.xkcdcomics.screens.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.xkcdcomics.R
import com.example.xkcdcomics.databinding.FragmentlistComicsRecyclerlistItemBinding
import com.example.xkcdcomics.domain.XKCDComic

class ComicClick(val block: (XKCDComic) -> Unit) {
    fun onClick(comic: XKCDComic) = block(comic)
}

class ComicsRecyclerViewAdapter(private val comicClick: ComicClick) :
    RecyclerView.Adapter<ComicsRecyclerViewAdapter.ComicsRecyclerViewViewHolder>() {

    var comics: List<XKCDComic> = emptyList()
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    class ComicsRecyclerViewViewHolder(val binding: FragmentlistComicsRecyclerlistItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ComicsRecyclerViewViewHolder {
        val binding: FragmentlistComicsRecyclerlistItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), viewType,
            parent, false
        )
        return ComicsRecyclerViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComicsRecyclerViewViewHolder, position: Int) {
        holder.binding.also {
            it.comic = comics[position]
            it.comicCallback = comicClick
        }
    }

    override fun getItemCount(): Int {
        return comics.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.fragmentlist_comics_recyclerlist_item
    }
}