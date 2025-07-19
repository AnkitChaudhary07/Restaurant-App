package com.example.restaurantapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapplication.data.RestaurantRepository
import com.example.restaurantapplication.data.model.CuisineCategory
import com.example.restaurantapplication.data.model.TopDish
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val repository = RestaurantRepository()

    private val _categories = MutableLiveData<List<CuisineCategory>>()
    val categories: LiveData<List<CuisineCategory>> = _categories

    private val _topDishes = MutableLiveData<List<TopDish>>()
    val topDishes: LiveData<List<TopDish>> = _topDishes

    private var currentPage = 1
    private var totalPages = 1
    private var isLoading = false

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (isLoading || currentPage > totalPages) return

        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            val (cuisines, dishes, totalPg) = repository.fetchCuisineCategoriesFromApi(currentPage)

            withContext(Dispatchers.Main) {
                _categories.value = (_categories.value ?: emptyList()) + cuisines
                _topDishes.value = (_topDishes.value ?: emptyList()) + dishes
                currentPage++
                totalPages = totalPg
                isLoading = false
            }
        }
    }
}