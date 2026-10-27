package com.car.dh.app

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.car.dh.ui.screen.base.WidgetBootUpdater

/** 设备启动广播 **/
class BootReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        // if (intent?.action !in WidgetBootUpdater.BOOT_ACTIONS) return
        // 传入 this 让更新任务通过 goAsync 保活，避免 onReceive 返回后进程被回收
        context?.let { WidgetBootUpdater.updateAllAppWidgets(it, this) }
    }

}