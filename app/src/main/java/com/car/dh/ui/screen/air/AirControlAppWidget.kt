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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
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
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.FixedColorProvider
import com.car.dh.R
import com.car.dh.app.DHApplication
import com.car.dh.app.LocalGlobalConfig
import com.car.dh.ui.screen.base.CarDataBusDataObserver
import com.car.dh.ui.theme.DHCarInfoTheme
import com.car.dh.utils.ActivityLaunch

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
                if (maxSize < 350.dp) maxSize = 350.dp
                currentSize = DpSize(maxSize, maxSize + 20.dp)
            }
            AirControlAppWidgetContent(widgetSize = currentSize)
        }
    }

}

@Composable
@SuppressLint("RestrictedApi", "ResourceType")
private fun AirControlAppWidgetContent(
    widgetSize: DpSize = DpSize(400.dp, 300.dp)
) {
    val dataVersion by CarDataBusDataObserver
        .dataChangeFlow
        .collectAsState(0L)
    key(dataVersion) {
        val globalConfig = LocalGlobalConfig.current
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
            tempTextSize,
            isPowerOn,
        ) {
            AcArcDrawer.drawAcArc(
                globalConfig = globalConfig,
                active = isPowerOn,
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
                .cornerRadius(android.R.dimen.system_app_widget_background_radius)
                .run {
                    if (!globalConfig.isAirAppWidgetRenderBackground) this
                    else background(ImageProvider(R.drawable.bg_app_widget))
                }
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            ImageButton(
                modifier = GlanceModifier
                    .background(
                        ImageProvider(
                            if (!isPowerOn)
                                R.drawable.bg_circle_btn_inactive
                            else R.drawable.bg_circle_btn_active
                        )
                    )
                    .size(50.dp)
                    .padding(10.dp),
                active = isPowerOn,
                onClick = airController::togglePower,
                activeIcon = R.drawable.ic_air_power_status_on,
                inactiveIcon = R.drawable.ic_air_power_status_off,
            )
            Box(GlanceModifier.fillMaxWidth()) {
                ImageButton(
                    modifier = GlanceModifier
                        .background(
                            ImageProvider(
                                if (!isAcOn)
                                    R.drawable.bg_circle_btn_inactive
                                else R.drawable.bg_circle_btn_active
                            )
                        )
                        .size(50.dp)
                        .padding(10.dp),
                    active = isAcOn,
                    onClick = airController::toggleAc,
                    activeIcon = R.drawable.ic_air_ac_status_on,
                    inactiveIcon = R.drawable.ic_air_ac_status_off,
                )
            }
            Column(
                modifier = GlanceModifier
                    .padding(top = 50.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var reqHeight = widgetSize.width
                if (widgetSize.height < reqHeight)
                    reqHeight = widgetSize.height
                Image(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(reqHeight / 2f),
                    provider = ImageProvider(arcBitmap),
                    contentDescription = "空调档位与温度",
                )
                Row(
                    modifier = GlanceModifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ControlAdjustView(
                        modifier = GlanceModifier.defaultWeight(),
                        title = "风量",
                        active = isPowerOn,
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                        onDecrementClick = {
                            val next = (activeGear - 1)
                                .coerceAtLeast(AirController.WIND_MIN)
                            if (next != activeGear) airController.setWindLevel(next)
                        },
                        onIncrementClick = {
                            val next = (activeGear + 1)
                                .coerceAtMost(AirController.WIND_MAX)
                            if (next != activeGear) airController.setWindLevel(next)
                        }
                    )
                    Spacer(GlanceModifier.width(30.dp))
                    ControlAdjustView(
                        modifier = GlanceModifier.defaultWeight(),
                        title = "温度",
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                        active = isPowerOn,
                        onDecrementClick = airController::decreaseTemp,
                        onIncrementClick = airController::increaseTemp
                    )
                }
                Row(
                    modifier = GlanceModifier
                        .padding(top = 12.dp, start = 30.dp, end = 30.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImageButton(
                        modifier = GlanceModifier
                            .background(ImageProvider(R.drawable.bg_btn_inactive))
                            .size(50.dp)
                            .padding(8.dp),
                        title = "APP",
                        active = true,
                        activeIcon = R.drawable.ic_relation_air_app,
                        onClick = { ActivityLaunch.openSelfApp() },
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    ImageButton(
                        modifier = GlanceModifier
                            .background(
                                ImageProvider(
                                    if (!isPowerOn ||
                                        !isFrontDefrost
                                    ) R.drawable.bg_btn_inactive
                                    else R.drawable.bg_btn_active
                                )
                            )
                            .size(50.dp)
                            .padding(8.dp),
                        title = "除雾",
                        active = isPowerOn && isFrontDefrost,
                        onClick = airController::toggleFrontDefrost,
                        activeIcon = R.drawable.ic_air_front_defrost,
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    ImageButton(
                        modifier = GlanceModifier
                            .background(
                                ImageProvider(
                                    if (!isPowerOn ||
                                        cycleMode !in arrayOf(0, 1)
                                    ) R.drawable.bg_btn_inactive
                                    else R.drawable.bg_btn_active
                                )
                            )
                            .size(50.dp)
                            .padding(8.dp),
                        title = "模式",
                        onClick = airController::toggleCycle,
                        active = cycleMode in arrayOf(0, 1),
                        activeIcon = when (cycleMode) {
                            0 -> R.drawable.ic_air_mode_out_cycle
                            else -> R.drawable.ic_air_mode_in_cycle
                        },
                        inactiveIcon = R.drawable.ic_air_mode_auto_cycle,
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                    val isDirAvailable = (isPowerOn &&
                            (airController.getBlowUp() == 1 ||
                                    airController.getBlowBody() == 1 ||
                                    airController.getBlowFoot() == 1))
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
                            .padding(8.dp),
                        onClick = airController::toggleMode,
                        title = "风向",
                        active = isDirAvailable,
                        activeIcon = when {
                            airController.getBlowUp() == 1 ->
                                R.drawable.ic_air_dir_face

                            airController.getBlowUp() == 1 ->
                                R.drawable.ic_air_dir_body

                            airController.getBlowFoot() == 1 ->
                                R.drawable.ic_air_dir_frost

                            else -> R.drawable.ic_air_dir_frost
                        },
                        inactiveIcon = R.drawable.ic_air_dir_frost,
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                }
            }
        }
    }
}

/** 控制调整视图 **/
@Composable
@SuppressLint("RestrictedApi")
private fun ControlAdjustView(
    modifier: GlanceModifier = GlanceModifier,
    title: String,
    titleColor: Color = DHCarInfoTheme.subText,
    active: Boolean = false,
    onDecrementClick: () -> Unit = {},
    onIncrementClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 13.sp,
                color = FixedColorProvider(titleColor)
            )
        )
        Row(
            GlanceModifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            Alignment.CenterHorizontally
        ) {
            ImageButton(
                onClick = onDecrementClick,
                modifier = GlanceModifier.size(48.dp),
                active = active,
                useColorTint = false,
                activeIcon = R.drawable.ic_data_decrement,
                inactiveIcon = R.drawable.ic_data_decrement_inactive,
            )
            ImageButton(
                onClick = onIncrementClick,
                modifier = GlanceModifier.size(48.dp),
                margin = PaddingValues(start = 20.dp),
                active = active,
                useColorTint = false,
                activeIcon = R.drawable.ic_data_increment,
                inactiveIcon = R.drawable.ic_data_increment_inactive,
            )
        }
    }
}

@Composable
@SuppressLint("RestrictedApi")
private fun ImageButton(
    modifier: GlanceModifier = GlanceModifier,
    margin: PaddingValues? = null,
    title: String? = null,
    titleColor: Color = DHCarInfoTheme.subText,
    active: Boolean,
    useColorTint: Boolean = true,
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
    Column(
        modifier = GlanceModifier.padding(
            start = paddingStart,
            end = paddingEnd,
            top = paddingTop,
            bottom = paddingBottom,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
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
            colorFilter = if (!useColorTint) null else ColorFilter.tint(ColorProvider(Color.White)),
        )
        if (!title.isNullOrEmpty()) {
            Spacer(GlanceModifier.height(3.dp))
            Text(
                text = title,
                style = TextStyle(fontSize = 11.sp, color = ColorProvider(titleColor))
            )
        }
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