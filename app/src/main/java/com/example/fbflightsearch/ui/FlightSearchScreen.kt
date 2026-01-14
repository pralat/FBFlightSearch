package com.example.fbflightsearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fbflightsearch.FlightSearchViewModel
import com.example.fbflightsearch.data.Airport

/**
 * Main flight search screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    viewModel: FlightSearchViewModel = viewModel(),
    contentPadding: PaddingValues = PaddingValues()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val selectedAirport by viewModel.selectedAirport.collectAsState()
    val destinations by viewModel.destinations.collectAsState()
    val favoriteDestinations by viewModel.favoriteDestinations.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Flight Search") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Search Box - only show when not viewing flight results
                if (selectedAirport == null) {
                    SearchBox(
                        query = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onClearClick = { viewModel.onSearchQueryChanged("") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Suggestions or Destinations
                when {
                    suggestions.isNotEmpty() -> {
                        AirportSuggestions(
                            airports = suggestions,
                            onAirportClick = { viewModel.onAirportSelected(it) }
                        )
                    }
                    favoriteDestinations.isNotEmpty() && searchQuery.isEmpty() && selectedAirport == null -> {
                        FavoriteDestinationsList(
                            favoriteDestinations = favoriteDestinations,
                            onFavoriteClick = { dep, dest ->
                                viewModel.onToggleFavorite(dep, dest)
                            },
                            isRouteFavorited = { dep, dest ->
                                viewModel.isRouteFavorited(dep, dest)
                            }
                        )
                    }
                    selectedAirport != null && destinations.isEmpty() -> {
                        EmptyState(
                            message = "No flights available from ${selectedAirport?.iataCode ?: ""}"
                        )
                    }
                    selectedAirport != null -> {
                        val airport = selectedAirport!!
                        FlightsList(
                            departureAirport = airport,
                            destinations = destinations,
                            onBackClick = { viewModel.onClearSelection() },
                            onToggleFavorite = { dep, dest ->
                                viewModel.onToggleFavorite(dep, dest)
                            },
                            isRouteFavorited = { dep, dest ->
                                viewModel.isRouteFavorited(dep, dest)
                            }
                        )
                    }
                    else -> {
                        EmptyState(
                            message = "Search for an airport to find flights"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Search box component
 */
@Composable
fun SearchBox(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChanged,
            placeholder = { Text("Search for airport") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearClick) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

/**
 * Airport suggestions dropdown
 */
@Composable
fun AirportSuggestions(
    airports: List<Airport>,
    onAirportClick: (Airport) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        LazyColumn {
            items(airports) { airport ->
                AirportItem(
                    airport = airport,
                    onClick = { onAirportClick(airport) }
                )
            }
        }
    }
}

/**
 * Single airport item
 */
@Composable
fun AirportItem(
    airport: Airport,
    onClick: () -> Unit,
    showFavoriteButton: Boolean = false,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = airport.iataCode,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = airport.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showFavoriteButton && onFavoriteClick != null) {
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * List of saved favorite destinations
 */
@Composable
fun FavoriteDestinationsList(
    favoriteDestinations: List<Pair<Airport, Airport>>,
    onFavoriteClick: (String, String) -> Unit,
    isRouteFavorited: (String, String) -> Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Saved Favorites",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoriteDestinations.isEmpty()) {
            EmptyState(
                message = "No saved favorites yet.\nSearch for flights and tap the heart icon to save them."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteDestinations) { (departure, destination) ->
                    FlightCard(
                        departure = departure,
                        destination = destination,
                        isFavorite = isRouteFavorited(departure.iataCode, destination.iataCode),
                        onFavoriteClick = {
                            onFavoriteClick(departure.iataCode, destination.iataCode)
                        }
                    )
                }
            }
        }
    }
}

/**
 * List of flights from a departure airport
 */
@Composable
fun FlightsList(
    departureAirport: Airport,
    destinations: List<Airport>,
    onBackClick: () -> Unit,
    onToggleFavorite: (String, String) -> Unit,
    isRouteFavorited: (String, String) -> Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header with back button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Flights from ${departureAirport.iataCode}",
                style = MaterialTheme.typography.titleLarge
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(destinations) { destination ->
                FlightCard(
                    departure = departureAirport,
                    destination = destination,
                    isFavorite = isRouteFavorited(departureAirport.iataCode, destination.iataCode),
                    onFavoriteClick = {
                        onToggleFavorite(departureAirport.iataCode, destination.iataCode)
                    }
                )
            }
        }
    }
}

/**
 * Flight card showing departure and destination
 */
@Composable
fun FlightCard(
    departure: Airport,
    destination: Airport,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Departure
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = departure.iataCode,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Depart",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow icon
            Icon(
                imageVector = Icons.Default.Search, // Using search as a placeholder for airplane
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            // Destination
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = destination.iataCode,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = destination.name.split(",").firstOrNull() ?: "Destination",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Favorite button
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Empty state component
 */
@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}