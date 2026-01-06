package com.example.fbflightsearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for Airport operations
 */
@Dao
interface AirportDao {

    /**
     * Get all airports
     */
    @Query("SELECT * FROM airport")
    suspend fun getAllAirports(): List<Airport>

    /**
     * Get airport by IATA code
     */
    @Query("SELECT * FROM airport WHERE iata_code LIKE :query || '%'")
    suspend fun getAirportByCodeStartingWith(query: String): List<Airport>

    /**
     * Search airports by name containing the query
     */
    @Query("SELECT * FROM airport WHERE name LIKE '%' || :query || '%'")
    suspend fun searchAirportsByName(query: String): List<Airport>

    /**
     * Get all possible destination airports from a departure airport
     */
    @Query("SELECT * FROM airport WHERE id != :departureAirportId ORDER BY passengers DESC")
    suspend fun getDestinations(departureAirportId: Int): List<Airport>

    /**
     * Insert airports (replace on conflict)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(airports: List<Airport>)
}