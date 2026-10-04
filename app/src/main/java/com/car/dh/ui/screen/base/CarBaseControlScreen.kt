package com.car.dh.ui.screen.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import com.car.dh.R
import com.car.dh.app.GlobalConfig
import com.car.dh.app.LocalGlobalConfig
import com.car.dh.ui.theme.DHCarInfoTheme

@Composable
fun CarBaseControlScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        val carBaseController = remember { CarBaseController.singleton() }
        Text(text = "其他控制", fontSize = 14.sp, color = DHCarInfoTheme.subText)
        val sysLanguage by when {
            LocalInspectionMode.current ->
                remember { mutableIntStateOf(0) }

            else -> {
                val globalConfig = LocalGlobalConfig.current
                remember { mutableIntStateOf(globalConfig.carSystemLanguage) }
            }
        }
        val sysLanguages = listOf("中文", "英文", "俄语")
        ControlItemView(
            modifier = Modifier.padding(top = 12.dp),
            title = "车机语言",
            options = sysLanguages,
            selection = sysLanguages[sysLanguage],
            onOptionSelected = { index, _ ->
                val targetValue = if(index == 2) 3 else index
                GlobalConfig.singleton()
                    .carSystemLanguage = targetValue
                carBaseController.setLanguage(targetValue)
            }
        )
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        val driveMode by when {
            LocalInspectionMode.current ->
                remember { mutableStateOf("经济") }

            else -> {
                val mode = CarBaseController
                    .singleton()
                    .getDriveMode2()
                remember(mode) {
                    mutableStateOf(
                        when (mode) {
                            2 -> "经济"
                            3 -> "舒适"
                            4 -> "舒适"
                            else -> "默认"
                        }
                    )
                }
            }
        }
        val driveModeTexts = listOf("经济", "舒适", "运动")
        ControlItemView(
            title = "驾驶模式",
            selection = driveMode,
            options = driveModeTexts,
            onOptionSelected = { index, _ ->
                val value =
                    if (index == 0) 2 else if (index == 1) 3 else 4
                GlobalConfig.singleton().carDriveMode = value
                carBaseController.setDriveMode2(value)
            }
        )
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isWelcomeLightingEnabled by remember {
            mutableStateOf(carBaseController.isWelcomeLightingEnabled())
        }
        ControlItemView(
            title = "迎宾照明",
            value = if (isWelcomeLightingEnabled) "启用" else "不使用"
        ) {
            carBaseController
                .setWelcomeLightingEnabled(!isWelcomeLightingEnabled)
            isWelcomeLightingEnabled = !isWelcomeLightingEnabled
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        val homeDelaySecTexts = listOf("30s", "60s", "90s")
        var homeDelayIndex by remember {
            mutableIntStateOf(carBaseController.getHomeDelay())
        }
        ControlItemView(
            title = "伴我回家持续时间",
            options = homeDelaySecTexts,
            selection = "${homeDelaySecTexts[homeDelayIndex]}s",
            onOptionSelected = { index, _ ->
                homeDelayIndex = index
                carBaseController.setHomeDelay(index)
            }
        )
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var autoLockValue by remember {
            mutableIntStateOf(carBaseController.getRunAutoLock())
        }
        val autoLockValues = listOf("关闭", "10km/h", "20km/h")
        ControlItemView(
            title = "行车自动落锁",
            options = autoLockValues,
            selection = autoLockValues[autoLockValue],
            onOptionSelected = { index, _ ->
                autoLockValue = index
                carBaseController.setRunAutoLock(index)
            }
        )
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isParkUnlocked by remember {
            mutableStateOf(carBaseController.isParkUnlockEnabled())
        }
        ControlItemView(
            title = "停车解锁",
            value = if (!isParkUnlocked) "启用" else "不使用"
        ) {
            carBaseController
                .setParkUnlockEnabled(!isParkUnlocked)
            isParkUnlocked = !isParkUnlocked
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isLockAutoCloseWindow by remember {
            mutableStateOf(carBaseController.isLockAutoCloseWindowEnabled())
        }
        ControlItemView(
            title = "闭锁车门自动关窗",
            value = if (!isLockAutoCloseWindow) "启用" else "不使用"
        ) {
            carBaseController
                .setLockAutoCloseWindowEnabled(!isLockAutoCloseWindow)
            isLockAutoCloseWindow = !isLockAutoCloseWindow
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        val findCarTags = listOf("仅灯光", "灯光与喇叭")
        var findCarTagIndex by remember {
            mutableIntStateOf(carBaseController.getFindCarIndicator())
        }
        ControlItemView(
            title = "寻车指示",
            selection = findCarTags[findCarTagIndex],
            options = findCarTags,
            onOptionSelected = {index, _ ->
                findCarTagIndex = index
                carBaseController.setFindCarIndicator(index)
            }
        )
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isActiveCabinCleanEnabled by remember {
            mutableStateOf(carBaseController.isActiveCabinCleanEnabled())
        }
        ControlItemView(
            title = "主动座舱清洁",
            value = if (isActiveCabinCleanEnabled) "启用" else "不使用"
        ) {
            carBaseController
                .setActiveCabinCleanEnabled(!isActiveCabinCleanEnabled)
            isActiveCabinCleanEnabled = !isActiveCabinCleanEnabled
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isAirAutoDryEnabled by remember {
            mutableStateOf(carBaseController.isAirAutoDryEnabled())
        }
        ControlItemView(
            title = "空调自干燥",
            value = if (isAirAutoDryEnabled) "启用" else "不使用"
        ) {
            carBaseController
                .setAirAutoDryEnabled(!isAirAutoDryEnabled)
            isAirAutoDryEnabled = !isAirAutoDryEnabled
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isRegularVentilationEnabled by remember {
            mutableStateOf(carBaseController.isRegularVentilationEnabled())
        }
        ControlItemView(
            title = "定时通风",
            value = if (!isRegularVentilationEnabled) "启用" else "不使用"
        ) {
            carBaseController
                .setRegularVentilationEnabled(!isRegularVentilationEnabled)
            isRegularVentilationEnabled = !isRegularVentilationEnabled
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isTimeSyncEnabled by remember {
            mutableStateOf(carBaseController.isTimeSyncEnabled())
        }
        ControlItemView(
            title = "原车时间同步",
            value = if (isActiveCabinCleanEnabled) "启用" else "不使用"
        ) {
            carBaseController
                .setTimeSyncEnabled(!isTimeSyncEnabled)
            isTimeSyncEnabled = !isTimeSyncEnabled
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(
            title = "胎压监测系统校准",
            onClick = { carBaseController.calibrateTirePressure() })
        HorizontalDivider(color = DHCarInfoTheme.inactive)
    }
}

@Composable
private fun ControlItemView(
    modifier: Modifier = Modifier,
    title: String,
    value: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = { onClick() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, color = DHCarInfoTheme.text)
        Spacer(Modifier.weight(1f))
        if (!value.isNullOrEmpty())
            Text(text = value, fontSize = 14.sp, color = DHCarInfoTheme.subText)
        Image(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(20.dp),
            contentDescription = null,
            painter = painterResource(R.drawable.ic_nav_forward)
        )
    }
}


@Composable
private fun ControlItemView(
    modifier: Modifier = Modifier,
    title: String,
    selection: String,
    options: List<String>,
    onOptionSelected: (Int, String) -> Unit = { _, _ -> }
) {
    val selectedIndex =
        remember(selection, options) { options.indexOf(selection) }
    Row(
        modifier = Modifier
            .then(modifier)
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = title, fontSize = 16.sp, color = DHCarInfoTheme.text)
        SingleChoiceSegmentedButtonRow(Modifier.sizeIn(maxHeight = 40.dp)) {
            options.fastForEachIndexed { index, itemText ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults
                        .itemShape(index = index, count = options.size),
                    icon = {},
                    selected = index == selectedIndex,
                    contentPadding =
                        PaddingValues(horizontal = 8.dp),
                    onClick = { onOptionSelected(index, options[index]) },
                ) {
                    Text(text = itemText, color = DHCarInfoTheme.subText, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
@Preview(name = "预览")
private fun CarBaseControlScreenPreview() = DHCarInfoTheme { CarBaseControlScreen() }