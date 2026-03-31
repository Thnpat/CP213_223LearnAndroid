package com.example.a223lablearnandroid

import org.junit.Assert.assertEquals
import org.junit.Test

class SensorViewModelTest {

    @Test
    fun updateAccelerometerData_updatesState() {
        val viewModel = SensorViewModel()
        viewModel.updateAccelerometerData(1.0f, 2.0f, 3.0f)

        val data = viewModel.accelerometerData.value
        assertEquals(1.0f, data.x)
        assertEquals(2.0f, data.y)
        assertEquals(3.0f, data.z)
    }

    @Test
    fun updateLocationData_updatesState() {
        val viewModel = SensorViewModel()
        val testLocation = "13.7563, 100.5018"
        viewModel.updateLocationData(testLocation)

        assertEquals(testLocation, viewModel.locationData.value)
    }

    @Test
    fun initialState_isCorrect() {
        val viewModel = SensorViewModel()
        assertEquals(0f, viewModel.accelerometerData.value.x)
        assertEquals(0f, viewModel.accelerometerData.value.y)
        assertEquals(0f, viewModel.accelerometerData.value.z)
        assertEquals("Waiting for location...", viewModel.locationData.value)
    }
}