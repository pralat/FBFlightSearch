package com.example.fbflightsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fbflightsearch.data.FlightRepository

/**
 * Factory class for creating FlightSearchViewModel
 */
class FlightSearchViewModelFactory(
    private val repository: FlightRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightSearchViewModel::class.java)) {
            return FlightSearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}