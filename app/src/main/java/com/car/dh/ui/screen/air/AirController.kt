package com.car.dh.ui.screen.air

import com.car.dh.AirControlAppWidget
import com.car.dh.canbus.DataCanbus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds


class AirController private constructor(
    val carType: Int = CAR_RZC_XP1_YuanJingX1
) : CoroutineScope {

    companion object {
        const val CAR_RZC_XP1_YuanJingX1 = 196747
        const val CAR_RZC_XP1_YuanJingX6 = 131211
        const val CAR_RZC_XP1_18YuanJingSUV = 721035
        const val CAR_RZC_XP1_18YuanJingSUV_H = 852107
        const val CAR_RZC_XP1_16YuanJingSUV = 917643
        const val CAR_RZC_XP1_16YuanJingSUV_H = 983179
        const val CAR_RZC_XP1_18YuanJing = 1114251
        const val CAR_RZC_XP1_YuanJingX3_19_20 = 1835147
        const val CAR_RZC_XP1_20YuanJingSUV = 2031755
        const val CAR_RZC_XP1_YuanJingX3Pro_21 = 3604619
        const val CAR_RZC_Jili_15YuanJing = 3014795
        const val CAR_RZC_XP1_20YuanJingSUV_H = 3997835

        const val C_CONTRAL = 0

        const val U_AIR_POWER = 10
        const val U_AIR_AC = 11
        const val U_AIR_CYCLE = 12
        const val U_AIR_AUTO = 13
        const val U_AIR_REAR = 16
        const val U_AIR_FRONT = 65

        const val U_AIR_SEATHEAT_LEFT = 29

        const val U_AIR_SEATHEAT_RIGHT = 30
        const val U_AIR_BLOW_UP_LEFT = 18
        const val U_AIR_BLOW_BODY_LEFT = 19
        const val U_AIR_BLOW_FOOT_LEFT = 20
        const val U_AIR_WIND_LEVEL_LEFT = 21
        const val U_AIR_TEMP_LEFT = 27
        const val U_AIR_TEMP_UNIT = 37
        const val U_AIR_TEMP_RIGHT_X1 = 27
        const val U_AIR_TEMP_RIGHT_OTHER = 28

        val OBSERVED_IDS = intArrayOf(
            U_AIR_POWER, U_AIR_AC, U_AIR_CYCLE, U_AIR_AUTO,
            U_AIR_REAR, U_AIR_FRONT,
            U_AIR_BLOW_UP_LEFT, U_AIR_BLOW_BODY_LEFT, U_AIR_BLOW_FOOT_LEFT,
            U_AIR_WIND_LEVEL_LEFT, U_AIR_TEMP_LEFT, U_AIR_TEMP_UNIT
        )

        const val TEMP_LOW = -1f
        const val TEMP_HIGH = -2f
        const val TEMP_NONE = -3f

        // 温度范围
        const val TEMP_MIN = 16.0f
        const val TEMP_MAX = 32.0f
        const val TEMP_STEP = 0.5f

        // 默认温度（未从 DATA 读到有效值时使用）
        const val TEMP_DEFAULT = 24.0f

        // 风量范围
        const val WIND_MIN = 0
        const val WIND_MAX = 8

        private val _instance by lazy { AirController() }

        @JvmStatic
        fun singleton(): AirController = _instance
    }

    private val isX1Family: Boolean
        get() = intArrayOf(
            CAR_RZC_XP1_YuanJingX1, CAR_RZC_XP1_YuanJingX6,
            CAR_RZC_XP1_18YuanJingSUV, CAR_RZC_XP1_18YuanJingSUV_H,
            CAR_RZC_XP1_16YuanJingSUV, CAR_RZC_XP1_16YuanJingSUV_H,
            CAR_RZC_XP1_18YuanJing, CAR_RZC_XP1_YuanJingX3_19_20,
            CAR_RZC_XP1_20YuanJingSUV, CAR_RZC_XP1_YuanJingX3Pro_21,
            CAR_RZC_Jili_15YuanJing, CAR_RZC_XP1_20YuanJingSUV_H
        ).contains(carType)

    private val uAirTempRight: Int
        get() = if (isX1Family) U_AIR_TEMP_RIGHT_X1 else U_AIR_TEMP_RIGHT_OTHER

    // ---------------- 发送 ----------------

    private fun send(vararg bytes: Int) {
        DataCanbus.PROXY.cmd(C_CONTRAL, bytes, null, null)
        launch {
            delay(250.milliseconds)
            AirControlAppWidget.updateAll()
        } //发送更新小组件的方法
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    fun pressKey(bytes: IntArray) = send(*bytes)

    fun releaseKey() {
        DataCanbus.PROXY.cmd(
            C_CONTRAL,
            IntArray(6),
            null,
            null
        )
        launch {
            delay(500.milliseconds)
            AirStateDataChange.sendDataChangeMessage()
        }
    }

    // ---------------- 状态读取 ----------------
    fun getPower(): Int = DataCanbus.DATA[U_AIR_POWER]

    fun getAc(): Int = DataCanbus.DATA[U_AIR_AC]

    fun getCycle(): Int = DataCanbus.DATA[U_AIR_CYCLE]

    fun getAuto(): Int = DataCanbus.DATA[U_AIR_AUTO]

    fun getFrontDefrost(): Int = DataCanbus.DATA[U_AIR_FRONT]

    fun getRearDefrost(): Int = DataCanbus.DATA[U_AIR_REAR]

    fun getWindLevel(): Int = DataCanbus.DATA[U_AIR_WIND_LEVEL_LEFT]

    fun getBlowUp(): Int = DataCanbus.DATA[U_AIR_BLOW_UP_LEFT]

    fun getBlowBody(): Int = DataCanbus.DATA[U_AIR_BLOW_BODY_LEFT]

    fun getBlowFoot(): Int = DataCanbus.DATA[U_AIR_BLOW_FOOT_LEFT]

    fun getTempLeft(): Float = convertTemp(DataCanbus.DATA[U_AIR_TEMP_LEFT])

    fun getTempRight(): Float = convertTemp(DataCanbus.DATA[uAirTempRight])

    private fun convertTemp(raw: Int): Float {
        if (raw == 1048576) return TEMP_LOW
        if (raw == 1048577) return TEMP_HIGH
        if (raw == 1048578) return TEMP_NONE
        val t = if (raw in 32..34) (raw - 32) * 5 + 160 else raw * 5 + 170
        return t / 10f
    }

    // ---------------- 控制 ----------------

    fun togglePower() = pressKey(intArrayOf(0x80, 0, 0, 0, 0, 0))
    fun toggleAc() = pressKey(intArrayOf(0x02, 0, 0, 0, 0, 0))
    fun toggleFrontDefrost() = pressKey(intArrayOf(0x10, 0, 0, 0, 0, 0))
    fun toggleCycle() = pressKey(intArrayOf(0, 0, 0x01, 0, 0, 0))
    fun toggleMode() = pressKey(intArrayOf(0x40, 0, 0, 0, 0, 0))

    fun increaseTemp() = pressKey(intArrayOf(0, 0, 0, 0x02, 0x02, 0))
    fun decreaseTemp() = pressKey(intArrayOf(0, 0, 0, 0x01, 0x01, 0))

    fun setWindLevel(level: Int) {
        val v = level.coerceIn(WIND_MIN, WIND_MAX)
        pressKey(intArrayOf(0, v shl 4, 0, 0, 0, 0))
    }
}