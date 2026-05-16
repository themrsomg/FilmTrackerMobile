package com.example.santabarbaramobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDialog(
    targetType: String,
    targetId: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val reasons = mapOf(
        "SPAM" to "Spam o contenido comercial",
        "OFFENSIVE_CONTENT" to "Contenido ofensivo o abusivo",
        "HARASSMENT" to "Acoso o intimidación",
        "HATE_SPEECH" to "Incitación al odio",
        "SEXUAL_CONTENT" to "Contenido sexual",
        "VIOLENCE" to "Violencia",
        "SPOILER" to "Spoiler sin advertencia",
        "FAKE_PROFILE" to "Perfil falso",
        "INAPPROPRIATE_IMAGE" to "Imagen inapropiada",
        "OTHER" to "Otro motivo"
    )
    var selectedReasonCode by remember { mutableStateOf(reasons.keys.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Emitir Reporte") },
        text = {
            Column {
                Text("Selecciona el motivo:")
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = reasons[selectedReasonCode] ?: "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        reasons.forEach { (code, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedReasonCode = code
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(targetType, targetId, selectedReasonCode, description) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Enviar Reporte", color = androidx.compose.ui.graphics.Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}