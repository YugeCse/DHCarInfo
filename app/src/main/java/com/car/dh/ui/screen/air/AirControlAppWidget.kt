package com.car.dh.ui.screen.air

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.car.dh.R
import com.car.dh.app.DHApplication
import com.car.dh.app.LocalGlobalConfig
import com.car.dh.ui.screen.base.CarDataBusDataObserver
import com.car.dh.ui.screen.base.WidgetBootUpdater
import com.car.dh.ui.screen.component.ImageButton
import com.car.dh.ui.screen.component.WidgetUiCompat
import com.car.dh.ui.theme.DHCarInfoTheme
import com.car.dh.utils.ActivityLaunch

/** 空调控制微件BroadcastReceiver **/
class AirControlAppWidget : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = impl

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action in WidgetBootUpdater.BOOT_ACTIONS) {
            CarDataBusDataObserver.notifyDataChanged()
            WidgetBootUpdater.updateAfterBoot(
                receiver = this,
                context = context,
                widgetClass = AirControlAppWidget::class.java,
                updateAll = { AirControlAppWidget.updateAll() },
            )
        }
    }

    companion object {

        private val impl by lazy { AirControlAppWidgetImpl() }

        /** 更新所有的空调组件 **/
        @JvmStatic
        suspend fun updateAll() = impl.updateAll(DHApplication.singleton())

    }

}

/** 空调控制微件实现 **/
class AirControlAppWidgetImpl : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            var currentSize = LocalSize.current
            if (!LocalInspectionMode.current &&
                currentSize.width < 60.dp
            ) {
                // 部分桌面在预绑定阶段测量尺寸异常，回退到常规 4x3 尺寸
                var maxSize = maxOf(currentSize.width, currentSize.height)
                if (maxSize < 350.dp) maxSize = 370.dp
                currentSize = DpSize(maxSize, maxSize + 30.dp)
            }
            AirControlAppWidgetContent(widgetSize = currentSize)
        }
    }

}

/**
 * 空调控制微件内容。
 *
 * 布局参考业内主流车机/车控小组件（比亚迪、HarmonyOS 车控卡片等）：
 * 顶栏：电源 + A/C 快捷开关；
 * 中央：大号当前温度，两侧 −/+ 调温按钮，下方为风量档位条；
 * 底部：除雾 / 内外循环 / 风向 / APP 功能开关排。
 * （实车不支持 AUTO、后除雾功能，不放置对应入口）
 *
 * 全部控件尺寸随组件宽度等比缩放，兼容不同桌面格子（2x2 ~ 5x4）。
 */
@Composable
@SuppressLint("RestrictedApi")
private fun AirControlAppWidgetContent(
    widgetSize: DpSize = DpSize(400.dp, 300.dp)
) {
    val dataVersion by CarDataBusDataObserver
        .dataChangeFlow
        .collectAsState(0L)
    key(dataVersion) {
        val globalConfig = LocalGlobalConfig.current
        val context = LocalContext.current
        val airController = AirController.singleton()
        val isPowerOn = airController.getPower() == 1
        val isAcOn = airController.getAc() == 1
        val temp = airController.getTempLeft()
        val isFrontDefrost = airController.getFrontDefrost() == 1
        val windLevel = airController.getWindLevel()
            .coerceIn(AirController.WIND_MIN, AirController.WIND_MAX)
        val cycleMode = airController.getCycle()

        // —— 自适应尺寸：以组件宽度为基准等比缩放，并限制在可用范围内 ——
        val width = widgetSize.width
        val height = widgetSize.height
        val outerPadding = (width * 0.05f).coerceIn(12.dp, 20.dp)
        val cornerBtnSize = (width * 0.15f).coerceIn(42.dp, 58.dp)
        val roundBtnSize = (width * 0.14f).coerceIn(40.dp, 54.dp)
        val tileBtnSize = (width * 0.15f).coerceIn(40.dp, 54.dp)
        val tempMaxFontSize = (width.value * 0.18f).coerceIn(32f, 60f)
        val labelFontSize = (width.value * 0.042f).coerceIn(10f, 13f).sp
        val tempBtnSpacing = (width * 0.05f).coerceIn(12.dp, 24.dp)
        val bottomTopPadding = (width * 0.03f).coerceIn(8.dp, 14.dp)
        val pillWidth = (width * 0.13f).coerceIn(36.dp, 48.dp)
        val pillHeight = (pillWidth * 0.42f).coerceIn(16.dp, 20.dp)
        val pillFontSize = (pillWidth.value * 0.28f).coerceIn(10f, 12f).sp
        val showExtras = height >= width * 0.62f

        // —— 温度区防挤压 + 防抖动：浮点温度（如 26.5°）比整数宽。
        //    温度文字放在一个固定宽度的居中盒子内，按文案长度动态收缩字号；
        //    盒子两侧的 ± 按钮与 LO/HI 全部锚定固定位置，度数变化时按钮不位移 ——
        val tempText = formatTemp(temp)
        val baseAvailWidth = (width - outerPadding * 2 -
                roundBtnSize * 2 - tempBtnSpacing * 2).coerceAtLeast(0.dp)
        val showTempExtremes = baseAvailWidth >= pillWidth * 2 + 40.dp
        val tempGap = 8.dp
        val tempBoxWidth = if (showTempExtremes)
            (baseAvailWidth - pillWidth * 2 - tempGap * 2).coerceAtLeast(0.dp)
        else baseAvailWidth
        val tempFontSize = (tempBoxWidth.value / (tempText.length * 0.62f))
            .coerceIn(14f, tempMaxFontSize)
            .sp

        Box(
            modifier = GlanceModifier
                .cornerRadius(WidgetUiCompat.backgroundRadius(context))
                .run {
                    if (!globalConfig.isAirAppWidgetRenderBackground) this
                    else background(ImageProvider(R.drawable.bg_app_widget))
                }
                .fillMaxSize()
                .padding(outerPadding)
        ) {
            Column(modifier = GlanceModifier.fillMaxSize()) {
                // 顶栏：电源（左） + A/C（右）。（实车无 AUTO 功能，不放置入口）
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageButton(
                        modifier = GlanceModifier
                            .background(
                                ImageProvider(
                                    if (isPowerOn) R.drawable.bg_circle_btn_active
                                    else R.drawable.bg_circle_btn_inactive
                                )
                            )
                            .size(cornerBtnSize)
                            .padding(cornerBtnSize * 0.22f),
                        active = isPowerOn,
                        onClick = airController::togglePower,
                        activeIcon = R.drawable.ic_air_power_status_on,
                        inactiveIcon = R.drawable.ic_air_power_status_off,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    ImageButton(
                        modifier = GlanceModifier
                            .background(
                                ImageProvider(
                                    if (isAcOn) R.drawable.bg_circle_btn_active
                                    else R.drawable.bg_circle_btn_inactive
                                )
                            )
                            .size(cornerBtnSize)
                            .padding(cornerBtnSize * 0.22f),
                        active = isAcOn,
                        onClick = airController::toggleAc,
                        activeIcon = R.drawable.ic_air_ac_status_on,
                        inactiveIcon = R.drawable.ic_air_ac_status_off,
                    )
                }
                // 中央：温度调节 + 风量档位条
                Column(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 温度控制条：LO 在左、HI 在右，± 与温度居中，形成从 LO 到 HI 的标尺感
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showTempExtremes) {
                            TempExtremeButton(
                                label = "LO",
                                width = pillWidth,
                                height = pillHeight,
                                fontSize = pillFontSize,
                                selected = isPowerOn && temp <= AirController.TEMP_MIN,
                            ) { if (isPowerOn) airController.setTempMin() }
                            Spacer(GlanceModifier.width(tempGap))
                        } else {
                            Spacer(GlanceModifier.defaultWeight())
                        }
                        ImageButton(
                            modifier = GlanceModifier
                                .background(
                                    ImageProvider(
                                        if (isPowerOn) R.drawable.bg_circle_btn_active
                                        else R.drawable.bg_circle_btn_inactive
                                    )
                                )
                                .size(roundBtnSize)
                                .padding(roundBtnSize * 0.22f),
                            active = isPowerOn,
                            onClick = airController::decreaseTemp,
                            activeIcon = R.drawable.ic_widget_minus,
                        )
                        Spacer(GlanceModifier.width(tempBtnSpacing))
                        Box(
                            modifier = GlanceModifier.width(tempBoxWidth),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tempText,
                                maxLines = 1,
                                style = TextStyle(
                                    fontSize = tempFontSize,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorProvider(
                                        if (isPowerOn) globalConfig.airAppWidgetTempTextColor
                                        else globalConfig.airAppWidgetSubTextColor
                                    )
                                )
                            )
                        }
                        Spacer(GlanceModifier.width(tempBtnSpacing))
                        ImageButton(
                            modifier = GlanceModifier
                                .background(
                                    ImageProvider(
                                        if (isPowerOn) R.drawable.bg_circle_btn_active
                                        else R.drawable.bg_circle_btn_inactive
                                    )
                                )
                                .size(roundBtnSize)
                                .padding(roundBtnSize * 0.22f),
                            active = isPowerOn,
                            onClick = airController::increaseTemp,
                            activeIcon = R.drawable.ic_widget_plus,
                        )
                        if (showTempExtremes) {
                            Spacer(GlanceModifier.width(tempGap))
                            TempExtremeButton(
                                label = "HI",
                                width = pillWidth,
                                height = pillHeight,
                                fontSize = pillFontSize,
                                selected = isPowerOn &&
                                        (temp >= AirController.TEMP_MAX ||
                                                temp == AirController.TEMP_HIGH),
                            ) { if (isPowerOn) airController.setTempMax() }
                        } else {
                            Spacer(GlanceModifier.defaultWeight())
                        }
                    }
                    Spacer(GlanceModifier.height(12.dp))
                    WindLevelBar(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(horizontal = tempBtnSpacing),
                        level = if (isPowerOn) windLevel else 0,
                        maxLevel = AirController.WIND_MAX,
                        activeColor = globalConfig.airAppWidgetMarkOvalSelectColor,
                        inactiveColor = globalConfig.airAppWidgetMarkOvalColor,
                        onLevelClick = { level ->
                            if (isPowerOn) airController.setWindLevel(level)
                        },
                    )
                    if (showExtras) {
                        Spacer(GlanceModifier.height(6.dp))
                        Text(
                            text = "风量 ${if (isPowerOn) windLevel else 0} 档",
                            style = TextStyle(
                                fontSize = labelFontSize,
                                color = ColorProvider(globalConfig.airAppWidgetSubTextColor)
                            )
                        )
                    }
                }
                // 底部：功能开关排
                Row(
                    modifier = GlanceModifier
                        .padding(top = bottomTopPadding)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FunctionTile(
                        size = tileBtnSize,
                        title = "除雾",
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                        selected = isPowerOn && isFrontDefrost,
                        icon = R.drawable.ic_air_front_defrost,
                        onClick = airController::toggleFrontDefrost,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    FunctionTile(
                        size = tileBtnSize,
                        title = "循环",
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                        selected = cycleMode == 1,
                        icon = if (cycleMode == 1)
                            R.drawable.ic_air_mode_in_cycle
                        else R.drawable.ic_air_mode_out_cycle,
                        onClick = airController::toggleCycle,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    val isDirAvailable = isPowerOn &&
                            (airController.getBlowUp() == 1 ||
                                    airController.getBlowBody() == 1 ||
                                    airController.getBlowFoot() == 1)
                    FunctionTile(
                        size = tileBtnSize,
                        title = "风向",
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                        selected = isDirAvailable,
                        icon = when {
                            airController.getBlowUp() == 1 -> R.drawable.ic_air_dir_face
                            airController.getBlowBody() == 1 -> R.drawable.ic_air_dir_body
                            airController.getBlowFoot() == 1 -> R.drawable.ic_air_dir_foot
                            else -> R.drawable.ic_air_dir_frost
                        },
                        onClick = airController::toggleMode,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    FunctionTile(
                        size = tileBtnSize,
                        title = "APP",
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                        selected = false,
                        icon = R.drawable.ic_relation_air_app,
                        onClick = { ActivityLaunch.openSelfApp() },
                    )
                }
            }
        }
    }
}

/** 底部功能开关块 **/
@Composable
@SuppressLint("RestrictedApi")
private fun FunctionTile(
    size: Dp,
    title: String,
    titleColor: Color,
    selected: Boolean,
    icon: Int,
    onClick: () -> Unit,
) {
    ImageButton(
        modifier = GlanceModifier
            .background(
                ImageProvider(
                    if (selected) R.drawable.bg_btn_active
                    else R.drawable.bg_btn_inactive
                )
            )
            .size(size)
            .padding(size * 0.17f),
        title = title,
        titleColor = titleColor,
        active = true,
        activeIcon = icon,
        onClick = onClick,
    )
}

/** 风量档位条：业内常见的阶梯高度条样式，高度随档位递增；点击档位设置对应风量 **/
@Composable
@SuppressLint("RestrictedApi")
private fun WindLevelBar(
    modifier: GlanceModifier = GlanceModifier,
    level: Int,
    maxLevel: Int,
    activeColor: Color,
    inactiveColor: Color,
    onLevelClick: (Int) -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(maxLevel) { index ->
            val barHeight =
                6.dp + 12.dp * (index.toFloat() / (maxLevel - 1).coerceAtLeast(1))
            // 外层为 24dp 高的透明触控区，避免档位条太矮点不中
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .height(24.dp)
                    .clickable { onLevelClick(index + 1) },
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .cornerRadius(3.dp)
                        .background(
                            if (index < level) activeColor
                            else inactiveColor
                        ),
                    content = {}
                )
            }
            if (index != maxLevel - 1) {
                Spacer(GlanceModifier.width(5.dp))
            }
        }
    }
}

/** 温度极值快捷按钮 (LO / HI)：到达对应极值时高亮 **/
@Composable
@SuppressLint("RestrictedApi")
private fun TempExtremeButton(
    label: String,
    width: Dp,
    height: Dp,
    fontSize: TextUnit,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = GlanceModifier
            .background(
                ImageProvider(
                    if (selected) R.drawable.bg_btn_active
                    else R.drawable.bg_btn_inactive
                )
            )
            .size(width, height)
            .clickable(block = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(Color.White)
            )
        )
    }
}

/** 温度文案：与车机端一致，极值显示 LO/HI **/
private fun formatTemp(temp: Float): String = when (temp) {
    AirController.TEMP_LOW -> "LO"
    AirController.TEMP_HIGH -> "HI"
    AirController.TEMP_NONE -> "--"
    temp.toInt().toFloat() -> "${temp.toInt()}°"
    else -> "$temp°"
}

@Composable
@Preview(widthDp = 400, heightDp = 300)
@OptIn(ExperimentalGlancePreviewApi::class)
private fun AirControlAppWidgetContentPreview() = AirControlAppWidgetContent()
