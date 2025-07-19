package com.example.restaurantapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantapplication.R
import com.example.restaurantapplication.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MenuViewModel::class.java]

        val cuisineName = intent.getStringExtra("CUISINE_NAME")
        val cuisineId = intent.getStringExtra("CUISINE_ID")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set title and enable back arrow
        supportActionBar?.apply {
            title = cuisineName
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        if (cuisineName != null) {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.fetchDishesByCuisine(cuisineName)
        }

        viewModel.filteredDishes.observe(this) { dishes ->
            Log.d("MenuActivity", "Fetched ${dishes.size} dishes for $cuisineName")
            dishes.forEach {
                Log.d("MenuActivity", "Dish: ${it.name}, id=${it.id}, image=${it.image_url}")
            }

            binding.llDishesContainer.removeAllViews() // Clear old views if any

            dishes.forEach { dish ->
                val dishView = layoutInflater.inflate(R.layout.menu_dish_card, binding.llDishesContainer, false)

                val ivDish = dishView.findViewById<ImageView>(R.id.ivDishImage)
                val tvDishName = dishView.findViewById<TextView>(R.id.tvDishName)

                tvDishName.text = dish.name

                // Load image manually using HttpURLConnection
                Thread {
                    try {
                        val inputStream = java.net.URL(dish.image_url).openStream()
                        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                        runOnUiThread {
                            ivDish.setImageBitmap(bitmap)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()

                binding.llDishesContainer.addView(dishView)
            }

            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}