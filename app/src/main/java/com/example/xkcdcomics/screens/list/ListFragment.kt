package com.example.xkcdcomics.screens.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xkcdcomics.R
import com.example.xkcdcomics.XKCDComicsApplication
import com.example.xkcdcomics.databinding.FragmentListBinding
import com.example.xkcdcomics.utility.canScrollVertically
import com.example.xkcdcomics.utility.hasScrolledToLastItem
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate

class ListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentListBinding
    lateinit var viewModel: ListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_list,
            container, false
        )

        (requireActivity().application as XKCDComicsApplication).applicationComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ListViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        // region INITIALIZE SPINNER //
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_choices_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.fragmentlistFilterSpinner.adapter = it
        }
        // endregion //

        // region SPINNER CALLBACKS //
        binding.fragmentlistFilterSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                with(viewModel) {
                    searchFilter.searchSetting = when (parent?.getItemAtPosition(position)) {
                        "asc" -> SearchFilter.Setting.ASC
                        "desc" -> SearchFilter.Setting.DESC
                        else -> SearchFilter.Setting.ASC
                    }

                    fireResetRecyclerViewPositionEvent()
                    fireUpdateRecyclerAdapterDataEvent()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // endregion //

        // region INITIALIZE RECYCLERVIEW //
        val comicsRecyclerViewAdapter = ComicsRecyclerViewAdapter(ComicClick {
            findNavController().navigate(ListFragmentDirections.actionListFragmentToSlideFragment(it.number))
        })

        binding.fragmentlistComicsRecyclerview.apply {
            adapter = comicsRecyclerViewAdapter
            layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (recyclerView.hasScrolledToLastItem()) {
                        viewModel.loadComics(50)
                    }
                }
            })
        }
        // endregion //

        // EVENTS //
        var isTimerActive = false
        viewModel.updateRecyclerAdapterDataEvent.observe(viewLifecycleOwner) {
            if (it) {
                with(viewModel) {
                    if (!binding.fragmentlistComicsRecyclerview.canScrollVertically()) {
                        if (!isTimerActive) {
                            isTimerActive = true
                            Timer("", true)
                                .scheduleAtFixedRate(0L, 2500L) {
                                if (!binding.fragmentlistComicsRecyclerview.canScrollVertically()) {
                                    loadComics(50)
                                } else {
                                    cancel()
                                    isTimerActive = false
                                }
                            }
                        }
                    }

                    comicsRecyclerViewAdapter.comics = applyFilter() ?: emptyList()
                    resetUpdateRecyclerAdapterDataEvent()
                }
            }
        }

        viewModel.resetRecyclerViewPositionEvent.observe(viewLifecycleOwner) {
            if (it) {
                binding.fragmentlistComicsRecyclerview.scrollToPosition(0)
                viewModel.resetResetRecyclerViewPositionEvent()
            }
        }
        ////

        // region OBSERVE LIVEDATA //
        viewModel.comics.observe(viewLifecycleOwner) {}
        viewModel.latestComic.observe(viewLifecycleOwner) {}
        // endregion //

        return binding.root
    }
}