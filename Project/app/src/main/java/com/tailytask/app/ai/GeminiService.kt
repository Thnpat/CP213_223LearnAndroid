package com.tailytask.app.ai

import com.tailytask.app.model.Category
import com.tailytask.app.model.Priority
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * GeminiService — AI Fast Record
 *
 * ===== วิธีเชื่อมต่อ Gemini API จริง =====
 *
 * 1. ไปที่ https://aistudio.google.com/apikey แล้วสร้าง API Key
 *
 * 2. เพิ่ม dependency ใน app/build.gradle.kts:
 *    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
 *
 * 3. เก็บ API Key ใน local.properties (อย่า commit ขึ้น git!):
 *    GEMINI_API_KEY=your_api_key_here
 *
 * 4. อ่าน API Key ใน build.gradle.kts:
 *    android {
 *        defaultConfig {
 *            val props = java.util.Properties()
 *            props.load(rootProject.file("local.properties").inputStream())
 *            buildConfigField("String", "GEMINI_API_KEY", "\"${props["GEMINI_API_KEY"]}\"")
 *        }
 *        buildFeatures { buildConfig = true }
 *    }
 *
 * 5. ใช้ใน code:
 *    import com.google.ai.client.generativeai.GenerativeModel
 *
 *    val model = GenerativeModel(
 *        modelName = "gemini-pro",
 *        apiKey = BuildConfig.GEMINI_API_KEY
 *    )
 *
 *    val prompt = """
 *    วิเคราะห์ข้อความต่อไปนี้และแปลงเป็น JSON:
 *    "$userInput"
 *
 *    ตอบเป็น JSON format นี้เท่านั้น:
 *    {
 *      "title": "ชื่องาน",
 *      "description": "รายละเอียด",
 *      "dueDate": "yyyy-MM-dd",
 *      "startTime": "HH:mm",
 *      "endTime": "HH:mm",
 *      "category": "WORK|PERSONAL|SHOPPING|STUDY|HEALTH|OTHER",
 *      "priority": "LOW|MEDIUM|HIGH"
 *    }
 *    """.trimIndent()
 *
 *    val response = model.generateContent(prompt)
 *    // Parse JSON response...
 *
 * ===== ตอนนี้ใช้ Mock Parser (ไม่ต้องใช้ API Key) =====
 */

data class ParsedTask(
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val category: String = Category.PERSONAL.name,
    val priority: String = Priority.MEDIUM.name
)

class GeminiService {

    /**
     * วิเคราะห์ข้อความภาษาธรรมชาติแล้วสร้าง ParsedTask
     * ตอนนี้เป็น Mock — ใช้ keyword matching แทน AI จริง
     * เมื่อมี API Key แล้ว ให้เปลี่ยนเป็นเรียก Gemini API ตาม instructions ด้านบน
     */
    fun parseNaturalLanguage(input: String): ParsedTask {
        val lowerInput = input.lowercase(Locale.getDefault())

        // --- Parse Date ---
        val calendar = Calendar.getInstance()
        val dueDate: Long? = when {
            lowerInput.contains("พรุ่งนี้") || lowerInput.contains("tomorrow") -> {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.timeInMillis
            }
            lowerInput.contains("มะรืน") || lowerInput.contains("day after tomorrow") -> {
                calendar.add(Calendar.DAY_OF_YEAR, 2)
                calendar.timeInMillis
            }
            lowerInput.contains("วันนี้") || lowerInput.contains("today") -> {
                calendar.timeInMillis
            }
            lowerInput.contains("อาทิตย์หน้า") || lowerInput.contains("next week") -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.timeInMillis
            }
            else -> calendar.timeInMillis // default = วันนี้
        }

        // --- Parse Time ---
        val timeRegex = Regex("(\\d{1,2})[:.](\\d{2})")
        val thaiTimeRegex = Regex("(\\d{1,2})\\s*โมง")
        val baiRegex = Regex("บ่าย\\s*(\\d{1,2})")

        var startTime: String? = null
        var endTime: String? = null

        val timeMatches = timeRegex.findAll(input).toList()
        if (timeMatches.isNotEmpty()) {
            val h = timeMatches[0].groupValues[1].padStart(2, '0')
            val m = timeMatches[0].groupValues[2]
            startTime = "$h:$m"
            if (timeMatches.size > 1) {
                val h2 = timeMatches[1].groupValues[1].padStart(2, '0')
                val m2 = timeMatches[1].groupValues[2]
                endTime = "$h2:$m2"
            }
        } else {
            val baiMatch = baiRegex.find(lowerInput)
            val thaiMatch = thaiTimeRegex.find(lowerInput)
            if (baiMatch != null) {
                val hour = baiMatch.groupValues[1].toInt() + 12
                startTime = "${hour.toString().padStart(2, '0')}:00"
            } else if (thaiMatch != null) {
                val hour = thaiMatch.groupValues[1].toInt()
                val adjustedHour = if (hour in 1..6 && lowerInput.contains("เย็น")) hour + 12
                                   else if (hour in 1..5 && !lowerInput.contains("เช้า")) hour + 12
                                   else hour
                startTime = "${adjustedHour.toString().padStart(2, '0')}:00"
            }
        }

        // --- Parse Category ---
        val category = when {
            lowerInput.contains("ประชุม") || lowerInput.contains("meeting") ||
            lowerInput.contains("ทำงาน") || lowerInput.contains("work") ||
            lowerInput.contains("ออฟฟิศ") || lowerInput.contains("office") -> Category.WORK.name
            lowerInput.contains("ซื้อ") || lowerInput.contains("shop") ||
            lowerInput.contains("ตลาด") || lowerInput.contains("buy") -> Category.SHOPPING.name
            lowerInput.contains("เรียน") || lowerInput.contains("สอบ") ||
            lowerInput.contains("study") || lowerInput.contains("exam") ||
            lowerInput.contains("การบ้าน") || lowerInput.contains("homework") -> Category.STUDY.name
            lowerInput.contains("ออกกำลัง") || lowerInput.contains("หมอ") ||
            lowerInput.contains("gym") || lowerInput.contains("health") ||
            lowerInput.contains("วิ่ง") || lowerInput.contains("run") -> Category.HEALTH.name
            else -> Category.PERSONAL.name
        }

        // --- Parse Priority ---
        val priority = when {
            lowerInput.contains("ด่วน") || lowerInput.contains("urgent") ||
            lowerInput.contains("สำคัญมาก") || lowerInput.contains("important") ||
            lowerInput.contains("ด่วนมาก") -> Priority.HIGH.name
            lowerInput.contains("สำคัญ") || lowerInput.contains("ประชุม") ||
            lowerInput.contains("สอบ") || lowerInput.contains("deadline") -> Priority.MEDIUM.name
            else -> Priority.LOW.name
        }

        // --- Extract Title ---
        // ลบคำที่เป็นวัน/เวลาออก เหลือเป็นชื่องาน
        var title = input.trim()
        listOf(
            "พรุ่งนี้", "มะรืน", "วันนี้", "อาทิตย์หน้า",
            "tomorrow", "today", "next week",
            "เช้า", "เย็น", "บ่าย"
        ).forEach { keyword ->
            title = title.replace(keyword, "", ignoreCase = true)
        }
        // Remove time patterns
        title = timeRegex.replace(title, "")
        title = thaiTimeRegex.replace(title, "")
        title = baiRegex.replace(title, "")
        title = title.replace(Regex("\\s+"), " ").trim()

        if (title.isBlank()) title = input.trim()

        return ParsedTask(
            title = title,
            description = "Created via Fast Record: \"$input\"",
            dueDate = dueDate,
            startTime = startTime,
            endTime = endTime,
            category = category,
            priority = priority
        )
    }
}
