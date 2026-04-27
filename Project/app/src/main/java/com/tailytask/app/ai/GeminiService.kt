package com.tailytask.app.ai

import com.tailytask.app.BuildConfig
import com.tailytask.app.model.Category
import com.tailytask.app.model.Priority
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val hasApiKey = apiKey.isNotBlank()

    // Model instance (lazy init only if API key exists)
    private val model by lazy {
        if (hasApiKey) {
            try {
                com.google.ai.client.generativeai.GenerativeModel(
                    modelName = "gemini-2.0-flash",
                    apiKey = apiKey
                )
            } catch (e: Exception) {
                null
            }
        } else null
    }

    /**
     * Parse natural language input into a structured task.
     * Uses real Gemini API if API key is available, falls back to mock parser.
     */
    suspend fun parseNaturalLanguage(input: String): ParsedTask {
        // Try Gemini API first
        if (hasApiKey && model != null) {
            try {
                return parseWithGemini(input)
            } catch (e: Exception) {
                // Fall back to mock parser if API call fails
                e.printStackTrace()
            }
        }
        // Fallback: mock parser
        return mockParse(input)
    }

    private suspend fun parseWithGemini(input: String): ParsedTask {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Calendar.getInstance().time)

        val prompt = """
            วิเคราะห์ข้อความต่อไปนี้และแปลงเป็น task สำหรับ to-do app:
            "$input"
            
            วันนี้คือ $today
            **สำคัญ: หากผู้ใช้พิมพ์เป็นภาษาไทย ให้ตั้งชื่องาน (title) และรายละเอียด (description) เป็นภาษาไทย ห้ามแปลเป็นภาษาอังกฤษ**
            **สำคัญ: ถ้าผู้ใช้ระบุแค่วันที่ (เช่น 'วันที่ 29') ให้ใช้เดือนและปีจาก '$today' เพื่อสร้างวันที่ในรูปแบบ yyyy-MM-dd**
            
            ตอบเป็น JSON format นี้เท่านั้น (ไม่ต้องมี markdown):
            {
              "title": "ชื่องาน (สั้นกระชับ คงภาษาเดิมที่ผู้ใช้พิมพ์)",
              "description": "รายละเอียดเพิ่มเติม (ถ้าไม่มีให้ใส่ \"\")",
              "dueDate": "yyyy-MM-dd (ถ้าไม่มีให้ใส่ \"\")",
              "startTime": "HH:mm (ถ้าไม่มีให้ใส่ \"\")",
              "endTime": "HH:mm (ถ้าไม่มีให้ใส่ \"\")",
              "category": "WORK|PERSONAL|SHOPPING|STUDY|HEALTH|OTHER",
              "priority": "LOW|MEDIUM|HIGH"
            }
        """.trimIndent()

        val response = model!!.generateContent(prompt)
        val text = response.text ?: throw Exception("Empty response")

        // Extract JSON from response
        val jsonStr = text.replace("```json", "").replace("```", "").trim()

        val gson = com.google.gson.Gson()
        val map = gson.fromJson(jsonStr, Map::class.java)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dueDateStr = map["dueDate"] as? String
        val dueDate = if (!dueDateStr.isNullOrBlank() && dueDateStr != "null") {
            try { dateFormat.parse(dueDateStr)?.time } catch (e: Exception) { null }
        } else null

        val category = try {
            Category.valueOf(map["category"] as? String ?: "PERSONAL")
            map["category"] as String
        } catch (e: Exception) { Category.PERSONAL.name }

        val priority = try {
            Priority.valueOf(map["priority"] as? String ?: "MEDIUM")
            map["priority"] as String
        } catch (e: Exception) { Priority.MEDIUM.name }

        return ParsedTask(
            title = map["title"] as? String ?: input,
            description = map["description"] as? String ?: "",
            dueDate = dueDate,
            startTime = (map["startTime"] as? String)?.takeIf { it.isNotBlank() && it != "null" },
            endTime = (map["endTime"] as? String)?.takeIf { it.isNotBlank() && it != "null" },
            category = category,
            priority = priority
        )
    }

    /**
     * Mock parser — uses keyword matching (no API key needed)
     */
    private fun mockParse(input: String): ParsedTask {
        val lowerInput = input.lowercase(Locale.getDefault())

        // --- Parse Date ---
        val calendar = Calendar.getInstance()
        val dateRegex = Regex("วันที่\\s*(\\d{1,2})")
        val dateMatch = dateRegex.find(lowerInput)
        
        val dueDate: Long? = when {
            dateMatch != null -> {
                val day = dateMatch.groupValues[1].toInt()
                calendar.set(Calendar.DAY_OF_MONTH, day)
                if (calendar.timeInMillis < System.currentTimeMillis() - 86400000) {
                    calendar.add(Calendar.MONTH, 1) // If the date is past this month, assume next month
                }
                calendar.timeInMillis
            }
            lowerInput.contains("พรุ่งนี้") || lowerInput.contains("tomorrow") -> {
                calendar.add(Calendar.DAY_OF_YEAR, 1); calendar.timeInMillis
            }
            lowerInput.contains("มะรืน") || lowerInput.contains("day after tomorrow") -> {
                calendar.add(Calendar.DAY_OF_YEAR, 2); calendar.timeInMillis
            }
            lowerInput.contains("วันนี้") || lowerInput.contains("today") -> calendar.timeInMillis
            lowerInput.contains("อาทิตย์หน้า") || lowerInput.contains("next week") -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1); calendar.timeInMillis
            }
            else -> calendar.timeInMillis
        }

        // --- Parse Time ---
        val timeRegex = Regex("(\\d{1,2})[:.](\\d{2})")
        val thaiTimeRegex = Regex("(\\d{1,2})\\s*โมง")
        val baiRegex = Regex("บ่าย\\s*(\\d{1,2})")
        val thungRegex = Regex("(\\d{1,2})\\s*ทุ่ม")

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
            val thungMatch = thungRegex.find(lowerInput)
            val baiMatch = baiRegex.find(lowerInput)
            val thaiMatch = thaiTimeRegex.find(lowerInput)
            if (thungMatch != null) {
                val hour = thungMatch.groupValues[1].toInt() + 18
                startTime = "${hour.toString().padStart(2, '0')}:00"
            } else if (baiMatch != null) {
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
            lowerInput.contains("สำคัญมาก") || lowerInput.contains("important") -> Priority.HIGH.name
            lowerInput.contains("สำคัญ") || lowerInput.contains("ประชุม") ||
            lowerInput.contains("สอบ") || lowerInput.contains("deadline") -> Priority.MEDIUM.name
            else -> Priority.LOW.name
        }

        // --- Extract Title ---
        var title = input.trim()
        listOf("พรุ่งนี้", "มะรืน", "วันนี้", "อาทิตย์หน้า",
            "tomorrow", "today", "next week", "เช้า", "เย็น", "บ่าย"
        ).forEach { keyword ->
            title = title.replace(keyword, "", ignoreCase = true)
        }
        title = dateRegex.replace(title, "")
        title = timeRegex.replace(title, "")
        title = thaiTimeRegex.replace(title, "")
        title = baiRegex.replace(title, "")
        title = thungRegex.replace(title, "")
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
