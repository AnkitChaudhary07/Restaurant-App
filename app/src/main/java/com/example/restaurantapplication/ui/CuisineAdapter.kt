package com.example.restaurantapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.databinding.ItemCuisineCardBinding

class CuisineAdapter(
    private val cuisines: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CuisineAdapter.CuisineViewHolder>() {

    inner class CuisineViewHolder(val binding: ItemCuisineCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cuisine: String) {
            binding.tvCuisineName.text = cuisine
            binding.ivCuisineImage.setImageResource(R.drawable.ic_launcher_background) // placeholder
            binding.root.setOnClickListener { onClick(cuisine) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineViewHolder {
        val binding = ItemCuisineCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CuisineViewHolder(binding)
    }

    override fun getItemCount() = cuisines.size

    override fun onBindViewHolder(holder: CuisineViewHolder, position: Int) {
        holder.bind(cuisines[position])
    }
}
