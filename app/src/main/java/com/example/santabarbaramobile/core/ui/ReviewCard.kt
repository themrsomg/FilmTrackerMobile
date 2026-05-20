package com.example.santabarbaramobile.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.santabarbaramobile.feature.reviews.domain.ReviewDto

@Composable
fun ReviewCard(
    review: ReviewDto,
    isOwner: Boolean,
    isAdmin: Boolean = false,
    onEditClick: (ReviewDto) -> Unit,
    onDeleteClick: (String) -> Unit,
    onLikeClick: (String, Boolean) -> Unit,
    onCardClick: () -> Unit,
    onReportClick: () -> Unit,
    onRemoveImageClick: () -> Unit
) {
    val showEditButton = isOwner
    val showDeleteButton = isOwner || isAdmin

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = review.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)

                if (showEditButton || showDeleteButton) {
                    Row {
                        if (showEditButton) {
                            IconButton(onClick = { onEditClick(review) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        if (showDeleteButton) {
                            IconButton(onClick = { onDeleteClick(review.id.toString()) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            Text(text = "⭐ ".repeat(review.rating), color = Color(0xFFFFCC00))

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = review.content, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)

            if (!review.imageUrl.isNullOrBlank()) {
                val fixedUrl = review.imageUrl.replace("localhost", "10.0.2.2")
                Column {
                    AsyncImage(
                        model = fixedUrl,
                        contentDescription = "Imagen de la reseña",
                        modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)).padding(top = 8.dp),
                        contentScale = ContentScale.Crop
                    )

                    if (isAdmin) {
                        TextButton(onClick = { onRemoveImageClick() }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Eliminar foto (Admin)", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onLikeClick(review.id.toString(), review.likedByMe) }) {
                        Icon(
                            imageVector = if (review.likedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (review.likedByMe) Color.Red else Color.Gray
                        )
                    }
                    Text(text = "${review.likesCount} likes", color = Color.Gray)
                }

                if (!isOwner) {
                    TextButton(onClick = { onReportClick() }) {
                        Icon(Icons.Outlined.Flag, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reportar", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}