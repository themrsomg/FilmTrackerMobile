package com.example.santabarbaramobile.ui.auth.Screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.text.HtmlCompat
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.santabarbaramobile.data.model.CastMember
import com.example.santabarbaramobile.data.model.Episode
import com.example.santabarbaramobile.data.model.Season
import com.example.santabarbaramobile.data.model.Show
import com.example.santabarbaramobile.ui.auth.ViewModels.ShowDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    showId: String,
    viewModel: ShowDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToShowDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(showId) { viewModel.fetchFullShowDetails(showId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.show?.name ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(showId) }) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (uiState.isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (uiState.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = { viewModel.toggleWatchlist(showId) }) {
                        Icon(
                            imageVector = if (uiState.isInWatchlist) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (uiState.isInWatchlist) "Quitar de watchlist" else "Agregar a watchlist",
                            tint = if (uiState.isInWatchlist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Algo salió mal",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(text = uiState.error ?: "Error desconocido")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchFullShowDetails(showId) }) {
                            Text("Reintentar")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        uiState.show?.let { show ->
                            ShowHeader(show)
                        }
                    }

                    if (uiState.similarShows.isNotEmpty()) {
                        item {
                            Text(
                                "Series Similares", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                            SimilarShowsCarousel(uiState.similarShows, onNavigateToShowDetail)
                        }
                    }

                    if (uiState.cast.isNotEmpty()) {
                        item {
                            Text(
                                "Elenco Principal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                            CastCarousel(uiState.cast)
                        }
                    }

                    if (uiState.seasonsWithEpisodes.isNotEmpty()) {
                        item {
                            Text(
                                "Temporadas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        items(uiState.seasonsWithEpisodes.toList()) { (season, episodes) ->
                            ExpandableSeasonCard(season = season, episodes = episodes)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowHeader(show: Show) {
    val cleanSummary = remember(show.summary) {
        if (show.summary.isNullOrEmpty()) {
            "Sin descripción disponible."
        } else {
            HtmlCompat.fromHtml(show.summary, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(450.dp)) {
        AsyncImage(
            model = show.image?.original ?: show.image?.medium,
            contentDescription = "Poster",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                    startY = 500f
                )
            )
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Text(show.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(cleanSummary, style = MaterialTheme.typography.bodyMedium, maxLines = 4, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun CastCarousel(cast: List<CastMember>) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(cast) { member ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
                AsyncImage(
                    model = member.person.image?.medium,
                    contentDescription = member.person.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.DarkGray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(member.person.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(member.character.name, style = MaterialTheme.typography.labelSmall, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun ExpandableSeasonCard(season: Season, episodes: List<Episode>) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Temporada ${season.number}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expandir"
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    episodes.forEach { episode -> EpisodeRow(episode) }
                }
            }
        }
    }
}

@Composable
private fun EpisodeRow(episode: Episode) {
    val cleanSummary = remember(episode.summary) {
        if (episode.summary.isNullOrEmpty()) {
            "Sin descripción."
        } else {
            HtmlCompat.fromHtml(episode.summary, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = episode.image?.medium,
            contentDescription = "Imagen del episodio: ${episode.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp, 68.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${episode.number}. ${episode.name}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = cleanSummary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun SimilarShowsCarousel(shows: List<Show>, onShowClick: (String) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(shows) { show ->
            Card(
                modifier = Modifier.size(width = 120.dp, height = 180.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = { onShowClick(show.tvmazeId.toString()) }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = show.image?.medium ?: show.image?.original,
                        contentDescription = show.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)), startY = 200f)
                    ))
                    Text(
                        text = show.name,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                    )
                }
            }
        }
    }
}