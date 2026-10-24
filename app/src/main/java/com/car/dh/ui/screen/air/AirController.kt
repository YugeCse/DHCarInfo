package com.car.dh.ui.screen.air

import com.car.dh.canbus.data.DataCanbus
import com.car.dh.ui.screen.base.CarDataBusDataObserver
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
    // 6 字节按键模拟帧，格式来自原车 YuanJingX1AirControlAct.sendCmd()（本车型权威实现）：
    // b0: 0x80电源 0x02A/C 0x10前除雾 0x40风向 0x20AUTO
    // b1: 高4位=风量档位 0x04后除雾
    // b2: 0x01内外循环
    // b3: 0x02温度+ 0x01温度-
    // 按下 100ms 后必须补发全零释放帧（原车如此）

    private fun send(vararg bytes: Int) {
        DataCanbus.PROXY.cmd(C_CONTRAL, bytes, null, null)
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    fun pressKey(bytes: IntArray) {
        send(*bytes)
        launch {
            delay(100.milliseconds)
            releaseKey()
            delay(400.milliseconds)
            AirControlAppWidget.updateAll()
            CarDataBusDataObserver.notifyDataChanged()
        } //发送更新小组件的方法
    }

    fun releaseKey() {
        DataCanbus.PROXY.cmd(
            C_CONTRAL,
            IntArray(6),
            null,
            null
        )
    }

    // ---------------- 状态读取 ----------------
    /** 获取空调是否打开，1：已打开 **/
    fun getPower(): Int = DataCanbus.DATA[U_AIR_POWER]

    /** 获取 AC 是否打开，1：已打开 **/
    fun getAc(): Int = DataCanbus.DATA[U_AIR_AC]

    /** 获取循环模式，0-外循环，1-内循环，其他：自动 **/
    fun getCycle(): Int = DataCanbus.DATA[U_AIR_CYCLE]

    /** 获取是否是自动模式 **/
    fun getAuto(): Int = DataCanbus.DATA[U_AIR_AUTO]

    /** 获取前档除雾是否开启，1：已开启 **/
    fun getFrontDefrost(): Int = DataCanbus.DATA[U_AIR_FRONT]

    fun getRearDefrost(): Int = DataCanbus.DATA[U_AIR_REAR]

    /** 获取当前风量 **/
    fun getWindLevel(): Int = DataCanbus.DATA[U_AIR_WIND_LEVEL_LEFT]

    /** 获取风向，迎面吹 **/
    fun getBlowUp(): Int = DataCanbus.DATA[U_AIR_BLOW_UP_LEFT]

    /** 获取风向，吹面+吹脚 **/
    fun getBlowBody(): Int = DataCanbus.DATA[U_AIR_BLOW_BODY_LEFT]

    /** 获取风向，吹脚 **/
    fun getBlowFoot(): Int = DataCanbus.DATA[U_AIR_BLOW_FOOT_LEFT]

    /** 获取左边温度 **/
    fun getTempLeft(): Float = convertTemp(DataCanbus.DATA[U_AIR_TEMP_LEFT])

    /** 获取右边温度 **/
    fun getTempRight(): Float = convertTemp(DataCanbus.DATA[uAirTempRight])

    private fun convertTemp(raw: Int): Float {
        if (raw == 1048576) return TEMP_LOW
        if (raw == 1048577) return TEMP_HIGH
        if (raw == 1048578) return TEMP_NONE
        val t = if (raw in 32..34) (raw - 32) * 5 + 160 else raw * 5 + 170
        return t / 10f
    }

    // ---------------- 控制 ----------------
    // 均为 6 字节按键模拟帧，与原车 YuanJingX1AirControlAct 一致

    /** 电源切换（b0=0x80） **/
    fun togglePower() = pressKey(intArrayOf(0x80, 0, 0, 0, 0, 0))

    /** A/C 切换（b0=0x02） **/
    fun toggleAc() = pressKey(intArrayOf(0x02, 0, 0, 0, 0, 0))

    /** 前除雾切换（b0=0x10） **/
    fun toggleFrontDefrost() = pressKey(intArrayOf(0x10, 0, 0, 0, 0, 0))

    /**
     * 后除雾切换（b1=0x04）。
     * 注意：实车（帝豪22款尊贵版）无后除雾功能，协议方法保留但 UI 不挂入口。
     **/
    fun toggleRearDefrost() = pressKey(intArrayOf(0, 0x04, 0, 0, 0, 0))

    /** 内外循环切换（b2=0x01） **/
    fun toggleCycle() = pressKey(intArrayOf(0, 0, 0x01, 0, 0, 0))

    /**
     * 自动模式切换（b0=0x20）。
     * 注意：实车（帝豪22款尊贵版）无 AUTO 功能，协议方法保留但 UI 不挂入口。
     **/
    fun toggleAuto() = pressKey(intArrayOf(0x20, 0, 0, 0, 0, 0))

    /** 风向切换（b0=0x40） **/
    fun toggleMode() = pressKey(intArrayOf(0x40, 0, 0, 0, 0, 0))

    /** 温度升高（b3=0x02，单步 +0.5°C） **/
    fun increaseTemp() = pressKey(intArrayOf(0, 0, 0, 0x02, 0, 0))

    /** 温度降低（b3=0x01，单步 -0.5°C） **/
    fun decreaseTemp() = pressKey(intArrayOf(0, 0, 0, 0x01, 0, 0))

    /**
     * 快速将温度设定到目标值（用于 LO/HI 极值快捷键）。
     * CAN 协议仅支持单步 ±0.5°C 增减，按原车节奏（按下→100ms→释放）逐步发送。
     */
    fun setTempTo(target: Float) {
        val current = getTempLeft()
        val clamped = target.coerceIn(TEMP_MIN, TEMP_MAX)
        // 当前温度无效（LO/HI/未读取）或已到位时忽略
        if (current !in TEMP_MIN..TEMP_MAX || current == clamped) return
        val key = if (clamped > current) 0x02 else 0x01
        val steps = ((clamped - current) / TEMP_STEP).toInt().coerceAtLeast(1)
        launch {
            repeat(steps) {
                send(0, 0, 0, key, 0, 0)
                delay(100.milliseconds)
                releaseKey()
                delay(40.milliseconds)
            }
            AirControlAppWidget.updateAll()
            CarDataBusDataObserver.notifyDataChanged()
        }
    }

    /** 温度直接调到最高 (HI) **/
    fun setTempMax() = setTempTo(TEMP_MAX)

    /** 温度直接调到最低 (LO) **/
    fun setTempMin() = setTempTo(TEMP_MIN)

    fun setWindLevel(level: Int) {
        val v = level.coerceIn(WIND_MIN, WIND_MAX)
        pressKey(intArrayOf(0, v shl 4, 0, 0, 0, 0))
    }
}