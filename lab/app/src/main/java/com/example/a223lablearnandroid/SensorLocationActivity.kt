package com.example.a223lablearnandroid

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SensorLocationActivity : ComponentActivity() {

    private val viewModel: SensorViewModel by viewModels()
    private lateinit var sensorTracker: SensorTracker
    private lateinit var locationTracker: LocationTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sensorTracker = SensorTracker(this) { x, y, z ->
            viewModel.updateAccelerometerData(x, y, z)
        }
        
        locationTracker = LocationTracker(this) { location ->
            viewModel.updateLocationData(location)
        }

        setContent {
            val accelerometerValue by viewModel.accelerometerData.collectAsState()
            val locationValue by viewModel.locationData.collectAsState()

            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
                if (fineLocationGranted || coarseLocationGranted) {
                    locationTracker.startTracking()
                } else {
                    viewModel.updateLocationData("Permission Denied")
                }
            }

            DisposableEffect(Unit) {
                sensorTracker.startTracking()
                onDispose {
                    sensorTracker.stopTracking()
                    locationTracker.stopTracking()
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sensor & Location MVVM", fontSize = 24.sp)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("Accelerometer Value:")
                Text("X: ${String.format("%.2f", accelerometerValue.x)}", fontSize = 24.sp)
                Text("Y: ${String.format("%.2f", accelerometerValue.y)}", fontSize = 24.sp)
                Text("Z: ${String.format("%.2f", accelerometerValue.z)}", fontSize = 24.sp)

                Spacer(modifier = Modifier.height(32.dp))
                
                Text("Location:")
                Text(locationValue, fontSize = 20.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }) {
                    Text("Start Location Tracking")
                }
            }
        }
    }
}
