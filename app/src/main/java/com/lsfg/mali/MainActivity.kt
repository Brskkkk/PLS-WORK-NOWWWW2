@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.lsfg.mali

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop

class MainActivity : ComponentActivity() {
    init {
        System.loadLibrary("lsfg-mali")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }

    external fun getVersionString(): String
    external fun initBlender(): Int
    external fun blendFrame(): Int
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedApp by remember { mutableStateOf<String?>(null) }
    var isRunning by remember { mutableStateOf(false) }
    var multiplier by remember { mutableStateOf(1.5f) }
    var flowScale by remember { mutableStateOf(1.0f) }

    when (currentScreen) {
        "home" -> HomeScreen(
            onSelectApp = { currentScreen = "picker" },
            onSettings = { currentScreen = "settings" },
            selectedApp = selectedApp,
            isRunning = isRunning,
            onStart = { isRunning = true },
            onStop = { isRunning = false }
        )
        "picker" -> AppPickerScreen(
            onAppSelected = {
                selectedApp = it
                currentScreen = "home"
            },
            onBack = { currentScreen = "home" }
        )
        "settings" -> SettingsScreen(
            multiplier = multiplier,
            onMultiplierChange = { multiplier = it },
            flowScale = flowScale,
            onFlowScaleChange = { flowScale = it },
            onBack = { currentScreen = "home" }
        )
    }
}

@Composable
fun HomeScreen(
    onSelectApp: () -> Unit,
    onSettings: () -> Unit,
    selectedApp: String?,
    isRunning: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Mali G57 Frame Blender") },
            actions = {
                IconButton(onClick = onSettings) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Status Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Status", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    if (isRunning) "🟢 Running" else "🔴 Stopped",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "App: ${selectedApp ?: "None selected"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Selection
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectApp() }
                    .padding(16.dp)
            ) {
                Text("Selected App", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    selectedApp ?: "Tap to select an app",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Control Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onStart,
                enabled = !isRunning && selectedApp != null,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start")
            }

            Button(
                onClick = onStop,
                enabled = isRunning,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stop")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info Section
        Text("Information", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Mali G57 GPU Frame Blender\n" +
            "Version: 1.0\n" +
            "Interpolation: 50/50 motion-aware blending\n" +
            "No Vulkan extensions needed",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun AppPickerScreen(
    onAppSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val mockApps = listOf(
        "Game 1",
        "Game 2",
        "Video Player",
        "Browser",
        "Gallery"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Select App") })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mockApps.size) { index ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAppSelected(mockApps[index]) }
                ) {
                    Text(
                        mockApps[index],
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    multiplier: Float,
    onMultiplierChange: (Float) -> Unit,
    flowScale: Float,
    onFlowScaleChange: (Float) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(title = { Text("Settings") })

        Spacer(modifier = Modifier.height(24.dp))

        // Multiplier Slider
        Text("Frame Multiplier", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("${String.format("%.1f", multiplier)}x", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = multiplier,
            onValueChange = onMultiplierChange,
            valueRange = 1f..4f,
            steps = 5,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Generate more intermediate frames", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(24.dp))

        // Flow Scale Slider
        Text("Optical Flow Scale", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("${String.format("%.2f", flowScale)}", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = flowScale,
            onValueChange = onFlowScaleChange,
            valueRange = 0.5f..2.0f,
            steps = 5,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Motion estimation sensitivity", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(24.dp))

        // GPU Info
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("GPU Info", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Device: Mali-G57", style = MaterialTheme.typography.bodySmall)
                Text("Vulkan: Available", style = MaterialTheme.typography.bodySmall)
                Text("Compute: Enabled", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
