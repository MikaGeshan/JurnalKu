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

val defaultColor = Color.Black
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun CustomCanvas(
    paperColor: Color,
    paperType: String,
    initialText: String = "",
    initialPaths: List<DrawPath> = emptyList(),
    onClose: () -> Unit,
    onSave: (
        text: String,
        paths: List<DrawPath>,
        paperType: String,
        paperColor: Color
    ) -> Unit
) {
    var mode by remember { mutableStateOf(CanvasMode.TEXT) }
    var text by remember { mutableStateOf(initialText) }

    var selectedTool by remember { mutableStateOf(DrawTool.PEN) }
    var selectedColor by remember { mutableStateOf(defaultColor) }

    val paths = remember { mutableStateListOf<DrawPath>().apply { addAll(initialPaths) } }
    val undonePaths = remember { mutableStateListOf<DrawPath>() }

    fun handleSaveJournal() {
        onSave(
            text,
            paths.toList(),
            paperType,
            paperColor
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
                mode = if (mode == CanvasMode.TEXT) CanvasMode.DRAW else CanvasMode.TEXT
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
            onSave = ::handleSaveJournal
        )

        // default content
        Box(
            modifier = Modifier
                .weight(1f)
                .background(paperColor)
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

            // draw layer selalu tampil
            CanvasDrawMode(
                paths = paths,
                enabled = mode == CanvasMode.DRAW,
                color = drawColor,
                strokeWidth = strokeWidth,
            )
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