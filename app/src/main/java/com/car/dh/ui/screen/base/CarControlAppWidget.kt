package com.car.dh.ui.screen.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
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
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.car.dh.R
import com.car.dh.app.DHApplication
import com.car.dh.app.LocalGlobalConfig
import com.car.dh.ui.screen.component.ImageButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** 汽车控制小组件 **/
class CarControlAppWidget : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = impl

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            CoroutineScope(Dispatchers.Default)
                .launch { updateAll() }
        }
    }

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

@Composable
@SuppressLint("RestrictedApi", "ResourceType")
private fun CarControlAppWidgetContent() {
    val dataVersion by CarDataBusDataObserver
        .dataChangeFlow
        .collectAsState(0L)
    key(dataVersion) {
        val globalConfig = LocalGlobalConfig.current
        val carControl = CarBaseController.singleton()
        val driveModeInt by CarControlAppWidget.driveModeInt.collectAsState()
        Box(
            modifier = GlanceModifier
                .cornerRadius(android.R.dimen.system_app_widget_background_radius)
                .run {
                    if (!globalConfig.isAirAppWidgetRenderBackground) this
                    else background(ImageProvider(R.drawable.bg_app_widget))
                }
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = GlanceModifier
                    .padding(top = 20.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "驾驶模式",
                    style = TextStyle(
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                        color = ColorProvider(globalConfig.airAppWidgetSubTextColor)
                    ),
                )
                Row(
                    modifier = GlanceModifier
                        .padding(top = 12.dp, start = 30.dp, end = 30.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImageButton(
                        modifier = GlanceModifier
                            .background(
                                ImageProvider(
                                    if (arrayOf(1, 2).contains(driveModeInt))
                                        R.drawable.bg_btn_inactive
                                    else R.drawable.bg_btn_active
                                )
                            )
                            .size(50.dp)
                            .padding(8.dp),
                        title = "舒适",
                        active = true,
                        activeIcon = R.drawable.ic_drive_mode_default,
                        onClick = {
                            carControl.setDriveMode(0)
                            CarControlAppWidget.updateDriveModeInt(0)
                        },
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    ImageButton(
                        modifier = GlanceModifier
                            .background(
                                ImageProvider(
                                    if (driveModeInt != 1)
                                        R.drawable.bg_btn_inactive
                                    else R.drawable.bg_btn_active
                                )
                            )
                            .size(50.dp)
                            .padding(8.dp),
                        title = "经济",
                        active = true,
                        onClick = {
                            carControl.setDriveMode(1)
                            CarControlAppWidget.updateDriveModeInt(1)
                        },
                        activeIcon = R.drawable.ic_drive_mode_eco,
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    ImageButton(
                        modifier = GlanceModifier
                            .background(
                                ImageProvider(
                                    if (driveModeInt != 2)
                                        R.drawable.bg_btn_inactive
                                    else R.drawable.bg_btn_active
                                )
                            )
                            .size(50.dp)
                            .padding(8.dp),
                        title = "运动",
                        active = true,
                        onClick = {
                            carControl.setDriveMode(2)
                            CarControlAppWidget.updateDriveModeInt(2)
                        },
                        activeIcon = R.drawable.ic_drive_mode_run,
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                }
                Text(
                    text = "天窗控制",
                    style = TextStyle(
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                        color = ColorProvider(globalConfig.airAppWidgetSubTextColor)
                    ),
                    modifier = GlanceModifier.padding(top = 30.dp),
                )
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
                        title = "打开",
                        active = true,
                        activeIcon = R.drawable.ic_sky_door_open,
                        onClick = SkyController::open,
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    ImageButton(
                        modifier = GlanceModifier
                            .background(ImageProvider(R.drawable.bg_btn_inactive))
                            .size(50.dp)
                            .padding(8.dp),
                        title = "关闭",
                        active = true,
                        onClick = SkyController::close,
                        activeIcon = R.drawable.ic_sky_door_close,
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    ImageButton(
                        modifier = GlanceModifier
                            .background(ImageProvider(R.drawable.bg_btn_inactive))
                            .size(50.dp)
                            .padding(8.dp),
                        title = "停止",
                        active = true,
                        onClick = SkyController::stop,
                        activeIcon = R.drawable.ic_sky_door_stop,
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    ImageButton(
                        modifier = GlanceModifier
                            .background(ImageProvider(R.drawable.bg_btn_inactive))
                            .size(50.dp)
                            .padding(8.dp),
                        title = "透气",
                        active = true,
                        onClick = SkyController::freshAir,
                        activeIcon = R.drawable.ic_sky_door_fresh_air,
                        titleColor = globalConfig.airAppWidgetSubTextColor,
                    )
                }
            }
        }
    }
}