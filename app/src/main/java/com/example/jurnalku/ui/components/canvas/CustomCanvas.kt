package com.example.jurnalku.ui.components.canvas

import CanvasPattern
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.sp
import android.util.Log
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.LocalTextStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.TextFieldValue
import com.example.jurnalku.ui.journal.list.JournalImagePayload
import com.example.jurnalku.ui.journal.list.TextSpanPayload
import com.example.jurnalku.ui.journal.list.JournalPagePayload
import com.example.jurnalku.ui.journal.list.DrawPathPayload
import com.example.jurnalku.ui.theme.safeColor
import com.example.jurnalku.ui.journal.list.DrawPointPayload
import java.util.UUID
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.graphics.pdf.PdfDocument
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Environment
import android.widget.Toast
import android.text.TextPaint
import android.text.StaticLayout
import android.text.Layout
import androidx.compose.ui.unit.Density
import androidx.compose.ui.platform.LocalDensity

data class CanvasImage(
    val id: String = UUID.randomUUID().toString(),
    val base64: String,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var scale: Float = 1f,
    var rotation: Float = 0f
)

val defaultColor = Color.Black

private fun fontFamilyToString(family: FontFamily): String = when (family) {
    FontFamily.Serif -> "Serif"
    FontFamily.SansSerif -> "SansSerif"
    FontFamily.Monospace -> "Monospace"
    else -> "Default"
}

private fun stringToFontFamily(family: String): FontFamily = when (family) {
    "Serif" -> FontFamily.Serif
    "SansSerif" -> FontFamily.SansSerif
    "Monospace" -> FontFamily.Monospace
    else -> FontFamily.Default
}

private fun textAlignToString(align: TextAlign): String = when (align) {
    TextAlign.Center -> "Center"
    TextAlign.Right -> "Right"
    else -> "Left"
}

private fun stringToTextAlign(align: String): TextAlign = when (align) {
    "Center" -> TextAlign.Center
    "Right" -> TextAlign.Right
    else -> TextAlign.Left
}

private fun annotatedStringToSpans(annotatedString: AnnotatedString): List<TextSpanPayload> {
    return annotatedString.spanStyles.map { range ->
        val style = range.item
        TextSpanPayload(
            start = range.start,
            end = range.end,
            isBold = style.fontWeight == FontWeight.Bold,
            isItalic = style.fontStyle == FontStyle.Italic,
            isUnderlined = style.textDecoration?.contains(TextDecoration.Underline) == true,
            isStrikethrough = style.textDecoration?.contains(TextDecoration.LineThrough) == true,
            color = if (style.color != Color.Unspecified) style.color.toArgb().toLong() else defaultColor.toArgb().toLong(),
            fontSize = if (style.fontSize.isSp) style.fontSize.value else 16f,
            fontFamily = style.fontFamily?.let { fontFamilyToString(it) } ?: "Default"
        )
    }
}

private fun spansToAnnotatedString(text: String, spans: List<TextSpanPayload>): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        spans.forEach { span ->
            if (span.start < text.length) {
                val end = span.end.coerceAtMost(text.length)
                addStyle(
                    style = SpanStyle(
                        fontWeight = if (span.isBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (span.isItalic) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = TextDecoration.combine(
                            buildList {
                                if (span.isUnderlined) add(TextDecoration.Underline)
                                if (span.isStrikethrough) add(TextDecoration.LineThrough)
                            }
                        ),
                        color = safeColor(span.color),
                        fontSize = span.fontSize.sp,
                        fontFamily = stringToFontFamily(span.fontFamily)
                    ),
                    start = span.start,
                    end = end
                )
            }
        }
    }
}

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
                    paperColor = paperColor.toArgb().toLong()
                ))
            } else {
                addAll(initialPages)
            }
        }
    }
    var currentPageIndex by remember { mutableStateOf(0) }

    // State for current page
    var mode by remember { mutableStateOf(CanvasMode.TEXT) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    
    // Multiple Images State
    val canvasImages = remember { mutableStateListOf<CanvasImage>() }
    var selectedImageId by remember { mutableStateOf<String?>(null) }
    
    val paths = remember { mutableStateListOf<DrawPath>() }
    val undonePaths = remember { mutableStateListOf<DrawPath>() }

    // Text appearance state
    var fontFamilyState by remember { mutableStateOf<FontFamily>(FontFamily.Default) }
    var textAlignState by remember { mutableStateOf(TextAlign.Left) }
    var isUnderlinedState by remember { mutableStateOf(false) }
    var isBoldState by remember { mutableStateOf(false) }
    var isItalicState by remember { mutableStateOf(false) }
    var isStrikethroughState by remember { mutableStateOf(false) }
    var textColorState by remember { mutableStateOf(defaultColor) }
    var fontSizeState by remember { mutableStateOf(16f) }

    // Load page data when index changes
    LaunchedEffect(currentPageIndex) {
        val currentPage = pages[currentPageIndex]
        Log.d("LOAD_PAGE", "Loading page $currentPageIndex, text: ${currentPage.text}, spans: ${currentPage.spans.size}")
        textFieldValue = TextFieldValue(
            annotatedString = spansToAnnotatedString(currentPage.text, currentPage.spans)
        )
        
        // Load Images
        canvasImages.clear()
        canvasImages.addAll(currentPage.images.map { 
            CanvasImage(it.id, it.base64, it.offsetX, it.offsetY, it.scale, it.rotation) 
        })
        selectedImageId = null
        
        // Load text effects
        fontFamilyState = stringToFontFamily(currentPage.fontFamily)
        textAlignState = stringToTextAlign(currentPage.textAlign)
        isUnderlinedState = currentPage.isUnderlined
        isBoldState = currentPage.isBold
        isItalicState = currentPage.isItalic
        isStrikethroughState = currentPage.isStrikethrough
        textColorState = safeColor(currentPage.textColor)
        fontSizeState = currentPage.fontSize

        paths.clear()
        paths.addAll(currentPage.paths.map { payload ->
            DrawPath(
                points = payload.points.map { Offset(it.x, it.y) },
                color = safeColor(payload.color),
                strokeWidth = payload.strokeWidth
            )
        })
        undonePaths.clear()
    }

    fun saveCurrentPageState() {
        val currentSpans = annotatedStringToSpans(textFieldValue.annotatedString)
        Log.d("SAVE_PAGE", "Saving page $currentPageIndex, text: ${textFieldValue.text}, spans: ${currentSpans.size}")
        pages[currentPageIndex] = pages[currentPageIndex].copy(
            text = textFieldValue.text,
            spans = currentSpans,
            images = canvasImages.map { 
                JournalImagePayload(it.id, it.base64, it.offsetX, it.offsetY, it.scale, it.rotation) 
            },
            // Save text effects
            fontFamily = fontFamilyToString(fontFamilyState),
            textAlign = textAlignToString(textAlignState),
            isUnderlined = isUnderlinedState,
            isBold = isBoldState,
            isItalic = isItalicState,
            isStrikethrough = isStrikethroughState,
            textColor = textColorState.toArgb().toLong(),
            fontSize = fontSizeState,
            paths = paths.map { path ->
                DrawPathPayload(
                    points = path.points.map { DrawPointPayload(it.x, it.y) },
                    color = path.color.toArgb().toLong(),
                    strokeWidth = path.strokeWidth
                )
            }
        )
    }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    fun drawFullContent(canvas: android.graphics.Canvas) {
        val paint = Paint().apply { isAntiAlias = true }

        // 1. Background
        canvas.drawColor(paperColor.toArgb())

        // 2. Pattern
        val lineColor = Color.Gray.copy(alpha = 0.3f).toArgb()
        paint.color = lineColor
        paint.strokeWidth = 2f

        with(density) {
            when (paperType) {
                "Lined" -> {
                    val spacing = 24.dp.toPx()
                    var y = spacing
                    while (y < canvasSize.height) {
                        canvas.drawLine(0f, y, canvasSize.width.toFloat(), y, paint)
                        y += spacing
                    }
                    paint.color = Color.Red.copy(alpha = 0.3f).toArgb()
                    canvas.drawLine(80.dp.toPx(), 0f, 80.dp.toPx(), canvasSize.height.toFloat(), paint)
                }
                "Grid" -> {
                    val spacing = 24.dp.toPx()
                    var y = 0f
                    while (y < canvasSize.height) {
                        canvas.drawLine(0f, y, canvasSize.width.toFloat(), y, paint)
                        y += spacing
                    }
                    var x = 0f
                    while (x < canvasSize.width) {
                        canvas.drawLine(x, 0f, x, canvasSize.height.toFloat(), paint)
                        x += spacing
                    }
                }
                "Dot Grid" -> {
                    val spacing = 16.dp.toPx()
                    var y = spacing / 2
                    while (y < canvasSize.height) {
                        var x = spacing / 2
                        while (x < canvasSize.width) {
                            canvas.drawCircle(x, y, 2.dp.toPx(), paint)
                            x += spacing
                        }
                        y += spacing
                    }
                }
            }
        }

        // 3. Images
        canvasImages.forEach { canvasImage ->
            try {
                val bytes = Base64.decode(canvasImage.base64, Base64.DEFAULT)
                val imgBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (imgBitmap != null) {
                    val matrix = Matrix()

                    val scaleW = canvasSize.width.toFloat() / imgBitmap.width
                    val scaleH = canvasSize.height.toFloat() / imgBitmap.height
                    val baseScale = minOf(scaleW, scaleH)

                    val centerX = canvasSize.width / 2f
                    val centerY = canvasSize.height / 2f

                    matrix.postTranslate(-imgBitmap.width / 2f, -imgBitmap.height / 2f)
                    matrix.postScale(baseScale * canvasImage.scale, baseScale * canvasImage.scale)
                    matrix.postRotate(canvasImage.rotation)
                    matrix.postTranslate(centerX + canvasImage.offsetX, centerY + canvasImage.offsetY)

                    canvas.drawBitmap(imgBitmap, matrix, Paint().apply { isAntiAlias = true })
                }
            } catch (e: Exception) {
                Log.e("EXPORT_ERROR", "Failed to draw image", e)
            }
        }

        // 4. Text
        if (textFieldValue.text.isNotEmpty()) {
            val padding = with(density) { 16.dp.toPx() }
            val maxWidth = (canvasSize.width - (padding * 2)).toInt()
            
            val alignment = when (textAlignState) {
                TextAlign.Center -> Layout.Alignment.ALIGN_CENTER
                TextAlign.Right -> Layout.Alignment.ALIGN_OPPOSITE
                else -> Layout.Alignment.ALIGN_NORMAL
            }

            // We need to build a Spannable from AnnotatedString for StaticLayout
            val spannable = android.text.SpannableStringBuilder(textFieldValue.text)
            
            // Apply base style
            val baseTypeface = when (fontFamilyState) {
                FontFamily.Serif -> android.graphics.Typeface.SERIF
                FontFamily.SansSerif -> android.graphics.Typeface.SANS_SERIF
                FontFamily.Monospace -> android.graphics.Typeface.MONOSPACE
                else -> android.graphics.Typeface.DEFAULT
            }
            
            textFieldValue.annotatedString.spanStyles.forEach { range ->
                val style = range.item
                val start = range.start
                val end = range.end
                
                // Color
                if (style.color != Color.Unspecified) {
                    spannable.setSpan(
                        android.text.style.ForegroundColorSpan(style.color.toArgb()),
                        start, end, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                
                // Size
                if (style.fontSize.isSp) {
                    spannable.setSpan(
                        android.text.style.AbsoluteSizeSpan(with(density) { style.fontSize.toPx() }.toInt()),
                        start, end, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                
                // Typeface (Bold/Italic/FontFamily)
                val tfBase = when (style.fontFamily) {
                    FontFamily.Serif -> android.graphics.Typeface.SERIF
                    FontFamily.SansSerif -> android.graphics.Typeface.SANS_SERIF
                    FontFamily.Monospace -> android.graphics.Typeface.MONOSPACE
                    else -> baseTypeface
                }
                val isBold = style.fontWeight == FontWeight.Bold
                val isItalic = style.fontStyle == FontStyle.Italic
                val styleInt = when {
                    isBold && isItalic -> android.graphics.Typeface.BOLD_ITALIC
                    isBold -> android.graphics.Typeface.BOLD
                    isItalic -> android.graphics.Typeface.ITALIC
                    else -> android.graphics.Typeface.NORMAL
                }
                val tf = android.graphics.Typeface.create(tfBase, styleInt)
                spannable.setSpan(
                    android.text.style.TypefaceSpan(tf),
                    start, end, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                
                // Decorations
                if (style.textDecoration?.contains(TextDecoration.Underline) == true) {
                    spannable.setSpan(android.text.style.UnderlineSpan(), start, end, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (style.textDecoration?.contains(TextDecoration.LineThrough) == true) {
                    spannable.setSpan(android.text.style.StrikethroughSpan(), start, end, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            val textPaint = TextPaint().apply {
                isAntiAlias = true
                textSize = with(density) { fontSizeState.sp.toPx() }
                color = textColorState.toArgb()
            }

            val staticLayout = StaticLayout.Builder.obtain(spannable, 0, spannable.length, textPaint, maxWidth)
                .setAlignment(alignment)
                .build()

            canvas.save()
            canvas.translate(padding, padding)
            staticLayout.draw(canvas)
            canvas.restore()
        }

        // 5. Drawings
        paths.forEach { drawPath ->
            paint.color = drawPath.color.toArgb()
            paint.strokeWidth = drawPath.strokeWidth
            paint.style = Paint.Style.STROKE
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeJoin = Paint.Join.ROUND

            val path = AndroidPath()
            drawPath.points.forEachIndexed { i, pt ->
                if (i == 0) path.moveTo(pt.x, pt.y)
                else path.lineTo(pt.x, pt.y)
            }
            canvas.drawPath(path, paint)
        }
    }

    fun exportPage(format: String) {
        if (canvasSize.width == 0 || canvasSize.height == 0) return

        val fileNameBase = "Journal_${UUID.randomUUID()}"

        if (format == "JPG") {
            // --- Export to JPG ---
            val bitmap = Bitmap.createBitmap(canvasSize.width, canvasSize.height, Bitmap.Config.ARGB_8888)
            val bitmapCanvas = android.graphics.Canvas(bitmap)
            drawFullContent(bitmapCanvas)

            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileNameBase.jpg")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    }
                }
                Toast.makeText(context, "Page exported as JPG", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("EXPORT_ERROR", "Failed to save JPG", e)
                Toast.makeText(context, "JPG Export failed", Toast.LENGTH_SHORT).show()
            }
        } else if (format == "PDF") {
            // --- Export to PDF ---
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(canvasSize.width, canvasSize.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            
            drawFullContent(page.canvas)
            
            pdfDocument.finishPage(page)

            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileNameBase.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                }

                val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                }
                Toast.makeText(context, "Page exported as PDF", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("EXPORT_ERROR", "Failed to save PDF", e)
                Toast.makeText(context, "PDF Export failed", Toast.LENGTH_SHORT).show()
            } finally {
                pdfDocument.close()
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> 
            if (uri != null) {
                val base64 = uriToBase64(context, uri)
                if (base64 != null) {
                    val newImage = CanvasImage(base64 = base64)
                    canvasImages.add(newImage)
                    selectedImageId = newImage.id
                    mode = CanvasMode.IMAGE
                }
            }
        }
    )

    var selectedTool by remember { mutableStateOf(DrawTool.PEN) }
    var selectedColor by remember { mutableStateOf(defaultColor) }

    fun handleUndo() {
        if (paths.isNotEmpty()) {
            undonePaths.add(paths.removeAt(paths.size - 1))
        }
    }

    fun handleRedo() {
        if (undonePaths.isNotEmpty()) {
            paths.add(undonePaths.removeAt(undonePaths.size - 1))
        }
    }

    fun handlePathAdded(newPath: DrawPath) {
        paths.add(newPath)
        undonePaths.clear() // Clear redo history when a new path is drawn
    }

    fun handleClearAll() {
        paths.clear()
        undonePaths.clear()
    }

    fun handleDeleteImage() {
        selectedImageId?.let { id ->
            canvasImages.removeAll { it.id == id }
            selectedImageId = null
            mode = CanvasMode.TEXT
        }
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
            paperColor = paperColor.toArgb().toLong()
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
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {

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
                onExportPDF = { exportPage("PDF") },
                onExportJPG = { exportPage("JPG") },
                onSave = ::handleSaveJournal,
                onPickImage = {
                    try {
                        photoPickerLauncher.launch("image/*")
                    } catch (e: Exception) {
                        Log.e("IMAGE_ERROR", "Failed to launch picker", e)
                        Toast.makeText(context, "No image picker found", Toast.LENGTH_SHORT).show()
                    }
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
            androidx.compose.material3.Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                color = paperColor
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            val oldV = textFieldValue
                            val newV = newValue
                            
                            if (newV.text == oldV.text) {
                                // Selection or cursor move
                                textFieldValue = newV.copy(annotatedString = oldV.annotatedString)
                                return@TextField
                            }

                            val oldA = oldV.annotatedString
                            val newT = newV.text
                            
                            // Prefix/Suffix Diffing to preserve spans
                            var prefixLen = 0
                            while (prefixLen < oldA.text.length && prefixLen < newT.length && oldA.text[prefixLen] == newT[prefixLen]) {
                                prefixLen++
                            }
                            
                            var suffixLen = 0
                            while (suffixLen < (oldA.text.length - prefixLen) && 
                                   suffixLen < (newT.length - prefixLen) && 
                                   oldA.text[oldA.text.length - 1 - suffixLen] == newT[newT.length - 1 - suffixLen]) {
                                suffixLen++
                            }
                            
                            val builder = AnnotatedString.Builder()
                            try {
                                // 1. Keep prefix spans
                                builder.append(oldA.subSequence(0, prefixLen))
                                
                                // 2. Add styled new middle part
                                val middlePart = newT.substring(prefixLen, newT.length - suffixLen)
                                if (middlePart.isNotEmpty()) {
                                    builder.withStyle(
                                        style = SpanStyle(
                                            fontWeight = if (isBoldState) FontWeight.Bold else FontWeight.Normal,
                                            fontStyle = if (isItalicState) FontStyle.Italic else FontStyle.Normal,
                                            textDecoration = TextDecoration.combine(
                                                buildList {
                                                    if (isUnderlinedState) add(TextDecoration.Underline)
                                                    if (isStrikethroughState) add(TextDecoration.LineThrough)
                                                }
                                            ),
                                            color = textColorState,
                                            fontSize = fontSizeState.sp,
                                            fontFamily = fontFamilyState
                                        )
                                    ) {
                                        append(middlePart)
                                    }
                                }
                                
                                // 3. Keep suffix spans (shifted)
                                builder.append(oldA.subSequence(oldA.length - suffixLen, oldA.length))
                                
                                textFieldValue = newV.copy(annotatedString = builder.toAnnotatedString())
                            } catch (e: Exception) {
                                Log.e("TEXT_ERROR", "Error in prefix/suffix merge", e)
                                textFieldValue = newV
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        placeholder = { Text("Start writing...") },
                        enabled = mode == CanvasMode.TEXT,
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = FontFamily.Default, // Neutral base font
                            textAlign = textAlignState,
                            fontSize = 16.sp, // Neutral base size
                            color = defaultColor // Neutral base color
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    // image layer
                    canvasImages.forEach { canvasImage ->
                        val imageBytes = remember(canvasImage.base64) {
                            try {
                                Base64.decode(canvasImage.base64, Base64.DEFAULT)
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
                                        translationX = canvasImage.offsetX,
                                        translationY = canvasImage.offsetY,
                                        scaleX = canvasImage.scale,
                                        scaleY = canvasImage.scale,
                                        rotationZ = canvasImage.rotation
                                    )
                                    .then(
                                        if (mode == CanvasMode.TEXT) {
                                            Modifier.pointerInput(canvasImage.id) {
                                                detectTapGestures {
                                                    selectedImageId = canvasImage.id
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
                    if (mode == CanvasMode.IMAGE && selectedImageId != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        selectedImageId = null
                                        mode = CanvasMode.TEXT
                                    }
                                }
                                .pointerInput(selectedImageId) {
                                    detectTransformGestures { _, pan, zoom, rotation ->
                                        val index = canvasImages.indexOfFirst { it.id == selectedImageId }
                                        if (index != -1) {
                                            val img = canvasImages[index]
                                            val newScale = (img.scale * zoom).coerceIn(0.2f, 8f)
                                            val newRotation = img.rotation + rotation
                                            val newOffset = Offset(
                                                x = (img.offsetX + pan.x).coerceIn(-canvasSize.width.toFloat(), canvasSize.width.toFloat()),
                                                y = (img.offsetY + pan.y).coerceIn(-canvasSize.height.toFloat(), canvasSize.height.toFloat())
                                            )
                                            
                                            canvasImages[index] = img.copy(
                                                scale = newScale,
                                                rotation = newRotation,
                                                offsetX = newOffset.x,
                                                offsetY = newOffset.y
                                            )
                                        }
                                    }
                                }
                        )
                    }
                }
            }
            
            // Spacer to avoid canvas bottom being hidden by floating toolbars
            Spacer(modifier = Modifier.height(72.dp))
        }

        // Floating toolbars
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                .padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
        ) {
            // toolbar draw
            if (mode == CanvasMode.DRAW) {
                DrawToolbar(
                    selectedTool = selectedTool,
                    selectedColor = selectedColor,
                    onToolSelected = { selectedTool = it },
                    onColorSelected = {
                        selectedColor = it
                        Log.d("COLOR_DEBUG", "selectedColor = $it")
                    },
                    onClearAll = ::handleClearAll
                )
            }

            // toolbar image
            if (mode == CanvasMode.IMAGE) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF5F5F5.toInt()),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            modifier = Modifier
                                .clickable { handleDeleteImage() }
                        ) {
                            Text(
                                text = "Delete Image",
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // toolbar text
            if (mode == CanvasMode.TEXT) {
                TextToolbar(
                    selectedFontFamily = fontFamilyState,
                    onFontFamilyChange = { 
                        fontFamilyState = it 
                        if (textFieldValue.selection.length > 0) {
                            val builder = AnnotatedString.Builder(textFieldValue.annotatedString)
                            val start = minOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            val end = maxOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            builder.addStyle(SpanStyle(fontFamily = it), start, end)
                            textFieldValue = textFieldValue.copy(annotatedString = builder.toAnnotatedString())
                        }
                    },
                    textAlign = textAlignState,
                    onTextAlignChange = { textAlignState = it },
                    isUnderlined = isUnderlinedState,
                    onUnderlineChange = { 
                        isUnderlinedState = it
                        if (textFieldValue.selection.length > 0) {
                            val builder = AnnotatedString.Builder(textFieldValue.annotatedString)
                            val start = minOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            val end = maxOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            builder.addStyle(
                                SpanStyle(textDecoration = if (it) TextDecoration.Underline else TextDecoration.None),
                                start, end
                            )
                            textFieldValue = textFieldValue.copy(annotatedString = builder.toAnnotatedString())
                        }
                    },
                    isBold = isBoldState,
                    onBoldChange = { 
                        isBoldState = it
                        if (textFieldValue.selection.length > 0) {
                            val builder = AnnotatedString.Builder(textFieldValue.annotatedString)
                            val start = minOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            val end = maxOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            builder.addStyle(
                                SpanStyle(fontWeight = if (it) FontWeight.Bold else FontWeight.Normal),
                                start, end
                            )
                            textFieldValue = textFieldValue.copy(annotatedString = builder.toAnnotatedString())
                        }
                    },
                    isItalic = isItalicState,
                    onItalicChange = { 
                        isItalicState = it
                        if (textFieldValue.selection.length > 0) {
                            val builder = AnnotatedString.Builder(textFieldValue.annotatedString)
                            val start = minOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            val end = maxOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            builder.addStyle(
                                SpanStyle(fontStyle = if (it) FontStyle.Italic else FontStyle.Normal),
                                start, end
                            )
                            textFieldValue = textFieldValue.copy(annotatedString = builder.toAnnotatedString())
                        }
                    },
                    isStrikethrough = isStrikethroughState,
                    onStrikethroughChange = { 
                        isStrikethroughState = it
                        if (textFieldValue.selection.length > 0) {
                            val builder = AnnotatedString.Builder(textFieldValue.annotatedString)
                            val start = minOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            val end = maxOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            builder.addStyle(
                                SpanStyle(textDecoration = if (it) TextDecoration.LineThrough else TextDecoration.None),
                                start, end
                            )
                            textFieldValue = textFieldValue.copy(annotatedString = builder.toAnnotatedString())
                        }
                    },
                    selectedColor = textColorState,
                    onColorChange = { 
                        textColorState = it
                        if (textFieldValue.selection.length > 0) {
                            val builder = AnnotatedString.Builder(textFieldValue.annotatedString)
                            val start = minOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            val end = maxOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            builder.addStyle(SpanStyle(color = it), start, end)
                            textFieldValue = textFieldValue.copy(annotatedString = builder.toAnnotatedString())
                        }
                    },
                    fontSize = fontSizeState,
                    onFontSizeChange = { 
                        fontSizeState = it
                        if (textFieldValue.selection.length > 0) {
                            val builder = AnnotatedString.Builder(textFieldValue.annotatedString)
                            val start = minOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            val end = maxOf(textFieldValue.selection.start, textFieldValue.selection.end)
                            builder.addStyle(SpanStyle(fontSize = it.sp), start, end)
                            textFieldValue = textFieldValue.copy(annotatedString = builder.toAnnotatedString())
                        }
                    }
                )
            }
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
