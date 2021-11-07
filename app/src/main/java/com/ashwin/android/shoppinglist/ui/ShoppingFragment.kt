package com.ashwin.android.shoppinglist.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashwin.android.shoppinglist.Constant
import com.ashwin.android.shoppinglist.R
import com.ashwin.android.shoppinglist.adapter.ShoppingItemAdapter
import com.ashwin.android.shoppinglist.databinding.FragmentShoppingBinding
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

private const val SUB_TAG = "ShoppingFragment"

class ShoppingFragment @Inject constructor(
    val shoppingItemAdapter: ShoppingItemAdapter,
    var shoppingViewModel: ShoppingViewModel? = null
) : Fragment(R.layout.fragment_shopping) {
    private lateinit var binding: FragmentShoppingBinding

    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val item = shoppingItemAdapter.shoppingItems[position]
            shoppingViewModel?.deleteShoppingItem(item)
            Log.d(Constant.APP_TAG, "$SUB_TAG: Shopping item deleted")
            Snackbar.make(requireView(), "Successfully deleted item", Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    shoppingViewModel?.insertShoppingItemInDb(item)
                }
            }.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentShoppingBinding.bind(view)

        if (shoppingViewModel == null) {
            shoppingViewModel = ViewModelProvider(requireActivity()).get(ShoppingViewModel::class.java)
        } else {
            // ViewModel is passed in the constructor.
            // E.g. AndroidTestViewModel
        }

        setUpRecyclerView()

        subscribeToObservers()

        binding.fabAddShoppingItem.setOnClickListener {
            findNavController().navigate(
                ShoppingFragmentDirections.actionShoppingFragmentToAddShoppingItemFragment()
            )
        }
    }

    private fun setUpRecyclerView() {
        binding.rvShoppingItems.apply {
            adapter = shoppingItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }
    }

    private fun subscribeToObservers() {
        shoppingViewModel?.shoppingItems?.observe(viewLifecycleOwner) {
            shoppingItemAdapter.shoppingItems = it
        }

        shoppingViewModel?.totalPrice?.observe(viewLifecycleOwner) {
            val price = it ?: 0f
            binding.tvShoppingItemPrice.text = "Total price: USD $price"
        }
    }
}
