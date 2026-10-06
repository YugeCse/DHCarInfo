package com.car.dh.ui.screen.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.car.dh.R
import com.car.dh.ui.theme.DHCarInfoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CarBaseControlScreen(modifier: Modifier = Modifier) {
    val viewModel = viewModel<CarBaseControlViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dataVersion = CarDataBusDataObserver
        .dataChangeFlow
        .collectAsStateWithLifecycle()
    LaunchedEffect(dataVersion) {
        while (isActive) {
            delay(500.milliseconds)
            viewModel.syncCarBaseData()
        }
    }
    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "其他控制",
            fontSize = 14.sp,
            color = DHCarInfoTheme.subText
        )
        val sysLanguage by when {
            LocalInspectionMode.current ->
                remember { mutableIntStateOf(0) }

            else -> remember { derivedStateOf { uiState.carLanguageValue } }
        }
        val sysLanguages = listOf("中文", "英文", "俄语")
        ControlItemView(
            modifier = Modifier.padding(top = 12.dp),
            title = "车机语言",
            options = sysLanguages,
            selection = sysLanguages[sysLanguage],
            onOptionSelected = { index, _ ->
                val targetValue = if (index == 2) 3 else index
                viewModel.setSysLanguageValue(targetValue)
            }
        )
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        val driveMode by when {
            LocalInspectionMode.current ->
                remember { mutableStateOf("经济") }

            else -> {
                val mode = uiState.driveModeValue.coerceIn(0, 2)
                remember(mode) {
                    mutableStateOf(if (mode == 1) "经济" else if (mode == 2) "运动" else "舒适")
                }
            }
        }
        val driveModeTexts = listOf("舒适", "经济", "运动")
        ControlItemView(
            title = "驾驶模式",
            selection = driveMode,
            options = driveModeTexts,
            onOptionSelected = { index, _ -> viewModel.setDriveMode(index) }
        )
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(
            title = "迎宾照明",
            value = uiState.isWelcomeLightingEnabled
        ) { viewModel.toggleWelcomeLightingEnable() }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        val homeDelaySecTexts = listOf("30s", "60s", "90s")
        ControlItemView(
            title = "伴我回家持续时间",
            options = homeDelaySecTexts,
            selection = "${homeDelaySecTexts[uiState.homeLightDelayTimeValue]}s",
            onOptionSelected = { index, _ -> viewModel.setHomeDelayForLighting(index) }
        )
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        val autoLockValues = listOf("关闭", "10km/h", "20km/h")
        ControlItemView(
            title = "行车自动落锁",
            options = autoLockValues,
            selection = autoLockValues[uiState.autoLockValue],
            onOptionSelected = { index, _ -> viewModel.setRunAutoLock(index) }
        )
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(
            title = "停车解锁",
            value = uiState.isParkUnlocked
        ) { viewModel.toggleParkUnlockEnabled() }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(
            title = "闭锁车门自动关窗",
            value = uiState.isLockAutoCloseWindow
        ) { viewModel.toggleLockAutoCloseWindowEnabled() }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        val findCarTags = listOf(" 灯&声 ", "仅灯光")
        ControlItemView(
            title = "寻车指示",
            options = findCarTags,
            selection = findCarTags[uiState.findCarIndicatorValue],
            onOptionSelected = { index, _ -> viewModel.setFindCarIndicator(index) }
        )
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(
            title = "主动座舱清洁",
            value = uiState.isActiveCabinCleanEnabled
        ) { viewModel.toggleActiveCabinCleanEnabled() }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(
            title = "空调自干燥",
            value = uiState.isAirAutoDryEnabled
        ) { viewModel.toggleAirAutoDryEnabled() }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(
            title = "定时通风",
            value = uiState.isRegularVentilationEnabled
        ) { viewModel.toggleRegularVentilationEnabled() }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(
            title = "原车时间同步",
            value = uiState.isTimeSyncOriginalCarEnabled
        ) { viewModel.toggleSyncOriginalCarTime() }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(
            title = "胎压监测系统校准",
            onClick = { viewModel.calibrateTirePressure() })
        HorizontalDivider(color = DHCarInfoTheme.inactive)
    }
}

@Composable
private fun ControlItemView(
    modifier: Modifier = Modifier,
    title: String,
    value: Boolean,
    onValueChanged: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, color = DHCarInfoTheme.text)
        Spacer(Modifier.weight(1f))
        Switch(
            modifier = Modifier
                .padding(start = 8.dp)
                .height(20.dp),
            checked = value,
            onCheckedChange = onValueChanged
        )
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
                    Text(
                        text = itemText,
                        maxLines = 1,
                        fontSize = 14.sp,
                        color = DHCarInfoTheme.subText,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(name = "预览")
private fun ControlItemViewPreview() = DHCarInfoTheme {
    Column(Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)) {
        ControlItemView(title = "打开天窗", value = true)
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(title = "打开天窗", value = "好的")
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(title = "车机语言", selection = "中文", options = listOf("中文", "英语"))
    }
}

@Composable
@Preview(name = "预览")
private fun CarBaseControlScreenPreview() = DHCarInfoTheme { CarBaseControlScreen() }