package com.example.test22weatehrapp.data

import com.google.gson.annotations.SerializedName

data class RemoteWeatherData(
    val current: CurrentRemoteWeather,
    val forecast: ForecastRemote,
)

data class CurrentRemoteWeather(
    @SerializedName("temp_c") val temperature: Float,
    val condition: WeatherConditionRemote,
    @SerializedName("wind_kph") val wind: Float,
    val humidity: Int )

data class DayRemote(
    @SerializedName("daily_chance_of_rainy") val chanceOfRain: Int, )

data class ForecastRemote(
    @SerializedName("forecastday") val forecastDay: List<ForecastDayRemote>,

    )

data class ForecastDayRemote(
    val day: DayRemote,
    val hour: List<ForecastHourRemote>
)

data class ForecastHourRemote(
    val time: String,
    @SerializedName("temp_c") val temperature: Float,
    @SerializedName("feelslike_c") val feelsLikeTemperature: Float,
    val condition: WeatherConditionRemote )

data class WeatherConditionRemote(
    val icon: String
)