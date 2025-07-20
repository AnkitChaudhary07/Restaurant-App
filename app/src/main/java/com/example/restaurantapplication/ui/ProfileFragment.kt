package com.example.restaurantapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.model.LastPaymentManager
import com.example.restaurantapplication.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set action bar title
        requireActivity().title = "Profile"

        val lastPayment = LastPaymentManager.getLastPayment(requireContext())

        if (lastPayment != null) {
            val amount = lastPayment.getString("amount")
            val itemCount = lastPayment.getInt("itemCount")
            val items = lastPayment.getJSONArray("items")

            // Show on screen
            binding.tvLastPayment.text = "Last Payment: ₹$amount for $itemCount items"
        } else {
            binding.tvLastPayment.text = "No recent payments"
        }

        binding.btnProceedToCheckout.setOnClickListener {
            val fragment = CartFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)  // ID of your container layout
                .addToBackStack(null)  // Optional: adds to back stack
                .commit()

            val navView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
            navView.selectedItemId = R.id.nav_home


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}