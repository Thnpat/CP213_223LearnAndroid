package com.example.a223lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a223lablearnandroid.ui.theme._223LabLearnAndroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactViewModel : ViewModel() {
    private val alphabets = ('A'..'Z').toList()
    private var page = 1

    private val _contacts = MutableStateFlow<List<String>>(generateInitialData())
    val contacts: StateFlow<List<String>> = _contacts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private fun generateInitialData(): List<String> {
        return getContactsForInitials(alphabets.take(3))
    }

    private fun getContactsForInitials(initials: List<Char>): List<String> {
        return initials.flatMap { initial ->
            (1..5).map { "$initial Name $it" }
        }
    }

    fun loadMore() {
        if (_isLoading.value) return
        _isLoading.value = true
        viewModelScope.launch {
            delay(2000) // หน่วงเวลา 2 วินาที
            val startIndex = page * 3
            val newInitials = alphabets.drop(startIndex).take(3)
            
            if (newInitials.isNotEmpty()) {
                val newContacts = getContactsForInitials(newInitials)
                _contacts.value = _contacts.value + newContacts
                page++
            }
            _isLoading.value = false
        }
    }
}

class Part2Activity : ComponentActivity() {
    private val viewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _223LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactListScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(viewModel: ContactViewModel, modifier: Modifier = Modifier) {
    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // จัดกลุ่มตามตัวอักษรตัวแรก
    val grouped = contacts.groupBy { it.first() }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        grouped.forEach { (initial, contactsForInitial) ->
            stickyHeader(key = "header_$initial") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = initial.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            items(
                items = contactsForInitial,
                key = { "item_$it" } // ใช้ item key เพื่อให้เกิด Animation นุ่มนวลตอนเพิ่ม/ลบ
            ) { contact ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = contact,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        item(key = "loading_trigger") {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Pagination Trigger
                LaunchedEffect(Unit) {
                    viewModel.loadMore()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactListPreview() {
    _223LabLearnAndroidTheme {
        ContactListScreen(viewModel = ContactViewModel())
    }
}