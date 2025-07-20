package com.example.restaurantapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantapplication.data.RestaurantRepository
import com.example.restaurantapplication.data.model.TopDish

class MenuViewModel : ViewModel() {
    private val repository = RestaurantRepository()

    private val _filteredDishes = MutableLiveData<List<TopDish>>()
    val filteredDishes: LiveData<List<TopDish>> get() = _filteredDishes

    fun fetchDishesByCuisine(cuisineType: String) {
        Thread {
            val result = repository.getItemsByFilter(cuisineType)
            _filteredDishes.postValue(result)
        }.start()
    }

    fun getItemDetailsById(itemId: String): TopDish {
        return repository.getItemById(itemId)
    }

}