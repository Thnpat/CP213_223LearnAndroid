package com.example.a223lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a223lablearnandroid.ui.theme._223LabLearnAndroidTheme

class Part9Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _223LabLearnAndroidTheme {
                Part9Screen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Part9Screen() {
    // กำหนดรูปแบบการเลื่อนของ TopAppBar (Scroll Behavior)
    // exitUntilCollapsedScrollBehavior = เลื่อนจอขึ้นแล้วหดเหลือขนาดเล็กสุด แต่ไม่ให้หายไปหมด
    // หากใช้ enterAlwaysScrollBehavior จะเป็นการซ่อน Toolbar หายไปเลยทั้งชิ้นเมื่อเลื่อนลง
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        // หัวใจสำคัญ: เชื่อมต่อการเลื่อนของเนื้อหาและ TopAppBar เข้าด้วยกัน
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Collapsing TopBar") },
                navigationIcon = {
                    IconButton(onClick = { /* จำลองปุ่ม Back */ }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* จำลองปุ่ม Share */ }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { /* จำลองปุ่ม More */ }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Concept ของ Collapsing Toolbar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            text = "Collapsing TopBar ใน Jetpack Compose เป็นการซ่อนหรือ 'ยุบ' แถบด้านบน (Toolbar) เมื่อผู้ใช้ทำการเลื่อนอ่านเนื้อหาลง (Scroll Down) และ 'ขยาย' กลับมาเมื่อเลื่อนหน้าจอขึ้น (Scroll Up) เพื่อช่วยเพิ่มพื้นที่การอ่านเนื้อหาให้กว้างที่สุด",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            text = "การทำแบบนี้ใน Android XML เดิมยุ่งยากมาก (ต้องใช้ CoordinatorLayout + AppBarLayout) แต่ใน Compose เราใช้แค่ 'nestedScrollConnection' เชื่อม Scaffold กับ LazyColumn ด้วยกันแค่นั่นเอง! แรงต้านจากนิ้วเมื่อสัมผัสลากจอจะทำการปรับขนาดของ LargeTopAppBar โดยอัตโนมัติ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // สร้างไอเท็มขึ้นมาจำลองเพื่อให้หน้าจอมีความยาวจนสามารถ Scroll ได้
            items(20) { index ->
                ListItem(
                    headlineContent = { Text("รายการทดสอบที่ ${index + 1}") },
                    supportingContent = { Text("ลองใช้นิ้วปัดจอ Scroll เพื่อดูผลลัพธ์ของ Top Bar ด้านบน") },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part9ScreenPreview() {
    _223LabLearnAndroidTheme {
        Part9Screen()
    }
}
