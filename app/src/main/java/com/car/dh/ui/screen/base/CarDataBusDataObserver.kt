package com.car.dh.ui.screen.base

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** CarBus数据变化通知类 **/
object CarDataBusDataObserver {

    private val _dataChangeFlow = MutableStateFlow(0L)

    /** 数据变化数据流 **/
    val dataChangeFlow: StateFlow<Long> = _dataChangeFlow.asStateFlow()

    /** 通知数据变化 **/
    fun notifyDataChanged() {
        _dataChangeFlow.value = System.currentTimeMillis()
    }

}