package com.example.test22weatehrapp.fragments.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.test22weatehrapp.R
import com.example.test22weatehrapp.adapter.WeatherDataAdapter
import com.example.test22weatehrapp.data.CurrentLocation
import com.example.test22weatehrapp.databinding.FragmentHomeBinding
import com.example.test22weatehrapp.storage.SharedPreferencesManager
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    companion object {
        const val REQUEST_KEY_MANUAL_LOCATION_SEARCH = "manualLocationSearch"
        const val KEY_LOCATION_TEXT = "locationText"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val homeViewModel: HomeViewModel by viewModel()
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private val geoCoder by lazy { Geocoder(requireContext()) }

    private val weatherDataAdapter = WeatherDataAdapter(
        onLocationClicked = { showLocationOptions() }
    )

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (isLocationServiceEnabled()) {
                getCurrentLocation()
            } else {
                promptEnableLocationServices()
            }
        } else {
            Log.d("PermissionDenied", "Permission denied")
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private var isInitialLocation: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWeatherDataAdapter()
        setObservers()
        setListeners()
        if (!isInitialLocation) {
            setCurrentLocation(currentLocation = sharedPreferencesManager.getCurrentLocation())
            isInitialLocation = true
        }
    }

    private fun setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            setCurrentLocation(sharedPreferencesManager.getCurrentLocation())
        }
    }

    private fun setObservers() {
        homeViewModel.currentLocation.observe(viewLifecycleOwner) {
            val currentLocationDataState = it.getContentIfNotHandled() ?: return@observe
            if (currentLocationDataState.isLoading) {
                showLoading()
            }
            currentLocationDataState.currentLocation?.let { currentLocation ->
                hideLoading()
                sharedPreferencesManager.saveCurrentLocation(currentLocation)
                setCurrentLocation(currentLocation)
            }
            currentLocationDataState.error?.let { error ->
                hideLoading()
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
        homeViewModel.weatherData.observe(viewLifecycleOwner) {
            val weatherDataState = it.getContentIfNotHandled() ?: return@observe
            binding.swipeRefreshLayout.isRefreshing = weatherDataState.isLoading
            weatherDataState.currentWeather?.let { currentWeather ->
                weatherDataAdapter.setCurrentWeather(currentWeather)
            }
            weatherDataState.forecast?.let { forecasts ->
                weatherDataAdapter.setForecastData(forecasts)
            }
            weatherDataState.error?.let { error ->
                Toast.makeText(
                    requireContext(),
                    error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setWeatherDataAdapter() {
        binding.recyclerView.adapter = weatherDataAdapter
        binding.recyclerView.itemAnimator = null
    }

    private fun setCurrentLocation(currentLocation: CurrentLocation? = null) {
        weatherDataAdapter.setCurrentLocation(currentLocation ?: CurrentLocation())
        currentLocation?.let { getWeatherData(currentLocation = it) }
    }

    private fun getCurrentLocation() {
        homeViewModel.getCurrentLocation(fusedLocationProviderClient, geoCoder)
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun proceedWithCurrentLocation() {
        if (isLocationPermissionGranted()) {
            if (isLocationServiceEnabled()) {
                getCurrentLocation()
            } else {
                promptEnableLocationServices()
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun isLocationServiceEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER) }

    private fun promptEnableLocationServices() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Enable Location Services")
            setMessage("Location services are required to get your current location. Please enable them in settings.")
            setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent) }
            setNegativeButton("Cancel", null)
            show()
        }
    }

    private fun showLocationOptions() {
        val options = arrayOf("Current Location", "Search Manually")
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Choose Location Method")
            setItems(options) { _, which ->
                when (which) {
                    0 -> proceedWithCurrentLocation()
                    1 -> startManuallyLocationSearch()
                }
            }
            show()
        }
    }

    private fun showLoading() {
        with(binding) {
            recyclerView.visibility = View.GONE
            swipeRefreshLayout.isEnabled = false
            swipeRefreshLayout.isRefreshing = true
        }
    }

    private fun hideLoading() {
        with(binding) {
            recyclerView.visibility = View.VISIBLE
            swipeRefreshLayout.isEnabled = true
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun startManuallyLocationSearch() {
        startListeningManualLocationSelection()
        findNavController().navigate(R.id.action_homeFragment_to_locationFragment)
    }

    private fun startListeningManualLocationSelection() {
        setFragmentResultListener(REQUEST_KEY_MANUAL_LOCATION_SEARCH) { _, bundle ->
            stopListeningManualLocationSelection()
            val currentLocation = CurrentLocation(
                location = bundle.getString(KEY_LOCATION_TEXT) ?: "N/A",
                latitude = bundle.getDouble(KEY_LATITUDE),
                longitude = bundle.getDouble(KEY_LONGITUDE)
            )
            sharedPreferencesManager.saveCurrentLocation(currentLocation)
            setCurrentLocation(currentLocation)
        }
    }

    private fun stopListeningManualLocationSelection() {
        clearFragmentResultListener(REQUEST_KEY_MANUAL_LOCATION_SEARCH)
    }

    private fun getWeatherData(currentLocation: CurrentLocation) {
        if (currentLocation.latitude != null && currentLocation.longitude != null) {
            homeViewModel.getWeatherData(
                latitude = currentLocation.latitude,
                longitude = currentLocation.longitude,
            )
        }
    }
}