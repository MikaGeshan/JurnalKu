package com.example.jurnalku.ui.components.canvas

import CanvasPattern
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

import com.example.jurnalku.ui.journal.list.JournalPagePayload
import com.example.jurnalku.ui.journal.list.DrawPathPayload
import com.example.jurnalku.ui.journal.list.DrawPointPayload
import java.util.UUID

val defaultColor = Color.Black
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CustomCanvas(
    paperColor: Color,
    paperType: String,
    initialPages: List<JournalPagePayload> = emptyList(),
    onClose: () -> Unit,
    onSave: (List<JournalPagePayload>) -> Unit
) {
    val context = LocalContext.current
    
    // State for all pages
    val pages = remember { 
        mutableStateListOf<JournalPagePayload>().apply {
            if (initialPages.isEmpty()) {
                add(JournalPagePayload(
                    contentId = UUID.randomUUID().toString(),
                    paperType = paperType,
                    paperColor = paperColor.value.toLong()
                ))
            } else {
                addAll(initialPages)
            }
        }
    }
    var currentPageIndex by remember { mutableStateOf(0) }

    // State for current page
    var mode by remember { mutableStateOf(CanvasMode.TEXT) }
    var text by remember { mutableStateOf("") }
    var selectedImageBase64 by remember { mutableStateOf<String?>(null) }
    var imageOffset by remember { mutableStateOf(Offset.Zero) }
    var imageScale by remember { mutableStateOf(1f) }
    var imageRotation by remember { mutableStateOf(0f) }
    val paths = remember { mutableStateListOf<DrawPath>() }
    val undonePaths = remember { mutableStateListOf<DrawPath>() }

    // Load page data when index changes
    LaunchedEffect(currentPageIndex) {
        val currentPage = pages[currentPageIndex]
        text = currentPage.text
        selectedImageBase64 = currentPage.imageBase64
        imageOffset = Offset(currentPage.imageOffsetX, currentPage.imageOffsetY)
        imageScale = currentPage.imageScale
        imageRotation = currentPage.imageRotation
        paths.clear()
        paths.addAll(currentPage.paths.map { payload ->
            DrawPath(
                points = payload.points.map { Offset(it.x, it.y) },
                color = Color(payload.color.toULong()),
                strokeWidth = payload.strokeWidth
            )
        })
        undonePaths.clear()
    }

    fun saveCurrentPageState() {
        pages[currentPageIndex] = pages[currentPageIndex].copy(
            text = text,
            imageBase64 = selectedImageBase64,
            imageOffsetX = imageOffset.x,
            imageOffsetY = imageOffset.y,
            imageScale = imageScale,
            imageRotation = imageRotation,
            paths = paths.map { path ->
                DrawPathPayload(
                    points = path.points.map { DrawPointPayload(it.x, it.y) },
                    color = path.color.value.toLong(),
                    strokeWidth = path.strokeWidth
                )
            }
        )
    }

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

    fun handleUndo() {
        if (paths.isNotEmpty()) {
            undonePaths.add(paths.removeLast())
        }
    }

    fun handleRedo() {
        if (undonePaths.isNotEmpty()) {
            paths.add(undonePaths.removeLast())
        }
    }

    fun handlePathAdded(newPath: DrawPath) {
        paths.add(newPath)
        undonePaths.clear() // Clear redo history when a new path is drawn
    }

    fun handleSaveJournal() {
        saveCurrentPageState()
        onSave(pages.toList())
    }

    fun handleCreateNewPage() {
        saveCurrentPageState()
        pages.add(JournalPagePayload(
            contentId = UUID.randomUUID().toString(),
            paperType = paperType,
            paperColor = paperColor.value.toLong()
        ))
        currentPageIndex = pages.size - 1
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
            onUndo = ::handleUndo,
            onRedo = ::handleRedo,
            canUndo = paths.isNotEmpty(),
            canRedo = undonePaths.isNotEmpty(),
            onExportJournal ={},
            onSave = ::handleSaveJournal,
            onPickImage = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onCreateNewPage = ::handleCreateNewPage,
        )

        if (pages.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (currentPageIndex > 0) {
                            saveCurrentPageState()
                            currentPageIndex--
                        }
                    },
                    enabled = currentPageIndex > 0
                ) {
                    Text("<", fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Page ${currentPageIndex + 1} of ${pages.size}",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                IconButton(
                    onClick = {
                        if (currentPageIndex < pages.size - 1) {
                            saveCurrentPageState()
                            currentPageIndex++
                        }
                    },
                    enabled = currentPageIndex < pages.size - 1
                ) {
                    Text(">", fontWeight = FontWeight.Bold)
                }
            }
        }

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
                onPathAdded = ::handlePathAdded,
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
                            detectTapGestures {
                                mode = CanvasMode.TEXT
                            }
                        }
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
