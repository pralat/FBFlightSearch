package com.example.fbflightsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.fbflightsearch.data.AppDatabase
import com.example.fbflightsearch.data.DataStoreManager
import com.example.fbflightsearch.data.FlightRepository
import com.example.fbflightsearch.ui.FlightSearchScreen
import com.example.fbflightsearch.ui.theme.FBFlightSearchTheme
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Main Activity for the Flight Search App
 */
class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: FlightRepository
    private lateinit var viewModelFactory: FlightSearchViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize database
        database = AppDatabase.getDatabase(this)

        // Initialize DataStore
        val dataStoreManager = DataStoreManager(this)

        // Initialize repository
        repository = FlightRepository(
            airportDao = database.airportDao(),
            favoriteDao = database.favoriteDao(),
            dataStoreManager = dataStoreManager
        )

        // Initialize ViewModelFactory
        viewModelFactory = FlightSearchViewModelFactory(repository)

        setContent {
            FBFlightSearchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FlightSearchScreen(
                        viewModel = viewModel(factory = viewModelFactory),
                        contentPadding = innerPadding
                    )
                }
            }
        }
    }
}