package com.example.santabarbaramobile.ui.auth.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.santabarbaramobile.data.model.dtos.UserDto
import com.example.santabarbaramobile.ui.auth.ViewModels.FriendsManagerViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.RequestUIItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsManagerScreen(
    viewModel: FriendsManagerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String, String) -> Unit
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestor de Amistades") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text("Mis Amigos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (state.friends.isEmpty()) {
                    Text("No tienes amigos agregados aún.", color = Color.Gray)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.friends) { friend ->
                            FriendCard(user = friend.user, onClick = {
                                val targetId = friend.user.authId ?: friend.user.id ?: ""
                                onNavigateToProfile(targetId, friend.user.username)
                            })
                        }
                    }
                }

                HorizontalDivider()

                Text("Solicitudes Recibidas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (state.incomingRequests.isEmpty()) {
                    Text("No tienes solicitudes pendientes.", color = Color.Gray)
                } else {
                    state.incomingRequests.forEach { req ->
                        RequestCard(
                            req = req,
                            isIncoming = true,
                            onAccept = { viewModel.acceptRequest(req.requestId) },
                            onReject = { viewModel.rejectRequest(req.requestId) },
                            onCancel = {}
                        )
                    }
                }

                HorizontalDivider()

                Text("Solicitudes Enviadas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (state.outgoingRequests.isEmpty()) {
                    Text("No has enviado solicitudes.", color = Color.Gray)
                } else {
                    state.outgoingRequests.forEach { req ->
                        RequestCard(
                            req = req,
                            isIncoming = false,
                            onAccept = {}, onReject = {},
                            onCancel = { viewModel.cancelRequest(req.requestId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendCard(user: UserDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(130.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            if (user.profileImage.isNullOrBlank()) {
                Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.primary)
            } else {
                AsyncImage(model = user.profileImage, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(60.dp).clip(CircleShape))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(user.name ?: "Usuario", fontWeight = FontWeight.Bold, maxLines = 1)
            Text("@${user.username}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun RequestCard(req: RequestUIItem, isIncoming: Boolean, onAccept: () -> Unit, onReject: () -> Unit, onCancel: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (req.user.profileImage.isNullOrBlank()) {
                Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(48.dp))
            } else {
                AsyncImage(model = req.user.profileImage, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(48.dp).clip(CircleShape))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(req.user.name ?: "Usuario", fontWeight = FontWeight.Bold)
                Text("@${req.user.username}", style = MaterialTheme.typography.labelSmall)
            }
            if (isIncoming) {
                Row {
                    IconButton(onClick = onReject) { Icon(Icons.Default.Close, contentDescription = "Rechazar", tint = Color.Gray) }
                    IconButton(onClick = onAccept) { Icon(Icons.Default.CheckCircle, contentDescription = "Aceptar", tint = Color(0xFF4CAF50)) }
                }
            } else {
                Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Cancelar")
                }
            }
        }
    }
}