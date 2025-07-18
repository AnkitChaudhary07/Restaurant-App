package com.example.restaurantapplication.ui

import androidx.lifecycle.ViewModel
import com.example.restaurantapplication.data.RestaurantRepository

class HomeViewModel : ViewModel() {

    private val repository = RestaurantRepository()
}