package com.example.restaurantapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurantapplication.R
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

        // Optional: Show "Clicked: ..." on item click
        val cuisineAdapter = CuisineAdapter(emptyList()) { cuisine ->
            Toast.makeText(requireContext(), "Clicked: $cuisine", Toast.LENGTH_SHORT).show()
        }
        binding.rvCuisineCategories.adapter = cuisineAdapter

        // Observe cuisine list
        viewModel.cuisineList.observe(viewLifecycleOwner) { cuisines ->
            binding.rvCuisineCategories.adapter = CuisineAdapter(cuisines) { cuisine ->
                Toast.makeText(requireContext(), "Clicked: $cuisine", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to MenuFragment with selected cuisine
            }
        }

        // Load cuisines
        viewModel.loadCuisines()

        // Language toggle (Segment 4)
        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            val language = if (isChecked) "hi" else "en"
            Toast.makeText(requireContext(), "Switched to $language", Toast.LENGTH_SHORT).show()
            // TODO: Implement actual locale switching logic
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}