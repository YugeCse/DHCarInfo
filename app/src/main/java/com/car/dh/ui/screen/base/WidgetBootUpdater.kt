package com.car.dh.ui.screen.base

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.car.dh.ui.screen.widget.AirControlAppWidget
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

    private const val RETRY_INTERVAL_MS = 3_000L
    private const val MAX_RETRY_COUNT = 12

    /** 桌面绑定分多批完成，绑定成功后追加几轮刷新，覆盖晚绑定的情况 **/
    private val EXTRA_REFRESH_DELAYS_MS = longArrayOf(2_000L, 5_000L, 10_000L)

    /**
     * 开机后恢复小组件真实界面。
     *
     * 重启后桌面（Launcher）对小组件的重新绑定通常晚于 BOOT_COMPLETED 广播，
     * 若此时直接 updateAll，绑定尚未恢复，更新会落到空集合上，
     * 界面就会停留在 previewImage 预览图。
     *
     * 这里先立即尝试一次刷新，然后轮询等待桌面绑定恢复
     * （AppWidgetManager 中能查到 widget id）后再刷新，
     * 并通过 goAsync 保持接收器进程存活直到轮询结束
     * （不加 goAsync 时 onReceive 返回后进程随时可能被回收，轮询根本跑不完）。
     *
     * @param receiver 触发本次更新的广播接收器，用于 goAsync 保活，可为空
     */
    fun updateAfterBoot(
        receiver: BroadcastReceiver? = null,
        context: Context,
        widgetClass: Class<*>,
        updateAll: suspend () -> Unit,
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            val pendingResult = runCatching { receiver?.goAsync() }.getOrNull()
            val appContext = context.applicationContext
            try {
                runCatching { updateAll() }
                var attempts = 0
                while (attempts < MAX_RETRY_COUNT) {
                    delay(RETRY_INTERVAL_MS.milliseconds)
                    attempts++
                    val ids = widgetIds(appContext, widgetClass)
                    if (ids == null || ids.isEmpty()) continue
                    // 桌面已恢复绑定：先刷新一次，再追加几轮刷新覆盖晚绑定
                    CarDataBusDataObserver.notifyDataChanged()
                    runCatching { updateAll() }
                    EXTRA_REFRESH_DELAYS_MS.forEach { delayMs ->
                        delay(delayMs.milliseconds)
                        CarDataBusDataObserver.notifyDataChanged()
                        runCatching { updateAll() }
                    }
                    break
                }
            } catch (e: Exception) {
                Log.e("WidgetBootUpdater", "小组件开机恢复失败：${e.message}")
            } finally {
                runCatching { pendingResult?.finish() }
            }
        }
    }

    /** 查询指定小组件当前已绑定的 id 列表 **/
    private fun widgetIds(context: Context, widgetClass: Class<*>): IntArray? =
        runCatching {
            AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, widgetClass))
        }.getOrNull()

    /**
     * 更新所有小组件（空调 + 车辆控制）
     * @param context 上下文对象
     * @param receiver 触发更新的广播接收器，用于保活，可为空
     */
    fun updateAllAppWidgets(
        context: Context,
        receiver: BroadcastReceiver? = null,
    ) {
        CarDataBusDataObserver.notifyDataChanged()
        updateAfterBoot(
            receiver = receiver,
            context = context,
            widgetClass = AirControlAppWidget::class.java,
            updateAll = { AirControlAppWidget.updateAll() },
        )
        updateAfterBoot(
            receiver = receiver,
            context = context,
            widgetClass = CarControlAppWidget::class.java,
            updateAll = { CarControlAppWidget.updateAll() },
        )
    }

}
