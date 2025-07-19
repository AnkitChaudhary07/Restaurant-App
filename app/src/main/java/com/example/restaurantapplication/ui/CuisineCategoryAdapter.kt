package com.example.restaurantapplication.ui

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.data.model.CuisineCategory
import com.example.restaurantapplication.databinding.ItemCuisineCardBinding
import java.net.URL
import kotlin.concurrent.thread

class CuisineCategoryAdapter(
    private val cuisines: List<CuisineCategory>,
    private val onClick: (CuisineCategory) -> Unit
) : RecyclerView.Adapter<CuisineCategoryAdapter.CuisineViewHolder>() {

    inner class CuisineViewHolder(val binding: ItemCuisineCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cuisine: CuisineCategory) {
            binding.tvCuisineName.text = cuisine.cuisine_name
            binding.imageProgressBar.visibility = View.VISIBLE
            binding.ivCuisineImage.setImageBitmap(null)

            val tagUrl = cuisine.cuisine_image_url
            binding.ivCuisineImage.tag = tagUrl

            thread {
                try {
                    val url = URL(tagUrl)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                    binding.ivCuisineImage.post {
                        if (binding.ivCuisineImage.tag == tagUrl) { // prevent wrong reuse
                            binding.ivCuisineImage.setImageBitmap(bmp)
                            binding.imageProgressBar.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.imageProgressBar.post {
                        binding.imageProgressBar.visibility = View.GONE
                    }
                }
            }

            binding.root.setOnClickListener {
                onClick(cuisine)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineViewHolder {
        val binding = ItemCuisineCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CuisineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CuisineViewHolder, position: Int) {
        holder.bind(cuisines[position])
    }

    override fun getItemCount() = cuisines.size
}
