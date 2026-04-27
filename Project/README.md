# TailyTask 🦊

**TailyTask** คือแอปพลิเคชันบริหารจัดการงาน (Task Management) บนระบบปฏิบัติการ Android ที่ออกแบบมาให้ใช้งานง่าย รวดเร็ว และสวยงามแบบ Modern Minimalist พร้อมด้วยระบบ **AI Fast Record** ที่ช่วยเปลี่ยนข้อความธรรมดาของคุณให้กลายเป็น Task ที่ถูกจัดหมวดหมู่และกำหนดเวลาได้อย่างแม่นยำ

แอปนี้สร้างขึ้นเพื่อเน้นการเพิ่มประสิทธิภาพ (Productivity) และสร้างแรงจูงใจในการทำงานผ่านระบบคะแนน (Gamification)

<img width="693" height="682" alt="Wireframe drawio" src="https://github.com/user-attachments/assets/c89e8120-4d6e-41e0-910c-dbd58cf4ab88" />

---

## 🛠 Tech Stack

โปรเจกต์นี้พัฒนาด้วยเทคโนโลยีและเครื่องมือที่ทันสมัยของฝั่ง Android Development:

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material Design 3)
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Local Database:** Room Database
*   **Asynchronous Programming:** Kotlin Coroutines & Flow
*   **AI Integration:** Google Gemini API (Generative AI)
*   **Widget:** Jetpack Glance สำหรับแสดงผล Home Screen Widget
*   **Date/Time Processing:** `java.util.Calendar` & Custom Parsers

---

## ✨ Features (ความสามารถหลัก)

1. **Dashboard & Summary**
   * ดูภาพรวมงานทั้งหมด พร้อมคำนวณสัดส่วนงานที่เสร็จแล้ว (Progress Ring)
   * แยกแสดงงานตามหมวดหมู่ (Category: Work, Personal, Shopping, Study, Health, Other)
   * แสดงป้ายเตือนงานที่ล่าช้า (Overdue) 
2. **AI Fast Record**
   * เพิ่มงานแบบชาญฉลาดผ่านการพิมพ์เพียงประโยคเดียว (เช่น *"พรุ่งนี้บ่าย 2 ประชุมทีมด่วน"*)
   * ข้อมูลจะถูกประมวลผลผ่าน Gemini AI เพื่อแยก ชื่อ, วันที่, เวลา, หมวดหมู่ และระดับความสำคัญ (Priority) ให้อัตโนมัติ
   * มี Fallback Mock Parser ที่พัฒนาด้วย Regex ภาษาไทยรองรับกรณีทำงานแบบ Offline หรือเชื่อมต่อ AI ไม่ได้
3. **Projects & Gantt Calendar**
   * จัดการโปรเจกต์ใหญ่และมี Subtask ย่อยๆ
   * **Gantt Calendar View:** หน้าปฏิทินที่แสดงผล Task/Project แบบ Time-block สามารถแตะหรือลากเพื่อเลื่อนวัน (Drag and Drop) ได้ทันที
4. **Gamification (Shop & Points)**
   * สะสมคะแนนจากการทำงานเสร็จ (Task Points)
   * นำคะแนนไปแลกปลดล็อก Theme สีต่างๆ ในแอป
5. **Home Screen Widget**
   * แสดงสรุปงานที่ต้องทำในวันนี้ผ่าน Widget ด้วย `androidx.glance` โดยไม่ต้องเปิดเข้าแอป

---

## 🗄 Database (ฐานข้อมูล)

โปรเจกต์นี้ใช้ **Room Database** ในการเก็บข้อมูลทั้งหมดไว้ในเครื่อง (Local Storage) เพื่อความเป็นส่วนตัวและความเร็วในการเข้าถึงข้อมูล:

1. **TaskEntity:** เก็บข้อมูลงานรายย่อย (ชื่อ, รายละเอียด, วันที่, เวลา, หมวดหมู่, คะแนน, ความสำคัญ, สถานะเสร็จสิ้น)
2. **ProjectEntity:** เก็บข้อมูลโปรเจกต์ใหญ่ (ชื่อ, สี, วันที่เริ่ม, วันที่สิ้นสุด)
3. **SubtaskEntity:** เก็บงานย่อยที่ผูกกับ `ProjectEntity`
4. **ThemeEntity:** เก็บข้อมูล Theme ที่ผู้ใช้กดปลดล็อกจาก Shop 

---

## 🚀 วิธีการติดตั้งและรันโปรเจกต์ (Getting Started)

1. **Clone Repository:** โคลนโปรเจกต์นี้ลงเครื่องของคุณ
2. **API Key Setup:** นำ API Key ของ Google Gemini มาใส่ที่ไฟล์ `local.properties` ในระดับ Root ของโปรเจกต์:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```
3. **Sync & Build:** เปิดโปรเจกต์ด้วย Android Studio กดยอมรับการโหลด Gradle และรันแอปพลิเคชันบน Emulator หรือเครื่องจริง (Android 8.0 Oreo ขึ้นไป)

---

## 📌 หมายเหตุ (Notes)
- การประมวลผลภาษาไทยของ AI Fast Record อาศัยความเข้าใจ Context ผ่าน Prompt Tuning พิเศษ หาก AI ไม่สามารถตอบกลับได้ ระบบจะสลับไปใช้ Offline Regex Scanner แทนเพื่อไม่ให้เสียจังหวะการทำงาน
