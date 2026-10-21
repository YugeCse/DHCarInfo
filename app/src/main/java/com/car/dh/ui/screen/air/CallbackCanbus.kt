package com.car.dh.ui.screen.air

import com.car.dh.canbus.callback.CallbackCanbusBase
import com.car.dh.canbus.data.DataCanbus
import com.car.dh.canbus.handler.HandlerCanbus
import com.car.dh.canbus.callback.ModuleCallbackCanbusProxy
import com.car.dh.canbus.ipc.IModuleCallback
import com.car.dh.ui.screen.base.CarDataBusDataObserver

/* JADX INFO: loaded from: classes.dex */
class CallbackCanbus : CallbackCanbusBase() {

    companion object {
        const val U_CNT_MAX: Int = 2000
    }

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
        // The toolkit registers a wider update range than DataCanbus stores.
        // Ignore invalid codes instead of refreshing widgets for unusable events.
        if (updateCode < 0 || updateCode >= DataCanbus.DATA.size) return

        val previousValue = DataCanbus.DATA[updateCode]
        HandlerCanbus.update(updateCode, ints)
        // Only schedule a widget refresh when its backing integer state changed.
        // Glance compositions are snapshots and are not guaranteed to remain alive
        // to collect StateFlow emissions in the background.
        if (ints != null && ints.isNotEmpty() && previousValue != ints[0]) {
            CarDataBusDataObserver.notifyDataChanged()
        }
    }
}
