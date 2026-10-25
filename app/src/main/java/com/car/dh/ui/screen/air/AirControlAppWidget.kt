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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.car.dh.R
import com.car.dh.app.DHApplication
import com.car.dh.app.LocalGlobalConfig
import com.car.dh.ui.screen.base.CarDataBusDataObserver
import com.car.dh.ui.screen.base.WidgetBootUpdater
import com.car.dh.ui.screen.component.ImageButton
import com.car.dh.ui.screen.component.WidgetUiCompat
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
                updateAll = { updateAll() },
                widgetClass = AirControlAppWidget::class.java,
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
        globalConfig.isEditMode = true
        val context = LocalContext.current
        val airController = AirController.singleton()
        val isPowerOn = airController.getPower() == 1
        val isAcOn = airController.getAc() == 1
        val tempLeft = airController.getTempLeft()
        val tempRight = airController.getTempRight()
        val isFrontDefrost = airController.getFrontDefrost() == 1
        val windLevel = airController.getWindLevel()
            .coerceIn(AirController.WIND_MIN, AirController.WIND_MAX)
        val cycleMode = airController.getCycle()

        // —— 自适应尺寸：以组件宽度为基准等比缩放，并限制在可用范围内 ——
        val width = widgetSize.width
        val height = widgetSize.height
        val outerPadding = (width * 0.05f).coerceIn(12.dp, 20.dp)
        val cornerBtnSize = (width * 0.15f).coerceIn(42.dp, 58.dp)
        val tileBtnSize = (width * 0.15f).coerceIn(40.dp, 54.dp)
        val tempBtnSpacing = (width * 0.05f).coerceIn(12.dp, 24.dp)
        val bottomTopPadding = (width * 0.03f).coerceIn(8.dp, 14.dp)
        // BYD 风格：风量排在温控胶囊上一排，底部为左右两个温度胶囊
        val windBtnSize = (width * 0.11f).coerceIn(34.dp, 46.dp)
        val pillHeight = (width * 0.16f).coerceIn(52.dp, 64.dp)
        val pillBtnSize = (width * 0.10f).coerceIn(32.dp, 42.dp)
        val pillValueFontSize = (width.value * 0.105f).coerceIn(15f, 22f).sp
        // 温度文案：到极值时自动显示 LO / HI
        val tempTextLeft = formatTemp(tempLeft)
        val tempTextRight = formatTemp(tempRight)
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
                // 中央：功能排 → 风量排 → 双温度胶囊；行间距统一 12dp，
                // 剩余高度平均分给上下（内容垂直居中），避免某两排间距异常大
                Column(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(GlanceModifier.defaultWeight())
                    // 双温度胶囊：BYD 原味，值到极值时显示 LO / HI
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TempStepperPill(
                            modifier = GlanceModifier.defaultWeight(),
                            height = pillHeight,
                            btnSize = pillBtnSize,
                            valueFontSize = pillValueFontSize,
                            valueText = tempTextLeft,
                            valueColor = ColorProvider(
                                if (isPowerOn) globalConfig.airAppWidgetTempTextColor
                                else globalConfig.airAppWidgetSubTextColor
                            ),
                            enabled = isPowerOn,
                            onDecrease = airController::decreaseTemp,
                            onIncrease = airController::increaseTemp,
                        )
                    }
                    Spacer(GlanceModifier.defaultWeight())
                    // 风量排：− 阶梯条 ＋，位于温控胶囊上一排
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(pillHeight)
                            .background(ImageProvider(R.drawable.bg_pill))
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImageButton(
                            modifier = GlanceModifier
                                .background(
                                    ImageProvider(
                                        if (isPowerOn) R.drawable.bg_btn_active
                                        else R.drawable.bg_btn_inactive
                                    )
                                )
                                .size(pillBtnSize)
                                .padding(pillBtnSize * 0.22f),
                            active = isPowerOn,
                            onClick = {
                                if (isPowerOn) airController.setWindLevel(windLevel - 1)
                            },
                            activeIcon = R.drawable.ic_widget_minus,
                        )
                        WindLevelBar(
                            modifier = GlanceModifier
                                .defaultWeight()
                                .padding(horizontal = 6.dp),
                            level = if (isPowerOn) windLevel else 0,
                            maxLevel = AirController.WIND_MAX,
                            activeColor = globalConfig.airAppWidgetMarkOvalSelectColor,
                            inactiveColor = globalConfig.airAppWidgetMarkOvalColor,
                            onLevelClick = { level ->
                                if (isPowerOn) airController.setWindLevel(level)
                            },
                        )
                        ImageButton(
                            modifier = GlanceModifier
                                .background(
                                    ImageProvider(
                                        if (isPowerOn) R.drawable.bg_btn_active
                                        else R.drawable.bg_btn_inactive
                                    )
                                )
                                .size(pillBtnSize)
                                .padding(pillBtnSize * 0.22f),
                            active = isPowerOn,
                            onClick = {
                                if (isPowerOn) airController.setWindLevel(windLevel + 1)
                            },
                            activeIcon = R.drawable.ic_widget_plus,
                        )
                    }
                    Spacer(GlanceModifier.defaultWeight())
                    // 功能按键排
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
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
                                airController.getBlowUp() == 1 ->
                                    R.drawable.ic_air_dir_face

                                airController.getBlowBody() == 1 ->
                                    R.drawable.ic_air_dir_body

                                airController.getBlowFoot() == 1 ->
                                    R.drawable.ic_air_dir_foot

                                else -> R.drawable.ic_air_dir_frost
                            },
                            onClick = airController::toggleMode,
                        )
                        Spacer(GlanceModifier.defaultWeight())
                        FunctionTile(
                            size = tileBtnSize,
                            title = "A/C",
                            titleColor = globalConfig.airAppWidgetSubTextColor,
                            selected = isAcOn,
                            icon = if (isAcOn)
                                R.drawable.ic_air_ac_status_on
                            else R.drawable.ic_air_ac_status_off,
                            onClick = airController::toggleFrontDefrost,
                        )
                    }
                    Spacer(GlanceModifier.defaultWeight())
                    // 顶栏：电源（左） + A/C（右）。（实车无 AUTO 功能，不放置入口）
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FunctionTile(
                            size = tileBtnSize,
                            title = "开关",
                            titleColor = globalConfig.airAppWidgetSubTextColor,
                            selected = isPowerOn,
                            icon = if (isPowerOn)
                                R.drawable.ic_air_power_status_on
                            else R.drawable.ic_air_power_status_off,
                            onClick = airController::toggleFrontDefrost,
                        )
                        Spacer(GlanceModifier.defaultWeight())
                        FunctionTile(
                            size = tileBtnSize,
                            title = "MAX",
                            titleColor = globalConfig.airAppWidgetSubTextColor,
                            selected = isPowerOn,
                            icon = R.drawable.ic_air_wind_max,
                            onClick = { airController.setWindLevel(1) },
                        )
                        Spacer(GlanceModifier.defaultWeight())
                        FunctionTile(
                            size = tileBtnSize,
                            title = "MIN",
                            titleColor = globalConfig.airAppWidgetSubTextColor,
                            selected = isPowerOn,
                            icon = R.drawable.ic_air_wind_min,
                            onClick = { airController.setWindLevel(AirController.WIND_MAX) },
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
                6.dp + 12.dp * (index.toFloat() / (maxLevel - 1)
                    .coerceAtLeast(1))
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
                            if (index < level)
                                activeColor
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

/** 温度胶囊（BYD 风格）：− 值 ＋，值在极值时显示 LO / HI **/
@Composable
@SuppressLint("RestrictedApi")
private fun TempStepperPill(
    modifier: GlanceModifier = GlanceModifier,
    height: Dp,
    btnSize: Dp,
    valueFontSize: TextUnit,
    valueText: String,
    valueColor: ColorProvider,
    enabled: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(height)
            .background(ImageProvider(R.drawable.bg_pill)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(GlanceModifier.width(6.dp))
        ImageButton(
            modifier = GlanceModifier
                .background(
                    ImageProvider(
                        if (enabled)
                            R.drawable.bg_btn_active
                        else R.drawable.bg_btn_inactive
                    )
                )
                .size(btnSize)
                .padding(btnSize * 0.2f),
            active = enabled,
            onClick = onDecrease,
            activeIcon = R.drawable.ic_widget_minus,
        )
        Text(
            modifier = GlanceModifier.defaultWeight(),
            text = valueText,
            maxLines = 1,
            style = TextStyle(
                fontSize = valueFontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = valueColor
            )
        )
        ImageButton(
            modifier = GlanceModifier
                .background(
                    ImageProvider(
                        if (enabled)
                            R.drawable.bg_btn_active
                        else R.drawable.bg_btn_inactive
                    )
                )
                .size(btnSize)
                .padding(btnSize * 0.2f),
            active = enabled,
            onClick = onIncrease,
            activeIcon = R.drawable.ic_widget_plus,
        )
        Spacer(GlanceModifier.width(6.dp))
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
