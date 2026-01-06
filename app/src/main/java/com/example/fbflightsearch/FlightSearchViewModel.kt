package com.example.fbflightsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fbflightsearch.data.Airport
import com.example.fbflightsearch.data.Favorite
import com.example.fbflightsearch.data.FlightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the flight search screen
 */
class FlightSearchViewModel(
    private val repository: FlightRepository
) : ViewModel() {

    // Search query and airport suggestions
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _suggestions = MutableStateFlow<List<Airport>>(emptyList())
    val suggestions: StateFlow<List<Airport>> = _suggestions.asStateFlow()

    // Selected airport and its destinations
    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport.asStateFlow()

    private val _destinations = MutableStateFlow<List<Airport>>(emptyList())
    val destinations: StateFlow<List<Airport>> = _destinations.asStateFlow()

    // Favorite routes
    private val _favorites = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val favorites: StateFlow<Map<String, Boolean>> = _favorites.asStateFlow()

    /**
     * Update search query and fetch suggestions
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.length >= 2) {
            viewModelScope.launch {
                _suggestions.value = repository.searchAirportsCombined(query)
            }
        } else {
            _suggestions.value = emptyList()
        }
    }

    /**
     * Select an airport and load its destinations
     */
    fun onAirportSelected(airport: Airport) {
        _selectedAirport.value = airport
        _searchQuery.value = "${airport.iataCode} - ${airport.name}"
        _suggestions.value = emptyList()

        viewModelScope.launch {
            _destinations.value = repository.getFlightsFromAirport(airport.id)
        }
    }

    /**
     * Clear selection
     */
    fun onClearSelection() {
        _selectedAirport.value = null
        _searchQuery.value = ""
        _destinations.value = emptyList()
        _suggestions.value = emptyList()
    }

    /**
     * Toggle favorite status for a route
     */
    fun onToggleFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            val routeKey = "${departureCode}_$destinationCode"
            val isFavorite = _favorites.value[routeKey] != true

            repository.toggleFavorite(departureCode, destinationCode, isFavorite)

            // Also update Room database
            if (isFavorite) {
                repository.insertFavorite(Favorite(departureCode = departureCode, destinationCode = destinationCode))
            } else {
                repository.getFavorite(departureCode, destinationCode)?.let {
                    repository.deleteFavorite(it)
                }
            }

            // Update favorites map
            _favorites.value = _favorites.value.toMutableMap().apply {
                put(routeKey, isFavorite)
            }
        }
    }

    /**
     * Load all favorites from DataStore
     */
    private fun loadFavorites() {
        // Favorites are loaded per route when needed
        // This is a placeholder for loading all favorites if needed
    }

    /**
     * Check if a specific route is favorited
     */
    fun isRouteFavorited(departureCode: String, destinationCode: String): Boolean {
        val routeKey = "${departureCode}_$destinationCode"
        return _favorites.value[routeKey] == true
    }
}