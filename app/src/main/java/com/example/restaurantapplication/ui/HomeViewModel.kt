package com.example.restaurantapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapplication.data.RestaurantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val repository = RestaurantRepository()

    private val _cuisineList = MutableLiveData<List<String>>()
    val cuisineList: LiveData<List<String>> get() = _cuisineList

    fun loadCuisines() {
        viewModelScope.launch {
            val cuisines = withContext(Dispatchers.IO) {
                repository.fetchCuisineCategories()
            }
            _cuisineList.value = cuisines
        }
    }
}