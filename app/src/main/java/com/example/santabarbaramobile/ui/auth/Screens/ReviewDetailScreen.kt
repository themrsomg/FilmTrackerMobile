package com.example.santabarbaramobile.ui.auth.Screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.santabarbaramobile.ui.auth.ViewModels.ReviewDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    reviewId: String,
    viewModel: ReviewDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var showVerificationDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        selectedImageUri = uri
    }

    LaunchedEffect(reviewId) {
        viewModel.loadComments(reviewId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comentarios") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Añadir imagen",
                            tint = if (selectedImageUri != null) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(if (selectedImageUri != null) "Imagen adjunta..." else "Escribe un comentario...") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (viewModel.isEmailVerified) {
                                viewModel.postComment(context, reviewId, commentText, selectedImageUri)
                                commentText = ""
                                selectedImageUri = null
                            } else {
                                showVerificationDialog = true
                            }
                        },
                        enabled = commentText.isNotBlank() || selectedImageUri != null
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (viewModel.comments.isEmpty()) {
                    item {
                        Text("No hay comentarios aún. ¡Inicia la discusión!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    items(viewModel.comments) { comment ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Usuario Anónimo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)

                                    if (viewModel.currentUserId == comment.authId || viewModel.currentUserRole == "ADMIN") {
                                        IconButton(onClick = { viewModel.deleteComment(comment.id.toString(), reviewId) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))

                                Text(comment.content, style = MaterialTheme.typography.bodyMedium)

                                if (!comment.imageUrl.isNullOrBlank()) {
                                    val fixedUrl = comment.imageUrl.replace("localhost", "10.0.2.2")
                                    AsyncImage(
                                        model = fixedUrl,
                                        contentDescription = "Imagen del comentario",
                                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)).padding(top = 8.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showVerificationDialog) {
            AlertDialog(
                onDismissRequest = { showVerificationDialog = false },
                title = { Text("Verificación Requerida") },
                text = { Text("Debes verificar tu correo electrónico para poder comentar en las reseñas y participar en la comunidad.") },
                confirmButton = {
                    Button(onClick = {
                        showVerificationDialog = false
                    }) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}