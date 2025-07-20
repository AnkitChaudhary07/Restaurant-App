package com.example.restaurantapplication.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

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

        // Segment 1: Cuisine Categories
        binding.rvCuisineCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Observe cuisine list
        viewModel.categories.observe(viewLifecycleOwner) { cuisines ->
            binding.rvCuisineCategories.adapter = CuisineCategoryAdapter(cuisines) { cuisine ->
                val intent = Intent(requireContext(), MenuActivity::class.java)
                intent.putExtra("CUISINE_NAME", cuisine.cuisine_name)
                intent.putExtra("CUISINE_ID", cuisine.cuisine_id)
                startActivity(intent)
            }
        }

        // Language toggle (Segment 4)
        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            val language = if (isChecked) "hindi" else "english"
            Toast.makeText(requireContext(), "Switched to $language", Toast.LENGTH_SHORT).show()
            // TODO: Implement actual locale switching logic
        }

        // Snap one card at a time
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding.rvCuisineCategories)

// Preload next page when near the end
        binding.rvCuisineCategories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // ✅ Preload next page when you're viewing the second last image
                if (lastVisible >= totalItemCount - 2) {
                    viewModel.loadNextPage()
                }
            }
        })


        viewModel.topDishes.observe(viewLifecycleOwner) { dishes ->
            binding.rvTopDishes.adapter = TopDishAdapter(dishes)
            binding.rvTopDishes.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.btnProceedToCheckout.setOnClickListener {
            val fragment = CartFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)  // ID of your container layout
                .addToBackStack(null)  // Optional: adds to back stack
                .commit()

            val navView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
            navView.selectedItemId = R.id.nav_cart

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}