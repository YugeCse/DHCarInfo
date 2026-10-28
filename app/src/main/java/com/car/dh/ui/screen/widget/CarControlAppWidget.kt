package com.car.dh.ui.screen.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import com.car.dh.R
import com.car.dh.app.DHApplication
import com.car.dh.app.LocalGlobalConfig
import com.car.dh.ui.screen.base.CarBaseController
import com.car.dh.ui.screen.base.CarDataBusDataObserver
import com.car.dh.ui.screen.base.SkyController
import com.car.dh.ui.screen.component.ImageButton
import com.car.dh.ui.screen.component.WidgetSectionHeader
import com.car.dh.ui.screen.component.WidgetUiCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** 汽车控制小组件 **/
class CarControlAppWidget : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = impl

    companion object {

        private val _driveModeIntFlow: MutableStateFlow<Int> = MutableStateFlow(0)

        val driveModeInt: StateFlow<Int> = _driveModeIntFlow.asStateFlow()

        fun updateDriveModeInt(value: Int) = _driveModeIntFlow.update { value }

        private val impl by lazy { CarControlAppWidgetImpl() }

        /** 更新所有的空调组件 **/
        @JvmStatic
        suspend fun updateAll() = impl.updateAll(DHApplication.singleton())

    }

}


/** 汽车控制微件实现 **/
class CarControlAppWidgetImpl : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { CarControlAppWidgetContent() }
    }

}

/**
 * 汽车控制微件内容。
 *
 * 分区：驾驶模式（舒适/经济/运动）+ 天窗控制（打开/关闭/停止/透气）。
 * 控件尺寸随组件宽度等比缩放，兼容不同桌面格子；
 * 组件高度不足时压缩分区间距、隐藏分区标题。
 */
@Composable
@SuppressLint("RestrictedApi", "ResourceType")
private fun CarControlAppWidgetContent() {
    val dataVersion by CarDataBusDataObserver
        .dataChangeFlow
        .collectAsState(0L)
    key(dataVersion) {
        val globalConfig = LocalGlobalConfig.current
        val context = LocalContext.current
        val width = LocalSize.current.width
        val carControl = CarBaseController.singleton()
        val driveModeInt by CarControlAppWidget.driveModeInt.collectAsState()
        Column(
            modifier = GlanceModifier
                .cornerRadius(WidgetUiCompat.backgroundRadius(context))
                .run {
                    if (!globalConfig.isAirAppWidgetRenderBackground) this
                    else background(ImageProvider(R.drawable.bg_app_widget))
                }
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(GlanceModifier.defaultWeight())
            WidgetSectionHeader(
                title = "驾驶模式",
                fontSize = 14.sp,
                titleColor = globalConfig.airAppWidgetSubTextColor,
            )
            Row(
                modifier = GlanceModifier
                    .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val tileBtnSize = (width * 0.25f).coerceIn(40.dp, 54.dp)
                val iconPadding = 4.dp
                ModeTile(
                    modifier = GlanceModifier.size(tileBtnSize),
                    iconPadding = iconPadding,
                    title = null,
                    titleColor = globalConfig.airAppWidgetSubTextColor,
                    selected = driveModeInt == 0,
                    icon = R.drawable.ic_drive_mode_default,
                    onClick = {
                        carControl.setDriveMode(0)
                        CarControlAppWidget.updateDriveModeInt(0)
                    },
                )
                Spacer(GlanceModifier.defaultWeight())
                ModeTile(
                    modifier = GlanceModifier.size(tileBtnSize),
                    iconPadding = iconPadding,
                    title = null,
                    titleColor = globalConfig.airAppWidgetSubTextColor,
                    selected = driveModeInt == 1,
                    icon = R.drawable.ic_drive_mode_eco,
                    onClick = {
                        carControl.setDriveMode(1)
                        CarControlAppWidget.updateDriveModeInt(1)
                    },
                )
                Spacer(GlanceModifier.defaultWeight())
                ModeTile(
                    modifier = GlanceModifier.size(tileBtnSize),
                    iconPadding = iconPadding,
                    title = null,
                    titleColor = globalConfig.airAppWidgetSubTextColor,
                    selected = driveModeInt == 2,
                    icon = R.drawable.ic_drive_mode_run,
                    onClick = {
                        carControl.setDriveMode(2)
                        CarControlAppWidget.updateDriveModeInt(2)
                    },
                )
            }
            WidgetSectionHeader(
                title = "天窗控制",
                modifier = GlanceModifier.padding(top = 12.dp),
                fontSize = 14.sp,
                titleColor = globalConfig.airAppWidgetSubTextColor,
            )
            Row(
                modifier = GlanceModifier
                    .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val tileBtnSize = (width * 0.25f).coerceIn(40.dp, 54.dp)
                val iconPadding = 4.dp
                ModeTile(
                    modifier = GlanceModifier.size(tileBtnSize),
                    iconPadding = iconPadding,
                    title = null,
                    titleColor = globalConfig.airAppWidgetSubTextColor,
                    selected = false,
                    icon = R.drawable.ic_sky_door_open,
                    onClick = SkyController::open,
                )
                Spacer(GlanceModifier.defaultWeight())
                ModeTile(
                    modifier = GlanceModifier.size(tileBtnSize),
                    iconPadding = iconPadding,
                    title = null,
                    titleColor = globalConfig.airAppWidgetSubTextColor,
                    selected = false,
                    icon = R.drawable.ic_sky_door_close,
                    onClick = SkyController::close,
                )
                Spacer(GlanceModifier.defaultWeight())
                ModeTile(
                    modifier = GlanceModifier.size(tileBtnSize),
                    iconPadding = iconPadding,
                    title = null,
                    titleColor = globalConfig.airAppWidgetSubTextColor,
                    selected = false,
                    icon = R.drawable.ic_sky_door_stop,
                    onClick = SkyController::stop,
                )
                Spacer(GlanceModifier.defaultWeight())
                ModeTile(
                    modifier = GlanceModifier.size(tileBtnSize),
                    iconPadding = iconPadding,
                    title = null,
                    titleColor = globalConfig.airAppWidgetSubTextColor,
                    selected = false,
                    icon = R.drawable.ic_sky_door_fresh_air,
                    onClick = SkyController::freshAir,
                )
            }
            Spacer(GlanceModifier.defaultWeight())
        }
    }
}

/**
 * 功能方块按钮：选中态高亮
 * @param modifier 修饰器
 * @param iconPadding 图标内间距
 */
@Composable
@SuppressLint("RestrictedApi")
private fun ModeTile(
    modifier: GlanceModifier = GlanceModifier,
    title: String?,
    titleColor: Color,
    selected: Boolean,
    icon: Int,
    iconPadding: Dp = 0.dp,
    onClick: () -> Unit,
) {
    ImageButton(
        modifier = GlanceModifier
            .then(modifier)
            .background(
                ImageProvider(
                    if (selected) R.drawable.bg_btn_active
                    else R.drawable.bg_btn_inactive
                )
            )
            .padding(iconPadding),
        title = title,
        titleColor = titleColor,
        active = true,
        activeIcon = icon,
        onClick = onClick,
    )
}
