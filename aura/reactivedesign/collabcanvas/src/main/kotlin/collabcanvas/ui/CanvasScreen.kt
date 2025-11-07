package collabcanvas.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import java.lang.Math.PI
import java.lang.Math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

// LocalWindowInfo extension for keyboard and mouse state
data class WindowInfo(
    val keyboardModifiers: KeyboardModifiers? = null,
    val mousePosition: Offset? = null
)

data class KeyboardModifiers(
    val isShiftPressed: Boolean = false
)

@Composable
private fun getWindowInfo(): WindowInfo {
    // In a real app, you would get this from the actual window info
    return WindowInfo()
}

sealed class DrawingOperation {
    data class PathOp(
        val path: Path,
        val color: Color,
        val strokeWidth: Dp,
        val tool: DrawingTool
    ) : DrawingOperation()

    data class ShapeOp(
        val start: Offset,
        val end: Offset,
        val color: Color,
        val strokeWidth: Dp,
        val tool: DrawingTool
    ) : DrawingOperation()

    companion object
}

enum class DrawingTool {
    PEN, ERASER, LINE, RECTANGLE, CIRCLE, HIGHLIGHTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    isCollaborative: Boolean = false,
    collaborationEvents: MutableSharedFlow<DrawingOperation>? = null
) {
    val paths = remember { mutableStateListOf<DrawingOperation>() }
    val undonePaths = remember { mutableStateListOf<DrawingOperation>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var startPosition by remember { mutableStateOf(Offset.Zero) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentStrokeWidth by remember { mutableStateOf(4.dp) }
    var currentTool by remember { mutableStateOf(DrawingTool.PEN) }
    var showColorPicker by remember { mutableStateOf(false) }

    // For collaboration
    LaunchedEffect(Unit) {
        return@LaunchedEffect collaborationEvents?.collectLatest { operation -> paths.add(operation) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Main drawing canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            startPosition = offset
                            when (currentTool) {
                                DrawingTool.PEN, DrawingTool.ERASER, DrawingTool.HIGHLIGHTER -> {
                                    currentPath = Path().apply {
                                        moveTo(offset.x, offset.y)
                                    }
                                }
                                else -> { /* Shape tools handle this in onDrag */ }
                            }
                        },
                        onDrag = { change, _ ->
                            when (currentTool) {
                                DrawingTool.PEN, DrawingTool.ERASER, DrawingTool.HIGHLIGHTER -> {
                                    currentPath?.lineTo(change.position.x, change.position.y)
                                }
                                else -> { /* Update preview */ }
                            }
                        },
                        onDragEnd = {
                            val endPosition = it
                            when (currentTool) {
                                DrawingTool.PEN, DrawingTool.ERASER, DrawingTool.HIGHLIGHTER -> {
                                    currentPath?.let { path ->
                                        val op: Unit = DrawingOperation.PathOp(
                                            path = android.graphics.Path(path),
                                            color = if (currentTool == DrawingTool.ERASER)
                                                colorScheme.background else currentColor,
                                            strokeWidth = if (currentTool == DrawingTool.HIGHLIGHTER)
                                                16.dp else currentStrokeWidth,
                                            tool = currentTool
                                        )
                                        paths.add(op)
                                        collaborationEvents?.tryEmit(op)
                                    }
                                }
                                else -> {
                                    val op = DrawingOperation.ShapeOp(
                                        start = startPosition,
                                        end = endPosition,
                                        color = currentColor,
                                        strokeWidth = currentStrokeWidth,
                                        tool = currentTool
                                    )
                                    paths.add(op)
                                    collaborationEvents?.tryEmit(op)
                                }
                            }
                            currentPath = null
                            undonePaths.clear()
                        }
                    )
                }
        ) {
            // Draw all saved paths
            paths.forEach { operation ->
                when (operation) {
                    is DrawingOperation.PathOp -> {
                        drawPath(
                            path = operation.path,
                            color = operation.color,
                            style = Stroke(
                                width = operation.strokeWidth.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                    is DrawingOperation.ShapeOp -> {
                        when (operation.tool) {
                            DrawingTool.LINE -> {
                                drawLine(
                                    color = operation.color,
                                    start = operation.start,
                                    end = operation.end,
                                    strokeWidth = operation.strokeWidth.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                            DrawingTool.RECTANGLE -> {
                                drawRect(
                                    color = operation.color,
                                    topLeft = operation.start,
                                    size = (operation.end - operation.start).toSize(),
                                    style = Stroke(width = operation.strokeWidth.toPx())
                                )
                            }
                            DrawingTool.CIRCLE -> {
                                val radius = (operation.end - operation.start).getDistance()
                                drawCircle(
                                    color = operation.color,
                                    radius = radius,
                                    center = operation.start,
                                    style = Stroke(width = operation.strokeWidth.toPx())
                                )
                            }
                            else -> { /* Other tools */ }
                        }
                    }
                }
            }

            // Draw current path (preview)
            currentPath?.let { path ->
                drawPath(
                    path, if (currentTool == DrawingTool.ERASER)
                        colorScheme.background else currentColor, style = Stroke(
                        width = if (currentTool == DrawingTool.HIGHLIGHTER)
                            16.dp.toPx() else currentStrokeWidth.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Draw shape preview
            if (currentPath == null && currentTool in listOf(
                    DrawingTool.LINE,
                    DrawingTool.RECTANGLE,
                    DrawingTool.CIRCLE
                ) && startPosition != Offset.Zero
            ) {
                val windowInfo = getWindowInfo()
                val mousePos = windowInfo.mousePosition ?: startPosition
                val endPosition = windowInfo.keyboardModifiers?.takeIf { it.isShiftPressed }?.let {
                    // Snap to 45-degree angles when shift is held
                    val delta = mousePos.minus(startPosition)
                    val angle = kotlin.math.atan2(delta.y.toDouble(), delta.x.toDouble())
                    val snappedAngle = (angle / (PI / 4)).roundToInt() * (PI / 4)
                    val distance = delta.getDistance()
                    startPosition + Offset(
                        (distance * cos(snappedAngle)).toFloat(),
                        (distance * sin(snappedAngle)).toFloat()
                    )
                } ?: mousePos

                when (currentTool) {
                    DrawingTool.LINE -> {
                        drawLine(
                            color = currentColor,
                            start = startPosition,
                            end = endPosition,
                            strokeWidth = currentStrokeWidth.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    DrawingTool.RECTANGLE -> {
                        drawRect(
                            color = currentColor,
                            topLeft = startPosition,
                            size = (endPosition - startPosition).toSize(),
                            style = Stroke(width = currentStrokeWidth.toPx())
                        )
                    }
                    DrawingTool.CIRCLE -> {
                        val radius = (endPosition - startPosition).getDistance()
                        drawCircle(
                            color = currentColor,
                            radius = radius,
                            center = startPosition,
                            style = Stroke(width = currentStrokeWidth.toPx())
                        )
                    }
                    else -> { /* Other tools */ }
                }
            }
        }

        // Top app bar
        TopAppBar(
            title = { Text("Collaborative Canvas") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            actions = {
                // Undo/Redo buttons
                IconButton(
                    onClick = {
                        if (paths.isNotEmpty()) {
                            undonePaths.add(paths.removeAt(paths.size - 1))
                        }
                    },
                    enabled = paths.isNotEmpty()
                ) {
                    Icon(Icons.Default.Undo, "Undo")
                }
                IconButton(
                    onClick = {
                        if (undonePaths.isNotEmpty()) {
                            paths.add(undonePaths.removeAt(undonePaths.size - 1))
                        }
                    },
                    enabled = undonePaths.isNotEmpty()
                ) {
                    Icon(Icons.Default.Redo, "Redo")
                }

                // Clear canvas
                IconButton(
                    onClick = { paths.clear() },
                    enabled = paths.isNotEmpty()
                ) {
                    Icon(Icons.Default.Clear, "Clear")
                }

                // Color picker toggle
                IconButton(onClick = { showColorPicker = !showColorPicker }) {
                    Icon(Icons.Default.Palette, "Colors")
                }
            }
        )

        // Tool selection bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(colorScheme.surfaceVariant.copy(alpha = 0.9f))
                .padding(8.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(DrawingTool.values()) { tool ->
                    val isSelected = currentTool == tool
                    val tint = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant

                    IconButton(
                        onClick = { currentTool = tool },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isSelected) colorScheme.primaryContainer
                                else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        val icon = when (tool) {
                            DrawingTool.PEN -> Icons.Default.Brush
                            DrawingTool.ERASER -> Icons.Default.Highlight
                            DrawingTool.LINE -> Icons.Default.Straighten
                            DrawingTool.RECTANGLE -> Icons.Default.Rectangle
                            DrawingTool.CIRCLE -> Icons.Default.Circle
                            DrawingTool.HIGHLIGHTER -> Icons.Default.Highlight
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = tool.name,
                            tint = if (tool == DrawingTool.ERASER) colorScheme.error else tint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Stroke width slider
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Slider(
                    value = currentStrokeWidth.value,
                    onValueChange = { currentStrokeWidth = it.dp },
                    valueRange = 1f..32f,
                    modifier = Modifier
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = currentColor,
                        activeTrackColor = currentColor.copy(alpha = 0.5f),
                        inactiveTrackColor = currentColor.copy(alpha = 0.2f)
                    )
                )
            }
        }

        // Color picker
        if (showColorPicker) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 56.dp, end = 8.dp)
                    .background(
                        colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    )
                    .padding(8.dp)
            ) {
                val colors = listOf(
                    Color.Black, Color.DarkGray, Color.Gray, Color.LightGray,
                    Color.White, Color.Red, Color.Green, Color.Blue,
                    Color.Yellow, Color.Cyan, Color.Magenta, Color(0xFFFFA500) // Orange
                )

                Column {
                    Text(
                        "Select Color",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(4.dp)
                    )

                    LazyRow {
                        items(colors) { color ->
                            val isSelected = color == currentColor
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(32.dp)
                                    .background(
                                        color,
                                        CircleShape
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) colorScheme.primary
                                        else colorScheme.outline,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        currentColor = color
                                        showColorPicker = false
                                    }
                            )
                        }
                    }
                }
            }
        }

        // Collaboration indicator
        if (isCollaborative) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 64.dp, start = 8.dp)
                    .background(
                        colorScheme.primaryContainer,
                        MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "Collaborative Mode",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

private fun Unit.Path(src: Path): android.graphics.Path {
    TODO("Not yet implemented")
}

private fun DrawingOperation.Companion.PathOp(
    path: android.graphics.Path,
    color: Color,
    strokeWidth: Dp,
    tool: DrawingTool
) {
    TODO("Not yet implemented")
}

private fun SliderDefaults.colors(
    thumbColor: Any,
    activeTrackColor: Path,
    inactiveTrackColor: Path
): SliderColors {
    TODO("Not yet implemented")
}

private fun DrawScope.drawLine(
    color: Any,
    start: Any,
    end: Any,
    strokeWidth: Any,
    cap: StrokeCap
) {
    TODO("Not yet implemented")
}

// Extension functions
private fun Offset.toSize() = Size(x, y)
private fun Size.toOffset() = Offset(width, height)
private fun Offset.plus(other: Offset) = Offset(x + other.x, y + other.y)
private fun Offset.getDistance() = sqrt(x * x + y * y)
