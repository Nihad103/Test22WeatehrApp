package com.example.test22weatehrapp.network.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import com.example.test22weatehrapp.data.CurrentLocation
import com.example.test22weatehrapp.data.RemoteLocation
import com.example.test22weatehrapp.data.RemoteWeatherData
import com.example.test22weatehrapp.data.WeatherData
import com.example.test22weatehrapp.network.api.WeatherApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import retrofit2.Response
import java.lang.StringBuilder

class WeatherDataRepository(private val weatherApi: WeatherApi) {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation: CurrentLocation) -> Unit,
        onFailure: () -> Unit
    ) {
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            location ?: onFailure()
            onSuccess(
                CurrentLocation(
                    longitude = location.longitude,
                    latitude = location.latitude
                )
            )
        } .addOnFailureListener { onFailure() }
    }

    @Suppress("DEPRECATION")
    fun updateAddressText(
        currentLocation: CurrentLocation,
        geocoder: Geocoder
    ) : CurrentLocation {
        val longitude = currentLocation.longitude ?: return currentLocation
        val latitude = currentLocation.latitude ?: return currentLocation
        return geocoder.getFromLocation(latitude, longitude, 1)?.let { addresses ->
            val address = addresses[0]
            val addressText = StringBuilder()
            addressText.append(address.locality).append(", ")
            addressText.append(address.adminArea).append(", ")
            addressText.append(address.countryName)
            currentLocation.copy(location = addressText.toString())
        } ?: currentLocation
    }

    suspend fun searchLocation(query: String) : List<RemoteLocation>? {
        val response = weatherApi.searchLocation(query = query)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getWeatherData(latitude: Double, longitude: Double) : RemoteWeatherData? {
        val response = weatherApi.getWeatherData(query = "$latitude, $longitude")
        return if (response.isSuccessful) response.body() else null
    }
}