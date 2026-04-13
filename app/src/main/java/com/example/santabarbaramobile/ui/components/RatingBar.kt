package com.example.santabarbaramobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SeriesRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starIndex = index + 1
            Icon(
                imageVector = if (starIndex <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Valoración $starIndex",
                tint = if (starIndex <= rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChanged(starIndex) }
            )
        }
    }
}