package com.car.dh.ui.screen.air

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** 空调数据变化通知类 **/
object AirStateDataChange {

    private val _dataChangeFlow = MutableStateFlow(0L)

    /** 数据变化数据流 **/
    val dataChangeFlow: StateFlow<Long> = _dataChangeFlow.asStateFlow()

    /** 通知数据变化 **/
    fun notifyDataChanged() {
        _dataChangeFlow.value = System.currentTimeMillis()
    }

}