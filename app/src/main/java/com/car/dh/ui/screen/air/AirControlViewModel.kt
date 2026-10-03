package com.car.dh.ui.screen.air

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.car.dh.data.AirControlStateInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/** 空调控制 UI 状态类 **/
@Stable
data class AirControlUiState(
    val airStateInfo: AirControlStateInfo,
    val isAirSettingsDialogVisible: Boolean
) {

    companion object {

        @JvmStatic
        fun default(): AirControlUiState {
            return AirControlUiState(
                isAirSettingsDialogVisible = false,
                airStateInfo = AirControlStateInfo.default(),
            )
        }

    }

}

/** 空调界面的ViewModel对象 **/
class AirControlViewModel : ViewModel() {

    val controller by lazy { AirController.singleton() }

    private val _uiState = MutableStateFlow(AirControlUiState.default())

    val uiState: StateFlow<AirControlUiState> = _uiState.asStateFlow()

    /** 同步空调状态数据 **/
    internal fun syncAirStateData() {
        _uiState.value = _uiState.value.copy(
            airStateInfo = AirControlStateInfo(
                power = controller.getPower(),
                ac = controller.getAc(),
                cycle = controller.getCycle(),
                frontDefrost = controller.getFrontDefrost(),
                modeUp = controller.getBlowUp(),
                modeBody = controller.getBlowBody(),
                modeFoot = controller.getBlowFoot()
            ),
        )
        viewModelScope.launch {
            AirControlAppWidget.updateAll()
            AirStateDataChange.notifyDataChanged()
        }
    }

    /** 获取或设置空调设置弹窗是否可见 **/
    var isAirSettingDialogVisible: Boolean
        get() = _uiState.value.isAirSettingsDialogVisible
        set(value) {
            _uiState.value = _uiState.value.copy(isAirSettingsDialogVisible = value)
        }

}