package com.example.jurnalku.ui.journal.edit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.jurnalku.ui.theme.safeColor
import androidx.compose.ui.geometry.Offset
import com.example.jurnalku.ui.components.canvas.CustomCanvas
import com.example.jurnalku.ui.components.canvas.DrawPath
import com.example.jurnalku.ui.journal.list.JournalPagePayload

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun EditJournalScreen(
    pages: List<JournalPagePayload>,
    onBack: () -> Unit,
    onSave: (List<JournalPagePayload>) -> Unit
) {
    val firstPage = pages.firstOrNull() ?: JournalPagePayload()

    CustomCanvas(
        paperColor = safeColor(firstPage.paperColor),
        paperType = firstPage.paperType,
        initialPages = pages,
        onClose = onBack,
        onSave = onSave
    )
}
