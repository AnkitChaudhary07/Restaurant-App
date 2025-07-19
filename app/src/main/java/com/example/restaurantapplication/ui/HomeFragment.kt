package com.example.restaurantapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set action bar title
        requireActivity().title = "Home"

        // Initialize your ViewModel
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Set up cuisine categories RecyclerView (Segment 1)
        binding.rvCuisineCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Observe cuisine list
        viewModel.categories.observe(viewLifecycleOwner) { cuisines ->
            binding.rvCuisineCategories.adapter = CuisineCategoryAdapter(cuisines) { cuisine ->
                Toast.makeText(requireContext(), "Clicked: ${cuisine.cuisine_name}", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to MenuFragment with selected cuisine
            }
        }

        // Snap one card at a time
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCuisineCategories)


        // Language toggle (Segment 4)
        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            val language = if (isChecked) "hindi" else "english"
            Toast.makeText(requireContext(), "Switched to $language", Toast.LENGTH_SHORT).show()
            // TODO: Implement actual locale switching logic
        }

        binding.rvCuisineCategories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Load next page if we're near the end
                if (lastVisible >= totalItemCount - 2) {
                    viewModel.loadNextPage()
                }
            }
        })

        binding.rvCuisineCategories.post {
            snapHelper.attachToRecyclerView(binding.rvCuisineCategories)
            binding.rvCuisineCategories.scrollToPosition(0)
        }

        viewModel.topDishes.observe(viewLifecycleOwner) { dishes ->
            binding.rvTopDishes.adapter = TopDishAdapter(dishes)
            binding.rvTopDishes.layoutManager = LinearLayoutManager(requireContext())
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}