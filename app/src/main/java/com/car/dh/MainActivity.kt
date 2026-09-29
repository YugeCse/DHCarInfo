package com.car.dh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import com.car.dh.ui.theme.DHCarInfoTheme
import com.car.dh.ui.screen.air.AirControlScreen
import com.car.dh.ui.screen.air.AirController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DHCarInfoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DHCarInfoTheme.bg
                ) { innerPadding ->
                    SideEffect {
                        WindowInsetsControllerCompat(window, window.decorView)
                            .isAppearanceLightStatusBars = false
                    }
                    AirControlScreen(
                        modifier = Modifier.padding(innerPadding),
                        carType = AirController.CAR_RZC_XP1_YuanJingX1
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DHCarInfoTheme {
        Greeting("Android")
    }
}