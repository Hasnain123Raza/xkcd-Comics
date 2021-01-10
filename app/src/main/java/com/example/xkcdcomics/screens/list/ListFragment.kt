package com.example.xkcdcomics.screens.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xkcdcomics.R
import com.example.xkcdcomics.databinding.FragmentListBinding
import com.example.xkcdcomics.databinding.FragmentlistComicsRecyclerlistItemBinding
import com.example.xkcdcomics.domain.XKCDComic
import com.example.xkcdcomics.utility.canScrollVertically

class ListFragment() : Fragment() {

    lateinit var binding: FragmentListBinding
    lateinit var viewModel: ListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_list,
            container, false
        )

        viewModel = ViewModelProvider(
            this,
            ListViewModel.Factory(requireNotNull(activity).application)
        ).get(ListViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_choices_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.fragmentlistFilterSpinner.adapter = it
        }

        binding.fragmentlistFilterSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                with(viewModel) {
                    searchFilter = when (parent?.getItemAtPosition(position)) {
                        "asc" -> ListViewModel.SearchFilter.ASC
                        "desc" -> ListViewModel.SearchFilter.DESC
                        else -> ListViewModel.SearchFilter.ASC
                    }

                    fireUpdateRecyclerAdapterDataEvent()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        val comicsRecyclerViewAdapter = ComicsRecyclerViewAdapter(ComicClick {
            findNavController().navigate(ListFragmentDirections.actionListFragmentToSlideFragment())
        })

        binding.fragmentlistComicsRecyclerview.apply {
            adapter = comicsRecyclerViewAdapter
            layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findLastCompletelyVisibleItemPosition() + 1
                    val numberOfItems = recyclerView.adapter?.itemCount?:0

                    if (position >= numberOfItems) {
                        viewModel.loadComics(250)
                    }
                }
            })
        }

        viewModel.comics.observe(viewLifecycleOwner) {
            if (!binding.fragmentlistComicsRecyclerview.canScrollVertically()) {
                with(viewModel) {
                    comicsRecyclerViewAdapter.comics = applyFilter(comics.value!!)
                }
            }
        }

        viewModel.updateRecyclerAdapterDataEvent.observe(viewLifecycleOwner) {
            if (it) {
                with(viewModel) {
                    if (binding.fragmentlistComicsRecyclerview.canScrollVertically()) {
                        comicsRecyclerViewAdapter.comics = applyFilter(comics.value!!)
                    }
                    resetUpdateRecyclerAdapterDataEvent()
                }
            }
        }

        return binding.root
    }
}

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