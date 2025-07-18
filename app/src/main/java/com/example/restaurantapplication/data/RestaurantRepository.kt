// data/RestaurantRepository.kt
package com.example.restaurantapplication.data

import com.example.restaurantapplication.data.model.FoodItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class RestaurantRepository {

    fun fetchCuisineCategories(): List<String> {
        val url = URL("https://uat.onebanc.ai/emulator/interview/get_item_list")
        val connection = url.openConnection() as HttpURLConnection
        val cuisineList = mutableListOf<String>()

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("X-Partner-API-Key", "uonebancservceemultrS3cg8RaL30")
            connection.setRequestProperty("X-Forward-Proxy-Action", "get_item_list")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val requestBody = JSONObject()
            requestBody.put("page", 1)
            requestBody.put("count", 10)

            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(requestBody.toString())
            writer.flush()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val stream = BufferedReader(InputStreamReader(connection.inputStream))
                val response = stream.readText()
                stream.close()

                val json = JSONObject(response)
                val data = json.getJSONArray("data")

                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    val cuisine = item.getString("cuisine_type")
                    cuisineList.add(cuisine)
                }

                return cuisineList.distinct()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }

        return emptyList()
    }
}
