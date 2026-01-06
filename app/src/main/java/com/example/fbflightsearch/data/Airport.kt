package com.example.fbflightsearch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an airport in the flight search app
 */
@Entity(tableName = "airport")
data class Airport(
    @PrimaryKey
    val id: Int,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("iata_code")
    val iataCode: String,

    @ColumnInfo("passengers")
    val passengers: Int,

    @ColumnInfo("destinations")
    val destinations: Int
)