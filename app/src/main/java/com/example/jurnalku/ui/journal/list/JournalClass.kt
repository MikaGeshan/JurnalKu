package com.example.jurnalku.ui.journal.list

data class JournalEntry(
    val journalId: String = "",
    val journalName: String = "",
    val pages: List<JournalPagePayload> = emptyList()
) {
    val payload: JournalPagePayload get() = pages.firstOrNull() ?: JournalPagePayload()
}

data class TextSpanPayload(
    val start: Int = 0,
    val end: Int = 0,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderlined: Boolean = false,
    val isStrikethrough: Boolean = false,
    val color: Long = 0xFF000000,
    val fontSize: Float = 16f,
    val fontFamily: String = "Default"
)

data class JournalImagePayload(
    val id: String = "",
    val base64: String = "",
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f
)

data class JournalPagePayload(
    val contentId: String = "",
    val text: String = "",
    val paperType: String = "",
    val paperColor: Long = 0xFFFFFFFF,
    val paths: List<DrawPathPayload> = emptyList(),
    val images: List<JournalImagePayload> = emptyList(),
    val fontFamily: String = "Default",
    val textAlign: String = "Left",
    val isUnderlined: Boolean = false,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isStrikethrough: Boolean = false,
    val textColor: Long = 0xFF000000,
    val fontSize: Float = 16f,
    val spans: List<TextSpanPayload> = emptyList()
)

data class DrawPointPayload(
    val x: Float = 0f,
    val y: Float = 0f
)

data class DrawPathPayload(
    val points: List<DrawPointPayload> = emptyList(),
    val color: Long = 0xFF000000,
    val strokeWidth: Float = 0f
)

data class RecentPageEntry(
    val journalId: String = "",
    val journalName: String = "",
    val pageIndex: Int = 0,
    val paperType: String = "",
    val paperColor: Long = 0xFFFFFFFF,
    val text: String = "",
    val paths: List<DrawPathPayload> = emptyList(),
    val images: List<JournalImagePayload> = emptyList(),
    val fontFamily: String = "Default",
    val textAlign: String = "Left",
    val isUnderlined: Boolean = false,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isStrikethrough: Boolean = false,
    val textColor: Long = 0xFF000000,
    val fontSize: Float = 16f,
    val spans: List<TextSpanPayload> = emptyList(),
    val timestamp: Long = 0L
)

fun JournalPagePayload.toRecentPageEntry(journalId: String, journalName: String, pageIndex: Int): RecentPageEntry {
    return RecentPageEntry(
        journalId = journalId,
        journalName = journalName,
        pageIndex = pageIndex,
        paperType = this.paperType,
        paperColor = this.paperColor,
        text = this.text,
        paths = this.paths,
        images = this.images,
        fontFamily = this.fontFamily,
        textAlign = this.textAlign,
        isUnderlined = this.isUnderlined,
        isBold = this.isBold,
        isItalic = this.isItalic,
        isStrikethrough = this.isStrikethrough,
        textColor = this.textColor,
        fontSize = this.fontSize,
        spans = this.spans
    )
}
