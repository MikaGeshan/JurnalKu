package com.example.jurnalku.ui.journal.edit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import com.example.jurnalku.ui.components.canvas.CustomCanvas
import com.example.jurnalku.ui.components.canvas.DrawPath
import com.example.jurnalku.ui.journal.list.JournalPayload

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun EditJournalScreen(
    journal: JournalPayload,
    onBack: () -> Unit,
    onSave: (
        text: String,
        paths: List<DrawPath>,
        paperType: String,
        paperColor: Color
    ) -> Unit
) {
    val initialPaths = remember(journal) {
        journal.paths.map { pathPayload ->
            DrawPath(
                points = pathPayload.points.map { Offset(it.x, it.y) },
                color = Color(pathPayload.color.toULong()),
                strokeWidth = pathPayload.strokeWidth
            )
        }
    }

    CustomCanvas(
        paperColor = Color(journal.paperColor.toULong()),
        paperType = journal.paperType,
        initialText = journal.text,
        initialPaths = initialPaths,
        onClose = onBack,
        onSave = onSave
    )
}
