package com.car.dh.ui.screen.base

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.car.dh.ui.screen.widget.AirControlAppWidget
import com.car.dh.ui.screen.widget.AirControlAppWidget.Companion.updateAll
import com.car.dh.ui.screen.widget.CarControlAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/** 小组件开机自恢复工具 **/
object WidgetBootUpdater {

    /** 开机相关的广播 action（含车机常用的快速开机广播） **/
    val BOOT_ACTIONS: Set<String> = setOf(
        Intent.ACTION_BOOT_COMPLETED,
        Intent.ACTION_LOCKED_BOOT_COMPLETED,
        "android.intent.action.QUICKBOOT_POWERON",
        Intent.ACTION_REBOOT,
    )

    private const val RETRY_INTERVAL_MS = 5_000L
    private const val MAX_RETRY_COUNT = 12

    /**
     * 开机后恢复小组件真实界面。
     *
     * 重启后桌面（Launcher）对小组件的重新绑定通常晚于 BOOT_COMPLETED 广播，
     * 若此时直接 updateAll，绑定尚未恢复，更新会落到空集合上，
     * 界面就会停留在 previewImage 预览图。
     *
     * 这里先立即尝试一次刷新，然后轮询等待桌面绑定恢复
     * （AppWidgetManager 中能查到 widget id）后再刷新一次，最多约 1 分钟。
     * 通过 goAsync 保持接收器进程存活直到轮询结束。
     */
    fun updateAfterBoot(
        context: Context,
        widgetClass: Class<*>,
        updateAll: suspend () -> Unit,
    ) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.Default).launch {
            try {
                runCatching { updateAll() }
                var attempts = 0
                while (attempts < MAX_RETRY_COUNT) {
                    delay(RETRY_INTERVAL_MS.milliseconds)
                    val ids = runCatching {
                        AppWidgetManager.getInstance(appContext)
                            .getAppWidgetIds(ComponentName(appContext, widgetClass))
                    }.getOrNull()
                    if (ids != null && ids.isNotEmpty()) {
                        CarDataBusDataObserver.notifyDataChanged()
                        runCatching { updateAll() }
                        break
                    }
                    attempts++
                }
            } catch (e: Exception) {
                Log.e("updateAfterBoot", "抱歉，更新小组件失败：${e.message}")
            }
        }
    }

    /**
     * 更新所有小组件
     * @param context 上下文对象
     */
    fun updateAllAppWidgets(context: Context) {
        CarDataBusDataObserver.notifyDataChanged()
        updateAfterBoot(
            context = context,
            updateAll = { updateAll() },
            widgetClass = AirControlAppWidget::class.java,
        )
        updateAfterBoot(
            context = context,
            updateAll = { updateAll() },
            widgetClass = CarControlAppWidget::class.java,
        )
    }

}
