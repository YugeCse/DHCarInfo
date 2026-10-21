package com.car.dh.ui.screen.base

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** CarBus data change notifier. */
object CarDataBusDataObserver {
    private val _dataChangeFlow = MutableStateFlow(0L)

    /** Monotonically increasing revision; unlike currentTimeMillis this cannot collide within a millisecond. */
    val dataChangeFlow: StateFlow<Long> = _dataChangeFlow.asStateFlow()

    /** Notify in-process Compose consumers and explicitly refresh Glance remote widgets. */
    fun notifyDataChanged() {
        _dataChangeFlow.update { it + 1L }
        CarWidgetUpdateCoordinator.requestWidgetRefresh()
    }
}
