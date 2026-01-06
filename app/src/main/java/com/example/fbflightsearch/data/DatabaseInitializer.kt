package com.example.fbflightsearch.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Initializes the database with airport data from CSV
 */
class DatabaseInitializer(
    private val context: Context,
    private val database: AppDatabase
) {

    /**
     * Load airports from CSV file and insert into database
     */
    suspend fun loadAirportsFromAsset() = withContext(Dispatchers.IO) {
        try {
            // Check if database already has data
            val existingAirports = database.airportDao().getAllAirports()
            if (existingAirports.isNotEmpty()) {
                return@withContext
            }

            // Read CSV file
            val csvContent = context.assets.open("airports.csv").bufferedReader().use { it.readText() }

            // Parse CSV and create Airport objects
            val airports = parseCsvToAirports(csvContent)

            // Insert into database
            database.airportDao().insertAll(airports)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Parse CSV content to list of Airport objects
     */
    private fun parseCsvToAirports(csvContent: String): List<Airport> {
        val lines = csvContent.lines()

        // Skip header line
        if (lines.isEmpty()) return emptyList()

        return lines.drop(1).mapNotNull { line ->
            val parts = line.split(",")
            if (parts.size >= 5) {
                try {
                    Airport(
                        id = parts[0].trim().toInt(),
                        name = parts[1].trim(),
                        iataCode = parts[2].trim(),
                        passengers = parts[3].trim().toInt(),
                        destinations = parts[4].trim().toInt()
                    )
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }
}