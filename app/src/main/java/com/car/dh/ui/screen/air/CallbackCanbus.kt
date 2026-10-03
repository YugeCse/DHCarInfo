package com.car.dh.ui.screen.air

import com.car.dh.canbus.callback.CallbackCanbusBase
import com.car.dh.canbus.data.DataCanbus
import com.car.dh.canbus.handler.HandlerCanbus
import com.car.dh.canbus.callback.ModuleCallbackCanbusProxy
import com.car.dh.canbus.ipc.IModuleCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/* JADX INFO: loaded from: classes.dex */
class CallbackCanbus : CallbackCanbusBase(),
    CoroutineScope { // com.syu.module.canbus.CallbackCanbusBase

    companion object {
        const val U_CNT_MAX: Int = 2000
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Main

    override fun `in`() {
        val callback: IModuleCallback =
            ModuleCallbackCanbusProxy.getInstance()
        for (i in 0..<U_CNT_MAX) {
            DataCanbus.PROXY.register(callback, i, 1)
        }
        // AirHelper.getInstance().buildUi(new AIR_0438_DJ_YuanJingX1(TheApp.getInstance()));
        // for (int i2 = 10; i2 < 97; i2++) {
        //     DataCanbus.NOTIFY_EVENTS[i2]
        //             .addNotify(AirHelper.SHOW_AND_REFRESH);
        // }
    }

    // com.syu.module.canbus.CallbackCanbusBase
    override fun out() {
        // for (int i = 10; i < 97; i++) {
        //     DataCanbus.NOTIFY_EVENTS[i]
        //             .removeNotify(AirHelper.SHOW_AND_REFRESH);
        // }
        // AirHelper.getInstance().destroyUi();
    }

    // com.syu.ipc.IModuleCallback
    override fun update(
        updateCode: Int,
        ints: IntArray?,
        flts: FloatArray?,
        strs: Array<String?>?
    ) {
        // if (updateCode >= 0 && updateCode < 98) {
        HandlerCanbus.update(updateCode, ints)
        launch { AirControlAppWidget.updateAll() }
        AirStateDataChange.notifyDataChanged()
        // }
    }
}
