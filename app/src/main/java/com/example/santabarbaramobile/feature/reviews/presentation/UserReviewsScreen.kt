package com.example.santabarbaramobile.feature.reviews.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.santabarbaramobile.core.ui.ReviewCard
import com.example.santabarbaramobile.feature.reviews.domain.ReviewDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReviewsScreen(
    userId: String,
    viewModel: UserReviewsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToReviewDetail: (Int) -> Unit // Recibe un Int (tvmazeId) para ir a la serie
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUserReviews(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reseñas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
                uiState.reviews.isEmpty() -> Text(
                    text = "Este usuario aún no tiene reseñas publicadas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                else -> {
                    UserReviewsList(
                        reviews = uiState.reviews,
                        onReviewClick = onNavigateToReviewDetail
                    )
                }
            }
        }
    }
}

@Composable
fun UserReviewsList(
    reviews: List<ReviewDto>,
    onReviewClick: (Int) -> Unit, // Espera el tvmazeId
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = reviews,
            key = { it.id }
        ) { review ->

            ReviewCard(
                review = review,
                isOwner = false,
                isAdmin = false,
                onEditClick = { },
                onDeleteClick = { },
                onLikeClick = { _, _ -> /* Se puede implementar más adelante */ },
                onCardClick = { onReviewClick(review.tvmazeId) },
                onReportClick = { },
                onRemoveImageClick = { }
            )

        }
    }
}