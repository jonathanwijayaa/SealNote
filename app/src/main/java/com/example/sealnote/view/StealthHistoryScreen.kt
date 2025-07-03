package com.example.sealnote.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.offset
import androidx.lifecycle.viewmodel.compose.viewModel // Import untuk ViewModel
import androidx.navigation.NavHostController // Import NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch // Untuk CoroutineScope
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sealnote.ui.theme.SealnoteTheme
import com.example.sealnote.viewmodel.CalculatorHistoryViewModel // Import History ViewModel

// Definisi Warna dari XML
val HistoryScreenBackground = Color(0xFF152332)
val HistoryItemExpressionColor = Color(0xFF8090A6)
val HistoryItemResultColor = Color.White
val HistoryItemDividerColor = Color(0xFF2A2F3A)

// Data class untuk setiap entri riwayat (pastikan ini di tingkat atas file atau di folder model)
data class CalculationHistoryEntry(
    val id: String, // Untuk key jika menggunakan LazyColumn
    val expression: String,
    val result: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StealthHistoryScreen(
    navController: NavHostController,
    historyViewModel: CalculatorHistoryViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState() // Dapatkan rute saat ini
    val currentRoute = currentBackStackEntry?.destination?.route

    // Daftar menu navigasi untuk mode kalkulator (konsisten dengan kalkulator lainnya)
    val calculatorMenuItems = listOf(
        "stealthCalculator" to ("Kalkulator Standar" to Icons.Default.Calculate),
        "stealthScientific" to ("Kalkulator Ilmiah" to Icons.Filled.Functions),
        "stealthHistory" to ("Riwayat Kalkulasi" to Icons.Filled.History)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Mode Kalkulator",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                calculatorMenuItems.forEach { (route, details) ->
                    val (label, icon) = details
                    NavigationDrawerItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, style = MaterialTheme.typography.bodyLarge) },
                        selected = currentRoute == route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != route) {
                                // Navigasi khusus untuk riwayat agar popUpTo tidak mempengaruhi menu utama
                                navController.navigate(route) {
                                    popUpTo("stealthHistory") { inclusive = true } // Pop up ke history itu sendiri
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Divider setelah item standar
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.DeleteSweep, "Bersihkan Riwayat") }, // Ikon untuk bersihkan
                    label = { Text("Bersihkan Riwayat", style = MaterialTheme.typography.bodyLarge) },
                    selected = false,
                    onClick = {
                        historyViewModel.clearHistory()
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Riwayat Kalkulasi",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onSurface // Warna ikon dari tema
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Warna container TopAppBar dari tema
                        titleContentColor = MaterialTheme.colorScheme.onSurface // Warna teks judul dari tema
                    )
                )
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // Penting untuk apply padding dari Scaffold
                color = MaterialTheme.colorScheme.background
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 0.dp) // Sesuaikan offset setelah TopAppBar
                            .fillMaxSize() // Gunakan fillMaxSize dan biarkan scroll mengisi sisanya
                            .background(MaterialTheme.colorScheme.background)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (historyViewModel.historyEntries.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tidak ada riwayat kalkulasi.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            historyViewModel.historyEntries.forEach { entry ->
                                HistoryItemView(entry = entry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemView(entry: CalculationHistoryEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 15.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp - 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = entry.expression,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End
                )
                Text(
                    text = entry.result,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, widthDp = 416, heightDp = 891)
@Composable
fun CalculationHistoryScreenPreview() {
    val sampleHistory = listOf(
        CalculationHistoryEntry("id1", "25.000 - 5.000", "5.000"),
        CalculationHistoryEntry("id2", "50.000 - 15.000", "15.000"),
        CalculationHistoryEntry("id3", "100.000 - 32.500", "32.500"),
        CalculationHistoryEntry("id4", "75.000 - 10.000", "10.000"),
        CalculationHistoryEntry("id5", "150.000 - 45.000", "45.000"),
        CalculationHistoryEntry("id6", "30.000 - 7.500", "7.500"),
    )
    MaterialTheme {
        // Untuk preview, inisialisasi ViewModel dengan data dummy jika diperlukan
        val dummyHistoryViewModel: CalculatorHistoryViewModel = viewModel()
        dummyHistoryViewModel.historyEntries.addAll(sampleHistory) // Tambahkan data dummy
        StealthHistoryScreen(navController = rememberNavController(), historyViewModel = dummyHistoryViewModel)
    }
}