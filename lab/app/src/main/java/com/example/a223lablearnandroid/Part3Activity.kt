package com.example.a223lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a223lablearnandroid.ui.theme._223LabLearnAndroidTheme

class Part3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _223LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Part3Screen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Part3Screen(modifier: Modifier = Modifier) {
    val proportions = listOf(30f, 40f, 20f, 10f)
    val colors = listOf(
        Color(0xFFE91E63), // Pink
        Color(0xFF2196F3), // Blue
        Color(0xFFFFC107), // Amber
        Color(0xFF4CAF50)  // Green
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Expense Summary",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        DonutChart(
            proportions = proportions,
            colors = colors,
            modifier = Modifier.size(250.dp)
        )
    }
}

@Composable
fun DonutChart(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }

    val sweepAngle by animateFloatAsState(
        targetValue = if (animationPlayed) 360f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
        label = "donut_chart_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val total = proportions.sum()

    Canvas(modifier = modifier) {
        var startAngle = -90f
        var currentSweepSum = 0f
        val strokeWidthDp = 40.dp
        
        for (i in proportions.indices) {
            val segmentAngle = (proportions[i] / total) * 360f
            
            // คำนวณขอบเขตการวาดโค้งตาม sweepAngle ปัจจุบันของ Animation
            val sweep = if (sweepAngle > currentSweepSum + segmentAngle) {
                segmentAngle
            } else if (sweepAngle > currentSweepSum) {
                sweepAngle - currentSweepSum
            } else {
                0f
            }

            if (sweep > 0f) {
                drawArc(
                    color = colors[i],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidthDp.toPx(), cap = StrokeCap.Butt)
                )
            }
            startAngle += segmentAngle
            currentSweepSum += segmentAngle
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DonutChartPreview() {
    _223LabLearnAndroidTheme {
        Part3Screen()
    }
}