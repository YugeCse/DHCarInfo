package com.car.dh.ui.screen.air

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.car.dh.R
import com.car.dh.ui.theme.DHCarInfoTheme
import com.car.dh.utils.ActivityLaunch
import com.car.dh.utils.AcAirTempUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds


// ============================================================
// 主界面
// ============================================================

@Composable
fun AirControlScreen(modifier: Modifier = Modifier) {
    val viewModel = viewModel<AirControlViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // ---------- 温度：完全本地维护 ----------
    // 只在首次组合时读一次 DATA，之后用户点 +/- 只改本地值。
    // 如果首次读到无效值（-1/-2/-3 或 0），使用默认温度。
    val localTemp by remember {
        derivedStateOf {
            val raw = viewModel.controller.getTempLeft()
            if (raw >= AirController.TEMP_MIN &&
                raw <= AirController.TEMP_MAX
            ) raw else AirController.TEMP_DEFAULT
        }
    }

    // ---------- 风量：完全本地维护 ----------
    val localWind by remember {
        val raw = viewModel.controller.getWindLevel()
        derivedStateOf {
            raw.coerceIn(
                AirController.WIND_MIN,
                AirController.WIND_MAX
            )
        }
    }
    // ---------- A/C：本地预测 ----------
    var localAc by remember { mutableStateOf<Int?>(null) }
    val displayAc = localAc ?: uiState.ac
    LaunchedEffect(uiState.ac) {
        if (localAc != null && localAc == uiState.ac) localAc = null
    }

    // ---------- 循环：本地预测 ----------
    var localCycle by remember { mutableStateOf<Int?>(null) }
    val displayCycle = localCycle ?: uiState.cycle
    LaunchedEffect(uiState.cycle) {
        if (localCycle != null && localCycle == uiState.cycle) localCycle = null
    }

    // ---------- 模式：完全本地维护 ----------
    // 只有"模式切换"命令，没有独立吹风位命令，本地循环显示 5 种状态。
    val modeLabels = listOf("吹面", "吹面+吹脚", "吹脚", "吹脚+除霜", "除霜")
    var modeIndex by remember { mutableIntStateOf(0) }
    val modeLabel = modeLabels[modeIndex]
    // 兜底轮询：只用来同步开关类状态，不涉及温度/风量
    LaunchedEffect(viewModel) {
        while (isActive) {
            viewModel.syncAll()
            delay(300.milliseconds)
        }
    }
    Box(
        modifier = Modifier
            .then(modifier)
            .fillMaxSize()
            .background(DHCarInfoTheme.bg)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ---------- 顶部：温度面板 + 悬浮电源 ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DHCarInfoTheme.panel)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Column(Modifier.fillMaxSize()) {
                    val isLandscape =
                        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                    TemperaturePanel(
                        modifier = Modifier
                            .run {
                                if (!isLandscape) this
                                else padding(horizontal = 100.dp)
                            }
                            .fillMaxWidth()
                            .weight(1f),
                        temp = localTemp,
                        onTempUp = { viewModel.controller.increaseTemp() },
                        onTempDown = { viewModel.controller.decreaseTemp() },
                        onRelease = viewModel.controller::releaseKey,
                    )
                    HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f))
                    // ---------- 风量 ----------
                    Row(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WindPanel(
                            level = localWind,
                            onUpPress = {
                                val next = (localWind + 1)
                                    .coerceAtMost(AirController.WIND_MAX)
                                if (next != localWind) {
                                    viewModel.controller.setWindLevel(next)
                                }
                            },
                            onDownPress = {
                                val next = (localWind - 1)
                                    .coerceAtLeast(AirController.WIND_MIN)
                                if (next != localWind) {
                                    viewModel.controller.setWindLevel(next)
                                }
                            },
                            onRelease = viewModel.controller::releaseKey
                        )
                    }


                    // ---------- 功能按钮：A/C、循环、前除霜、模式 ----------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FunctionButton(
                            label = when (displayCycle) {
                                0 -> "外循环"
                                1 -> "内循环"
                                else -> "自动"
                            },
                            active = displayAc == 1,
                            onPress = viewModel.controller::toggleCycle,
                            onRelease = viewModel.controller::releaseKey,
                            modifier = Modifier.weight(1f)
                        )
                        FunctionButton(
                            modifier = Modifier.weight(1f),
                            label = "前除霜",
                            active = uiState.frontDefrost == 1,
                            onPress = viewModel.controller::toggleFrontDefrost,
                            onRelease = viewModel.controller::releaseKey,
                        )
                        FunctionButton(
                            modifier = Modifier.weight(1f),
                            label = modeLabel,
                            active = uiState.modeBody == 1 ||
                                    uiState.modeUp == 1 ||
                                    uiState.modeFoot == 1,
                            onPress = {
                                modeIndex = (modeIndex + 1) % modeLabels.size
                                viewModel.controller.toggleMode()
                            },
                            onRelease = viewModel.controller::releaseKey,
                        )
                        Image(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DHCarInfoTheme.inactive)
                                .padding(8.dp)
                                .size(42.dp)
                                //.background(DHCarInfoTheme.panel)
                                .clickable(onClick = { ActivityLaunch.backHomeDesktop() }),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            painter = painterResource(R.drawable.ic_back_home)
                        )
                    }
                }
            }
        }
        ImageButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(56.dp),
            active = uiState.ac == 1,
            activeIcon = R.drawable.ic_ac_status_on,
            inactiveIcon = R.drawable.ic_ac_status_off,
            onPress = viewModel.controller::toggleAc,
            onRelease = viewModel.controller::releaseKey,
        )
        ImageButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(56.dp),
            active = uiState.power == 1,
            activeIcon = R.drawable.ic_air_status_on,
            inactiveIcon = R.drawable.ic_air_status_off,
            onPress = viewModel.controller::togglePower,
            onRelease = viewModel.controller::releaseKey,
        )
    }
}

@Composable
private fun TemperaturePanel(
    temp: Float,
    onTempUp: () -> Unit,
    onTempDown: () -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "温度", color = DHCarInfoTheme.subText, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                ImageButton(
                    modifier = Modifier.size(42.dp),
                    onPress = onTempDown,
                    onRelease = onRelease,
                    active = true,
                    activeIcon = R.drawable.ic_data_decrement,
                )
                ImageButton(
                    modifier = Modifier.size(42.dp),
                    onPress = onTempUp,
                    onRelease = onRelease,
                    active = true,
                    activeIcon = R.drawable.ic_data_increment,
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = AcAirTempUtils.formatTemp(temp),
                    color = DHCarInfoTheme.text,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 200.sp)
                )
                Text(
                    text = "℃",
                    fontSize = 15.sp,
                    color = DHCarInfoTheme.accent
                )
            }
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                ImageButton(
                    modifier = Modifier.size(42.dp),
                    onPress = onTempDown,
                    onRelease = onRelease,
                    active = true,
                    activeIcon = R.drawable.ic_data_decrement,
                )
                ImageButton(
                    modifier = Modifier.size(42.dp),
                    onPress = onTempUp,
                    onRelease = onRelease,
                    active = true,
                    activeIcon = R.drawable.ic_data_increment,
                )
            }
        }
    }
}

@Composable
private fun WindPanel(
    level: Int,
    onUpPress: () -> Unit,
    onDownPress: () -> Unit,
    onRelease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DHCarInfoTheme.panel)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("风量", color = DHCarInfoTheme.subText, fontSize = 18.sp)
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(8) { index ->
                val active = index < level
                val color by animateColorAsState(
                    targetValue = if (active) DHCarInfoTheme.accent else DHCarInfoTheme.inactive,
                    animationSpec = tween(180),
                    label = "windBar$index"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                )
            }
        }

        Text(
            text = level.toString(),
            color = DHCarInfoTheme.text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(22.dp),
            textAlign = TextAlign.Center
        )
        ImageButton(
            modifier = Modifier.size(42.dp),
            onPress = onDownPress,
            onRelease = onRelease,
            active = true,
            activeIcon = R.drawable.ic_data_decrement,
        )
        ImageButton(
            modifier = Modifier.size(42.dp),
            onPress = onUpPress,
            onRelease = onRelease,
            active = true,
            activeIcon = R.drawable.ic_data_increment,
        )
    }
}

@Composable
private fun ImageButton(
    modifier: Modifier = Modifier,
    active: Boolean,
    @DrawableRes activeIcon: Int,
    @DrawableRes inactiveIcon: Int? = null,
    onPress: () -> Unit,
    onRelease: () -> Unit,
) {
    Image(
        modifier = Modifier
            .then(modifier)
            .pressRelease(onPress, onRelease),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        painter = painterResource(if (active || inactiveIcon == null) activeIcon else inactiveIcon)
    )
}

@Composable
private fun FunctionButton(
    label: String,
    active: Boolean,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        label = "fnBg",
        animationSpec = tween(200),
        targetValue = if (active) DHCarInfoTheme.accent else DHCarInfoTheme.inactive,
    )
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .pressRelease(onPress, onRelease),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = when {
                active -> Color.Black
                else -> DHCarInfoTheme.text
            },
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ============================================================
// 工具
// ============================================================

private fun Modifier.pressRelease(
    onPress: () -> Unit,
    onRelease: () -> Unit
): Modifier = this.pointerInput(Unit) {
    detectTapGestures(
        onPress = {
            onPress()
            tryAwaitRelease()
            onRelease()
        }
    )
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun AirControlScreenPreview() =
    DHCarInfoTheme { AirControlScreen() }