package com.car.dh.ui.screen.component

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.car.dh.ui.theme.DHCarInfoTheme

/** 小组件 UI 兼容工具 **/
object WidgetUiCompat {

    /**
     * 系统小组件背景圆角半径。
     * android.R.dimen.system_app_widget_background_radius 仅 API 31+ 存在，
     * 低版本回退为 15dp（与 bg_app_widget 的圆角保持一致）。
     */
    fun backgroundRadius(context: Context): androidx.compose.ui.unit.Dp {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            runCatching {
                val px = context.resources
                    .getDimensionPixelSize(
                        android.R.dimen.system_app_widget_background_radius
                    )
                if (px > 0) {
                    return (px / context.resources.displayMetrics.density).dp
                }
            }
        }
        return 15.dp
    }
}

/** 小组件分区标题：左侧强调色条 + 标题文字 **/
@SuppressLint("RestrictedApi")
@Composable
fun WidgetSectionHeader(
    title: String,
    modifier: GlanceModifier = GlanceModifier,
    fontSize: TextUnit = 13.sp,
    titleColor: Color = DHCarInfoTheme.subText,
    accentColor: Color = DHCarInfoTheme.accent,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .width(4.dp)
                .height(14.dp)
                .cornerRadius(2.dp)
                .background(accentColor),
            content = {}
        )
        Spacer(GlanceModifier.width(8.dp))
        Text(
            text = title,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
                color = ColorProvider(titleColor)
            )
        )
    }
}
