package com.example.a223lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a223lablearnandroid.ui.theme._223LabLearnAndroidTheme

class Part10Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _223LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Part10Screen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Part10Screen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Mission 10: App Widget",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Concept ของ App Widget",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "App Widget คือกล่องข้อมูลขนาดเล็ก (Miniature Application Views) ที่สามารถนำไปฝังตัวอยู่บนหน้า Home Screen ให้ผู้ใช้มองเห็นสถานะเด่น ๆ เข้าถึงทางลัดได้ไว โดยไม่ต้องเปิดแอปหลัก",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "อุปสรรคของการทำ Widget: \nเนื่องจาก Widget รันอยู่บนโปรแกรมระบบหลัก (Launcher) ต่าง Process กับแอปของคุณ คุณเลยไม่สามารถใช้โค้ด UI ปกติไปวาดตรงๆ ได้ ต้องส่งผ่านสิ่งที่เรียกว่า 'RemoteViews'",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "ปัจจุบัน Android มีไลบรารีชื่อ 'Jetpack Glance' ที่อำนวยความสะดวกให้เราสามารถเขียน Widget โดยใช้ไวยากรณ์คล้ายหน้า Compose ได้เลย ไม่ต้องไปปวดหัวกับ XML RemoteViews แบบเดิมๆ อีกต่อไป",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Mock UI วาดรูป Widget เลียนแบบ
        Text(text = "ตัวอย่างภาพการทำงาน (Mockup)", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFFE3F2FD), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("☀️ 32°C", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                Text("Bangkok, Thailand", color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part10ScreenPreview() {
    _223LabLearnAndroidTheme {
        Part10Screen()
    }
}
