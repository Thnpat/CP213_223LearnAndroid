package com.example.a223lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a223lablearnandroid.ui.theme._223LabLearnAndroidTheme

class Part12Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _223LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Part12Screen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Part12Screen(modifier: Modifier = Modifier) {
    // ชุดตัวแปรควบคุม State แบบ Dialog ปกติ
    var showDialog by remember { mutableStateOf(false) }
    
    // ชุดตัวแปรควบคุม State ของ Bottom Sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mission 12: Dialogs & Bottom Sheet",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Concept การแจ้งเตือน", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Text(
                    text = "- Middle Dialog (AlertDialog): กะพริบขึ้นกึ่งกลาง มักใช้ในการยืนยัน ตัดสินใจ หรือแจ้งเตือนขั้นเด็ดขาด (Interruptive) บังคับให้ผู้ใช้ต้องกดตอบโต้ก่อนไปต่อ\n\n- Modal Bottom Sheet: สไลด์ขึ้นมาจากด้านล่าง มีข้อดีกว่าตรงที่เอื้อมนิ้วโป้งกดได้ง่าย (Thumb-friendly) และใส่ข้อมูลยาวๆ ให้ Scroll ได้ มักใช้โชว์เมนูย่อยหรือฟิลเตอร์การค้นหา",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(onClick = { showDialog = true }, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("แสดง AlertDialog (ตรงกลาง)")
        }

        Button(onClick = { showBottomSheet = true }) {
            Text("แสดง Modal Bottom Sheet (ล่าง)")
        }

        // Logic การกาง Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("ลบข้อมูล?") },
                text = { Text("คุณแน่ใจนะว่าต้องการทำการลบข้อมูลนี้ทิ้งจริงๆ ตัวเลือกนี้ไม่สามารถกู้คืนได้") },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("แน่นอน", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("ยกเลิก")
                    }
                }
            )
        }

        // Logic การกาง Bottom Sheet
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                // เนื้อหาในแผ่นชีทที่จะเลื่อนป๊อปอัพขึ้นมา
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                ) {
                    Text("ตัวเลือกแชร์ด่วน 🚀", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    Button(onClick = { showBottomSheet = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("ส่งเข้า Line")
                    }
                    Button(onClick = { showBottomSheet = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("ส่งเข้า Facebook")
                    }
                    Button(onClick = { showBottomSheet = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("สำเนาลิงก์ (Copy Link)")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part12ScreenPreview() {
    _223LabLearnAndroidTheme {
        Part12Screen()
    }
}
