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
    private const val CONTENT_WIDTH =
        PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN

    private const val BOTTOM_LIMIT = 768f
    private const val FOOTER_TOP = 785f

    fun createPdf(
        context: Context,
        outputUri: Uri,
        result: String,
        conversationType: String,
        branding: PdfBranding,
        appLanguage: String
    ): Boolean {
        val document = PdfDocument()
        val strings = PdfLanguage.get(appLanguage)

        return try {
            val writer = ReportWriter(
                document = document,
                context = context,
                branding = branding,
                strings = strings,
                appLanguage = appLanguage
            )

            writer.startPage(isFirstPage = true)

            writer.drawReportInformation(
                conversationType = cleanConversationType(
                    conversationType = conversationType,
                    appLanguage = appLanguage
                )
            )

            writer.drawScoreSection(
                score = extractSection(
                    result,
                    "RELATIONSHIP_SCORE"
                ).ifBlank { "0" }
            )

            writer.drawToxicitySection(
                toxicity = extractSection(
                    result,
                    "TOXICITY_LEVEL"
                ).ifBlank { "-" }
            )

            val sections = listOf(
                strings.emotionalTone to extractSection(
                    result,
                    "EMOTIONAL_TONE"
                ),
                strings.hiddenIntent to extractSection(
                    result,
                    "HIDDEN_INTENT"
                ),
                strings.greenFlags to extractSection(
                    result,
                    "GREEN_FLAGS"
                ),
                strings.redFlags to extractSection(
                    result,
                    "RED_FLAGS"
                ),
                strings.summary to extractSection(
                    result,
                    "SUMMARY"
                ),
                strings.suggestedReply to extractSection(
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
        } catch (exception: Exception) {
            false
        } finally {
            document.close()
        }
    }

    private class ReportWriter(
        private val document: PdfDocument,
        private val context: Context,
        private val branding: PdfBranding,
        private val strings: PdfStrings,
        private val appLanguage: String
    ) {
        private var pageNumber = 0
        private var currentPage: PdfDocument.Page? = null

        private lateinit var canvas: Canvas
        private var y = 0f

        private val darkPaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(21, 26, 53)
        }

        private val purplePaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(111, 80, 181)
        }

        private val lightPurplePaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(241, 237, 252)
        }

        private val lightGrayPaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(247, 247, 250)
        }

        private val borderPaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(224, 224, 232)
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        private val titlePaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(21, 26, 53)
            textSize = 25f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )
        }

        private val companyPaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(21, 26, 53)
            textSize = 12.5f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )
        }

        private val headingPaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(111, 80, 181)
            textSize = 12.5f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )
        }

        private val whiteHeadingPaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.WHITE
            textSize = 12f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )
        }

        private val bodyPaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(45, 45, 55)
            textSize = 10.5f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.NORMAL
            )
        }

        private val smallPaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(100, 100, 112)
            textSize = 8.5f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.NORMAL
            )
        }

        private val footerPaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(125, 125, 135)
            textSize = 8f
            typeface = Typeface.create(
                "sans-serif",
                Typeface.NORMAL
            )
        }

        private val scorePaint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = Color.rgb(21, 26, 53)
            textSize = 39f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )
        }

        fun startPage(
            isFirstPage: Boolean
        ) {
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
            } else {
                drawContinuedPageTitle()
            }
        }

        private fun drawPageHeader() {
            val logo = loadLogo(
                context = context,
                logoUri = branding.logoUri
            )

            val headerTop = 20f
            val headerBottom = 148f

            if (logo != null) {
                val maxWidth = 155f
                val maxHeight = 100f

                val scale = minOf(
                    maxWidth / logo.width,
                    maxHeight / logo.height
                )

                val logoWidth = logo.width * scale
                val logoHeight = logo.height * scale

                val logoTop = headerTop +
                        ((headerBottom - headerTop - logoHeight) / 2f)

                canvas.drawBitmap(
                    logo,
                    null,
                    RectF(
                        LEFT_MARGIN,
                        logoTop,
                        LEFT_MARGIN + logoWidth,
                        logoTop + logoHeight
                    ),
                    null
                )
            }

            val companyX = if (logo != null) {
                215f
            } else {
                LEFT_MARGIN
            }

            val availableWidth =
                PAGE_WIDTH - RIGHT_MARGIN - companyX

            val companyName = branding.companyName
                .ifBlank { "Read Between" }

            val companyLines = wrapText(
                text = companyName,
                maxWidth = availableWidth,
                paint = companyPaint
            )

            var companyY = 39f

            companyLines.take(3).forEach { line ->
                canvas.drawText(
                    line,
                    companyX,
                    companyY,
                    companyPaint
                )

                companyY += 15f
            }

            val details = listOf(
                branding.website,
                branding.email,
                normalizePhone(branding.phone),
                branding.address
            ).filter { it.isNotBlank() }

            var detailY = companyY + 2f

            details.forEachIndexed { index, detail ->
                val maxLines = if (
                    index == details.lastIndex
                ) {
                    2
                } else {
                    1
                }

                val detailLines = wrapText(
                    text = detail,
                    maxWidth = availableWidth,
                    paint = smallPaint
                )

                detailLines.take(maxLines).forEach { line ->
                    canvas.drawText(
                        line,
                        companyX,
                        detailY,
                        smallPaint
                    )

                    detailY += 11f
                }

                detailY += 2f
            }

            canvas.drawRect(
                LEFT_MARGIN,
                headerBottom,
                PAGE_WIDTH - RIGHT_MARGIN,
                headerBottom + 3f,
                purplePaint
            )
        }

        private fun drawFirstPageTitle(): Float {
            val titleWidth =
                titlePaint.measureText(strings.reportTitle)

            canvas.drawText(
                strings.reportTitle,
                (PAGE_WIDTH - titleWidth) / 2f,
                184f,
                titlePaint
            )

            val subtitleWidth =
                smallPaint.measureText(strings.reportSubtitle)

            canvas.drawText(
                strings.reportSubtitle,
                (PAGE_WIDTH - subtitleWidth) / 2f,
                207f,
                smallPaint
            )

            canvas.drawLine(
                LEFT_MARGIN,
                222f,
                PAGE_WIDTH - RIGHT_MARGIN,
                222f,
                purplePaint
            )

            return 245f
        }

        private fun drawContinuedPageTitle(): Float {
            canvas.drawText(
                strings.reportContinued,
                LEFT_MARGIN,
                180f,
                headingPaint
            )

            canvas.drawLine(
                LEFT_MARGIN,
                195f,
                PAGE_WIDTH - RIGHT_MARGIN,
                195f,
                purplePaint
            )

            return 218f
        }

        fun drawReportInformation(
            conversationType: String
        ) {
            ensureSpace(84f)

            val box = RectF(
                LEFT_MARGIN,
                y,
                PAGE_WIDTH - RIGHT_MARGIN,
                y + 64f
            )

            canvas.drawRoundRect(
                box,
                12f,
                12f,
                lightPurplePaint
            )

            canvas.drawRoundRect(
                box,
                12f,
                12f,
                borderPaint
            )

            canvas.drawText(
                strings.conversationType,
                LEFT_MARGIN + 16f,
                y + 21f,
                headingPaint
            )

            canvas.drawText(
                conversationType,
                LEFT_MARGIN + 16f,
                y + 45f,
                bodyPaint
            )

            val dateLocale = if (appLanguage == "tr") {
                Locale("tr", "TR")
            } else {
                Locale.ENGLISH
            }

            val date = SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                dateLocale
            ).format(Date())

            val dateLabelWidth =
                headingPaint.measureText(strings.reportDate)

            val dateWidth =
                bodyPaint.measureText(date)

            canvas.drawText(
                strings.reportDate,
                PAGE_WIDTH -
                        RIGHT_MARGIN -
                        16f -
                        dateLabelWidth,
                y + 21f,
                headingPaint
            )

            canvas.drawText(
                date,
                PAGE_WIDTH -
                        RIGHT_MARGIN -
                        16f -
                        dateWidth,
                y + 45f,
                bodyPaint
            )

            y += 84f
        }

        fun drawScoreSection(
            score: String
        ) {
            ensureSpace(150f)

            val numericScore = score
                .filter { it.isDigit() }
                .toIntOrNull()
                ?.coerceIn(0, 100)
                ?: 0

            drawSectionHeader(
                title = strings.communicationScore
            )

            val scoreBox = RectF(
                LEFT_MARGIN,
                y,
                PAGE_WIDTH - RIGHT_MARGIN,
                y + 100f
            )

            canvas.drawRoundRect(
                scoreBox,
                14f,
                14f,
                lightGrayPaint
            )

            canvas.drawRoundRect(
                scoreBox,
                14f,
                14f,
                borderPaint
            )

            val centerX = PAGE_WIDTH / 2f

            canvas.drawText(
                numericScore.toString(),
                centerX,
                y + 48f,
                scorePaint
            )

            val outOfPaint = Paint(
                Paint.ANTI_ALIAS_FLAG
            ).apply {
                color = Color.rgb(100, 100, 112)
                textSize = 10f
                textAlign = Paint.Align.CENTER
            }

            canvas.drawText(
                "/ 100",
                centerX,
                y + 65f,
                outOfPaint
            )

            val progressLeft = LEFT_MARGIN + 45f
            val progressTop = y + 74f
            val progressWidth = CONTENT_WIDTH - 90f
            val progressHeight = 12f

            val trackPaint = Paint(
                Paint.ANTI_ALIAS_FLAG
            ).apply {
                color = Color.rgb(220, 220, 231)
            }

            canvas.drawRoundRect(
                RectF(
                    progressLeft,
                    progressTop,
                    progressLeft + progressWidth,
                    progressTop + progressHeight
                ),
                6f,
                6f,
                trackPaint
            )

            val filledWidth =
                progressWidth * (numericScore / 100f)

            if (filledWidth > 0f) {
                canvas.drawRoundRect(
                    RectF(
                        progressLeft,
                        progressTop,
                        progressLeft + filledWidth,
                        progressTop + progressHeight
                    ),
                    6f,
                    6f,
                    purplePaint
                )
            }

            val scoreLabel = if (appLanguage == "tr") {
                when {
                    numericScore >= 80 ->
                        "Güçlü iletişim"

                    numericScore >= 60 ->
                        "Genel olarak olumlu iletişim"

                    numericScore >= 40 ->
                        "İletişim dikkat gerektiriyor"

                    else ->
                        "Yüksek iletişim riski"
                }
            } else {
                when {
                    numericScore >= 80 ->
                        "Strong communication"

                    numericScore >= 60 ->
                        "Generally positive communication"

                    numericScore >= 40 ->
                        "Communication needs attention"

                    else ->
                        "High communication risk"
                }
            }

            val scoreLabelWidth =
                smallPaint.measureText(scoreLabel)

            canvas.drawText(
                scoreLabel,
                (PAGE_WIDTH - scoreLabelWidth) / 2f,
                y + 96f,
                smallPaint
            )

            y += 116f
        }

        fun drawToxicitySection(
            toxicity: String
        ) {
            ensureSpace(98f)

            drawSectionHeader(
                title = strings.toxicityLevel
            )

            val cleanToxicity =
                cleanPdfText(toxicity).uppercase(
                    Locale.getDefault()
                )

            val badgeColor = when {
                cleanToxicity.contains(
                    "YÜKSEK",
                    ignoreCase = true
                ) || cleanToxicity.contains(
                    "HIGH",
                    ignoreCase = true
                ) -> Color.rgb(255, 218, 224)

                cleanToxicity.contains(
                    "ORTA",
                    ignoreCase = true
                ) || cleanToxicity.contains(
                    "MEDIUM",
                    ignoreCase = true
                ) || cleanToxicity.contains(
                    "MODERATE",
                    ignoreCase = true
                ) -> Color.rgb(255, 239, 194)

                cleanToxicity.contains(
                    "DÜŞÜK",
                    ignoreCase = true
                ) || cleanToxicity.contains(
                    "LOW",
                    ignoreCase = true
                ) -> Color.rgb(220, 247, 231)

                else -> Color.rgb(235, 235, 242)
            }

            val badgePaint = Paint(
                Paint.ANTI_ALIAS_FLAG
            ).apply {
                color = badgeColor
            }

            val badgeTextPaint = Paint(
                Paint.ANTI_ALIAS_FLAG
            ).apply {
                color = Color.rgb(35, 35, 45)
                textSize = 11f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(
                    "sans-serif",
                    Typeface.BOLD
                )
            }

            val badgeWidth =
                maxOf(
                    110f,
                    badgeTextPaint.measureText(
                        cleanToxicity
                    ) + 38f
                )

            val badgeLeft =
                (PAGE_WIDTH - badgeWidth) / 2f

            val badgeRect = RectF(
                badgeLeft,
                y,
                badgeLeft + badgeWidth,
                y + 40f
            )

            canvas.drawRoundRect(
                badgeRect,
                20f,
                20f,
                badgePaint
            )

            canvas.drawText(
                cleanToxicity,
                PAGE_WIDTH / 2f,
                y + 25f,
                badgeTextPaint
            )

            y += 58f
        }

        fun drawSection(
            title: String,
            content: String
        ) {
            val cleanedContent =
                cleanPdfText(content)

            val lines = wrapText(
                text = cleanedContent,
                maxWidth = CONTENT_WIDTH - 28f,
                paint = bodyPaint
            )

            val lineCount =
                maxOf(1, lines.size)

            val requiredHeight =
                38f + 22f + (lineCount * 15f) + 18f

            ensureSpace(requiredHeight)

            drawSectionHeader(title)

            val cardTop = y
            val cardHeight =
                maxOf(
                    50f,
                    23f + (lineCount * 15f) + 15f
                )

            val card = RectF(
                LEFT_MARGIN,
                cardTop,
                PAGE_WIDTH - RIGHT_MARGIN,
                cardTop + cardHeight
            )

            canvas.drawRoundRect(
                card,
                10f,
                10f,
                lightGrayPaint
            )

            canvas.drawRoundRect(
                card,
                10f,
                10f,
                borderPaint
            )

            y = cardTop + 22f

            if (lines.isEmpty()) {
                drawBodyLine("-")
            } else {
                lines.forEach { line ->
                    drawBodyLine(line)
                }
            }

            y = card.bottom + 16f
        }

        private fun drawSectionHeader(
            title: String
        ) {
            ensureSpace(38f)

            val headerBox = RectF(
                LEFT_MARGIN,
                y,
                PAGE_WIDTH - RIGHT_MARGIN,
                y + 27f
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
                y + 18f,
                whiteHeadingPaint
            )

            y += 35f
        }

        private fun drawBodyLine(
            line: String
        ) {
            canvas.drawText(
                line,
                LEFT_MARGIN + 14f,
                y,
                bodyPaint
            )

            y += 15f
        }

        private fun ensureSpace(
            requiredHeight: Float
        ) {
            if (
                y + requiredHeight >= BOTTOM_LIMIT
            ) {
                startPage(
                    isFirstPage = false
                )
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
                strings.generatedBy,
                LEFT_MARGIN,
                FOOTER_TOP + 16f,
                footerPaint
            )

            canvas.drawText(
                strings.confidential,
                LEFT_MARGIN,
                FOOTER_TOP + 29f,
                footerPaint
            )

            val pageText =
                "${strings.page} $pageNumber"

            val pageTextWidth =
                footerPaint.measureText(pageText)

            canvas.drawText(
                pageText,
                PAGE_WIDTH -
                        RIGHT_MARGIN -
                        pageTextWidth,
                FOOTER_TOP + 16f,
                footerPaint
            )

            val warningWidth =
                footerPaint.measureText(strings.warning)

            canvas.drawText(
                strings.warning,
                PAGE_WIDTH -
                        RIGHT_MARGIN -
                        warningWidth,
                FOOTER_TOP + 29f,
                footerPaint
            )
        }

        private fun wrapText(
            text: String,
            maxWidth: Float,
            paint: Paint
        ): List<String> {
            val output =
                mutableListOf<String>()

            text.lines().forEach { paragraph ->
                val cleanParagraph =
                    paragraph.trim()

                if (cleanParagraph.isBlank()) {
                    output.add("")
                    return@forEach
                }

                val words = cleanParagraph.split(
                    Regex("\\s+")
                )

                var currentLine = ""

                words.forEach { word ->
                    val testLine =
                        if (currentLine.isBlank()) {
                            word
                        } else {
                            "$currentLine $word"
                        }

                    if (
                        paint.measureText(testLine) <=
                        maxWidth
                    ) {
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

        private fun normalizePhone(
            phone: String
        ): String {
            val clean = phone.trim()

            return when {
                clean.startsWith("0090") ->
                    "+90" + clean.removePrefix("0090")

                clean.startsWith("00 90") ->
                    "+90" + clean.removePrefix("00 90")

                else -> clean
            }
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
        conversationType: String,
        appLanguage: String
    ): String {
        val cleaned = cleanPdfText(
            conversationType
        ).trim()

        if (cleaned.isBlank()) {
            return if (appLanguage == "tr") {
                "Diğer"
            } else {
                "Other"
            }
        }

        return if (appLanguage == "tr") {
            when {
                cleaned.contains(
                    "Relationship",
                    ignoreCase = true
                ) -> "İlişki"

                cleaned.contains(
                    "Friend",
                    ignoreCase = true
                ) -> "Arkadaş"

                cleaned.contains(
                    "Family",
                    ignoreCase = true
                ) -> "Aile"

                cleaned.contains(
                    "Boss",
                    ignoreCase = true
                ) -> "Yönetici"

                cleaned.contains(
                    "Customer",
                    ignoreCase = true
                ) -> "Müşteri"

                cleaned.contains(
                    "Other",
                    ignoreCase = true
                ) -> "Diğer"

                else -> cleaned
            }
        } else {
            cleaned
        }
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
            .filter { it != sectionStart }
            .map {
                text.indexOf(
                    it,
                    contentStart
                )
            }
            .filter { it != -1 }
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
                val source =
                    ImageDecoder.createSource(
                        context.contentResolver,
                        uri
                    )

                ImageDecoder.decodeBitmap(
                    source
                ) { decoder, _, _ ->
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
        } catch (exception: Exception) {
            null
        }
    }
}