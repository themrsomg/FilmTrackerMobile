package com.example.santabarbaramobile.ui.auth.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.santabarbaramobile.data.model.models.LibraryItemDto
import com.example.santabarbaramobile.ui.auth.States.ProfileState
import com.example.santabarbaramobile.ui.auth.ViewModels.ProfileViewModel
import com.example.santabarbaramobile.ui.components.ReportDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToFriendsManager: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToMyReports: () -> Unit,
    onNavigateToConfirm: (String) -> Unit
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.isOwnProfile) "Mi perfil" else "Perfil de @${state.username}")
                },
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
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentAlignment = if (state.isLoading) Alignment.Center else Alignment.TopCenter
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                else -> ProfileContent(
                    state = state,
                    onLogout = onLogout,
                    onNavigateToFriendsManager = onNavigateToFriendsManager,
                    onNavigateToMyReports = onNavigateToMyReports,
                    onAddFriend = { viewModel.sendFriendRequest() },
                    onRemoveFriend = { viewModel.removeFriend() },
                    onNavigateToConfirm = onNavigateToConfirm,
                    onSuspendUser = { days -> viewModel.suspendCurrentUser(days) },
                    onBanUser = { viewModel.banCurrentUser() },
                    onUnbanUser = { viewModel.unbanCurrentUser() },
                    onReportUser = { reason, desc -> viewModel.reportCurrentUser(reason, desc) }
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    state: ProfileState,
    onLogout: () -> Unit,
    onNavigateToFriendsManager: () -> Unit,
    onNavigateToMyReports: () -> Unit,
    onAddFriend: () -> Unit,
    onRemoveFriend: () -> Unit,
    onNavigateToConfirm: (String) -> Unit,
    onSuspendUser: (Long) -> Unit,
    onBanUser: () -> Unit,
    onUnbanUser: () -> Unit,
    onReportUser: (String, String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 20.dp)
    ) {
        if (state.profileImage.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier.size(132.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            AsyncImage(
                model = state.profileImage,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(132.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = state.name.ifBlank { state.username.ifBlank { "Usuario" } },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "@${state.username.ifBlank { "sin-username" }}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (state.currentUserRole == "ADMIN") {
                Spacer(modifier = Modifier.width(6.dp))
                Badge(
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color.Black
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                        Icon(Icons.Default.Verified, contentDescription = "Admin", modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("ADMIN", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        if (state.isOwnProfile && !state.isEmailVerified) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tu cuenta no está verificada", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                    Text("No podrás escribir reseñas ni comentarios hasta que la verifiques.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onNavigateToConfirm(state.email) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Verificar Correo", color = Color.White)
                    }
                }
            }
        }

        if (!state.isOwnProfile && state.targetAuthId.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            when (state.friendshipStatus) {
                "LOADING" -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                "FRIENDS" -> OutlinedButton(onClick = onRemoveFriend) {
                    Text("Eliminar amigo")
                }
                "PENDING_OUTGOING" -> OutlinedButton(onClick = { }, enabled = false) {
                    Text("Solicitud enviada")
                }
                "PENDING_INCOMING" -> Button(onClick = { /* Solo informativo */ }, enabled = false) {
                    Text("Responder solicitud")
                }
                else -> Button(
                    onClick = onAddFriend,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Agregar amigo")
                }
            }

            var showReportDialog by remember { mutableStateOf(false) }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showReportDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Reportar Perfil")
            }

            if (showReportDialog) {
                ReportDialog(
                    targetType = "USER",
                    targetId = state.targetAuthId,
                    onDismiss = { showReportDialog = false },
                    onSubmit = { type, id, reason, desc ->
                        showReportDialog = false
                        onReportUser(reason, desc)
                    }
                )
            }

            if (state.currentUserRole == "ADMIN") {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Herramientas de Administrador", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))

                if (state.targetAccountStatus == "BANNED") {
                    Button(
                        onClick = onUnbanUser,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("Quitar Baneo / Activar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    var showSuspendDialog by remember { mutableStateOf(false) }
                    var showBanDialog by remember { mutableStateOf(false) }
                    val duraciones = listOf(1L to "1 día", 3L to "3 días", 7L to "7 días", 30L to "30 días")
                    var selectedDuration by remember { mutableStateOf(duraciones.first()) }

                    Button(
                        onClick = { showSuspendDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("Suspender temporalmente", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showBanDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("Banear permanentemente", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (showSuspendDialog) {
                        AlertDialog(
                            onDismissRequest = { showSuspendDialog = false },
                            title = { Text("Suspender a @${state.username}") },
                            text = {
                                Column {
                                    Text("Selecciona la duración de la suspensión:")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    duraciones.forEach { duracion ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = (duracion == selectedDuration),
                                                onClick = { selectedDuration = duracion }
                                            )
                                            Text(text = duracion.second)
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    showSuspendDialog = false
                                    onSuspendUser(selectedDuration.first)
                                }) { Text("Suspender") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showSuspendDialog = false }) { Text("Cancelar") }
                            }
                        )
                    }

                    if (showBanDialog) {
                        AlertDialog(
                            onDismissRequest = { showBanDialog = false },
                            title = { Text("¿Banear a @${state.username}?") },
                            text = { Text("¿Estás seguro de que deseas banear permanentemente a este usuario?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showBanDialog = false
                                        onBanUser()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))
                                ) { Text("Sí, Banear") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showBanDialog = false }) { Text("Cancelar") }
                            }
                        )
                    }
                }
            }

            if (!state.banSuccessMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.banSuccessMessage,
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

        } else if (state.isOwnProfile) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToFriendsManager,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Icon(Icons.Default.People, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gestor de Amistades")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onNavigateToMyReports,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Icon(Icons.Outlined.Flag, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mis Reportes", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (state.isOwnProfile) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Cuenta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileRow(label = "Email", value = state.email.ifBlank { "Sin correo registrado" })
                    ProfileRow(
                        label = "Estado",
                        value = if (state.isEmailVerified) "Email verificado" else "Email pendiente"
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            if (state.isOwnProfile) {
                CarouselSection(title = "Mi Watchlist", items = state.watchlist)
                Spacer(modifier = Modifier.height(24.dp))
                CarouselSection(title = "Mis Favoritos", items = state.favorites)
            } else {
                CarouselSection(title = "Favoritos de @${state.username}", items = state.favorites)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (!state.error.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (state.isOwnProfile) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Cerrar sesion")
            }
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value.contains("verificado", ignoreCase = true)) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CarouselSection(title: String, items: List<LibraryItemDto>) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    if (items.isEmpty()) {
        Text(
            text = "No hay elementos en esta lista.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                ShowCard(item)
            }
        }
    }
}

@Composable
fun ShowCard(item: LibraryItemDto) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (item.imageUrl != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin imagen",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(Color(0x99000000))
                    .padding(8.dp)
            ) {
                Text(
                    text = item.name ?: "Desconocido",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}