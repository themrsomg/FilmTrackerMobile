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
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.santabarbaramobile.feature.profile.domain.AdminReportDto
import com.example.santabarbaramobile.feature.profile.presentation.AdminDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Estadísticas", "Bandeja de Reportes")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Panel de Control Supremo", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = (selectedTab == index),
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold) },
                            icon = {
                                Icon(
                                    imageVector = if (index == 0) Icons.Default.QueryStats else Icons.Default.Gavel,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTab) {
                0 -> MetricsTabContent(viewModel)
                1 -> ReportsTabContent(viewModel)
            }
        }
    }
}

@Composable
private fun MetricsTabContent(viewModel: AdminDashboardViewModel) {
    if (viewModel.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { SectionTitle("Métricas de la Plataforma") }
            item {
                viewModel.authStats?.let { stats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Usuarios Totales", "${stats.totalUsers ?: 0}", Color(0xFF4CAF50), Modifier.weight(1f))
                        MetricCard("Usuarios Activos", "${stats.byStatus?.get("ACTIVE") ?: 0}", Color(0xFF4CAF50), Modifier.weight(1f))
                    }
                }
            }
            item {
                viewModel.authStats?.let { stats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Suspendidos", "${stats.byStatus?.get("SUSPENDED") ?: 0}", Color(0xFFFF9800), Modifier.weight(1f))
                        MetricCard("Baneados", "${stats.byStatus?.get("BANNED") ?: 0}", Color(0xFFE50914), Modifier.weight(1f))
                    }
                }
            }
            item { SectionTitle("Interacciones Globales") }
            item {
                viewModel.reviewStats?.let { stats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Reseñas", "${stats.totals?.get("reviews")?.toInt() ?: 0}", Color(0xFF2196F3), Modifier.weight(1f))
                        MetricCard("Comentarios", "${stats.totals?.get("comments")?.toInt() ?: 0}", Color(0xFF2196F3), Modifier.weight(1f))
                    }
                }
            }
            item { SectionTitle("Moderación General") }
            item {
                viewModel.modStats?.let { stats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Reportes Pendientes", "${stats.pendingReports ?: 0}", Color(0xFFFF9800), Modifier.weight(1f))
                        MetricCard("Reportes Resueltos", "${stats.resolvedReports ?: 0}", Color(0xFF4CAF50), Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportsTabContent(viewModel: AdminDashboardViewModel) {
    val filtros = listOf("PENDING" to "Pendientes", "DISMISSED" to "Descartados", "ACTION_TAKEN" to "Castigados")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            filtros.forEach { (code, label) ->
                FilterChip(
                    selected = (viewModel.currentFilter == code),
                    onClick = { viewModel.changeFilter(code) },
                    label = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.isReportsLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (viewModel.reportsList.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("No hay reportes en esta categoría.", color = Color.Gray) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(viewModel.reportsList, key = { it.id }) { reporte ->
                    ReportCardItem(reporte = reporte, onActionExecute = { type, note, dur ->
                        viewModel.applyAction(reporte.id.toString(), type, note, dur)
                    })
                }
            }
        }
    }
}

@Composable
fun ReportCardItem(reporte: AdminReportDto, onActionExecute: (String, String, String?) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val tipoTraducido = mapOf("USER" to "Usuario", "REVIEW" to "Reseña", "COMMENT" to "Comentario")[reporte.targetType] ?: reporte.targetType

    Card(
        modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Reporte #${reporte.id} [$tipoTraducido]", fontWeight = FontWeight.Bold, color = Color(0xFFE50914))
                    Text("Motivo: ${reporte.reason}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
                Icon(imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Queja de la comunidad:", fontWeight = FontWeight.Bold, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    Text(reporte.description.ifBlank { "Sin descripción adjunta." }, color = Color.LightGray)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Evidencia (Snapshot):", fontWeight = FontWeight.Bold, color = Color.Gray, style = MaterialTheme.typography.labelSmall)

                    Card(colors = CardDefaults.cardColors(containerColor = Color.Black), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = reporte.targetSnapshot?.get("content")?.toString()
                                ?: reporte.targetSnapshot?.get("username")?.toString()
                                ?: "Snapshot sin texto disponible",
                            modifier = Modifier.padding(12.dp),
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (reporte.status == "PENDING") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onActionExecute("DISMISS_REPORT", "Descartado desde dispositivo móvil.", null) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                modifier = Modifier.weight(1f)
                            ) { Text("Descartar") }

                            Button(
                                onClick = { onActionExecute("BAN_USER", "Baneado permanentemente vía revisión.", null) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                                modifier = Modifier.weight(1f)
                            ) { Text("Banear") }
                        }
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