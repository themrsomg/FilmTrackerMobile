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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.santabarbaramobile.core.ui.ReportDialog
import com.example.santabarbaramobile.core.ui.ReviewCard
import com.example.santabarbaramobile.feature.reviews.domain.ReviewDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReviewsScreen(
    userId: String,
    viewModel: UserReviewsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToReviewDetail: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showReportDialog by remember { mutableStateOf(false) }
    var reviewToReport by remember { mutableStateOf<String?>(null) }

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
                        onReviewClick = onNavigateToReviewDetail,
                        viewModel = viewModel,
                        onReportRequest = { rId ->
                            reviewToReport = rId
                            showReportDialog = true
                        }
                    )
                }
            }
        }
        if (showReportDialog && reviewToReport != null) {
            ReportDialog(
                targetType = "REVIEW",
                targetId = reviewToReport!!,
                onDismiss = { showReportDialog = false },
                onSubmit = { type, id, reason, desc ->
                    showReportDialog = false
                    viewModel.reportReview(id, reason, desc)
                }
            )
        }
    }
}

@Composable
fun UserReviewsList(
    reviews: List<ReviewDto>,
    onReviewClick: (Int) -> Unit,
    viewModel: UserReviewsViewModel,
    onReportRequest: (String) -> Unit,
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
                onLikeClick = { id, isLiked -> viewModel.toggleLike(id, isLiked) },
                onCardClick = { onReviewClick(review.tvmazeId) },
                onReportClick = { onReportRequest(review.id) },
                onRemoveImageClick = { }
            )
        }
    }
}