package com.car.dh.ui.screen.air

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.car.dh.R
import com.car.dh.app.DHApplication
import com.car.dh.ui.theme.DHCarInfoTheme
import com.car.dh.utils.TempUtils

/**
 * Implementation of App Widget functionality.
 */
class AirControlAppWidget : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = impl

    companion object {

        private val impl by lazy { AirControlAppWidgetImpl() }

        /** 更新所有的空调组件 **/
        @JvmStatic
        suspend fun updateAll() = impl.updateAll(DHApplication.singleton())

    }

}

class AirControlAppWidgetImpl : GlanceAppWidget() {

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) = provideContent {
        val airController = AirController.singleton()
        val isPowerOn = airController.getPower() == 1
        val temp = airController.getTempLeft()
        var localWind by remember(airController) {
            val raw = airController.getWindLevel()
            mutableIntStateOf(
                raw.coerceIn(
                    AirController.WIND_MIN,
                    AirController.WIND_MAX
                )
            )
        }
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(12.dp)
                .appWidgetBackground()
                //.background(Color.DarkGray)
                .padding(vertical = 12.dp, horizontal = 12.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight(),
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = TempUtils.formatTemp(temp),
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(DHCarInfoTheme.text)
                        )
                    )
                    Text(
                        text = "℃",
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = ColorProvider(DHCarInfoTheme.subText)
                        )
                    )
                }
                Row(
                    modifier = GlanceModifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = GlanceModifier.padding(end = 8.dp),
                        text = "风量",
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = ColorProvider(DHCarInfoTheme.subText)
                        )
                    )
                    Row(
                        modifier = GlanceModifier.defaultWeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(8) { index ->
                            Box(
                                GlanceModifier
                                    .padding(start = if (index == 0) 0.dp else 5.dp)
                                    .defaultWeight()
                            ) {
                                val active = index < localWind
                                val bgColor =
                                    if (active) DHCarInfoTheme.accent else DHCarInfoTheme.subText
                                Box(
                                    GlanceModifier
                                        .fillMaxWidth()
                                        .cornerRadius(4.dp)
                                        .height(15.dp)
                                        .background(bgColor)
                                ) {}
                            }
                        }
                    }
                    RoundIconButton(
                        symbol = "−",
                        onClick = airController::decreaseTemp,
                        modifier = GlanceModifier.padding(start = 8.dp)
                    )
                    RoundIconButton(
                        symbol = "+",
                        onClick = airController::increaseTemp,
                        modifier = GlanceModifier.padding(start = 8.dp)
                    )
                }
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FunctionButton(
                        label = "A/C",
                        active = airController.getAc() == 1,
                        onClick = airController::toggleAc
                    )
                    FunctionButton(
                        label = "前除雾",
                        active = airController.getFrontDefrost() == 1,
                        onClick = airController::toggleFrontDefrost,
                        defaultWidth = 72.dp,
                        modifier = GlanceModifier.padding(start = 8.dp),
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    if (temp < 20.0f) {
                        Image(
                            modifier = GlanceModifier
                                .size(32.dp),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            provider = ImageProvider(R.drawable.ic_temp_snow_flower)
                        )
                    }
                }
            }
            Image(
                modifier = GlanceModifier
                    .size(32.dp)
                    .clickable { airController.togglePower() },
                contentDescription = null,
                contentScale = ContentScale.Fit,
                provider = ImageProvider(if (isPowerOn) R.drawable.ic_air_status_on else R.drawable.ic_air_status_off)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
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
    @SuppressLint("RestrictedApi")
    private fun RoundIconButton(
        symbol: String,
        size: Dp = 40.dp,
        onClick: () -> Unit,
        modifier: GlanceModifier = GlanceModifier,
    ) {
        Box(GlanceModifier.then(modifier)) {
            Box(
                modifier = GlanceModifier
                    .cornerRadius(size)
                    .size(size)
                    .background(DHCarInfoTheme.accent.copy(alpha = 0.15f))
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(DHCarInfoTheme.accent),
                    )
                )
            }
        }
    }

}
