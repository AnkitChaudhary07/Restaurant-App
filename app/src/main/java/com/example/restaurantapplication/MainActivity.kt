package com.example.restaurantapplication

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.restaurantapplication.databinding.ActivityMainBinding
import com.example.restaurantapplication.ui.CartFragment
import com.example.restaurantapplication.ui.HomeFragment
import com.example.restaurantapplication.ui.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Check internet at startup
        if (!isNetworkAvailable()) {
            showNoInternetDialog()
            return // stop further setup
        }

        // ✅ Only load one fragment based on intent
        if (intent?.getBooleanExtra("open_cart", false) == true) {
            binding.bottomNav.selectedItemId = R.id.nav_cart
            loadFragment(CartFragment())
        } else {
            binding.bottomNav.selectedItemId = R.id.nav_home
            loadFragment(HomeFragment())
        }

        // Handle bottom nav selection
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_cart -> loadFragment(CartFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // ✅ Utility to check network availability
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // ✅ Dialog to notify user of no internet
    private fun showNoInternetDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("No Internet Connection")
        builder.setMessage("Please check your internet connection and try again.")
        builder.setCancelable(false)
        builder.setPositiveButton("Retry") { dialog, _ ->
            dialog.dismiss()
            recreate() // restart activity
        }
        builder.setNegativeButton("Exit") { _, _ ->
            finish()
        }
        builder.show()
    }

}