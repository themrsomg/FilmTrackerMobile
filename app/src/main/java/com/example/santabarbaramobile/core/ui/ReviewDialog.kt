package com.example.santabarbaramobile.core.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.santabarbaramobile.feature.reviews.domain.ReviewDto

@Composable
fun ReviewDialog(
    initialReview: ReviewDto? = null,
    onDismiss: () -> Unit,
    onSubmit: (Int, String, String, Uri?) -> Unit
) {
    var rating by remember { mutableIntStateOf(initialReview?.rating ?: 5) }
    var title by remember { mutableStateOf(initialReview?.title ?: "") }
    var content by remember { mutableStateOf(initialReview?.content ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> selectedImageUri = uri }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialReview == null) "Nueva Reseña" else "Editar Reseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row {
                    (1..5).forEach { index ->
                        IconButton(onClick = { rating = index }) {
                            Icon(
                                imageVector = if (index <= rating) Icons.Filled.Star else Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index <= rating) Color(0xFFFFCC00) else Color.Gray
                            )
                        }
                    }
                }
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Tu opinión") }, minLines = 3)

                if (initialReview == null) {
                    Button(onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Text(if (selectedImageUri == null) "Añadir Imagen" else "Imagen seleccionada")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating, title, content, selectedImageUri) }) {
                Text("Publicar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}