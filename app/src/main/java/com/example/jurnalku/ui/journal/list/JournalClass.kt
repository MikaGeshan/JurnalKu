package com.example.jurnalku.ui.journal.list

data class JournalEntry(
    val journalId: String = "",
    val journalName: String = "",
    val pages: List<JournalPagePayload> = emptyList()
) {
    val payload: JournalPagePayload get() = pages.firstOrNull() ?: JournalPagePayload()
}

data class JournalPagePayload(
    val contentId: String = "",
    val text: String = "",
    val paperType: String = "",
    val paperColor: Long = 0L,
    val paths: List<DrawPathPayload> = emptyList(),
    val imageBase64: String? = null,
    val imageOffsetX: Float = 0f,
    val imageOffsetY: Float = 0f,
    val imageScale: Float = 1f,
    val imageRotation: Float = 0f
)

data class DrawPointPayload(
    val x: Float = 0f,
    val y: Float = 0f
)

data class DrawPathPayload(
    val points: List<DrawPointPayload> = emptyList(),
    val color: Long = 0L,
    val strokeWidth: Float = 0f
)

data class RecentPageEntry(
    val journalId: String = "",
    val journalName: String = "",
    val pageIndex: Int = 0,
    val paperType: String = "",
    val paperColor: Long = 0L,
    val timestamp: Long = 0L
)
