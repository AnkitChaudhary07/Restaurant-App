// data/RestaurantRepository.kt
package com.example.restaurantapplication.data

import android.util.Log
import com.example.restaurantapplication.R
import com.example.restaurantapplication.data.model.CuisineCategory
import com.example.restaurantapplication.data.model.TopDish
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class RestaurantRepository {

//    fun getCuisineCategories(): List<CuisineCategory> {
//        return listOf(
//            CuisineCategory(1, "Indian", R.drawable.ic_indian),
//            CuisineCategory(2, "Chinese", R.drawable.ic_chinese),
//            CuisineCategory(3, "Brazilian", R.drawable.ic_brazilian),
//            CuisineCategory(4, "Moroccan", R.drawable.ic_moroccan)
//        )
//    }

    fun fetchCuisineCategoriesFromApi(page: Int): Triple<List<CuisineCategory>, List<TopDish>, Int> {
        val url = URL("https://uat.onebanc.ai/emulator/interview/get_item_list")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-Partner-API-Key", "uonebancservceemultrS3cg8RaL30")
            connection.setRequestProperty("X-Forward-Proxy-Action", "get_item_list")
            connection.doOutput = true

            val requestBody = """{ "page": $page, "count": 10 }"""
            connection.outputStream.use {
                it.write(requestBody.toByteArray())
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                val cuisinesList = mutableListOf<CuisineCategory>()
                val topDishesList = mutableListOf<TopDish>()
                val root = JSONObject(response)
                val cuisines = root.getJSONArray("cuisines")
                val totalPages = root.getInt("total_pages")

                for (i in 0 until cuisines.length()) {
                    val obj = cuisines.getJSONObject(i)

                    // Add to cuisine list
                    val cuisine = CuisineCategory(
                        cuisine_id = obj.getString("cuisine_id"),
                        cuisine_name = obj.getString("cuisine_name"),
                        cuisine_image_url = obj.getString("cuisine_image_url")
                    )
                    cuisinesList.add(cuisine)

                    // Add top 3 dishes of this cuisine
                    val items = obj.getJSONArray("items")
                    for (j in 0 until minOf(3, items.length())) {
                        val item = items.getJSONObject(j)
                        val dish = TopDish(
                            id = item.getString("id"),
                            name = item.getString("name"),
                            image_url = item.getString("image_url"),
                            price = item.getString("price"),
                            rating = item.getString("rating")
                        )
                        topDishesList.add(dish)
                    }
                }

                return Triple(cuisinesList, topDishesList, totalPages)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }

        // Return empty if failed
        return Triple(emptyList(), emptyList(), 1)
    }


}
