package com.example.a223lablearnandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a223lablearnandroid.LifeCycleComposeActivity
import com.example.a223lablearnandroid.PokedexActivity
import com.example.a223lablearnandroid.RPGCardActivity
import com.example.a223lablearnandroid.SharePreferencesActivity
import com.example.a223lablearnandroid.utils.SharedPreferencesUtil
import androidx.core.app.ActivityOptionsCompat
import androidx.compose.ui.platform.LocalView

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, RPGCardActivity::class.java))
                }) {
                    Text("RPGCardActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, PokedexActivity::class.java))
                }) {
                    Text("PokedexActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, LifeCycleComposeActivity::class.java))
                }) {
                    Text("LifeCycleComposeActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, SharePreferencesActivity::class.java))
                }) {
                    Text("SharedPreferencesUtil")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, CameraActivity::class.java))
                }) {
                    Text("CameraActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, GalleryActivity::class.java))
                }) {
                    Text("GalleryActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, SensorLocationActivity::class.java))
                }) {
                    Text("Sensor & MVVM")
                }
                val view = LocalView.current

                Button(onClick = {
                    val options = ActivityOptionsCompat.makeCustomAnimation(this@MenuActivity, android.R.anim.fade_in, android.R.anim.fade_out)
                    startActivity(Intent(this@MenuActivity, Part1AnimationActivity::class.java), options.toBundle())
                }) {
                    Text("Part 1: Animations (Fade In/Out)")
                }
                Button(onClick = {
                    val options = ActivityOptionsCompat.makeCustomAnimation(this@MenuActivity, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    startActivity(Intent(this@MenuActivity, Part2Activity::class.java), options.toBundle())
                }) {
                    Text("Part 2: List & Pagination (Slide Left/Right)")
                }
                Button(onClick = {
                    val options = ActivityOptionsCompat.makeScaleUpAnimation(view, view.width / 2, view.height / 2, view.width, view.height)
                    startActivity(Intent(this@MenuActivity, Part3Activity::class.java), options.toBundle())
                }) {
                    Text("Part 3: Canvas (Scale Up)")
                }
                Button(onClick = {
                    val options = ActivityOptionsCompat.makeClipRevealAnimation(view, view.width / 2, view.height / 2, view.width, view.height)
                    startActivity(Intent(this@MenuActivity, Part4Activity::class.java), options.toBundle())
                }) {
                    Text("Part 4: Swipe to Dismiss (Clip Reveal)")
                }
                Button(onClick = {
                    val options = ActivityOptionsCompat.makeCustomAnimation(this@MenuActivity, R.anim.slide_up_in, R.anim.stay)
                    startActivity(Intent(this@MenuActivity, Part5Activity::class.java), options.toBundle())
                }) {
                    Text("Part 5: Side Effects (Slide Up)")
                }
                Button(onClick = {
                    val options = ActivityOptionsCompat.makeCustomAnimation(this@MenuActivity, R.anim.zoom_in, R.anim.stay)
                    startActivity(Intent(this@MenuActivity, Part6Activity::class.java), options.toBundle())
                }) {
                    Text("Part 6: View Interop (Zoom In)")
                }
                Button(onClick = {
                    val options = ActivityOptionsCompat.makeCustomAnimation(this@MenuActivity, android.R.anim.fade_in, android.R.anim.fade_out)
                    startActivity(Intent(this@MenuActivity, Part8Activity::class.java), options.toBundle())
                }) {
                    Text("Part 8: Adaptive Layouts (Tablet)")
                }
                Button(onClick = {
                    val options = ActivityOptionsCompat.makeCustomAnimation(this@MenuActivity, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    startActivity(Intent(this@MenuActivity, Part9Activity::class.java), options.toBundle())
                }) {
                    Text("Part 9: Collapsing TopBar")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part10Activity::class.java))
                }) {
                    Text("Part 10: App Widget")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part11Activity::class.java))
                }) {
                    Text("Part 11: Skeleton Loading")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part12Activity::class.java))
                }) {
                    Text("Part 12: Dialog & BottomSheet")
                }
            }
        }
    }
}