package com.example.a223lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a223lablearnandroid.ui.theme._223LabLearnAndroidTheme

class Part8Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _223LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Part8Screen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Part8Screen(modifier: Modifier = Modifier) {
    // หัวใจของ Adaptive Layout คือ BoxWithConstraints ที่สามารถวัดขนาดพื้นที่หน้าจอได้จริง
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (maxWidth < 600.dp) {
            // โหมดมือถือ / แนวตั้ง (Mobile / Portrait)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ProfilePicture(modifier = Modifier.size(200.dp))
                Spacer(modifier = Modifier.height(24.dp))
                ProfileDetails()
            }
        } else {
            // โหมดแท็บเล็ต / แนวนอน (Tablet / Landscape)
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                ProfilePicture(modifier = Modifier.size(250.dp).padding(end = 32.dp))
                Box(modifier = Modifier.weight(1f)) {
                    ProfileDetails()
                }
            }
        }
    }
}

@Composable
fun ProfilePicture(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // ให้เป็นสี่เหลี่ยมจัตุรัสเสมอ
            .clip(CircleShape) // ตัดเป็นวงกลม
            .background(Color.LightGray)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Picture",
            tint = Color.DarkGray,
            modifier = Modifier.fillMaxSize(0.6f)
        )
    }
}

@Composable
fun ProfileDetails() {
    Column {
        Text(
            text = "John Doe",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Senior Android Developer",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "ประสบการณ์กว่า 10 ปีในการพัฒนาแอปพลิเคชันบนระบบปฏิบัติการ Android มีความเชี่ยวชาญในภาษา Kotlin, สถาปัตยกรรม MVVM และ Jetpack Compose อย่างลึกซึ้ง ชอบเรียนรู้เทคโนโลยีใหม่ๆ และปรับปรุงประสิทธิภาพของแอปพลิเคชันให้ลื่นไหลอยู่เสมอ",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun Part8ScreenPreviewMobile() {
    _223LabLearnAndroidTheme {
        Part8Screen()
    }
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun Part8ScreenPreviewTablet() {
    _223LabLearnAndroidTheme {
        Part8Screen()
    }
}
