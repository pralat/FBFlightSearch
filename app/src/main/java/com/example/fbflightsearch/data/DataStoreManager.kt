package com.example.fbflightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore for storing user preferences
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Manages user preferences using DataStore
 */
class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
    }

    /**
     * Get the saved search query
     */
    fun getSearchQuery(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[SEARCH_QUERY_KEY] ?: ""
        }
    }

    /**
     * Save the search query
     */
    suspend fun setSearchQuery(query: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_QUERY_KEY] = query
        }
    }

    /**
     * Clear the search query
     */
    suspend fun clearSearchQuery() {
        dataStore.edit { preferences ->
            preferences.remove(SEARCH_QUERY_KEY)
        }
    }
}