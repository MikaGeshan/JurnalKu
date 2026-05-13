package com.example.jurnalku.ui.components.canvas

import CanvasPattern
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.util.Log
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

val defaultColor = Color.Black
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CustomCanvas(
    paperColor: Color,
    paperType: String,
    initialText: String = "",
    initialPaths: List<DrawPath> = emptyList(),
    initialImageUri: String? = null,
    initialImageOffsetX: Float = 0f,
    initialImageOffsetY: Float = 0f,
    initialImageScale: Float = 1f,
    initialImageRotation: Float = 0f,
    onClose: () -> Unit,
    onSave: (
        text: String,
        paths: List<DrawPath>,
        paperType: String,
        paperColor: Color,
        imageUri: String?,
        imageOffsetX: Float,
        imageOffsetY: Float,
        imageScale: Float,
        imageRotation: Float
    ) -> Unit
) {
    var mode by remember { mutableStateOf(CanvasMode.TEXT) }
    var text by remember { mutableStateOf(initialText) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(initialImageUri?.let { Uri.parse(it) }) }

    // image transformation state
    var imageOffset by remember { mutableStateOf(Offset(initialImageOffsetX, initialImageOffsetY)) }
    var imageScale by remember { mutableStateOf(initialImageScale) }
    var imageRotation by remember { mutableStateOf(initialImageRotation) }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            if (uri != null) {
                selectedImageUri = uri
                mode = CanvasMode.IMAGE
            }
        }
    )

    var selectedTool by remember { mutableStateOf(DrawTool.PEN) }
    var selectedColor by remember { mutableStateOf(defaultColor) }

    val paths = remember { mutableStateListOf<DrawPath>().apply { addAll(initialPaths) } }
    val undonePaths = remember { mutableStateListOf<DrawPath>() }

    fun handleSaveJournal() {
        onSave(
            text,
            paths.toList(),
            paperType,
            paperColor,
            selectedImageUri?.toString(),
            imageOffset.x,
            imageOffset.y,
            imageScale,
            imageRotation
        )
    }

    val strokeWidth = when (selectedTool) {
        DrawTool.PEN -> 6f
        DrawTool.PENCIL -> 3f
        DrawTool.HIGHLIGHTER -> 12f
        DrawTool.ERASER -> 20f
    }
    val drawColor = if (selectedTool == DrawTool.ERASER) paperColor else selectedColor
    Column(modifier = Modifier.fillMaxSize() ) {

        // header
        CanvasHeader(
            onClose = onClose,
            onToggleDraw = {
                mode = if (mode == CanvasMode.DRAW) CanvasMode.TEXT else CanvasMode.DRAW
            },
            onUndo = {
                if (paths.isNotEmpty()) {
                    undonePaths.add(paths.removeLast())
                }
            },
            onRedo = {
                if (undonePaths.isNotEmpty()) {
                    paths.add(undonePaths.removeLast())
                }
            },
            onSave = ::handleSaveJournal,
            onPickImage = {
                if (selectedImageUri == null) {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    mode = if (mode == CanvasMode.IMAGE) CanvasMode.TEXT else CanvasMode.IMAGE
                }
            }
        )

        // default content
        Box(
            modifier = Modifier
                .weight(1f)
                .background(paperColor)
                .clipToBounds()
                .onGloballyPositioned { coordinates ->
                    canvasSize = coordinates.size
                }
        ) {

            CanvasPattern(
                type = paperType
            )

            // image layer
            selectedImageUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            translationX = imageOffset.x,
                            translationY = imageOffset.y,
                            scaleX = imageScale,
                            scaleY = imageScale,
                            rotationZ = imageRotation
                        ),
                    contentScale = ContentScale.Fit
                )
            }

            // text layer
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                placeholder = { Text("Start writing...") },
                enabled = mode == CanvasMode.TEXT,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // draw layer selalu tampil
            CanvasDrawMode(
                paths = paths,
                enabled = mode == CanvasMode.DRAW,
                color = drawColor,
                strokeWidth = strokeWidth,
            )

            // Image transformation overlay
            if (mode == CanvasMode.IMAGE) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, rotation ->
                                // Constrain Scale
                                imageScale = (imageScale * zoom).coerceIn(0.2f, 8f)
                                
                                // Apply Rotation
                                imageRotation += rotation
                                
                                // Apply Pan with boundaries
                                val newOffset = imageOffset + pan
                                
                                // Allow moving the image center up to its own scaled size away from canvas center
                                // This keeps at least a part of the image visible
                                val boundX = canvasSize.width.toFloat()
                                val boundY = canvasSize.height.toFloat()
                                
                                imageOffset = Offset(
                                    x = newOffset.x.coerceIn(-boundX, boundX),
                                    y = newOffset.y.coerceIn(-boundY, boundY)
                                )
                            }
                        }
                )
            }
        }

        // toolbar draw
        if (mode == CanvasMode.DRAW) {
            DrawToolbar(
                selectedTool = selectedTool,
                selectedColor = selectedColor,
                onToolSelected = { selectedTool = it },
                onColorSelected = {
                    selectedColor = it
                    Log.d("COLOR_DEBUG", "selectedColor = $it")
                }
            )
        }
    }
}