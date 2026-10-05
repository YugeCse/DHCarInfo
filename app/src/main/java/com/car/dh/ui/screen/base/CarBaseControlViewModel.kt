package com.car.dh.ui.screen.base

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.car.dh.app.GlobalConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 汽车基础控制的 UI 状态实体类
 * @param carLanguageValue 车机语言
 * @param driveModeValue 驾驶模式
 * @param isWelcomeLightingEnabled 迎宾灯照明
 * @param homeLightDelayTimeValue 照我回家的延迟时间
 * @param autoLockValue 自动落锁
 * @param isParkUnlocked 停车解锁
 * @param isLockAutoCloseWindow 落锁自动关窗
 * @param findCarIndicatorValue 寻车指示
 * @param isActiveCabinCleanEnabled 主动座舱清洁
 * @param isAirAutoDryEnabled 空调自干燥
 * @param isRegularVentilationEnabled 定时通风
 * @param isTimeSyncOriginalCarEnabled 原车时间同步
 */
@Stable
data class CarBaseControlUiState(
    val carLanguageValue: Int = 0,
    val driveModeValue: Int = 0,
    val isWelcomeLightingEnabled: Boolean = false,
    val homeLightDelayTimeValue: Int = 0,
    val autoLockValue: Int = 0,
    val isParkUnlocked: Boolean = false,
    val isLockAutoCloseWindow: Boolean = false,
    val findCarIndicatorValue: Int = 0,
    val isActiveCabinCleanEnabled: Boolean = false,
    val isAirAutoDryEnabled: Boolean = false,
    val isRegularVentilationEnabled: Boolean = false,
    val isTimeSyncOriginalCarEnabled: Boolean = false
)

class CarBaseControlViewModel : ViewModel() {

    private val globalConfig by lazy { GlobalConfig.singleton() }

    private val carBaseController by lazy { CarBaseController.singleton() }

    private val _uiState by lazy { MutableStateFlow(CarBaseControlUiState()) }

    val uiState: StateFlow<CarBaseControlUiState> = _uiState.asStateFlow()

    private fun applyNewUiState(
        assign: (CarBaseControlUiState) -> CarBaseControlUiState
    ) {
        _uiState.value = assign(_uiState.value)
    }

    init {
        syncCarBaseData()
    }

    /** 同步车辆数据 **/
    fun syncCarBaseData() = applyNewUiState {
        it.copy(
            carLanguageValue = globalConfig.carSystemLanguage,
            driveModeValue = globalConfig.carDriveMode,
            isWelcomeLightingEnabled = carBaseController.isWelcomeLightingEnabled(),
            homeLightDelayTimeValue = carBaseController.getHomeDelay(),
            autoLockValue = carBaseController.getRunAutoLock(),
            isParkUnlocked = carBaseController.isParkUnlockEnabled(),
            isLockAutoCloseWindow = carBaseController.isLockAutoCloseWindowEnabled(),
            findCarIndicatorValue = carBaseController.getFindCarIndicator(),
            isActiveCabinCleanEnabled = carBaseController.isActiveCabinCleanEnabled(),
            isAirAutoDryEnabled = carBaseController.isAirAutoDryEnabled(),
            isRegularVentilationEnabled = carBaseController.isRegularVentilationEnabled(),
            isTimeSyncOriginalCarEnabled = carBaseController.isTimeSyncEnabled()
        )
    }

    /**
     * 设置系统语言
     * @param value 0-中文，1-英语，3-俄语
     */
    fun setSysLanguageValue(value: Int) {
        globalConfig.carSystemLanguage = value
        carBaseController.setLanguage(value)
        applyNewUiState { it.copy(carLanguageValue = value) }
    }

    /**
     * 设置驾驶模式
     * @param value 0-舒适，1-经济，2-运动
     */
    fun setDriveMode(value: Int) {
        globalConfig.carDriveMode = value
        carBaseController.setDriveMode(value)
        applyNewUiState { it.copy(driveModeValue = value) }
    }

    /** 切换迎宾灯是否开启 **/
    fun toggleWelcomeLightingEnable() {
        val value =
            !carBaseController.isWelcomeLightingEnabled()
        carBaseController.setWelcomeLightingEnabled(value)
        applyNewUiState { it.copy(isWelcomeLightingEnabled = value) }
    }

    /**
     * 设置照我回家配置
     * @param value 0-30s、1-60s、2-90s
     */
    fun setHomeDelayForLighting(value: Int) {
        carBaseController.setHomeDelay(value)
        applyNewUiState { it.copy(homeLightDelayTimeValue = value) }
    }

    /**
     * 设置行车落锁设置
     * @param value 0-关闭, 1-10km/h, 2-20km/h
     */
    fun setRunAutoLock(value: Int) {
        carBaseController.setRunAutoLock(value)
        applyNewUiState { it.copy(autoLockValue = value) }
    }

    /** 设置切换停车解锁 **/
    fun toggleParkUnlockEnabled() {
        val value = !carBaseController.isParkUnlockEnabled()
        carBaseController.setParkUnlockEnabled(value)
        applyNewUiState { it.copy(isParkUnlocked = value) }
    }

    /** 设置锁车自动闭窗 **/
    fun toggleLockAutoCloseWindowEnabled() {
        val value =
            !carBaseController.isLockAutoCloseWindowEnabled()
        carBaseController.setLockAutoCloseWindowEnabled(value)
        applyNewUiState { it.copy(isParkUnlocked = value) }
    }

    /**
     * 设置寻车指示
     * @param value 0-仅灯光,1-灯&声
     */
    fun setFindCarIndicator(value: Int) {
        carBaseController.setFindCarIndicator(value)
        applyNewUiState { it.copy(findCarIndicatorValue = value) }
    }

    /** 切换主动座舱清洁开关 **/
    fun toggleActiveCabinCleanEnabled() {
        val value =
            !carBaseController.isActiveCabinCleanEnabled()
        carBaseController.setActiveCabinCleanEnabled(value)
        applyNewUiState { it.copy(isActiveCabinCleanEnabled = value) }
    }

    /** 切换空调自干燥开关 **/
    fun toggleAirAutoDryEnabled() {
        val value = !carBaseController.isAirAutoDryEnabled()
        carBaseController.setAirAutoDryEnabled(value)
        applyNewUiState { it.copy(isAirAutoDryEnabled = value) }
    }

    /** 切换定时通风开关 **/
    fun toggleRegularVentilationEnabled() {
        val value = !carBaseController.isRegularVentilationEnabled()
        carBaseController.setRegularVentilationEnabled(value)
        applyNewUiState { it.copy(isRegularVentilationEnabled = value) }
    }

    /** 切换同步原车车机系统时间 **/
    fun toggleSyncOriginalCarTime() {
        val value = !carBaseController.isTimeSyncEnabled()
        carBaseController.setTimeSyncEnabled(value)
        applyNewUiState { it.copy(isTimeSyncOriginalCarEnabled = value) }
    }

    /** 胎压监测系统校准 **/
    fun calibrateTirePressure() = carBaseController.calibrateTirePressure()

}