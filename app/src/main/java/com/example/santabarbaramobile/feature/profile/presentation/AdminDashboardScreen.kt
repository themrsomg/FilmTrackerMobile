package com.example.santabarbaramobile.feature.profile.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.santabarbaramobile.feature.profile.domain.AdminDashboardState
import com.example.santabarbaramobile.feature.profile.domain.AdminReportDto
import com.example.santabarbaramobile.feature.profile.domain.UserDetailData
import com.example.santabarbaramobile.feature.profile.domain.UserDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Estadísticas", "Usuarios", "Bandeja de Reportes")
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Panel Supremo", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                    }
                )
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = (selectedTab == index),
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold) },
                            icon = {
                                val icon = when (index) {
                                    0 -> Icons.Default.QueryStats
                                    1 -> Icons.Default.PersonSearch
                                    else -> Icons.Default.Gavel
                                }
                                Icon(imageVector = icon, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTab) {
                0 -> MetricsTabContent(uiState)
                1 -> UsersSearchTabContent(uiState, searchQuery, viewModel::onSearchQueryChanged, viewModel::loadUserDetails, viewModel::executeDirectUserAction)
                2 -> ReportsTabContent(uiState, viewModel)
            }
        }
    }
}

private fun traducirMotivo(reason: String): String = when (reason) {
    "SPAM" -> "Spam o contenido comercial no deseado"
    "OFFENSIVE_CONTENT" -> "Contenido ofensivo o abusivo"
    "HARASSMENT" -> "Acoso o intimidación"
    "HATE_SPEECH" -> "Incitación al odio o discriminación"
    "SEXUAL_CONTENT" -> "Contenido sexual o explícito"
    "VIOLENCE" -> "Violencia o daño físico"
    "SPOILER" -> "Spoiler sin advertencia previa"
    "FAKE_PROFILE" -> "Perfil falso o suplantación de identidad"
    "INAPPROPRIATE_IMAGE" -> "Imagen inapropiada"
    else -> "Otro motivo"
}

@Composable
fun SuspendDurationDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    val options = listOf("1_DAY" to "1 día", "3_DAYS" to "3 días", "7_DAYS" to "7 días", "30_DAYS" to "30 días")
    var selectedOption by remember { mutableStateOf(options[0].first) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Suspender Usuario") },
        text = {
            Column {
                Text("Selecciona la duración del castigo:")
                Spacer(modifier = Modifier.height(8.dp))
                options.forEach { (backendCode, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedOption = backendCode }.fillMaxWidth()) {
                        RadioButton(selected = selectedOption == backendCode, onClick = { selectedOption = backendCode })
                        Text(label, color = Color.White)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(selectedOption) }) { Text("Suspender", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) } }
    )
}

@Composable
private fun UsersSearchTabContent(
    state: AdminDashboardState,
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onLoadUserDetails: (String) -> Unit,
    onUserAction: (String, String, String?) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar usuario (@username)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (state.isSearching) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (state.searchResults.isEmpty() && searchQuery.isNotEmpty()) {
            Text("No se encontraron usuarios.", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.searchResults, key = { it.id ?: it.hashCode() }) { user ->
                    UserAdminCard(
                        user = user,
                        userDetails = state.userDetailsMap[user.id],
                        onExpand = { onLoadUserDetails(user.id ?: "") },
                        onAction = onUserAction
                    )
                }
            }
        }
    }
}

@Composable
fun UserAdminCard(
    user: UserDto,
    userDetails: UserDetailData?,
    onExpand: () -> Unit,
    onAction: (String, String, String?) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showSuspendDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            isExpanded = !isExpanded
            if (isExpanded) onExpand()
        },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("@${user.username ?: "usuario"}", fontWeight = FontWeight.Bold, color = Color.White)
            Text(user.email ?: "Sin correo", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (userDetails == null || userDetails.details == null) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp))
                    } else {
                        val info = userDetails.details
                        val status = userDetails.status

                        Text("Nombre: ${info.name ?: "Desconocido"}", color = Color.LightGray)
                        Text("Rol: ${info.role ?: "Desconocido"}", color = Color.LightGray)
                        Text("Miembro desde: ${info.createdAt?.take(10) ?: "N/A"}", color = Color.LightGray)

                        val statusString = status?.accountStatus ?: "Desconocido"
                        val statusColor = when (statusString) {
                            "ACTIVE" -> Color(0xFF4CAF50)
                            "SUSPENDED" -> Color(0xFFFF9800)
                            "BANNED" -> Color(0xFFE50914)
                            else -> Color.Gray
                        }

                        Text("Estado: $statusString", fontWeight = FontWeight.Bold, color = statusColor)

                        if (statusString == "SUSPENDED" && status?.suspendedUntil != null) {
                            Text("Hasta: ${status.suspendedUntil.take(10)}", color = Color(0xFFFF9800), style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (statusString == "BANNED") {
                                Button(onClick = { onAction(user.id ?: "", "UNBAN", null) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("Desbanear") }
                            } else {
                                Button(onClick = { showSuspendDialog = true }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) { Text("Suspender") }
                                Button(onClick = { onAction(user.id ?: "", "BAN", null) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))) { Text("Banear") }
                            }
                        }
                        Button(onClick = { onAction(user.id ?: "", "REMOVE_PHOTO", null) }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) { Text("Quitar Foto de Perfil") }
                    }
                }
            }
        }
    }

    if (showSuspendDialog) {
        SuspendDurationDialog(
            onDismiss = { showSuspendDialog = false },
            onConfirm = { duration ->
                showSuspendDialog = false
                onAction(user.id ?: "", "SUSPEND", duration)
            }
        )
    }
}

@Composable
private fun ReportsTabContent(state: AdminDashboardState, viewModel: AdminDashboardViewModel) {
    val filtros = listOf("PENDING" to "Pendientes", "DISMISSED" to "Descartados", "ACTION_TAKEN" to "Castigados", "ALL" to "Todos")
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            filtros.forEach { (code, label) ->
                FilterChip(
                    selected = (state.currentFilter == code),
                    onClick = { viewModel.loadReports(code) },
                    label = { Text(label) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (state.isReportsLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (state.reportsList.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("No hay reportes en esta categoría.", color = Color.Gray) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(state.reportsList, key = { it.id }) { reporte ->
                    ReportCardItem(reporte = reporte, onActionExecute = { type, note, dur -> viewModel.applyAction(reporte.id.toString(), type, note, dur) })
                }
            }
        }
    }
}

@Composable
fun ReportCardItem(reporte: AdminReportDto, onActionExecute: (String, String, String?) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    var adminNote by remember { mutableStateOf("") }
    var showSuspendDialog by remember { mutableStateOf(false) }
    val tipoTraducido = mapOf("USER" to "Usuario", "REVIEW" to "Reseña", "COMMENT" to "Comentario")[reporte.targetType] ?: reporte.targetType
    val motivoTraducido = traducirMotivo(reporte.reason)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Reporte #${reporte.id} [$tipoTraducido]", fontWeight = FontWeight.Bold, color = Color(0xFFE50914))
                    Text(motivoTraducido, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
                Icon(imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Reportado por:", fontWeight = FontWeight.Bold, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = reporte.reporterAuthId.take(20) + if (reporte.reporterAuthId.length > 20) "…" else "",
                        color = Color(0xFF90CAF9), style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Descripción:", fontWeight = FontWeight.Bold, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    Text(text = if (reporte.description.isNullOrBlank()) "Sin descripción adjunta." else reporte.description, color = Color.LightGray)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Evidencia (Snapshot):", fontWeight = FontWeight.Bold, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    Card(colors = CardDefaults.cardColors(containerColor = Color.Black), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = reporte.targetSnapshot?.get("content")?.toString()
                                ?: reporte.targetSnapshot?.get("username")?.toString()
                                ?: reporte.targetSnapshot?.get("name")?.toString()
                                ?: "Sin evidencia",
                            modifier = Modifier.padding(12.dp), color = Color(0xFF4CAF50), style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (reporte.status == "PENDING") {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = adminNote,
                            onValueChange = { adminNote = it },
                            label = { Text("Nota Administrativa (Opcional)", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF9800))
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { onActionExecute("DISMISS_REPORT", adminNote, null) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text("Descartar") }
                                Button(onClick = { showSuspendDialog = true }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) { Text("Suspender") }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                when (reporte.targetType) {
                                    "REVIEW" -> Button(onClick = { onActionExecute("DELETE_REVIEW", adminNote, null) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))) { Text("Eliminar Reseña") }
                                    "COMMENT" -> Button(onClick = { onActionExecute("DELETE_COMMENT", adminNote, null) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))) { Text("Eliminar Comentario") }
                                    "USER" -> Button(onClick = { onActionExecute("REMOVE_PROFILE_IMAGE", adminNote, null) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037))) { Text("Quitar Foto") }
                                }
                                Button(onClick = { onActionExecute("BAN_USER", adminNote, null) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))) { Text("Banear Usuario") }
                            }
                        }
                    } else if (reporte.adminNote?.isNotBlank() == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Nota de Resolución:", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                Text(reporte.adminNote, color = Color.White, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSuspendDialog) {
        SuspendDurationDialog(
            onDismiss = { showSuspendDialog = false },
            onConfirm = { duration ->
                showSuspendDialog = false
                onActionExecute("SUSPEND_USER", adminNote, duration)
            }
        )
    }
}

@Composable
private fun MetricsTabContent(state: AdminDashboardState) {
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { SectionTitle("Métricas de la Plataforma") }
            item {
                state.authStats?.let { stats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Usuarios Totales", "${stats.totalUsers ?: 0}", Color(0xFF4CAF50), Modifier.weight(1f))
                        MetricCard("Usuarios Activos", "${stats.byStatus?.get("ACTIVE") ?: 0}", Color(0xFF4CAF50), Modifier.weight(1f))
                    }
                }
            }
            item {
                state.authStats?.let { stats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Suspendidos", "${stats.byStatus?.get("SUSPENDED") ?: 0}", Color(0xFFFF9800), Modifier.weight(1f))
                        MetricCard("Baneados", "${stats.byStatus?.get("BANNED") ?: 0}", Color(0xFFE50914), Modifier.weight(1f))
                    }
                }
            }
            item { SectionTitle("Interacciones Globales") }
            item {
                state.reviewStats?.let { stats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Reseñas", "${stats.totals?.get("reviews")?.toInt() ?: 0}", Color(0xFF2196F3), Modifier.weight(1f))
                        MetricCard("Comentarios", "${stats.totals?.get("comments")?.toInt() ?: 0}", Color(0xFF2196F3), Modifier.weight(1f))
                    }
                }
            }
            item {
                state.reviewStats?.let { stats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Likes Repartidos", "${stats.totals?.get("likes")?.toInt() ?: 0}", Color(0xFFE91E63), Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            item { SectionTitle("Moderación General") }
            item {
                state.modStats?.let { stats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Reportes Históricos", "${stats.totalReports ?: 0}", Color(0xFF9C27B0), Modifier.weight(1f))
                        MetricCard("Reportes Pendientes", "${stats.pendingReports ?: 0}", Color(0xFFFF9800), Modifier.weight(1f))
                    }
                }
            }
            item {
                state.modStats?.let { stats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Reportes Resueltos", "${stats.resolvedReports ?: 0}", Color(0xFF4CAF50), Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
fun MetricCard(title: String, value: String, borderColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxHeight().width(5.dp).background(borderColor))
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}