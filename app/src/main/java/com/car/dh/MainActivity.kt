package com.car.dh

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.car.dh.ui.screen.air.AirControlScreen
import com.car.dh.ui.screen.base.CarBaseControlScreen
import com.car.dh.ui.screen.base.CarDataBusDataObserver
import com.car.dh.ui.screen.base.SkyControlScreen
import com.car.dh.ui.screen.base.WidgetBootUpdater
import com.car.dh.ui.theme.DHCarInfoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DHCarInfoTheme {
                SideEffect {
                    WindowInsetsControllerCompat(window, window.decorView)
                        .isAppearanceLightStatusBars = false
                }
                LaunchedEffect(Unit) {
                    WidgetBootUpdater.updateAllAppWidgets(this@MainActivity)
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DHCarInfoTheme.bg
                ) { innerPadding ->
                    ActivityContentView(Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    private fun ActivityContentView(modifier: Modifier = Modifier) {
        val isLandscape = LocalConfiguration.current.orientation ==
                Configuration.ORIENTATION_LANDSCAPE
        when {
            isLandscape ->
                LandscapeContentView(modifier = modifier)

            else -> PortraitContentView(modifier = modifier)
        }
    }

    /**
     * 横屏显示的内容视图
     * @param modifier 修饰器
     */
    @Composable
    private fun LandscapeContentView(modifier: Modifier = Modifier) {
        Row(
            Modifier
                .then(modifier)
                .fillMaxSize()
        ) {
            AirControlScreen(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
            ) {
                val dataVersion = CarDataBusDataObserver
                    .dataChangeFlow
                    .collectAsStateWithLifecycle()
                key(dataVersion) {
                    SkyControlScreen()
                    CarBaseControlScreen()
                }
            }
        }
    }

    /**
     * 竖屏显示的内容视图
     * @param modifier 修饰器
     */
    @Composable
    private fun PortraitContentView(modifier: Modifier = Modifier) {
        Column(
            Modifier
                .then(modifier)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val screenHeight =
                LocalWindowInfo.current.containerDpSize.height
            AirControlScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight / 2f)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val dataVersion = CarDataBusDataObserver
                    .dataChangeFlow
                    .collectAsStateWithLifecycle()
                key(dataVersion) {
                    SkyControlScreen()
                    CarBaseControlScreen()
                }
            }
        }
    }

}