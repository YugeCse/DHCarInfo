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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.car.dh.utils.AcAirTempUtils
import com.car.dh.utils.ActivityLaunch


/**
 * 空调屏幕显示
 * @param modifier 修饰器
 */
@Composable
fun AirControlScreen(modifier: Modifier = Modifier) {
    Box(
        Modifier
            .then(modifier)
            .fillMaxSize()
    ) {
        val viewModel = viewModel<AirControlViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val airStateInfo = uiState.airStateInfo
        val isPowerOn = airStateInfo.isPower
        val localTemp by remember {
            derivedStateOf {
                val raw = uiState.airStateInfo.temp
                if (raw >= AirController.TEMP_MIN &&
                    raw <= AirController.TEMP_MAX
                ) raw else AirController.TEMP_DEFAULT
            }
        }
        val windLevel = airStateInfo.windLevel
        val isAcOn = airStateInfo.isAcOn
        val windCycle = airStateInfo.cycle
        val modeLabels = listOf("吹面", "吹体", "吹脚", "吹脚+除霜", "吹面+吹脚")
        var modeIndex by remember { mutableIntStateOf(0) }
        val modeLabel = modeLabels[modeIndex]
        Box(
            modifier = Modifier
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
                                    else padding(horizontal = 50.dp)
                                }
                                .fillMaxWidth()
                                .weight(1f),
                            active = airStateInfo.isPower,
                            temp = localTemp,
                            onTempUp = viewModel::increaseTemp,
                            onTempDown = viewModel::decreaseTemp,
                            onSetMin = viewModel::setTempMin,
                            onSetMax = viewModel::setTempMax,
                            onRelease = viewModel::releaseKey,
                        )
                        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f))
                        WindPanel(
                            modifier = Modifier.padding(top = 20.dp),
                            active = isPowerOn,
                            level = windLevel,
                            onChangeWindLevel = viewModel::setWindLevel,
                            onUpPress = viewModel::incrementWindLevel,
                            onDownPress = viewModel::decrementWindLevel,
                            onRelease = viewModel::releaseKey
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FunctionButton(
                                modifier = Modifier.weight(1f),
                                label = when (windCycle) {
                                    0 -> "外循环"
                                    1 -> "内循环"
                                    else -> "自动"
                                },
                                active = isPowerOn && isAcOn,
                                onPress = viewModel::toggleCycle,
                                onRelease = viewModel::releaseKey,
                            )
                            FunctionButton(
                                modifier = Modifier.weight(1f),
                                label = "前除霜",
                                active = isPowerOn &&
                                        airStateInfo.frontDefrost == 1,
                                onPress = viewModel::toggleFrontDefrost,
                                onRelease = viewModel::releaseKey,
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FunctionButton(
                                modifier = Modifier.weight(1f),
                                label = modeLabel,
                                active = isPowerOn &&
                                        (airStateInfo.modeBody == 1 ||
                                                airStateInfo.modeUp == 1 ||
                                                airStateInfo.modeFoot == 1),
                                onPress = {
                                    modeIndex = (modeIndex + 1) % modeLabels.size
                                    viewModel.toggleMode()
                                },
                                onRelease = viewModel::releaseKey,
                            )
                            Image(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DHCarInfoTheme.inactive)
                                    .size(56.dp)
                                    .clickable { viewModel.isAirSettingDialogVisible = true }
                                    .padding(12.dp),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                painter = painterResource(R.drawable.ic_air_settings)
                            )
                            Image(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DHCarInfoTheme.inactive)
                                    .size(56.dp)
                                    .clickable { ActivityLaunch.backHomeDesktop() }
                                    .padding(12.dp),
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
                    .clip(CircleShape)
                    .background(
                        if (isPowerOn && isAcOn)
                            DHCarInfoTheme.accent
                        else DHCarInfoTheme.inactive
                    )
                    .size(46.dp)
                    .padding(8.dp),
                active = isPowerOn && isAcOn,
                activeIcon = R.drawable.ic_air_ac_status_on,
                inactiveIcon = R.drawable.ic_air_ac_status_off,
                onPress = viewModel::toggleAc,
                onRelease = viewModel::releaseKey,
            )
            ImageButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPowerOn)
                            DHCarInfoTheme.accent
                        else DHCarInfoTheme.inactive
                    )
                    .size(46.dp)
                    .padding(8.dp),
                active = isPowerOn,
                activeIcon = R.drawable.ic_air_power_status_on,
                inactiveIcon = R.drawable.ic_air_power_status_off,
                onPress = viewModel::togglePower,
                onRelease = viewModel::releaseKey,
            )
        }
        AirSettingsDialog(isVisible = uiState.isAirSettingsDialogVisible) {
            viewModel.isAirSettingDialogVisible = false
        }
    }
}

@Composable
private fun TemperaturePanel(
    temp: Float,
    active: Boolean,
    onTempUp: () -> Unit,
    onTempDown: () -> Unit,
    onSetMin: () -> Unit = {},
    onSetMax: () -> Unit = {},
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TempExtremeButton(
                label = "LO",
                atExtreme = AcAirTempUtils.formatTemp(temp) == "LO",
                enabled = active,
                onClick = onSetMin
            )
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                ImageButton(
                    modifier = Modifier.size(42.dp),
                    onPress = onTempDown,
                    onRelease = onRelease,
                    active = active,
                    activeIcon = R.drawable.ic_data_decrement,
                    inactiveIcon = R.drawable.ic_data_decrement_inactive
                )
                ImageButton(
                    modifier = Modifier.size(42.dp),
                    onPress = onTempUp,
                    onRelease = onRelease,
                    active = active,
                    activeIcon = R.drawable.ic_data_increment,
                    inactiveIcon = R.drawable.ic_data_increment_inactive
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
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 200.sp)
                )
                Text(
                    text = "℃",
                    fontSize = 15.sp,
                    color = DHCarInfoTheme.subText
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
                    active = active,
                    activeIcon = R.drawable.ic_data_decrement,
                    inactiveIcon = R.drawable.ic_data_decrement_inactive
                )
                ImageButton(
                    modifier = Modifier.size(42.dp),
                    onPress = onTempUp,
                    onRelease = onRelease,
                    active = active,
                    activeIcon = R.drawable.ic_data_increment,
                    inactiveIcon = R.drawable.ic_data_increment_inactive,
                )
            }
            TempExtremeButton(
                label = "HI",
                atExtreme = AcAirTempUtils.formatTemp(temp) == "HI",
                enabled = active,
                onClick = onSetMax
            )
        }
    }
}

/** LO / HI 温度极值快捷按钮：温度到达对应极值时高亮 **/
@Composable
private fun TempExtremeButton(
    label: String,
    atExtreme: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (atExtreme) DHCarInfoTheme.accent else DHCarInfoTheme.inactive,
        animationSpec = tween(180),
        label = "tempExtreme$label"
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .size(width = 44.dp, height = 28.dp)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = DHCarInfoTheme.text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WindPanel(
    modifier: Modifier = Modifier,
    level: Int,
    active: Boolean,
    onUpPress: () -> Unit = {},
    onDownPress: () -> Unit = {},
    onRelease: () -> Unit = {},
    onChangeWindLevel: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .then(modifier)
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
                    Modifier
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                        .clickable { onChangeWindLevel(index + 1) }
                )
            }
        }
        // Text(
        //     text = level.toString(),
        //     color = DHCarInfoTheme.text,
        //     fontSize = 18.sp,
        //     fontWeight = FontWeight.Bold,
        //     modifier = Modifier.width(22.dp),
        //     textAlign = TextAlign.Center
        // )
        ImageButton(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(42.dp),
            onPress = onDownPress,
            onRelease = onRelease,
            active = active,
            activeIcon = R.drawable.ic_data_decrement,
            inactiveIcon = R.drawable.ic_data_decrement_inactive,
        )
        ImageButton(
            modifier = Modifier.size(42.dp),
            onPress = onUpPress,
            onRelease = onRelease,
            active = active,
            activeIcon = R.drawable.ic_data_increment,
            inactiveIcon = R.drawable.ic_data_increment_inactive,
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