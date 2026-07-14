package com.toxiclens.app.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.toxiclens.app.data.PdfBranding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val LEFT_MARGIN = 40f
    private const val RIGHT_MARGIN = 40f
    private const val CONTENT_WIDTH = PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN
    private const val FOOTER_TOP = 785f
    private const val BOTTOM_LIMIT = 770f

    fun createPdf(
        context: Context,
        outputUri: Uri,
        result: String,
        conversationType: String,
        branding: PdfBranding
    ): Boolean {
        val document = PdfDocument()

        return try {
            val writer = ReportWriter(
                document = document,
                context = context,
                branding = branding
            )

            writer.startPage(isFirstPage = true)

            writer.drawReportInformation(
                conversationType = cleanConversationType(conversationType)
            )

            writer.drawScoreSection(
                score = extractSection(
                    result,
                    "RELATIONSHIP_SCORE"
                ).ifBlank { "0" }
            )

            val sections = listOf(
                "Toxicity Level" to extractSection(
                    result,
                    "TOXICITY_LEVEL"
                ),
                "Emotional Tone" to extractSection(
                    result,
                    "EMOTIONAL_TONE"
                ),
                "Hidden Intent" to extractSection(
                    result,
                    "HIDDEN_INTENT"
                ),
                "Green Flags" to extractSection(
                    result,
                    "GREEN_FLAGS"
                ),
                "Red Flags" to extractSection(
                    result,
                    "RED_FLAGS"
                ),
                "Summary" to extractSection(
                    result,
                    "SUMMARY"
                ),
                "Suggested Reply" to extractSection(
                    result,
                    "SUGGESTED_REPLY"
                )
            )

            sections.forEach { (title, content) ->
                writer.drawSection(
                    title = title,
                    content = content.ifBlank { "-" }
                )
            }

            writer.finishDocument()

            context.contentResolver
                .openOutputStream(outputUri)
                ?.use { outputStream ->
                    document.writeTo(outputStream)
                } ?: return false

            true
        } catch (e: Exception) {
            false
        } finally {
            document.close()
        }
    }

    private class ReportWriter(
        private val document: PdfDocument,
        private val context: Context,
        private val branding: PdfBranding
    ) {
        private var pageNumber = 0
        private var currentPage: PdfDocument.Page? = null
        private lateinit var canvas: Canvas
        private var y = 0f

        private val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(21, 26, 53)
        }

        private val purplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(111, 80, 181)
        }

        private val lightPurplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(240, 235, 252)
        }

        private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(21, 26, 53)
            textSize = 22f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )
        }

        private val companyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(21, 26, 53)
            textSize = 16f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )
        }

        private val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(111, 80, 181)
            textSize = 13f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )
        }

        private val whiteHeadingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 12f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )
        }

        private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(45, 45, 55)
            textSize = 10.5f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.NORMAL
            )
        }

        private val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(105, 105, 115)
            textSize = 8.5f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.NORMAL
            )
        }

        private val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(120, 120, 130)
            textSize = 8f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.NORMAL
            )
        }

        private val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(21, 26, 53)
            textSize = 30f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )
        }

        fun startPage(isFirstPage: Boolean) {
            finishCurrentPage()

            pageNumber++

            val pageInfo = PdfDocument.PageInfo.Builder(
                PAGE_WIDTH,
                PAGE_HEIGHT,
                pageNumber
            ).create()

            currentPage = document.startPage(pageInfo)
            canvas = currentPage!!.canvas

            drawPageHeader()

            y = if (isFirstPage) {
                drawFirstPageTitle()
                195f
            } else {
                canvas.drawText(
                    "Conversation Analysis Report - Continued",
                    LEFT_MARGIN,
                    135f,
                    headingPaint
                )
                160f
            }
        }

        private fun drawPageHeader() {
            val logo = loadLogo(
                context = context,
                logoUri = branding.logoUri
            )

            if (logo != null) {
                val maxWidth = 90f
                val maxHeight = 55f

                val scale = minOf(
                    maxWidth / logo.width,
                    maxHeight / logo.height
                )

                val logoWidth = logo.width * scale
                val logoHeight = logo.height * scale

                val destination = RectF(
                    LEFT_MARGIN,
                    35f,
                    LEFT_MARGIN + logoWidth,
                    35f + logoHeight
                )

                canvas.drawBitmap(
                    logo,
                    null,
                    destination,
                    null
                )
            }

            val companyX = if (logo != null) {
                150f
            } else {
                LEFT_MARGIN
            }

            canvas.drawText(
                branding.companyName.ifBlank {
                    "Read Between"
                },
                companyX,
                52f,
                companyPaint
            )

            val details = listOf(
                branding.phone,
                branding.email,
                branding.website,
                branding.address
            ).filter { it.isNotBlank() }

            var detailY = 69f

            details.take(4).forEach { detail ->
                val shortened = if (detail.length > 65) {
                    detail.take(62) + "..."
                } else {
                    detail
                }

                canvas.drawText(
                    shortened,
                    companyX,
                    detailY,
                    smallPaint
                )

                detailY += 11f
            }

            canvas.drawRect(
                LEFT_MARGIN,
                112f,
                PAGE_WIDTH - RIGHT_MARGIN,
                115f,
                purplePaint
            )
        }

        private fun drawFirstPageTitle(): Float {
            canvas.drawText(
                "Conversation Analysis Report",
                LEFT_MARGIN,
                150f,
                titlePaint
            )

            canvas.drawText(
                "Confidential AI-Assisted Report",
                LEFT_MARGIN,
                172f,
                smallPaint
            )

            return 195f
        }

        fun drawReportInformation(
            conversationType: String
        ) {
            ensureSpace(75f)

            val box = RectF(
                LEFT_MARGIN,
                y,
                PAGE_WIDTH - RIGHT_MARGIN,
                y + 58f
            )

            canvas.drawRoundRect(
                box,
                10f,
                10f,
                lightPurplePaint
            )

            canvas.drawText(
                "Conversation Type",
                LEFT_MARGIN + 14f,
                y + 20f,
                headingPaint
            )

            canvas.drawText(
                conversationType,
                LEFT_MARGIN + 14f,
                y + 40f,
                bodyPaint
            )

            val date = SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())

            val dateWidth = bodyPaint.measureText(date)

            canvas.drawText(
                "Report Date",
                PAGE_WIDTH - RIGHT_MARGIN - dateWidth,
                y + 20f,
                headingPaint
            )

            canvas.drawText(
                date,
                PAGE_WIDTH - RIGHT_MARGIN - dateWidth,
                y + 40f,
                bodyPaint
            )

            y += 76f
        }

        fun drawScoreSection(
            score: String
        ) {
            ensureSpace(105f)

            val numericScore = score
                .filter { it.isDigit() }
                .toIntOrNull()
                ?.coerceIn(0, 100)
                ?: 0

            drawSectionHeader("Communication Score")

            y += 20f

            canvas.drawText(
                "$numericScore / 100",
                LEFT_MARGIN,
                y + 25f,
                scorePaint
            )

            val progressLeft = 190f
            val progressTop = y + 7f
            val progressWidth = 330f
            val progressHeight = 18f

            val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(225, 225, 235)
            }

            canvas.drawRoundRect(
                RectF(
                    progressLeft,
                    progressTop,
                    progressLeft + progressWidth,
                    progressTop + progressHeight
                ),
                9f,
                9f,
                trackPaint
            )

            canvas.drawRoundRect(
                RectF(
                    progressLeft,
                    progressTop,
                    progressLeft +
                            progressWidth * (numericScore / 100f),
                    progressTop + progressHeight
                ),
                9f,
                9f,
                purplePaint
            )

            y += 58f
        }

        fun drawSection(
            title: String,
            content: String
        ) {
            ensureSpace(55f)
            drawSectionHeader(title)

            y += 18f

            val cleanedContent = cleanPdfText(content)
            val lines = wrapText(
                text = cleanedContent,
                maxWidth = CONTENT_WIDTH - 18f,
                paint = bodyPaint
            )

            if (lines.isEmpty()) {
                drawBodyLine("-")
            } else {
                lines.forEach { line ->
                    if (y + 16f >= BOTTOM_LIMIT) {
                        startPage(isFirstPage = false)
                    }

                    drawBodyLine(line)
                }
            }

            y += 15f
        }

        private fun drawSectionHeader(
            title: String
        ) {
            ensureSpace(35f)

            val headerBox = RectF(
                LEFT_MARGIN,
                y,
                PAGE_WIDTH - RIGHT_MARGIN,
                y + 25f
            )

            canvas.drawRoundRect(
                headerBox,
                7f,
                7f,
                darkPaint
            )

            canvas.drawText(
                title,
                LEFT_MARGIN + 12f,
                y + 17f,
                whiteHeadingPaint
            )

            y += 32f
        }

        private fun drawBodyLine(
            line: String
        ) {
            canvas.drawText(
                line,
                LEFT_MARGIN + 8f,
                y,
                bodyPaint
            )

            y += 15f
        }

        private fun ensureSpace(
            requiredHeight: Float
        ) {
            if (y + requiredHeight >= BOTTOM_LIMIT) {
                startPage(isFirstPage = false)
            }
        }

        private fun finishCurrentPage() {
            val page = currentPage ?: return

            drawFooter()
            document.finishPage(page)
            currentPage = null
        }

        fun finishDocument() {
            finishCurrentPage()
        }

        private fun drawFooter() {
            canvas.drawLine(
                LEFT_MARGIN,
                FOOTER_TOP,
                PAGE_WIDTH - RIGHT_MARGIN,
                FOOTER_TOP,
                footerPaint
            )

            canvas.drawText(
                "Generated by Read Between AI",
                LEFT_MARGIN,
                FOOTER_TOP + 17f,
                footerPaint
            )

            canvas.drawText(
                "Confidential Report",
                LEFT_MARGIN,
                FOOTER_TOP + 30f,
                footerPaint
            )

            val pageText = "Page $pageNumber"
            val pageTextWidth = footerPaint.measureText(pageText)

            canvas.drawText(
                pageText,
                PAGE_WIDTH - RIGHT_MARGIN - pageTextWidth,
                FOOTER_TOP + 17f,
                footerPaint
            )

            val warning =
                "AI-generated content is supportive guidance, not definitive proof."

            val warningWidth = footerPaint.measureText(warning)

            canvas.drawText(
                warning,
                PAGE_WIDTH - RIGHT_MARGIN - warningWidth,
                FOOTER_TOP + 30f,
                footerPaint
            )
        }

        private fun wrapText(
            text: String,
            maxWidth: Float,
            paint: Paint
        ): List<String> {
            val output = mutableListOf<String>()

            text.lines().forEach { paragraph ->
                val cleanParagraph = paragraph.trim()

                if (cleanParagraph.isBlank()) {
                    output.add("")
                    return@forEach
                }

                val words = cleanParagraph.split(
                    Regex("\\s+")
                )

                var currentLine = ""

                words.forEach { word ->
                    val testLine = if (currentLine.isBlank()) {
                        word
                    } else {
                        "$currentLine $word"
                    }

                    if (paint.measureText(testLine) <= maxWidth) {
                        currentLine = testLine
                    } else {
                        if (currentLine.isNotBlank()) {
                            output.add(currentLine)
                        }

                        currentLine = word
                    }
                }

                if (currentLine.isNotBlank()) {
                    output.add(currentLine)
                }
            }

            return output
        }
    }

    private fun cleanPdfText(
        text: String
    ): String {
        return text
            .replace("✅", "-")
            .replace("⚠️", "-")
            .replace("⚠", "-")
            .replace("🚩", "-")
            .replace("💚", "")
            .replace("❤️", "")
            .replace("👥", "")
            .replace("👨‍👩‍👧", "")
            .replace("💼", "")
            .replace("👔", "")
            .replace("🛒", "")
            .replace("📱", "")
            .replace("•", "-")
            .replace("*", "-")
            .trim()
    }

    private fun cleanConversationType(
        conversationType: String
    ): String {
        return cleanPdfText(conversationType)
            .ifBlank { "Other" }
    }

    private fun extractSection(
        text: String,
        sectionName: String
    ): String {
        val sectionStart = "$sectionName:"
        val startIndex = text.indexOf(sectionStart)

        if (startIndex == -1) {
            return ""
        }

        val contentStart =
            startIndex + sectionStart.length

        val sections = listOf(
            "RELATIONSHIP_SCORE:",
            "TOXICITY_LEVEL:",
            "EMOTIONAL_TONE:",
            "HIDDEN_INTENT:",
            "GREEN_FLAGS:",
            "RED_FLAGS:",
            "SUMMARY:",
            "SUGGESTED_REPLY:"
        )

        val nextIndex = sections
            .filter {
                it != sectionStart
            }
            .map {
                text.indexOf(
                    it,
                    contentStart
                )
            }
            .filter {
                it != -1
            }
            .minOrNull()
            ?: text.length

        return text.substring(
            contentStart,
            nextIndex
        ).trim()
    }

    private fun loadLogo(
        context: Context,
        logoUri: String
    ): Bitmap? {
        if (logoUri.isBlank()) {
            return null
        }

        return try {
            val uri = Uri.parse(logoUri)

            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.P
            ) {
                val source = ImageDecoder.createSource(
                    context.contentResolver,
                    uri
                )

                ImageDecoder.decodeBitmap(source) {
                        decoder,
                        _,
                        _ ->

                    decoder.allocator =
                        ImageDecoder.ALLOCATOR_SOFTWARE
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    uri
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}