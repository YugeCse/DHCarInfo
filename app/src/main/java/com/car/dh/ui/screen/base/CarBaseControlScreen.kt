package com.car.dh.ui.screen.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.car.dh.R
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
        Text(text = "其他控制", fontSize = 14.sp, color = DHCarInfoTheme.subText)
        ControlItemView(
            modifier = Modifier.padding(top = 12.dp),
            title = "车机语言"
        ) { } //语言设置	—	setLanguage(langValue)	—	1	26，值 0/1/3
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(title = "驾驶模式") {
            CarBaseController.singleton().getDriveMode2()
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isWelcomeLightingEnabled by remember {
            mutableStateOf(
                CarBaseController.singleton()
                    .isWelcomeLightingEnabled()
            )
        }
        ControlItemView(
            title = "迎宾照明",
            value = if (isWelcomeLightingEnabled) "启用" else "不使用"
        ) {
            CarBaseController.singleton()
                .setWelcomeLightingEnabled(!isWelcomeLightingEnabled)
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        //伴我回家持续时间	getHomeDelay()	setHomeDelay(value) //0=30s / 1=60s / 2=90s
        var isParkUnlocked by remember {
            mutableStateOf(
                CarBaseController.singleton()
                    .isParkUnlockEnabled()
            )
        }
        ControlItemView(
            title = "停车解锁",
            value = if (!isParkUnlocked) "启用" else "不使用"
        ) {
            CarBaseController.singleton()
                .setParkUnlockEnabled(!isParkUnlocked)
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isLockAutoCloseWindow by remember {
            mutableStateOf(
                CarBaseController.singleton()
                    .isLockAutoCloseWindowEnabled()
            )
        }
        ControlItemView(
            title = "闭锁车门自动关窗",
            value = if (!isLockAutoCloseWindow) "启用" else "不使用"
        ) {
            CarBaseController.singleton()
                .setLockAutoCloseWindowEnabled(!isLockAutoCloseWindow)
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isActiveCabinCleanEnabled by remember {
            mutableStateOf(
                CarBaseController.singleton()
                    .isActiveCabinCleanEnabled()
            )
        }
        ControlItemView(
            title = "主动座舱清洁",
            value = if (isActiveCabinCleanEnabled) "启用" else "不使用"
        ) {
            CarBaseController.singleton()
                .setActiveCabinCleanEnabled(!isActiveCabinCleanEnabled)
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isAirAutoDryEnabled by remember {
            mutableStateOf(
                CarBaseController.singleton()
                    .isAirAutoDryEnabled()
            )
        }
        ControlItemView(
            title = "空调自干燥",
            value = if (isActiveCabinCleanEnabled) "启用" else "不使用"
        ) {
            CarBaseController.singleton()
                .setAirAutoDryEnabled(!isAirAutoDryEnabled)
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isRegularVentilationEnabled by remember {
            mutableStateOf(
                CarBaseController.singleton()
                    .isRegularVentilationEnabled()
            )
        }
        ControlItemView(
            title = "定时通风",
            value = if (!isActiveCabinCleanEnabled) "启用" else "不使用"
        ) {
            CarBaseController.singleton()
                .setRegularVentilationEnabled(!isRegularVentilationEnabled)
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        var isTimeSyncEnabled by remember {
            mutableStateOf(
                CarBaseController.singleton()
                    .isTimeSyncEnabled()
            )
        }
        ControlItemView(
            title = "原车时间同步",
            value = if (isActiveCabinCleanEnabled) "启用" else "不使用"
        ) {
            CarBaseController.singleton()
                .setTimeSyncEnabled(!isTimeSyncEnabled)
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        ControlItemView(title = "胎压监测系统校准") {
            CarBaseController.singleton().calibrateTirePressure()
        }
        HorizontalDivider(color = DHCarInfoTheme.inactive)
        //行车自动落锁	getRunAutoLock()	setRunAutoLock(value)	197	1	49，0=off / 1=10km/h / 2=20km/h
        //寻车指示	getFindCarIndicator()	setFindCarIndicator(value)	206	1	58，0=仅灯光 / 1=灯光与喇叭
    }
}

@Composable
private fun ControlItemView(
    modifier: Modifier = Modifier,
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .height(45.dp)
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