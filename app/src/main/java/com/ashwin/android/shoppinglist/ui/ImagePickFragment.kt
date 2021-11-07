package com.ashwin.android.shoppinglist.ui

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ashwin.android.shoppinglist.Constant
import com.ashwin.android.shoppinglist.R
import com.ashwin.android.shoppinglist.adapter.ImageAdapter
import com.ashwin.android.shoppinglist.databinding.FragmentImagePickBinding
import com.ashwin.android.shoppinglist.util.Status
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImagePickFragment @Inject constructor(
    val imageAdapter: ImageAdapter
) : Fragment(R.layout.fragment_image_pick) {
    lateinit var binding: FragmentImagePickBinding
    lateinit var shoppingViewModel: ShoppingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentImagePickBinding.bind(view)
        shoppingViewModel = ViewModelProvider(requireActivity()).get(ShoppingViewModel::class.java)

        setUpRecyclerView()

        subscribeToObservers()

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(Constant.SEARCH_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        shoppingViewModel.searchForImage(editable.toString())
                    }
                }
            }
        }

        imageAdapter.setOnItemClickListener {
            shoppingViewModel.setCurrentImageUrl(it)
            findNavController().popBackStack()
        }
    }

    private fun setUpRecyclerView() {
        binding.rvImages.apply {
            adapter = imageAdapter
            layoutManager = GridLayoutManager(requireContext(), Constant.GRID_SPAN_COUNT)
        }
    }

    private fun subscribeToObservers() {
        shoppingViewModel.images.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        val urls = result.data?.hits?.map { imageResult ->  imageResult.previewURL }
                        imageAdapter.images = urls ?: listOf()
                        binding.progressBar.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        Snackbar.make(requireView(), result.message ?: "An unknown error occured.", Snackbar.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}
