package com.example.restaurantapplication.ui

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.data.model.TopDish
import com.example.restaurantapplication.databinding.ItemDishTileBinding
import java.net.URL
import kotlin.concurrent.thread

class TopDishAdapter(private val dishes: List<TopDish>) :
    RecyclerView.Adapter<TopDishAdapter.TopDishViewHolder>() {

    inner class TopDishViewHolder(val binding: ItemDishTileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dish: TopDish) {
            binding.tvDishName.text = dish.name
            binding.tvDishPrice.text = "₹${dish.price}"
            binding.tvDishRating.text = "⭐ ${dish.rating}"
            binding.btnIncrease.setOnClickListener {
                Toast.makeText(binding.root.context, "${dish.name} added to cart", Toast.LENGTH_SHORT).show()
            }

            binding.btnDecrease.setOnClickListener {
                Toast.makeText(binding.root.context, "${dish.name} removed from cart", Toast.LENGTH_SHORT).show()
            }

            // Show loader and clear previous image
            binding.ivDishImage.setImageDrawable(null)
            binding.ivDishImage.tag = dish.image_url
            binding.progressBar.visibility = View.VISIBLE

            thread {
                try {
                    val bitmap = BitmapFactory.decodeStream(URL(dish.image_url).openStream())
                    binding.ivDishImage.post {
                        if (binding.ivDishImage.tag == dish.image_url) {
                            binding.ivDishImage.setImageBitmap(bitmap)
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.progressBar.post {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopDishViewHolder {
        val binding = ItemDishTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopDishViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("TopDishAdapter", "Total dishes in adapter: ${dishes.size}")
        return dishes.size
    }


    override fun onBindViewHolder(holder: TopDishViewHolder, position: Int) {
        holder.bind(dishes[position])
    }
}
