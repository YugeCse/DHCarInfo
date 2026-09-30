package com.car.dh.ui.screen.air

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
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
import androidx.glance.layout.ContentScale
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
import androidx.glance.unit.FixedColorProvider
import com.car.dh.R
import com.car.dh.app.DHApplication
import com.car.dh.ui.theme.DHCarInfoTheme

/** 空调控制微件BroadcastReceiver **/
class AirControlAppWidget : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = impl

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
            if (currentSize.width < 60.dp) {
                var maxSize = max(currentSize.width, currentSize.height)
                if (maxSize < 300.dp) maxSize = 300.dp
                currentSize = DpSize(maxSize, maxSize + 20.dp)
            }
            AirControlAppWidgetContent(widgetSize = currentSize)
        }
    }

}

@SuppressLint("RestrictedApi", "ResourceType")
@Composable
private fun AirControlAppWidgetContent(widgetSize: DpSize = DpSize(400.dp, 300.dp)) {
    val dataVersion by AirStateDataChange
        .dataChangeFlow
        .collectAsState(0L)
    key(dataVersion) {
        val airController = AirController.singleton()
        val isPowerOn = airController.getPower() == 1
        val isAcOn = airController.getAc() == 1
        val temp = airController.getTempLeft()
        val isFrontDefrost = airController.getFrontDefrost() == 1
        var activeGear by remember(airController) {
            val raw = airController.getWindLevel()
            mutableIntStateOf(
                raw.coerceIn(
                    AirController.WIND_MIN,
                    AirController.WIND_MAX
                )
            )
        }
        val cycleMode = airController.getCycle()
        val pixelSize =
            Size(widgetSize.width.toPx(), widgetSize.height.toPx())
        val arcPadding = 20.dp.toPx()
        val arcStrokeWidth = 8.dp.toPx()
        val gearTextSize = 18.dp.toPx()
        val tempTextSize = (widgetSize.width / 5f).toPx()
        // 生成 Bitmap (建议宽度与小组件宽度一致，例如 300px)
        val arcBitmap = remember(
            pixelSize,
            temp,
            activeGear,
            tempTextSize
        ) {
            AcArcDrawer.drawAcArc(
                currentTemp = temp,
                totalGears = 8,
                activeGear = activeGear,
                width = pixelSize.width.toInt(),
                height = pixelSize.height.toInt(),
                padding = arcPadding,
                arcStrokeWidth = arcStrokeWidth,
                gearTextSize = gearTextSize,
                tempTextSize = tempTextSize
            )
        }
        Box(
            modifier = GlanceModifier
                // .run {
                //     if (!BuildConfig.DEBUG) this
                //     else background(DHCarInfoTheme.bg)
                // }
                .cornerRadius(android.R.dimen.system_app_widget_background_radius)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            ImageButton(
                modifier = GlanceModifier.size(40.dp),
                active = isPowerOn,
                onClick = airController::togglePower,
                activeIcon = R.drawable.ic_air_status_on,
                inactiveIcon = R.drawable.ic_air_status_off,
            )
            Box(GlanceModifier.fillMaxWidth()) {
                ImageButton(
                    modifier = GlanceModifier.size(36.dp),
                    active = isAcOn,
                    onClick = airController::toggleAc,
                    activeIcon = R.drawable.ic_ac_status_on,
                    inactiveIcon = R.drawable.ic_ac_status_off,
                )
            }
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 显示绘制好的半圆弧
                var reqHeight = widgetSize.width
                if (widgetSize.height < reqHeight)
                    reqHeight = widgetSize.height
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(reqHeight / 2f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        provider = ImageProvider(arcBitmap),
                        contentDescription = "空调档位与温度",
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
                Row(
                    modifier = GlanceModifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "风量",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = FixedColorProvider(DHCarInfoTheme.subText)
                            )
                        )
                        Row(
                            GlanceModifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            Alignment.CenterHorizontally
                        ) {
                            ImageButton(
                                modifier = GlanceModifier.size(42.dp),
                                active = false,
                                activeIcon = R.drawable.ic_data_decrement,
                                onClick = {
                                    val next = (activeGear - 1)
                                        .coerceAtLeast(AirController.WIND_MIN)
                                    if (next != activeGear) {
                                        airController.setWindLevel(next)
                                    }
                                }
                            )
                            ImageButton(
                                modifier = GlanceModifier.size(42.dp),
                                margin = PaddingValues(start = 12.dp),
                                active = false,
                                activeIcon = R.drawable.ic_data_increment,
                                onClick = {
                                    val next = (activeGear + 1)
                                        .coerceAtMost(AirController.WIND_MAX)
                                    if (next != activeGear) {
                                        airController.setWindLevel(next)
                                    }
                                }
                            )
                        }
                    }
                    Spacer(GlanceModifier.width(30.dp))
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "温度",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = FixedColorProvider(DHCarInfoTheme.subText)
                            )
                        )
                        Row(
                            GlanceModifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            Alignment.CenterHorizontally
                        ) {
                            ImageButton(
                                modifier = GlanceModifier.size(42.dp),
                                active = false,
                                activeIcon = R.drawable.ic_data_decrement,
                                onClick = airController::decreaseTemp
                            )
                            ImageButton(
                                modifier = GlanceModifier.size(42.dp),
                                margin = PaddingValues(start = 12.dp),
                                active = false,
                                activeIcon = R.drawable.ic_data_increment,
                                onClick = airController::increaseTemp
                            )
                        }
                    }
                }
                Row(
                    modifier = GlanceModifier
                        .padding(top = 18.dp, start = 30.dp, end = 30.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImageButton(
                        modifier = GlanceModifier
                            .background(
                                ImageProvider(
                                    if (!isFrontDefrost)
                                        R.drawable.bg_btn_inactive
                                    else R.drawable.bg_btn_active
                                )
                            )
                            .size(50.dp)
                            .padding(5.dp),
                        onClick = airController::toggleFrontDefrost,
                        active = isFrontDefrost,
                        activeIcon = R.drawable.ic_air_front_defrost,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    ImageButton(
                        modifier = GlanceModifier
                            .background(
                                ImageProvider(
                                    if (cycleMode !in arrayOf(0, 1))
                                        R.drawable.bg_btn_inactive
                                    else R.drawable.bg_btn_active
                                )
                            )
                            .size(50.dp)
                            .padding(5.dp),
                        onClick = airController::toggleMode,
                        active = cycleMode == 1,
                        inactiveIcon = when (cycleMode) {
                            0 -> R.drawable.ic_air_mode_out_cycle
                            else -> R.drawable.ic_air_mode_auto_cycle
                        },
                        activeIcon = R.drawable.ic_air_mode_in_cycle,
                    )
                    val isDirAvailable =
                        airController.getBlowUp() == 1 ||
                                airController.getBlowBody() == 1 ||
                                airController.getBlowFoot() == 1
                    Spacer(GlanceModifier.defaultWeight())
                    ImageButton(
                        modifier = GlanceModifier
                            .background(
                                ImageProvider(
                                    if (!isDirAvailable)
                                        R.drawable.bg_btn_inactive
                                    else R.drawable.bg_btn_active
                                )
                            )
                            .size(50.dp)
                            .padding(5.dp),
                        onClick = airController::toggleMode,
                        active = isDirAvailable,
                        inactiveIcon = R.drawable.ic_air_dir_frost,
                        activeIcon = when {
                            airController.getBlowUp() == 1 ->
                                R.drawable.ic_air_dir_face

                            airController.getBlowUp() == 1 ->
                                R.drawable.ic_air_dir_body

                            airController.getBlowFoot() == 1 ->
                                R.drawable.ic_air_dir_frost

                            else -> R.drawable.ic_air_dir_frost
                        },
                    )
                }
            }
        }
    }
}


@Composable
private fun ImageButton(
    modifier: GlanceModifier = GlanceModifier,
    margin: PaddingValues? = null,
    active: Boolean,
    @DrawableRes activeIcon: Int,
    @DrawableRes inactiveIcon: Int? = null,
    onClick: () -> Unit
) {
    val layoutDirection = LayoutDirection.Ltr
    val paddingStart =
        margin?.calculateStartPadding(layoutDirection) ?: 0.dp
    val paddingEnd =
        margin?.calculateEndPadding(layoutDirection) ?: 0.dp
    val paddingTop = margin?.calculateTopPadding() ?: 0.dp
    val paddingBottom = margin?.calculateBottomPadding() ?: 0.dp
    Box(
        GlanceModifier.padding(
            start = paddingStart,
            end = paddingEnd,
            top = paddingTop,
            bottom = paddingBottom,
        )
    ) {
        Image(
            modifier = GlanceModifier
                .then(modifier)
                .clickable { onClick() },
            provider = ImageProvider(
                if (active || inactiveIcon == null)
                    activeIcon
                else inactiveIcon
            ),
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )
    }
}


@Composable
@SuppressLint("RestrictedApi")
private fun FunctionButton(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    defaultWidth: Dp = 60.dp,
    modifier: GlanceModifier = GlanceModifier,
) {
    val bgColor =
        if (active) DHCarInfoTheme.accent else DHCarInfoTheme.inactive
    Box(GlanceModifier.then(modifier)) {
        Box(
            modifier = GlanceModifier
                .width(defaultWidth)
                .height(42.dp)
                .cornerRadius(android.R.dimen.system_app_widget_inner_radius)
                .background(bgColor)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = TextStyle(
                    color = ColorProvider(
                        when {
                            active -> Color.Black
                            else -> DHCarInfoTheme.text
                        }
                    ),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun Dp.toSp(): TextUnit {
    val resources = LocalContext.current.resources
    val density = resources.displayMetrics.density
    val fontScale = resources.configuration.fontScale
    return with(Density(density, fontScale)) {
        // 这里使用 Compose 的 Dp.toSp() 扩展函数
        this@toSp.toSp()
    }
}

@Composable
private fun Dp.toPx(): Float {
    val resources = LocalContext.current.resources
    val density = resources.displayMetrics.density
    val fontScale = resources.configuration.fontScale
    return with(Density(density, fontScale)) {
        // 这里使用 Compose 的 Dp.toSp() 扩展函数
        this@toPx.toPx()
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 600)
@OptIn(ExperimentalGlancePreviewApi::class)
private fun AirControlAppWidgetContentPreview() = AirControlAppWidgetContent()