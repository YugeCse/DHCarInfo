package com.car.dh

import android.content.Intent
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.car.dh.app.GlobalConfig
import com.car.dh.ui.screen.air.AirControlScreen
import com.car.dh.ui.screen.base.CarDataBusDataObserver
import com.car.dh.ui.screen.base.CarBaseControlScreen
import com.car.dh.ui.screen.base.CarBaseController
import com.car.dh.ui.screen.base.SkyControlScreen
import com.car.dh.ui.theme.DHCarInfoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleAliasIntent(intent) {
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
                        val isLandscape = LocalConfiguration.current.orientation ==
                                Configuration.ORIENTATION_LANDSCAPE
                        if (isLandscape) {
                            Row(
                                Modifier
                                    .padding(innerPadding)
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
                        } else {
                            Column(
                                Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(innerPadding)
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
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent?.let { handleAliasIntent(it) }
    }

    /** 根据 intent 信息处理不同 activity 的行为 **/
    private fun handleAliasIntent(intent: Intent, onLaunched: () -> Unit = {}) {
        val component = intent.component ?: return
        val className = component.className
        if (className.endsWith("MainActivityDefault")) {
            try {
                val globalConfig = GlobalConfig.singleton()
                val carBaseController = CarBaseController.singleton()
                carBaseController.setLanguage(globalConfig.carSystemLanguage)
                carBaseController.setDriveMode2(globalConfig.carDriveMode)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                finish() //关掉当前页面
            }
        } else if (className.endsWith("MainActivityDefault2")) {
            onLaunched()
        }
    }

}