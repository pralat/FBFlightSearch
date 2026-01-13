package com.example.fbflightsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fbflightsearch.data.Airport
import com.example.fbflightsearch.data.FlightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    init {
        loadSavedSearchQuery()
        loadFavoritesFromDatabase()
    }

    /**
     * Load saved search query from DataStore
     */
    private fun loadSavedSearchQuery() {
        viewModelScope.launch {
            val query = repository.getSearchQuery().first()
            _searchQuery.value = query
            // Trigger search with saved query
            if (query.length >= 2) {
                _suggestions.value = repository.searchAirportsCombined(query)
            }
        }
    }

    /**
     * Load all favorites from database
     */
    private fun loadFavoritesFromDatabase() {
        viewModelScope.launch {
            val favoritesList = repository.getAllFavorites()
            val favoritesMap = mutableMapOf<String, Boolean>()
            favoritesList.forEach { favorite ->
                favoritesMap["${favorite.departureCode}_${favorite.destinationCode}"] = true
            }
            _favorites.value = favoritesMap
        }
    }

    /**
     * Update search query and fetch suggestions
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query

        // Save search query to DataStore
        viewModelScope.launch {
            repository.setSearchQuery(query)
        }

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
        // Don't modify the search query - preserve the user's input
        _selectedAirport.value = airport
        _suggestions.value = emptyList()

        viewModelScope.launch {
            _destinations.value = repository.getFlightsFromAirport(airport.id)
        }
    }

    /**
     * Clear selection (go back to search screen)
     */
    fun onClearSelection() {
        _selectedAirport.value = null
        _destinations.value = emptyList()

        // Restore the saved search query (what the user originally typed)
        viewModelScope.launch {
            val savedQuery = repository.getSearchQuery().first()
            _searchQuery.value = savedQuery

            // Trigger suggestions with the restored query
            if (savedQuery.length >= 2) {
                _suggestions.value = repository.searchAirportsCombined(savedQuery)
            } else {
                _suggestions.value = emptyList()
            }
        }
    }

    /**
     * Toggle favorite status for a route
     */
    fun onToggleFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            val routeKey = "${departureCode}_$destinationCode"
            val isFavorite = _favorites.value[routeKey] != true

            repository.toggleFavorite(departureCode, destinationCode, isFavorite)

            // Update favorites map
            _favorites.value = _favorites.value.toMutableMap().apply {
                put(routeKey, isFavorite)
            }
        }
    }

    /**
     * Check if a specific route is favorited
     */
    fun isRouteFavorited(departureCode: String, destinationCode: String): Boolean {
        val routeKey = "${departureCode}_$destinationCode"
        return _favorites.value[routeKey] == true
    }
}