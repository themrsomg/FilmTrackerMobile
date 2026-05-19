package com.example.santabarbaramobile.ui.auth.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.santabarbaramobile.data.model.models.AdminReportDto
import com.example.santabarbaramobile.ui.auth.ViewModels.MyReportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsScreen(
    viewModel: MyReportsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reportes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                viewModel.isLoading -> CircularProgressIndicator()

                viewModel.error != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error al cargar", color = MaterialTheme.colorScheme.error)
                        Text(text = viewModel.error ?: "")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchMyReports() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }

                viewModel.reports.isEmpty() -> {
                    Text(text = "No has enviado ningún reporte aún.", color = Color.Gray)
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.reports, key = { it.id }) { report ->
                            UserReportCard(report)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserReportCard(report: AdminReportDto) {
    val statusColor = when (report.status) {
        "PENDING" -> Color(0xFFFF9800)
        "ACTION_TAKEN" -> Color(0xFF4CAF50)
        "DISMISSED" -> Color.Gray
        else -> Color.White
    }

    val statusText = when (report.status) {
        "PENDING" -> "En Revisión"
        "ACTION_TAKEN" -> "Resuelto (Sancionado)"
        "DISMISSED" -> "Descartado"
        else -> report.status
    }

    val tipoTraducido = mapOf("USER" to "Usuario", "REVIEW" to "Reseña", "COMMENT" to "Comentario")[report.targetType] ?: report.targetType

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Reporte de $tipoTraducido",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = statusText,
                    color = statusColor,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Motivo: ${report.reason}", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)

            if (report.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Tu nota: ${report.description}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}