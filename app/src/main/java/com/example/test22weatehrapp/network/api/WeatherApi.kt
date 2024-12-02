package com.example.test22weatehrapp.network.api

import com.example.test22weatehrapp.data.RemoteLocation
import com.example.test22weatehrapp.data.RemoteWeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    companion object {
        val BASE_URL = "https://api.weatherapi.com/v1/"
        val API_KEY = "4d87f975fb174cc79c9135341243011"
    }

    @GET("search.json")
    suspend fun searchLocation(
        @Query("key") key: String = API_KEY,
        @Query("q") query: String
    ) : Response<List<RemoteLocation>>

    @GET("forecast.json")
    suspend fun getWeatherData(
        @Query("key") key: String = API_KEY,
        @Query("q") query: String
    ) : Response<RemoteWeatherData>
}