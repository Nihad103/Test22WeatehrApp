package com.example.test22weatehrapp.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class WeatherData

data class CurrentLocation(
    val date: String = currentDate(),
    val location: String = "Choose your Location",
    val latitude: Double? = null,
    val longitude: Double? = null, ) : WeatherData()

data class CurrentWeather(
    val icon: String,
    val temperature: Float,
    val wind: Float,
    val humidity: Int,
    val chanceOfRain: Int, ) : WeatherData()

data class Forecast(
    val icon: String,
    val temperature: Float,
    val time: String,
    val feelsLikeTemperature: Float ) : WeatherData()

private fun currentDate() : String {
    val currentDate = Date()
    val formatter = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return "Today, ${formatter.format(currentDate)}"
}
