package com.example.test22weatehrapp.fragments.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test22weatehrapp.data.RemoteLocation
import com.example.test22weatehrapp.network.repository.WeatherDataRepository
import kotlinx.coroutines.launch

class LocationViewModel(private val weatherDataRepository: WeatherDataRepository) : ViewModel() {
    private val _searchResult = MutableLiveData<SearchResultDataState>()
    val searchResult: LiveData<SearchResultDataState> get() = _searchResult

    fun searchLocation(query: String) {
        viewModelScope.launch {
            emitSearchResultUIState(isLoading = true)
            val searchResult = weatherDataRepository.searchLocation(query)
            if (searchResult.isNullOrEmpty()) {
                emitSearchResultUIState(error = "Location not found, please try again")
            } else {
                emitSearchResultUIState(locations = searchResult)
            }
        }
    }

    private fun emitSearchResultUIState(
        isLoading: Boolean = false,
        locations: List<RemoteLocation>? = null,
        error: String? = null
    ) {
        val searchResultDataState = SearchResultDataState(isLoading, locations, error)
        _searchResult.value = searchResultDataState
    }

    data class SearchResultDataState(
        val isLoading: Boolean,
        val locations: List<RemoteLocation>?,
        val error: String?
    )

}