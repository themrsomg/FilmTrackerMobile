package com.example.santabarbaramobile.ui.auth.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.AdminDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                item { SectionTitle("Métricas de la Plataforma") }
                item {
                    viewModel.authStats?.let { stats ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricCard("Usuarios Registrados", "${stats.totalUsers ?: 0}", Color(0xFF4CAF50), Modifier.weight(1f))
                            MetricCard("Usuarios Activos", "${stats.byStatus?.get("ACTIVE") ?: 0}", Color(0xFF4CAF50), Modifier.weight(1f))
                        }
                    }
                }
                item {
                    viewModel.authStats?.let { stats ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricCard("Cuentas Suspendidas", "${stats.byStatus?.get("SUSPENDED") ?: 0}", Color(0xFFFF9800), Modifier.weight(1f))
                            MetricCard("Cuentas Baneadas", "${stats.byStatus?.get("BANNED") ?: 0}", Color(0xFFE50914), Modifier.weight(1f))
                        }
                    }
                }

                item { SectionTitle("Interacciones Globales") }
                item {
                    viewModel.reviewStats?.let { stats ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricCard("Reseñas Globales", "${stats.totals?.get("reviews")?.toInt() ?: 0}", Color(0xFF2196F3), Modifier.weight(1f))
                            MetricCard("Comentarios", "${stats.totals?.get("comments")?.toInt() ?: 0}", Color(0xFF2196F3), Modifier.weight(1f))
                        }
                    }
                }
                item {
                    viewModel.reviewStats?.let { stats ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricCard("Likes Repartidos", "${stats.totals?.get("likes")?.toInt() ?: 0}", Color(0xFFE91E63), Modifier.weight(1f))
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                item { SectionTitle("Moderación y Reportes") }
                item {
                    viewModel.modStats?.let { stats ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricCard("Reportes Históricos", "${stats.totalReports ?: 0}", Color(0xFF9C27B0), Modifier.weight(1f))
                            MetricCard("Reportes Pendientes", "${stats.pendingReports ?: 0}", Color(0xFFFF9800), Modifier.weight(1f))
                        }
                    }
                }
                item {
                    viewModel.modStats?.let { stats ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricCard("Reportes Resueltos", "${stats.resolvedReports ?: 0}", Color(0xFF4CAF50), Modifier.weight(1f))
                            Spacer(modifier = Modifier.weight(1f))
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
        color = Color.Black
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
                    fontWeight = FontWeight.Bold
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