package com.example.a223lablearnandroid

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AccelerometerData(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f)

class SensorViewModel : ViewModel() {
    private val _accelerometerData = MutableStateFlow(AccelerometerData())
    val accelerometerData: StateFlow<AccelerometerData> = _accelerometerData.asStateFlow()

    private val _locationData = MutableStateFlow("Waiting for location...")
    val locationData: StateFlow<String> = _locationData.asStateFlow()

    fun updateAccelerometerData(x: Float, y: Float, z: Float) {
        _accelerometerData.value = AccelerometerData(x, y, z)
    }

    fun updateLocationData(location: String) {
        _locationData.value = location
    }
}
