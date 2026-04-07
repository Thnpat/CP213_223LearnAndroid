package com.example.a223lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a223lablearnandroid.ui.theme._223LabLearnAndroidTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ErrorViewModel : ViewModel() {
    // Channel is perfect for one-time events (like showing a Snackbar)
    private val _errorChannel = Channel<String>(Channel.BUFFERED)
    val errorFlow = _errorChannel.receiveAsFlow()

    fun triggerError() {
        viewModelScope.launch {
            _errorChannel.send("เกิดข้อผิดพลาดในการเชื่อมต่อเซิร์ฟเวอร์! (Error 500)")
        }
    }
}

class Part5Activity : ComponentActivity() {
    private val viewModel: ErrorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _223LabLearnAndroidTheme {
                Part5Screen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun Part5Screen(viewModel: ErrorViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Side Effect: เมื่อ Component ถูกสร้าง จะเริ่มดักฟัง Flow จาก ViewModel ทันที
    LaunchedEffect(Unit) {
        viewModel.errorFlow.collect { errorMessage ->
            // เมื่อได้รับข้อความ จะแสดง Snackbar
            snackbarHostState.showSnackbar(message = errorMessage)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Part 5: Side Effects",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 32.dp, bottom = 32.dp)
            )

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Button(onClick = { viewModel.triggerError() }) {
                    Text("Trigger Error", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Part5ScreenPreview() {
    _223LabLearnAndroidTheme {
        Part5Screen(viewModel = ErrorViewModel())
    }
}
