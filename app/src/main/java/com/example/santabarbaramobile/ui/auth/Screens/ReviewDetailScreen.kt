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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.santabarbaramobile.ui.auth.ViewModels.ReviewDetailViewModel
import com.example.santabarbaramobile.ui.components.ReportDialog

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
    var showReportDialog by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        selectedImageUri = uri
    }

    LaunchedEffect(reviewId) {
        viewModel.loadData(reviewId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hilo de Comentarios") },
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
        if (viewModel.isLoading && viewModel.mainReview == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                viewModel.mainReview?.let { review ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = CardDefaults.outlinedCardBorder(true)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(review.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(review.content, style = MaterialTheme.typography.bodyLarge)

                                if (!review.imageUrl.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AsyncImage(
                                        model = review.imageUrl.replace("localhost", "10.0.2.2"),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Comentarios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                if (viewModel.commentsUI.isEmpty()) {
                    item {
                        Text("No hay comentarios aún. ¡Inicia la discusión!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    items(viewModel.commentsUI) { item ->
                        val comment = item.comment
                        val user = item.user
                        var showMenu by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (user?.profileImage.isNullOrBlank()) {
                                            Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                                        } else {
                                            AsyncImage(
                                                model = user?.profileImage?.replace("localhost", "10.0.2.2"),
                                                contentDescription = null,
                                                modifier = Modifier.size(32.dp).clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(user?.name ?: "Usuario", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text("@${user?.username ?: "anonimo"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }

                                    Box {
                                        IconButton(onClick = { showMenu = true }) {
                                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                                        }
                                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                            if (viewModel.currentUserId == comment.authId || viewModel.currentUserRole == "ADMIN") {
                                                DropdownMenuItem(
                                                    text = { Text("Eliminar Comentario", color = MaterialTheme.colorScheme.error) },
                                                    onClick = {
                                                        showMenu = false
                                                        viewModel.deleteComment(comment.id.toString(), reviewId)
                                                    }
                                                )
                                            }

                                            if (viewModel.currentUserRole == "ADMIN" && !comment.imageUrl.isNullOrBlank()) {
                                                DropdownMenuItem(
                                                    text = { Text("Eliminar foto (Admin)", color = Color(0xFFFF9800)) },
                                                    onClick = {
                                                        showMenu = false
                                                        viewModel.removeCommentImage(comment.id.toString(), reviewId)
                                                    }
                                                )
                                            }

                                            if (viewModel.currentUserId != comment.authId) {
                                                DropdownMenuItem(
                                                    text = { Text("Reportar Comentario") },
                                                    onClick = {
                                                        showMenu = false
                                                        showReportDialog = comment.id.toString()
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(comment.content, style = MaterialTheme.typography.bodyMedium)

                                if (!comment.imageUrl.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AsyncImage(
                                        model = comment.imageUrl.replace("localhost", "10.0.2.2"),
                                        contentDescription = "Imagen del comentario",
                                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showReportDialog != null) {
            ReportDialog(
                targetType = "COMMENT",
                targetId = showReportDialog!!,
                onDismiss = { showReportDialog = null },
                onSubmit = { type, id, reason, desc ->
                    viewModel.reportComment(id, reason, desc)
                    showReportDialog = null
                }
            )
        }

        if (showVerificationDialog) {
            AlertDialog(
                onDismissRequest = { showVerificationDialog = false },
                title = { Text("Verificación Requerida") },
                text = { Text("Debes verificar tu correo electrónico para poder comentar en las reseñas y participar en la comunidad.") },
                confirmButton = {
                    Button(onClick = { showVerificationDialog = false }) { Text("Entendido") }
                }
            )
        }
    }
}