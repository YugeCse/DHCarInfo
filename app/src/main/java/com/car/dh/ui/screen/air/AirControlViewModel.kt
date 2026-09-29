package com.car.dh.ui.screen.air

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.car.dh.AirControlAppWidget
import com.car.dh.data.AirControlStateInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AirControlViewModel : ViewModel() {

    val controller by lazy { AirController.singleton() }

    private val _uiState = MutableStateFlow(AirControlStateInfo.default())

    val uiState: StateFlow<AirControlStateInfo> = _uiState.asStateFlow()

    internal fun syncAll() {
        _uiState.value = _uiState.value.copy(
            power = controller.getPower(),
            ac = controller.getAc(),
            cycle = controller.getCycle(),
            frontDefrost = controller.getFrontDefrost(),
            modeUp = controller.getBlowUp(),
            modeBody = controller.getBlowBody(),
            modeFoot = controller.getBlowFoot()
        )
        viewModelScope.launch { AirControlAppWidget.updateAll() }
    }

}