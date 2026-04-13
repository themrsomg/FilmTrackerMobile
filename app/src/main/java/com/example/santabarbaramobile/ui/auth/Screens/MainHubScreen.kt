package com.example.santabarbaramobile.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainHubScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Inicio", "Buscar", "Favoritos", "Listas", "Reportes", "Perfil")
    val icons = listOf(Icons.Default.Home, Icons.Default.Search, Icons.Default.Favorite,
        Icons.Default.List, Icons.Default.Info, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        // Contenido Principal
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { SectionHeader("Series Populares") }
            item { MediaCarousel(listOf("Breaking Bad", "Stranger Things", "Succession")) }

            item { SectionHeader("Recomendaciones") }
            item { MediaCarousel(listOf("The Bear", "Dark", "The Office")) }

            item { SectionHeader("Últimos vistos") }
            item { MediaCarousel(listOf("Better Call Saul", "Mindhunter")) }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun MediaCarousel(items: List<String>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { movie ->
            MediaCard(movie)
        }
    }
}

@Composable
fun MediaCard(title: String) {
    Card(
        modifier = Modifier
            .size(width = 140.dp, height = 200.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
        }
    }
}