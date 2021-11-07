package com.ashwin.android.shoppinglist.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ashwin.android.shoppinglist.Constant
import com.ashwin.android.shoppinglist.R
import com.ashwin.android.shoppinglist.databinding.FragmentAddShoppingItemBinding
import com.ashwin.android.shoppinglist.util.Status
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val SUB_TAG = "AddShoppingItemFragment"

@AndroidEntryPoint
class AddShoppingItemFragment @Inject constructor(
    val glide: RequestManager
) : Fragment(R.layout.fragment_add_shopping_item) {
    private lateinit var binding: FragmentAddShoppingItemBinding
    lateinit var shoppingViewModel: ShoppingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddShoppingItemBinding.bind(view)

        shoppingViewModel = ViewModelProvider(requireActivity()).get(ShoppingViewModel::class.java)

        binding.btnAddShoppingItem.setOnClickListener {
            shoppingViewModel.insertShoppingItem(
                binding.etShoppingItemName.text.toString(),
                binding.etShoppingItemAmount.text.toString(),
                binding.etShoppingItemPrice.text.toString()
            )
        }

        binding.ivShoppingImage.setOnClickListener {
            findNavController().navigate(
                AddShoppingItemFragmentDirections.actionAddShoppingItemFragmentToImagePickFragment()
            )
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                shoppingViewModel.setCurrentImageUrl("")
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        shoppingViewModel.currentImageUrl.observe(viewLifecycleOwner, Observer {
            glide.load(it).into(binding.ivShoppingImage)
        })

        shoppingViewModel.insertShoppingItemStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.LOADING -> {
                        Log.d(Constant.APP_TAG, "$SUB_TAG: Loading...")
                        Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_LONG).show()
                        // NO-OP
                    }
                    Status.SUCCESS -> {
                        Log.d(Constant.APP_TAG, "$SUB_TAG: Success")
                        Snackbar.make(requireView(), "Added Shopping Item", Snackbar.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    }
                    Status.ERROR -> {
                        Log.d(Constant.APP_TAG, "$SUB_TAG: Error")
                        Snackbar.make(requireView(), result.message ?: "An unknown error occcured", Snackbar.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    }
                }
            }
        })
    }
}
