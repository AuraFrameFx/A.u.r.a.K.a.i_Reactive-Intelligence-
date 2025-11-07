package dev.aurakai.auraframefx.ui.overlays

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.iconify.IconifyService
import dev.aurakai.auraframefx.iconify.IconPicker
import kotlinx.coroutines.delay
import kotlin.math.abs

/**
 * 🎮 3D Icon Overlay Editor
 *
 * Full editing interface for 3D icon overlays with gyroscope control.
 * Features:
 * - Live 3D preview with gyroscope
 * - Drag to reposition icons
 * - Edit icon properties
 * - Layout presets (hub, orbit, grid)
 * - Icon selection from Iconify
 * - Export/import layouts
 */

/**
 * Gyroscope sensor state
 */
@Composable
fun rememberGyroscopeState(): GyroscopeState {
    val context = LocalContext.current
    val state = remember { GyroscopeState(context) }

    DisposableEffect(Unit) {
        state.start()
        onDispose {
            state.stop()
        }
    }

    return state
}

class GyroscopeState(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    var x by mutableStateOf(0f)
        private set
    var y by mutableStateOf(0f)
        private set
    var z by mutableStateOf(0f)
        private set

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                // Integrate gyroscope data to get orientation
                x = (x + it.values[0] * 0.1f).coerceIn(-1f, 1f)
                y = (y + it.values[1] * 0.1f).coerceIn(-1f, 1f)
                z = (z + it.values[2] * 0.1f).coerceIn(-1f, 1f)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun start() {
        gyroscope?.let {
            sensorManager.registerListener(
                listener,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stop() {
        sensorManager.unregisterListener(listener)
    }
}

/**
 * Main Icon Overlay Editor Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconOverlayEditor(
    iconifyService: IconifyService,
    initialOverlays: List<IconOverlay3D> = IconOverlayPresets.mainMenuHub(),
    onSave: (List<IconOverlay3D>) -> Unit = {},
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var overlays by remember { mutableStateOf(initialOverlays) }
    var selectedOverlay by remember { mutableStateOf<IconOverlay3D?>(null) }
    var showIconPicker by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var showPresets by remember { mutableStateOf(false) }

    val gyroscope = rememberGyroscopeState()

    val context = LocalContext.current
    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxSize()) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0A))
        )

        // 3D Preview Scene
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Holographic platform
            HolographicPlatform(
                gyroscopeX = gyroscope.x,
                gyroscopeY = gyroscope.y
            )

            // Overlay cards
            with(density) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val screenWidth = maxWidth.toPx()
                    val screenHeight = maxHeight.toPx()

                    overlays.forEach { overlay ->
                        IconOverlay3DCard(
                            overlay = overlay.copy(isEditing = editMode && selectedOverlay?.id == overlay.id),
                            gyroscopeX = gyroscope.x,
                            gyroscopeY = gyroscope.y,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight,
                            onClick = {
                                if (editMode) {
                                    selectedOverlay = overlay
                                } else {
                                    // Navigate to destination
                                    overlay.destination?.let { /* Navigate */ }
                                }
                            },
                            onDrag = { dragAmount ->
                                if (editMode) {
                                    overlays = overlays.map {
                                        if (it.id == overlay.id) {
                                            it.copy(
                                                x = (it.x + dragAmount.x / screenWidth * 2f).coerceIn(-1f, 1f),
                                                y = (it.y + dragAmount.y / screenHeight * 2f).coerceIn(-1f, 1f)
                                            )
                                        } else {
                                            it
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Top toolbar
        IconOverlayToolbar(
            editMode = editMode,
            onEditModeToggle = { editMode = !editMode },
            onAddOverlay = {
                val newOverlay = IconOverlay3D(
                    id = "overlay_${System.currentTimeMillis()}",
                    iconId = "mdi:plus",
                    label = "NEW ITEM",
                    x = 0f,
                    y = 0f,
                    z = 0.2f
                )
                overlays = overlays + newOverlay
                selectedOverlay = newOverlay
            },
            onPresets = { showPresets = true },
            onSave = { onSave(overlays) },
            onClose = onClose
        )

        // Bottom property editor (when editing)
        AnimatedVisibility(
            visible = editMode && selectedOverlay != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it }
            ) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedOverlay?.let { overlay ->
                IconOverlayPropertyEditor(
                    overlay = overlay,
                    onUpdate = { updated ->
                        overlays = overlays.map {
                            if (it.id == overlay.id) updated else it
                        }
                        selectedOverlay = updated
                    },
                    onDelete = {
                        overlays = overlays.filter { it.id != overlay.id }
                        selectedOverlay = null
                    },
                    onSelectIcon = { showIconPicker = true },
                    onClose = { selectedOverlay = null }
                )
            }
        }

        // Gyroscope indicator
        GyroscopeIndicator(
            x = gyroscope.x,
            y = gyroscope.y,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        // Presets dialog
        if (showPresets) {
            Dialog(onDismissRequest = { showPresets = false }) {
                PresetSelector(
                    onPresetSelected = { preset ->
                        overlays = preset
                        showPresets = false
                    },
                    onDismiss = { showPresets = false }
                )
            }
        }

        // Icon picker
        if (showIconPicker) {
            Dialog(onDismissRequest = { showIconPicker = false }) {
                IconPicker(
                    iconifyService = iconifyService,
                    currentIcon = selectedOverlay?.iconId,
                    onIconSelected = { iconId ->
                        selectedOverlay?.let { overlay ->
                            val updated = overlay.copy(iconId = iconId)
                            overlays = overlays.map {
                                if (it.id == overlay.id) updated else it
                            }
                            selectedOverlay = updated
                        }
                        showIconPicker = false
                    },
                    onDismiss = { showIconPicker = false }
                )
            }
        }
    }
}

/**
 * Top toolbar
 */
@Composable
fun IconOverlayToolbar(
    editMode: Boolean,
    onEditModeToggle: () -> Unit,
    onAddOverlay: () -> Unit,
    onPresets: () -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF1A1A1A).copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title
            Text(
                text = "3D Icon Overlay Editor",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CyberpunkPink
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Edit mode toggle
                IconButton(
                    onClick = onEditModeToggle,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (editMode) CyberpunkPink else Color.Transparent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Mode",
                        tint = if (editMode) Color.Black else Color.White
                    )
                }

                // Add overlay
                if (editMode) {
                    IconButton(onClick = onAddOverlay) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Overlay",
                            tint = Color.White
                        )
                    }
                }

                // Presets
                IconButton(onClick = onPresets) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = "Presets",
                        tint = Color.White
                    )
                }

                // Save
                IconButton(onClick = onSave) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        tint = CyberpunkCyan
                    )
                }

                // Close
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Property editor for selected overlay
 */
@Composable
fun IconOverlayPropertyEditor(
    overlay: IconOverlay3D,
    onUpdate: (IconOverlay3D) -> Unit,
    onDelete: () -> Unit,
    onSelectIcon: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        color = Color(0xFF1A1A1A).copy(alpha = 0.98f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 16.dp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Overlay",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberpunkPink
                    )

                    Row {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFFF1744)
                            )
                        }

                        IconButton(onClick = onClose) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Label
            item {
                OutlinedTextField(
                    value = overlay.label,
                    onValueChange = { onUpdate(overlay.copy(label = it)) },
                    label = { Text("Label") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberpunkPink,
                        focusedLabelColor = CyberpunkPink
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Icon selector
            item {
                Button(
                    onClick = onSelectIcon,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A2A2A)
                    )
                ) {
                    Icon(Icons.Default.InsertEmoticon, contentDescription = null, tint = CyberpunkCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Icon: ${overlay.iconId}")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Position sliders
            item {
                Text(
                    text = "Position",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // X position
                Text("X: ${(overlay.x * 100).toInt()}%", fontSize = 12.sp, color = Color.Gray)
                Slider(
                    value = overlay.x,
                    onValueChange = { onUpdate(overlay.copy(x = it)) },
                    valueRange = -1f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberpunkPink,
                        activeTrackColor = CyberpunkPink
                    )
                )

                // Y position
                Text("Y: ${(overlay.y * 100).toInt()}%", fontSize = 12.sp, color = Color.Gray)
                Slider(
                    value = overlay.y,
                    onValueChange = { onUpdate(overlay.copy(y = it)) },
                    valueRange = -1f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberpunkPink,
                        activeTrackColor = CyberpunkPink
                    )
                )

                // Z depth
                Text("Depth: ${(overlay.z * 100).toInt()}%", fontSize = 12.sp, color = Color.Gray)
                Slider(
                    value = overlay.z,
                    onValueChange = { onUpdate(overlay.copy(z = it)) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberpunkCyan,
                        activeTrackColor = CyberpunkCyan
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Rotation sliders
            item {
                Text(
                    text = "Rotation",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rotation Y
                Text("Rotation Y: ${overlay.rotationY.toInt()}°", fontSize = 12.sp, color = Color.Gray)
                Slider(
                    value = overlay.rotationY,
                    onValueChange = { onUpdate(overlay.copy(rotationY = it)) },
                    valueRange = -180f..180f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberpunkPink,
                        activeTrackColor = CyberpunkPink
                    )
                )
            }
        }
    }
}

/**
 * Gyroscope indicator
 */
@Composable
fun GyroscopeIndicator(
    x: Float,
    y: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1A1A1A).copy(alpha = 0.8f),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ScreenRotation,
                contentDescription = "Gyroscope",
                tint = CyberpunkCyan,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "X: ${(x * 100).toInt()}%",
                fontSize = 10.sp,
                color = Color.White
            )

            Text(
                text = "Y: ${(y * 100).toInt()}%",
                fontSize = 10.sp,
                color = Color.White
            )
        }
    }
}

/**
 * Preset selector dialog
 */
@Composable
fun PresetSelector(
    onPresetSelected: (List<IconOverlay3D>) -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1A1A1A),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.6f)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Layout Presets",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CyberpunkPink
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Menu Hub
            Button(
                onClick = { onPresetSelected(IconOverlayPresets.mainMenuHub()) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Main Menu Hub (4 cards)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Circular Orbit
            Button(
                onClick = { onPresetSelected(IconOverlayPresets.circularOrbit(count = 6)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Circular Orbit (6 items)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid 2x3
            Button(
                onClick = { onPresetSelected(IconOverlayPresets.grid(rows = 2, cols = 3)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grid 2x3 (6 items)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid 3x3
            Button(
                onClick = { onPresetSelected(IconOverlayPresets.grid(rows = 3, cols = 3)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grid 3x3 (9 items)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}
