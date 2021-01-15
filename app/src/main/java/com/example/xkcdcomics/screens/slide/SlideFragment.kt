package com.example.xkcdcomics.screens.slide

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.xkcdcomics.R
import com.example.xkcdcomics.XKCDComicsApplication
import com.example.xkcdcomics.databinding.FragmentSlideBinding
import javax.inject.Inject


class SlideFragment : Fragment() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentSlideBinding
    lateinit var viewModel: SlideViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragmentslide_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.fragmentslidemenu_share_menuitem -> {
                viewModel.onShareMenuItemSelected()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_slide,
            container, false
        )

        (requireActivity().application as XKCDComicsApplication).applicationComponent.inject(this)
        viewModel = ViewModelProvider(this, modelFactory).get(SlideViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.loadComic(SlideFragmentArgs.fromBundle(requireArguments()).number)

        return binding.root
    }

}