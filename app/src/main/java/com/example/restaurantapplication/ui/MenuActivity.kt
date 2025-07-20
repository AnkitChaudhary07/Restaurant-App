package com.example.restaurantapplication.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantapplication.MainActivity
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.model.CartManager
import com.example.restaurantapplication.databinding.ActivityMenuBinding
import java.net.URL

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

        viewModel.filteredDishes.observe(this) { basicDishes ->
            Log.d("MenuActivity", "Received ${basicDishes.size} basic dishes")

            binding.llDishesContainer.removeAllViews()
            binding.progressBar.visibility = View.VISIBLE

            Thread {
                val fullDishList = basicDishes.map { viewModel.getItemDetailsById(it.id) }

                runOnUiThread {
                    fullDishList.forEach { dish ->
                        Log.d("MenuActivity", "Dish loaded: id=${dish.id}, name=${dish.name}, price=${dish.price}, rating=${dish.rating}")
                        val dishView = layoutInflater.inflate(R.layout.menu_dish_card, binding.llDishesContainer, false)

                        val ivDish = dishView.findViewById<ImageView>(R.id.ivDishImage)
                        val tvDishName = dishView.findViewById<TextView>(R.id.tvDishName)
                        val tvDishPrice = dishView.findViewById<TextView>(R.id.tvDishPrice)
                        val tvDishRating = dishView.findViewById<TextView>(R.id.tvDishRating)
                        val btnIncrease = dishView.findViewById<Button>(R.id.btnIncrease)
                        val btnDecrease = dishView.findViewById<Button>(R.id.btnDecrease)
                        val tvCount = dishView.findViewById<TextView>(R.id.tvCount)

                        tvDishName.text = dish.name
                        tvDishPrice.text = "₹${dish.price}"
                        tvDishRating.text = "⭐ ${dish.rating}"

                        Thread {
                            try {
                                val input = URL(dish.image_url).openStream()
                                val bitmap = android.graphics.BitmapFactory.decodeStream(input)
                                runOnUiThread {
                                    ivDish.setImageBitmap(bitmap)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }.start()

                        //Add Food Items...
//                        var count = CartManager.getItemCountById(this, dish.id)
                        var count = 0
                        tvCount.text = count.toString()

                        //Show Proceed Button
                        if (CartManager.getCartSize(this) > 0) {
                            binding.btnProceedToCheckout.visibility = View.VISIBLE
                        }


                        btnIncrease.setOnClickListener {
                            count++
                            tvCount.text = count.toString()

                            // Add to SharedPreferences
                            CartManager.addItem(this, dish.id, dish.name, dish.price)
                            Toast.makeText(this, "${dish.name} added to cart", Toast.LENGTH_SHORT).show()
                            // Show Proceed to Checkout button
                            binding.btnProceedToCheckout.visibility = View.VISIBLE
                        }

                        btnDecrease.setOnClickListener {
                            if (count > 0) {
                                count--
                                tvCount.text = count.toString()

                                // Always update cart
                                CartManager.removeItem(this, dish.id)

                                // Check if cart became empty
                                if (CartManager.getCartSize(this) == 0) {
                                    binding.btnProceedToCheckout.visibility = View.GONE
                                }
                            }
                        }

                        binding.btnProceedToCheckout.setOnClickListener {
                            val cartItems = CartManager.getCartItems(this)

                            if (cartItems.isEmpty()) {
                                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show()
                            } else {
                                val message = cartItems.joinToString("\n") { (name, count, price) ->
                                    "$name x$count @ ₹$price"
                                }

                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("open_cart", true)
                                startActivity(intent)

                            }
                        }

                        binding.llDishesContainer.addView(dishView)
                    }

                    binding.progressBar.visibility = View.GONE
                }
            }.start()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}