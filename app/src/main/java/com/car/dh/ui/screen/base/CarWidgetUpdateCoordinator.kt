package com.car.dh.ui.screen.base

import android.util.Log
import com.car.dh.ui.screen.air.AirControlAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Coalesces high-frequency CAN notifications into timely Glance widget updates. */
object CarWidgetUpdateCoordinator {
    private const val TAG = "CarWidgetUpdater"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val requests = Channel<Unit>(Channel.CONFLATED)

    init {
        scope.launch {
            for (ignored in requests) {
                // CAN updates can arrive in bursts. Coalesce the burst, but keep refreshes prompt.
                delay(100)
                while (requests.tryReceive().isSuccess) { /* drain pending refreshes */ }
                try {
                    AirControlAppWidget.updateAll()
                } catch (exception: Exception) {
                    Log.w(TAG, "Failed to refresh air widget", exception)
                }
                try {
                    CarControlAppWidget.updateAll()
                } catch (exception: Exception) {
                    Log.w(TAG, "Failed to refresh car widget", exception)
                }
            }
        }
    }

    fun requestWidgetRefresh() {
        requests.trySend(Unit)
    }
}
