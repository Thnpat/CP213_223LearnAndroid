package com.example.a223lablearnandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.w3c.dom.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            RPGCardView()
        }
    }

    @Composable
    fun RPGCardView() {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
            .padding(32.dp)
        ) {
            // hp
            Box (modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(color = Color.White)
                .padding(2.dp)
            ) {
                Text(
                    text = "hp",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterStart)
                        .fillMaxWidth(fraction = 0.223f)
                        .background(color = Color.Red)
                        .padding(6.dp)
                )

            }
            // image
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(450.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
                    .clickable {
                        startActivity(Intent(this@MainActivity,ListActivity::class.java))
                    }
            )

            var str by remember { mutableStateOf(0) }
            var agi by remember { mutableStateOf(0) }
            var int by remember { mutableStateOf(0) }

            // status
            Row(modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = {
                        str = str + 1
                    })  {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_arrow_drop_up_24),
                            contentDescription = "Up",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                    Text(text = "STR", fontSize = 32.sp)
                    Text(text = str.toString(), fontSize = 32.sp)
                    IconButton(onClick = {
                        str = str - 1
                    })  {
                        Image(
                            painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                            contentDescription = "Down",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = {
                        agi += 1
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_arrow_drop_up_24),
                            contentDescription = "Up",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                    Text(text = "AGI", fontSize = 32.sp)
                    Text(text = agi.toString(), fontSize = 32.sp)
                    IconButton (onClick = {
                        agi -= 1
                    })  {
                        Image(
                            painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                            contentDescription = "Down",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = {
                        int += 1
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_arrow_drop_up_24),
                            contentDescription = "Up",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                    Text(text = "INT", fontSize = 32.sp)
                    Text(text = int.toString(), fontSize = 32.sp)
                    IconButton(onClick = {
                        int -= 1
                    })  {
                        Image(
                            painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                            contentDescription = "Down",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }

            }
        }
    }

    @Preview
    @Composable
    fun previewScreen() {
        RPGCardView()
    }
}

