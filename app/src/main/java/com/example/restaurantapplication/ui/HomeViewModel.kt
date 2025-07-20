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

    private val cuisineList = mutableListOf<CuisineCategory>()  // ✅ Internal cache
    private val dishList = mutableListOf<TopDish>()             // ✅ Optional deduped top dishes

    private var currentPage = 1
    private var totalPages = Int.MAX_VALUE
    private var isLoading = false

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (isLoading || currentPage > totalPages) return

        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            val (newCuisines, newDishes, totalPg) = repository.fetchCuisineCategoriesFromApi(currentPage)

            withContext(Dispatchers.Main) {
                // ✅ Deduplicate cuisines by cuisine_id
                val uniqueCuisines = newCuisines.filter { new ->
                    cuisineList.none { existing -> existing.cuisine_id == new.cuisine_id }
                }
                cuisineList.addAll(uniqueCuisines)
                _categories.value = cuisineList.toList()

                // Optional: deduplicate top dishes by id
                val uniqueDishes = newDishes.filter { new ->
                    dishList.none { existing -> existing.id == new.id }
                }
                dishList.addAll(uniqueDishes)
                _topDishes.value = dishList.toList()

                currentPage++
                totalPages = totalPg
                isLoading = false
            }
        }
    }
}
