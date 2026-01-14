package com.example.fbflightsearch.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository for accessing flight data
 * Acts as a single source of truth for data operations
 */
class FlightRepository(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao,
    private val dataStoreManager: DataStoreManager
) {

    /**
     * Search airports by IATA code starting with the given query
     */
    suspend fun searchAirports(query: String): List<Airport> {
        return airportDao.getAirportByCodeStartingWith(query)
    }

    /**
     * Search airports by name containing the query
     */
    suspend fun searchAirportsByName(query: String): List<Airport> {
        return airportDao.searchAirportsByName(query)
    }

    /**
     * Get flights from a specific departure airport
     */
    suspend fun getFlightsFromAirport(departureAirportId: Int): List<Airport> {
        return airportDao.getDestinations(departureAirportId)
    }

    /**
     * Search airports combining both code and name search
     */
    suspend fun searchAirportsCombined(query: String): List<Airport> {
        val byCode = airportDao.getAirportByCodeStartingWith(query)
        val byName = airportDao.searchAirportsByName(query)
        // Combine and remove duplicates
        return (byCode + byName).distinctBy { it.id }
    }

    /**
     * Get all airports
     */
    suspend fun getAllAirports(): List<Airport> {
        return airportDao.getAllAirports()
    }

    /**
     * Get search query from DataStore
     */
    fun getSearchQuery(): Flow<String> {
        return dataStoreManager.getSearchQuery()
    }

    /**
     * Save search query to DataStore
     */
    suspend fun setSearchQuery(query: String) {
        dataStoreManager.setSearchQuery(query)
    }

    /**
     * Clear search query from DataStore
     */
    suspend fun clearSearchQuery() {
        dataStoreManager.clearSearchQuery()
    }

    /**
     * Get all favorite routes
     */
    suspend fun getAllFavorites(): List<Favorite> {
        return favoriteDao.getAllFavorites()
    }

    /**
     * Check if a route is favorited
     */
    suspend fun isRouteFavorited(departureCode: String, destinationCode: String): Boolean {
        return favoriteDao.getFavorite(departureCode, destinationCode) != null
    }

    /**
     * Toggle favorite status for a route
     */
    suspend fun toggleFavorite(departureCode: String, destinationCode: String, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteDao.insertFavorite(Favorite(departureCode = departureCode, destinationCode = destinationCode))
        } else {
            favoriteDao.getFavorite(departureCode, destinationCode)?.let {
                favoriteDao.deleteFavorite(it)
            }
        }
    }

    /**
     * Get favorite by route
     */
    suspend fun getFavorite(departureCode: String, destinationCode: String): Favorite? {
        return favoriteDao.getFavorite(departureCode, destinationCode)
    }

    /**
     * Insert a favorite route to database
     */
    suspend fun insertFavorite(favorite: Favorite) {
        favoriteDao.insertFavorite(favorite)
    }

    /**
     * Delete a favorite route from database
     */
    suspend fun deleteFavorite(favorite: Favorite) {
        favoriteDao.deleteFavorite(favorite)
    }
}