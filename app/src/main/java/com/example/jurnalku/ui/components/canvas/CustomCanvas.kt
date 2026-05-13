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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
    initialImageBase64: String? = null,
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
        imageBase64: String?,
        imageOffsetX: Float,
        imageOffsetY: Float,
        imageScale: Float,
        imageRotation: Float
    ) -> Unit
) {
    val context = LocalContext.current
    var mode by remember { mutableStateOf(CanvasMode.TEXT) }
    var text by remember { mutableStateOf(initialText) }
    var selectedImageBase64 by remember { mutableStateOf(initialImageBase64) }

    // image transformation state
    var imageOffset by remember { mutableStateOf(Offset(initialImageOffsetX, initialImageOffsetY)) }
    var imageScale by remember { mutableStateOf(initialImageScale) }
    var imageRotation by remember { mutableStateOf(initialImageRotation) }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            if (uri != null) {
                val base64 = uriToBase64(context, uri)
                if (base64 != null) {
                    selectedImageBase64 = base64
                    mode = CanvasMode.IMAGE
                }
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
            selectedImageBase64,
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
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
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

            // image layer - moved above TextField to catch touches
            selectedImageBase64?.let { base64String ->
                val imageBytes = remember(base64String) {
                    try {
                        Base64.decode(base64String, Base64.DEFAULT)
                    } catch (e: Exception) {
                        null
                    }
                }
                
                if (imageBytes != null) {
                    AsyncImage(
                        model = imageBytes,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                translationX = imageOffset.x,
                                translationY = imageOffset.y,
                                scaleX = imageScale,
                                scaleY = imageScale,
                                rotationZ = imageRotation
                            )
                            .then(
                                if (mode == CanvasMode.TEXT) {
                                    Modifier.pointerInput(Unit) {
                                        detectTapGestures {
                                            mode = CanvasMode.IMAGE
                                        }
                                    }
                                } else Modifier
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
            }

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

private fun uriToBase64(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (originalBitmap == null) return null

        // Calculate scaled dimensions (max 1024px)
        val maxDimension = 1024
        val scale = Math.min(
            maxDimension.toFloat() / originalBitmap.width,
            maxDimension.toFloat() / originalBitmap.height
        ).coerceAtMost(1f)

        val scaledBitmap = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                originalBitmap,
                (originalBitmap.width * scale).toInt(),
                (originalBitmap.height * scale).toInt(),
                true
            )
        } else {
            originalBitmap
        }

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        
        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        Log.e("IMAGE_ERROR", "Failed to convert image to Base64", e)
        null
    }
}
