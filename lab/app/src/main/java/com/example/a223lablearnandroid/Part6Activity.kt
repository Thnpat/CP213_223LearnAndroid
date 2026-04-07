package com.example.a223lablearnandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import com.example.a223lablearnandroid.ui.theme._223LabLearnAndroidTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WebViewModel : ViewModel() {
    private val _url = MutableStateFlow("https://www.google.com")
    val url: StateFlow<String> = _url.asStateFlow()

    fun updateUrl(newUrl: String) {
        // เพิ่ม http:// อัตโนมัติถ้าไม่ได้พิมพ์มา
        val validUrl = if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
            "https://$newUrl"
        } else {
            newUrl
        }
        _url.value = validUrl
    }
}

class Part6Activity : ComponentActivity() {
    private val viewModel: WebViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _223LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Part6Screen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Part6Screen(viewModel: WebViewModel, modifier: Modifier = Modifier) {
    val currentUrl by viewModel.url.collectAsState()
    var inputText by remember { mutableStateOf(currentUrl) }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Part 6: View Interoperability",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                label = { Text("Enter URL") },
                singleLine = true
            )
            
            Button(
                onClick = { viewModel.updateUrl(inputText) },
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text("Go")
            }
        }

        // AndroidView component นำ WebView จาก XML มาใช้งานใน Compose
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                // บล็อก factory จะถูกเรียกแค่ครั้งเดียวตอนที่ Component เกิดขึ้น
                // ทำการดึง WebView มาจากไฟล์ XML (layout_webview.xml) ที่เพิ่งสร้าง
                val webView = LayoutInflater.from(context).inflate(R.layout.layout_webview, null, false) as WebView
                
                webView.apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient() // ป้องกันไม่ให้แอปเด้งออกไปเปิด Chrome ข้างนอก
                }
            },
            update = { webView ->
                // บล็อก update จะถูกเรียกใหม่เมื่อ State หรือ Data ที่ใช้งานมีการเปลี่ยนแปลง (currentUrl)
                if (webView.url != currentUrl) {
                    webView.loadUrl(currentUrl)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Part6ScreenPreview() {
    _223LabLearnAndroidTheme {
        Part6Screen(viewModel = WebViewModel())
    }
}
