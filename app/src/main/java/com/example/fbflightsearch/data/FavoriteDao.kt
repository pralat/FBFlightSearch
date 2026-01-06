package com.example.fbflightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Data Access Object for Favorite operations
 */
@Dao
interface FavoriteDao {

    /**
     * Get all favorites
     */
    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): List<Favorite>

    /**
     * Check if a route is favorited
     */
    @Query("SELECT * FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode LIMIT 1")
    suspend fun getFavorite(departureCode: String, destinationCode: String): Favorite?

    /**
     * Insert a favorite route
     */
    @Insert
    suspend fun insertFavorite(favorite: Favorite)

    /**
     * Delete a favorite route
     */
    @Delete
    suspend fun deleteFavorite(favorite: Favorite)
}