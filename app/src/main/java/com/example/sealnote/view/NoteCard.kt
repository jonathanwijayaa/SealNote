package com.example.sealnote.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sealnote.model.Notes
import com.example.sealnote.ui.theme.CardBackgroundColor
import com.example.sealnote.ui.theme.PrimaryTextColor
import com.example.sealnote.ui.theme.SecondaryTextColor
import com.example.sealnote.ui.theme.TertiaryTextColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteCard(
    note: Notes,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleSecretClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryTextColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.content,
                    fontSize = 13.sp,
                    color = SecondaryTextColor,
                    lineHeight = 18.sp,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = note.updatedAt?.formatToString() ?: "",
                    fontSize = 11.sp,
                    color = TertiaryTextColor
                )
            }
            Box {
                IconButton(onClick = { expanded = true }) { /* ... */ }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Edit") }, onClick = { /* ... */ })

                    // OPSI MENU BARU
                    DropdownMenuItem(
                        text = {
                            // Teks dinamis tergantung status saat ini
                            val text = if (note.isSecret) "Keluarkan dari Rahasia" else "Jadikan Rahasia"
                            Text(text)
                        },
                        onClick = {
                            onToggleSecretClick()
                            expanded = false
                        }
                    )

                    DropdownMenuItem(text = { Text("Hapus") }, onClick = { /* ... */ })
                }
            }
        }
    }
}

private fun Date.formatToString(): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(this)
}