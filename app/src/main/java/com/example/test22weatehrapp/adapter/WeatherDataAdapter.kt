package com.example.test22weatehrapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.test22weatehrapp.data.CurrentLocation
import com.example.test22weatehrapp.data.CurrentWeather
import com.example.test22weatehrapp.data.Forecast
import com.example.test22weatehrapp.data.WeatherData
import com.example.test22weatehrapp.databinding.ItemContainerCurrentLocationBinding
import com.example.test22weatehrapp.databinding.ItemContainerCurrentWeatherBinding
import com.example.test22weatehrapp.databinding.ItemContainerForecastBinding

class WeatherDataAdapter(
    private val onLocationClicked: () -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    private companion object {
        const val INDEX_CURRENT_LOCATION = 0
        const val INDEX_CURRENT_WEATHER = 1
        const val INDEX_FORECAST = 2
    }

    private val weatherData = mutableListOf<WeatherData>()

     fun setCurrentLocation(currentLocation: CurrentLocation) {
        if (weatherData.isEmpty()) {
            weatherData.add(INDEX_CURRENT_LOCATION, currentLocation)
            notifyItemInserted(INDEX_CURRENT_LOCATION)
        } else {
            weatherData[INDEX_CURRENT_LOCATION] = currentLocation
            notifyItemChanged(INDEX_CURRENT_LOCATION)
        }
    }

    inner class CurrentLocationViewHolder(
        private val binding: ItemContainerCurrentLocationBinding
    ) : ViewHolder(binding.root) {
        fun bind(currentLocation: CurrentLocation) {
            with(binding) {
                textCurrentLocation.text = currentLocation.location
                textCurrentDate.text = currentLocation.date
                textCurrentLocation.setOnClickListener { onLocationClicked() }
                imageMyLocation.setOnClickListener { onLocationClicked() }

            }
        }
    }

    inner class CurrentWeatherViewHolder(
        private val binding: ItemContainerCurrentWeatherBinding
    ) : ViewHolder(binding.root) {
        fun bind (currentWeather: CurrentWeather) {
            with(binding) {
                textWind.text = String.format("%s km/h", currentWeather.wind)
                imageIcon.load("https:${currentWeather.icon}") { crossfade(true)}
                textHumidity.text = String.format("%s%%", currentWeather.humidity)
                textRainy.text = String.format("%s%%", currentWeather.chanceOfRain)
                textTemperature.text = String.format("%s\u00B0C", currentWeather.temperature)
            }
        }
    }

    inner class ForecastViewHolder(
        private val binding: ItemContainerForecastBinding
    ) : ViewHolder(binding.root) {
       fun bind(forecast: Forecast) {
           with(binding) {
               textTime.text = forecast.time
               textTemperature.text = String.format("%s\u00B0C", forecast.temperature)
               textFeelsLikeTemperature.text =
                   String.format("%s\u00B0C", forecast.feelsLikeTemperature)
               imageIcon.load("https:${forecast.icon}") {crossfade(true)}
           }
       }
    }

    fun setCurrentWeather(currentWeather: CurrentWeather) {
        if (weatherData.getOrNull(INDEX_CURRENT_WEATHER) != null) {
            weatherData[INDEX_CURRENT_WEATHER] = currentWeather
            notifyItemChanged(INDEX_CURRENT_WEATHER)
        } else {
            weatherData.add(INDEX_CURRENT_WEATHER, currentWeather)
            notifyItemInserted(INDEX_CURRENT_WEATHER)
        }
    }

    fun setForecastData(forecast: List<Forecast>) {
        weatherData.removeAll { it is Forecast}
        notifyItemRangeRemoved(INDEX_FORECAST,weatherData.size)
        weatherData.addAll(INDEX_FORECAST, forecast)
        notifyItemRangeChanged(INDEX_FORECAST, weatherData.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        return when (viewType) {
            INDEX_CURRENT_LOCATION -> CurrentLocationViewHolder(
                ItemContainerCurrentLocationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            INDEX_FORECAST -> ForecastViewHolder(
                ItemContainerForecastBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> CurrentWeatherViewHolder(
                ItemContainerCurrentWeatherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder) {
            is CurrentLocationViewHolder -> holder.bind(weatherData[position] as CurrentLocation)
            is CurrentWeatherViewHolder -> holder.bind(weatherData[position] as CurrentWeather)
            is ForecastViewHolder -> holder.bind(weatherData[position] as Forecast)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(weatherData[position]) {
            is CurrentWeather -> INDEX_CURRENT_WEATHER
            is CurrentLocation -> INDEX_CURRENT_LOCATION
            is Forecast -> INDEX_FORECAST
        }
    }
}