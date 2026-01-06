package com.example.fbflightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore keys for storing user preferences
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Manages user preferences using DataStore
 */
class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    /**
     * Check if a route is favorited by combining departure and destination codes
     */
    fun isRouteFavorited(departureCode: String, destinationCode: String): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey("${departureCode}_$destinationCode")] ?: false
        }
    }

    /**
     * Set a route as favorited or unfavorited
     */
    suspend fun toggleFavorite(departureCode: String, destinationCode: String, isFavorite: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("${departureCode}_$destinationCode")] = isFavorite
        }
    }

    /**
     * Clear all favorites
     */
    suspend fun clearAllFavorites() {
        dataStore.edit { it.clear() }
    }
}