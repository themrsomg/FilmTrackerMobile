package com.example.santabarbaramobile.feature.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.santabarbaramobile.R
import com.example.santabarbaramobile.feature.shows.domain.Show
import com.example.santabarbaramobile.feature.profile.domain.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHubScreen(
    viewModel: MainHubViewModel = hiltViewModel(),
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    onNavigateToMyProfile: () -> Unit,
    onNavigateToOtherProfile: (String, String) -> Unit,
    onNavigateToShowDetail: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToLeaderboards: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchUserResult by viewModel.searchUserResult.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        notificationsViewModel.fetchUnreadCount()
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("FilmTracker", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onNavigateToLeaderboards) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Salón de la Fama",
                                modifier = Modifier.size(28.dp),
                                tint = Color(0xFFFFD700)
                            )
                        }
                        if (viewModel.currentUserRole == "ADMIN") {
                            IconButton(onClick = onNavigateToAdminPanel) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Panel de Administración",
                                    modifier = Modifier.size(28.dp),
                                    tint = Color(0xFFFFD700)
                                )
                            }
                        }

                        IconButton(onClick = onNavigateToNotifications) {
                            BadgedBox(
                                badge = {
                                    if (notificationsViewModel.unreadCount > 0) {
                                        Badge(
                                            containerColor = Color.Red,
                                            contentColor = Color.White
                                        ) {
                                            Text(notificationsViewModel.unreadCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notificaciones",
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        IconButton(onClick = onNavigateToMyProfile) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Ir a mi Perfil",
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    onSearch = { viewModel.setSearchActive(false) },
                    active = isSearchActive,
                    onActiveChange = { viewModel.setSearchActive(it) },
                    placeholder = { Text("Buscar series o usuarios...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    trailingIcon = {
                        if (isSearchActive) {
                            IconButton(onClick = { viewModel.setSearchActive(false) }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar búsqueda")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isSearchActive) 0.dp else 16.dp)
                        .padding(bottom = 8.dp)
                ) {
                    if (searchQuery.trim().lowercase() == "jaire") {
                        EasterEggContent()
                    } else if (searchResults.isNotEmpty() || searchUserResult != null) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {

                            if (searchUserResult != null) {
                                item {
                                    val user = searchUserResult!!
                                    ListItem(
                                        headlineContent = { Text(user.name ?: "Usuario", fontWeight = FontWeight.Bold) },
                                        supportingContent = { Text("@${user.username}", color = MaterialTheme.colorScheme.primary) },
                                        leadingContent = {
                                            if (user.profileImage.isNullOrBlank()) {
                                                Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
                                            } else {
                                                AsyncImage(
                                                    model = user.profileImage,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(56.dp).clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        },
                                        modifier = Modifier.clickable {
                                            viewModel.setSearchActive(false)
                                            val targetId = user.authId ?: user.id ?: ""
                                            onNavigateToOtherProfile(targetId, user.username)
                                        }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 2.dp)
                                }
                            }

                            items(searchResults, key = { it.tvmazeId }) { show ->
                                ListItem(
                                    headlineContent = { Text(show.name, fontWeight = FontWeight.Bold) },
                                    supportingContent = { Text(show.genres.joinToString(", "), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    leadingContent = {
                                        AsyncImage(
                                            model = show.image?.medium ?: show.image?.original,
                                            contentDescription = null,
                                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        viewModel.setSearchActive(false)
                                        onNavigateToShowDetail(show.tvmazeId.toString())
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    } else if (searchQuery.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron resultados para '$searchQuery'", color = Color.Gray)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (!isSearchActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> LoadingView()
                    is HomeUiState.Error -> ErrorView(message = state.message) {
                        viewModel.fetchHomeData()
                    }
                    is HomeUiState.Success -> {
                        MainContent(
                            featured = state.data.featured,
                            topRated = state.data.topRated,
                            recent = state.data.recent,
                            onShowClick = onNavigateToShowDetail
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    featured: List<Show>,
    topRated: List<Show>,
    recent: List<Show>,
    onShowClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { SectionHeader("Series Destacadas") }
        item { MediaCarousel(featured, onShowClick) }

        item { SectionHeader("Mejor Puntuadas") }
        item { MediaCarousel(topRated, onShowClick) }

        item { SectionHeader("Series Nuevas") }
        item { MediaCarousel(recent, onShowClick) }

        item { SectionHeader("Para Maratonear") }
        item { MediaCarousel(recent.asReversed(), onShowClick) }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun MediaCarousel(shows: List<Show>, onShowClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(shows, key = { it.tvmazeId }) { show ->
            MediaCard(show = show, onClick = { onShowClick(show.tvmazeId.toString()) })
        }
    }
}

@Composable
private fun MediaCard(show: Show, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(width = 160.dp, height = 240.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = show.image?.medium ?: show.image?.original,
                contentDescription = "Poster de ${show.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = show.name,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                show.rating?.average?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = rating.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Cargando catálogo...", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error al conectar con el servidor",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}

@Composable
fun EasterEggContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.jaire_egg),
                contentDescription = "Easter Egg Jaire",
                modifier = Modifier.size(250.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "¡Has encontrado a Jaire Alexander!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}