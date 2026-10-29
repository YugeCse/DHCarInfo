package com.car.dh.ui.screen.component.glance

//noinspection SuspiciousImport
import android.R
import android.content.Context
import android.os.Build
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 小组件 UI 兼容工具 **/
object AppWidgetUiCompat {

    /**
     * 系统小组件背景圆角半径。
     * android.R.dimen.system_app_widget_background_radius 仅 API 31+ 存在，
     * 低版本回退为 15dp（与 bg_app_widget 的圆角保持一致）。
     */
    fun backgroundRadius(context: Context): Dp {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            runCatching {
                val px = context.resources
                    .getDimensionPixelSize(
                        R.dimen.system_app_widget_background_radius
                    )
                if (px > 0) {
                    return (px / context.resources.displayMetrics.density).dp
                }
            }
        }
        return 15.dp
    }
}