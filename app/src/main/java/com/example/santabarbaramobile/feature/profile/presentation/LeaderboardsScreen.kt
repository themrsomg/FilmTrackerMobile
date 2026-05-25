package com.example.santabarbaramobile.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.santabarbaramobile.feature.profile.domain.TopReviewDto
import com.example.santabarbaramobile.feature.profile.domain.TopUserDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardsScreen(
    viewModel: LeaderboardsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onUserClick: (String, String) -> Unit,
    onReviewClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Usuarios Top", "Reseñas Top")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Salón de la Fama", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                viewModel.isLoading -> CircularProgressIndicator()
                viewModel.errorMessage != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadLeaderboards() }) { Text("Reintentar") }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (selectedTab == 0) {
                            itemsIndexed(viewModel.topUsers) { index, user ->
                                TopUserCard(index + 1, user, onUserClick)
                            }
                        } else {
                            itemsIndexed(viewModel.topReviews) { index, review ->
                                TopReviewCard(index + 1, review, onReviewClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopUserCard(rank: Int, user: TopUserDto, onUserClick: (String, String) -> Unit) {
    val medalColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            onUserClick(user.authId, user.username ?: "usuario")
        },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(medalColor),
                contentAlignment = Alignment.Center
            ) {
                Text("#$rank", color = Color.Black, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (user.profileImage.isNullOrBlank()) {
                Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.primary)
            } else {
                AsyncImage(
                    model = user.profileImage.replace("localhost", "10.0.2.2"),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(50.dp).clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text("@${user.username ?: "Usuario Desconocido"}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Puntuación Total: ${user.totalScore ?: 0}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun TopReviewCard(rank: Int, review: TopReviewDto, onReviewClick: (String) -> Unit) {
    val medalColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            onReviewClick(review.reviewId)
        },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("#$rank", color = medalColor, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.width(8.dp))
                Text(review.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${review.likesCount ?: 0}", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(review.content, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray, maxLines = 3, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Por @${review.username ?: "Usuario"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFCC00), modifier = Modifier.size(14.dp))
                    Text(" ${review.rating}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}