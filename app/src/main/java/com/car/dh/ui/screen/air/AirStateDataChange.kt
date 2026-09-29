package com.car.dh.ui.screen.air

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AirStateDataChange {

    private val _dataChangeFlow = MutableStateFlow(0L)

    val dataChangeFlow: StateFlow<Long> = _dataChangeFlow.asStateFlow()

    fun sendDataChangeMessage() {
        _dataChangeFlow.value = System.currentTimeMillis()
    }

}