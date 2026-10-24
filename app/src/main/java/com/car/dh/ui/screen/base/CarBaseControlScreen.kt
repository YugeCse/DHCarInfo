package com.car.dh.ui.screen.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // ---------- 门锁与钥匙 ----------
        SectionHeader(title = "门锁与钥匙")
        SettingCard {
            SettingRow(
                iconRes = R.drawable.ic_set_run_lock,
                title = "行车自动落锁",
                options = listOf("关闭", "10km/h", "20km/h"),
                selection = uiState.autoLockValue
            ) { viewModel.setRunAutoLock(it) }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_speed_lock,
                title = "车速锁止",
                checked = uiState.isSpeedLockEnabled
            ) { viewModel.toggleSpeedLockEnabled() }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_leave_lock,
                title = "离开自动落锁",
                checked = uiState.isLeaveAutoLockEnabled
            ) { viewModel.toggleLeaveAutoLockEnabled() }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_park_unlock,
                title = "停车解锁",
                checked = uiState.isParkUnlocked
            ) { viewModel.toggleParkUnlockEnabled() }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_lock_feedback,
                title = "遥控锁车反馈",
                checked = uiState.isRemoteLockFeedbackEnabled
            ) { viewModel.toggleRemoteLockFeedbackEnabled() }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_find_car,
                title = "寻车指示",
                options = listOf("仅灯光", "灯&声"),
                selection = uiState.findCarIndicatorValue
            ) { viewModel.setFindCarIndicator(it) }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_close_window,
                title = "闭锁车门自动关窗",
                checked = uiState.isLockAutoCloseWindow
            ) { viewModel.toggleLockAutoCloseWindowEnabled() }
        }
        // ---------- 灯光与迎宾 ----------
        SectionHeader(title = "灯光与迎宾")
        SettingCard {
            SettingRow(
                iconRes = R.drawable.ic_set_welcome_light,
                title = "迎宾照明",
                checked = uiState.isWelcomeLightingEnabled
            ) { viewModel.toggleWelcomeLightingEnable() }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_home_delay,
                title = "伴我回家",
                options = listOf("30s", "60s", "90s"),
                selection = uiState.homeLightDelayTimeValue
            ) { viewModel.setHomeDelayForLighting(it) }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_door_hazard,
                title = "开门双闪提示",
                checked = uiState.isOpenDoorLightEnabled
            ) { viewModel.toggleOpenDoorLightEnabled() }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_position_light,
                title = "闭锁熄灭位置灯",
                checked = uiState.isLockLightOffEnabled
            ) { viewModel.toggleLockLightOffEnabled() }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_drl,
                title = "日间行车灯",
                checked = uiState.isDaytimeRunningLightEnabled
            ) { viewModel.toggleDaytimeRunningLightEnabled() }
        }
        // ---------- 空调与座舱 ----------
        SectionHeader(title = "空调与座舱")
        SettingCard {
            SettingRow(
                iconRes = R.drawable.ic_set_cabin_clean,
                title = "主动座舱清洁",
                checked = uiState.isActiveCabinCleanEnabled
            ) { viewModel.toggleActiveCabinCleanEnabled() }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_dry,
                title = "空调自干燥",
                checked = uiState.isAirAutoDryEnabled
            ) { viewModel.toggleAirAutoDryEnabled() }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_ventilation,
                title = "定时通风",
                checked = uiState.isRegularVentilationEnabled
            ) { viewModel.toggleRegularVentilationEnabled() }
        }
        // ---------- 通用设置 ----------
        SectionHeader(title = "通用设置")
        SettingCard {
            SettingRow(
                iconRes = R.drawable.ic_drive_mode_default,
                title = "驾驶模式",
                options = listOf("舒适", "经济", "运动"),
                selection = uiState.driveModeValue.coerceIn(0, 2)
            ) { viewModel.setDriveMode(it) }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_language,
                title = "车机语言",
                options = listOf("中文", "英文", "俄语"),
                selection = uiState.carLanguageValue
            ) { index ->
                viewModel.setSysLanguageValue(if (index == 2) 3 else index)
            }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_time_sync,
                title = "原车时间同步",
                checked = uiState.isTimeSyncOriginalCarEnabled
            ) { viewModel.toggleSyncOriginalCarTime() }
            SettingDivider()
            SettingRow(
                iconRes = R.drawable.ic_set_tire,
                title = "胎压监测系统校准",
                value = "去校准",
                onClick = { viewModel.calibrateTirePressure() }
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

// ============================================================
// 组件
// ============================================================

/** 分区标题：强调色条 + 标题 **/
@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 13.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(DHCarInfoTheme.accent)
        )
        Spacer(Modifier.width(7.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DHCarInfoTheme.subText
        )
    }
}

/** 设置卡片容器 **/
@Composable
private fun SettingCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DHCarInfoTheme.panel)
            .padding(horizontal = 14.dp)
    ) { content() }
}

@Composable
private fun SettingDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 44.dp),
        thickness = 0.5.dp,
        color = DHCarInfoTheme.inactive.copy(alpha = 0.4f)
    )
}

/** 开关行：图标 + 标题 + Switch **/
@Composable
private fun SettingRow(
    iconRes: Int,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    BaseRow(iconRes = iconRes, title = title) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = DHCarInfoTheme.accent,
                checkedBorderColor = Color.Transparent,
                checkedThumbColor = Color.Black
            )
        )
    }
}

/** 操作行：图标 + 标题 + 值文案 + 箭头 **/
@Composable
private fun SettingRow(
    iconRes: Int,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    BaseRow(
        iconRes = iconRes,
        title = title,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(text = value, fontSize = 13.sp, color = DHCarInfoTheme.accent)
        Spacer(Modifier.width(2.dp))
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(R.drawable.ic_nav_forward),
            contentDescription = null,
            tint = DHCarInfoTheme.subText
        )
    }
}

/** 多档选择行：图标 + 标题 + 分段按钮 **/
@Composable
private fun SettingRow(
    iconRes: Int,
    title: String,
    options: List<String>,
    selection: Int,
    onOptionSelected: (Int) -> Unit
) {
    BaseRow(iconRes = iconRes, title = title) {
        SingleChoiceSegmentedButtonRow(
            Modifier
                .sizeIn(maxHeight = 32.dp)
                .weight(1.2f)
        ) {
            options.fastForEachIndexed { index, itemText ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults
                        .itemShape(index = index, count = options.size),
                    icon = {},
                    selected = index == selection,
                    contentPadding = PaddingValues(horizontal = 6.dp),
                    onClick = { onOptionSelected(index) },
                ) {
                    Text(
                        text = itemText,
                        maxLines = 1,
                        fontSize = 12.sp,
                        color = if (index == selection) DHCarInfoTheme.accent
                        else DHCarInfoTheme.subText
                    )
                }
            }
        }
    }
}

/** 行基础布局：图标 + 标题 + 尾部内容 **/
@Composable
private fun BaseRow(
    iconRes: Int,
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(DHCarInfoTheme.inactive.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(19.dp),
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = DHCarInfoTheme.text
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(text = title, fontSize = 15.sp, color = DHCarInfoTheme.text)
        Spacer(Modifier.weight(1f))
        trailing()
    }
}
