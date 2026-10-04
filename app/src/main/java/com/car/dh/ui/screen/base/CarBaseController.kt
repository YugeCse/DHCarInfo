package com.car.dh.ui.screen.base

import com.car.dh.canbus.data.DataCanbus


/**
 * 原车设置控制类（CarBaseController）
 *
 * 说明：
 * 1. 所有状态读取均来自 DataCanbus.DATA[updateCode]。
 * 2. 所有设置最终通过 DataCanbus.PROXY.cmd(cmdType, intArrayOf(funcCode, value), null, null) 发送。
 * 3. 开关类功能：value = 1 表示开启，0 表示关闭。
 * 4. 多档类功能：value 范围见各方法注释。
 * 5. 特殊指令已单独标注。
 *
 * 注意：
 * - 不同车型（DataCanbus.DATA[1000]）支持的功能不同，调用前请确认车型是否支持。
 * - 部分功能存在多个更新码（例如车速锁止有 98/135/151），这里只取最常用的一个，实际项目可按车型切换。
 */
class CarBaseController private constructor() {

    companion object {
        // ==================== 命令类型 ====================
        /** 普通设置命令 */
        private const val CMD_SET = 1

        /** 右视进入等特殊命令 */
        private const val CMD_SPECIAL_4 = 4

        /** 保养信息等特殊命令 */
        private const val CMD_SPECIAL_5 = 5

        /** 疲劳驾驶提醒时间等特殊命令 */
        private const val CMD_SPECIAL_6 = 6

        /** 初始化查询命令 */
        private const val CMD_QUERY = 7

        /** RGB 氛围灯命令 */
        private const val CMD_RGB = 9

        /** 原车时间同步命令 */
        private const val CMD_TIME_SYNC = 10

        // ==================== 更新码常量 ====================
        // 车门锁 / 遥控
        const val UPDATE_SPEED_LOCK = 98               // 车速锁止功能
        const val UPDATE_PARK_UNLOCK = 99              // 停车解锁
        const val UPDATE_OPEN_DOOR_LIGHT = 100         // 开门转向灯双闪提示
        const val UPDATE_REMOTE_LOCK_FEEDBACK = 101    // 遥控锁车反馈
        const val UPDATE_LOCK_LIGHT_OFF = 102          // 闭锁后自动熄灭位置灯
        const val UPDATE_SMART_CORNER_LIGHT = 111      // 智能弯道灯
        const val UPDATE_CLOSE_SUNSHADE = 115          // 闭锁关闭遮阳帘
        const val UPDATE_ARMING_PROMPT = 117           // 设防提示
        const val UPDATE_MIRROR_AUTO_FOLD = 130        // 后视镜自动折叠
        const val UPDATE_ACC_OFF_AUTOLOCK = 131        // 熄火自动解锁
        const val UPDATE_REMOTE_LOCK_PROMPT = 132      // 遥控落锁提示
        const val UPDATE_OPEN_DOOR_LIGHT_2 = 133       // 开门转向灯双闪提示（备用）
        const val UPDATE_LOCK_LIGHT_OFF_2 = 134        // 闭锁后自动熄灭位置灯（备用）
        const val UPDATE_SPEED_LOCK_2 = 135            // 车速锁止功能（备用）
        const val UPDATE_ESC = 136                     // ESC
        const val UPDATE_SPEED_LOCK_3 = 151            // 车速锁止功能（备用）
        const val UPDATE_FLAME_OFF_UNLOCK = 152        // 熄火解锁
        const val UPDATE_OPEN_DOOR_LIGHT_3 = 153       // 开门转向灯双闪提示（备用）
        const val UPDATE_REMOTE_LOCK_PROMPT_2 = 154    // 遥控落锁提示（备用）
        const val UPDATE_LOCK_LIGHT_OFF_3 = 155        // 闭锁后自动熄灭位置灯（备用）
        const val UPDATE_AUTO_CLOSE_WINDOW = 156       // 闭锁车门自动关窗（多档）
        const val UPDATE_MIRROR_AUTO_FOLD_2 = 157      // 后视镜自动折叠（备用）
        const val UPDATE_AMBIENT_THEME_COLOR = 158     // 主题颜色设置（多档）
        const val UPDATE_WINDOW_ANTI_PINCH = 166       // 车窗防夹报警
        const val UPDATE_DAYTIME_RUNNING_LIGHT = 167   // 日间行车灯
        const val UPDATE_TRUNK_AUTO_UNLOCK_DIST = 172  // 后备箱自动解锁距离
        const val UPDATE_TRUNK_AUTO_OPEN = 173         // 后备箱自动开启
        const val UPDATE_TRUNK_UNLOCK_NO_KEY = 174     // 后备箱无钥匙解锁
        const val UPDATE_ANY_DOOR_LOCK_ALARM = 175     // 任意门开启锁车报警配置
        const val UPDATE_SMART_NEAR_UNLOCK = 176       // 智能近车解锁
        const val UPDATE_LEAVE_AUTO_LOCK = 177         // 离开自动落锁
        const val UPDATE_LOCK_AUTO_CLOSE_WINDOW = 178  // 闭锁车门自动关窗
        const val UPDATE_PM25_TEST = 179               // PM2.5检测
        const val UPDATE_HOME_DELAY = 181              // 伴我回家持续时间（多档）
        const val UPDATE_VEHICLE_BACKLIGHT = 182       // 整车背光联动（数值）
        const val UPDATE_AIR_AUTO_LEVEL = 183          // 自动风量等级（多档）
        const val UPDATE_REAR_WIPER_REVERSE = 184      // 倒车时后雨刷开启
        const val UPDATE_RIGHT_CAMERA = 185            // 右转向灯开时进入右视
        const val UPDATE_BUZZER = 187                  // 蜂鸣器开关
        const val UPDATE_LOW_SPEED_WARNING = 188       // 低速警告音（多档，特殊）
        const val UPDATE_RAIN_CLOSE_WINDOW = 190       // 雨天关窗功能
        const val UPDATE_POWER_OFF_LOCK_ALARM = 191    // 电源非OFF锁车报警
        const val UPDATE_WELCOME_LIGHTING = 192        // 迎宾照明
        const val UPDATE_REMOTE_UNLOCK = 193           // 遥控解锁
        const val UPDATE_FATIGUE_TIME = 195            // 疲劳驾驶提醒时间（多档，特殊命令类型6）
        const val UPDATE_RUN_AUTO_LOCK = 197           // 行车自动落锁（多档）
        const val UPDATE_WIPER_SPEED_SENSE = 198       // 雨刮器速度感应
        const val UPDATE_READING_LIGHT_DOOR = 199      // 阅读灯门控开关
        const val UPDATE_ACTIVE_CABIN_CLEAN = 201      // 主动座舱清洁
        const val UPDATE_AIR_AUTO_DRY = 202            // 空调自干燥
        const val UPDATE_REGULAR_VENTILATION = 203     // 定时通风
        const val UPDATE_RADAR_SET = 204               // 泊车辅助设置
        const val UPDATE_FRONT_WIPER_MAINTENANCE = 205 // 前雨刮维护功能（特殊：发送当前值）
        const val UPDATE_FIND_CAR_INDICATOR = 206      // 寻车指示（多档）
        const val UPDATE_FRONT_WIPER_MAINTENANCE_2 = 207 // 前雨刮维护功能（备用）
        const val UPDATE_AVAS_DISABLE = 208            // AVAS 禁用
        const val UPDATE_AVM_CALIBRATION = 209         // AVM 道路中标定
        const val UPDATE_SUPER_LOCK = 210              // 超级锁设置（多档）
        const val UPDATE_DRIVE_MODE_1 = 211            // 驾驶模式1（多档）
        const val UPDATE_DRIVE_MODE_2 = 212            // 驾驶模式2（多档）
        const val UPDATE_ENERGY_RECOVERY = 213         // 能量回收（多档）
        const val UPDATE_AMBIENT_LINK_DRIVE = 217      // 氛围灯-关联驾驶模式
        const val UPDATE_AMBIENT_SWITCH = 218          // 氛围灯开关
        const val UPDATE_AMBIENT_BRIGHTNESS = 219      // 氛围灯亮度等级（多档）
        const val UPDATE_AMBIENT_COLOR = 220           // 氛围灯颜色（多档）
        const val UPDATE_BLIND_SPOT = 221              // 盲点监测器
        const val UPDATE_EXTERIOR_LIGHTING = 229       // 车外照明（多档）
        const val UPDATE_HDC = 230                     // 陡坡缓降
        const val UPDATE_TRUNK_OPEN_DEGREE = 232       // 后备箱开启程度（多档）
        const val UPDATE_REVERSE_TILT_MIRROR = 233     // 倒车时倾斜后视镜（多档）
        const val UPDATE_SEAT_EASY_ACCESS = 234        // 座椅便利进出
        const val UPDATE_WIRELESS_CHARGING = 235       // 无线充电
        const val UPDATE_FLOWING_LIGHT = 236           // 流光灯
        const val UPDATE_RGB_R = 237                   // RGB 红色值
        const val UPDATE_RGB_G = 238                   // RGB 绿色值
        const val UPDATE_RGB_B = 239                   // RGB 蓝色值
        const val UPDATE_WARNING_VOLUME = 240          // 警告音音量设置
        const val UPDATE_CARGO_LIGHT = 241             // 货箱照明灯（多档）
        const val UPDATE_TIME_SYNC = 242               // 原车时间同步
        const val UPDATE_AUTO_HOLD = 243               // Auto Hold
        const val UPDATE_WELCOME_LAMP_SIGNAL = 244     // 迎宾灯语
        const val UPDATE_LINK_SPEED = 245              // 与车速联动
        const val UPDATE_BREATHE = 246                 // 呼吸

        // ==================== 功能码常量（CMD_SET 时使用） ====================
        const val FUNC_SPEED_LOCK = 0
        const val FUNC_PARK_UNLOCK = 1
        const val FUNC_OPEN_DOOR_LIGHT = 2
        const val FUNC_REMOTE_LOCK_FEEDBACK = 3
        const val FUNC_LOCK_LIGHT_OFF = 4
        const val FUNC_SMART_CORNER_LIGHT = 5
        const val FUNC_CLOSE_WINDOW = 8
        const val FUNC_CLOSE_SUNSHADE = 9
        const val FUNC_DAYTIME_LIGHT = 10
        const val FUNC_ARMING_PROMPT = 17
        const val FUNC_ESC = 19
        const val FUNC_MIRROR_AUTO_FOLD = 20
        const val FUNC_LOW_SPEED_WARNING = 27
        const val FUNC_AMBIENT_THEME_COLOR = 28
        const val FUNC_ANY_DOOR_LOCK_ALARM = 30
        const val FUNC_SMART_NEAR_UNLOCK = 31
        const val FUNC_LEAVE_AUTO_LOCK = 32
        const val FUNC_WINDOW_ANTI_PINCH = 33
        const val FUNC_TRUNK_AUTO_OPEN = 34
        const val FUNC_TRUNK_AUTO_UNLOCK_DIST = 35
        const val FUNC_TRUNK_UNLOCK_NO_KEY = 36
        const val FUNC_PM25_TEST = 38
        const val FUNC_HOME_DELAY = 39
        const val FUNC_TIRE_CLEAN = 40
        const val FUNC_VEHICLE_BACKLIGHT = 41
        const val FUNC_AIR_AUTO_LEVEL = 42
        const val FUNC_REAR_WIPER_REVERSE = 43
        const val FUNC_RAIN_CLOSE_WINDOW = 45
        const val FUNC_POWER_OFF_LOCK_ALARM = 46
        const val FUNC_WELCOME_LIGHTING = 47
        const val FUNC_REMOTE_UNLOCK = 48
        const val FUNC_RUN_AUTO_LOCK = 49
        const val FUNC_WIPER_SPEED_SENSE = 50
        const val FUNC_READING_LIGHT_DOOR = 51
        const val FUNC_ACTIVE_CABIN_CLEAN = 53
        const val FUNC_AIR_AUTO_DRY = 54
        const val FUNC_REGULAR_VENTILATION = 55
        const val FUNC_RADAR_SET = 56
        const val FUNC_FRONT_WIPER_MAINTENANCE = 57
        const val FUNC_FIND_CAR_INDICATOR = 58
        const val FUNC_FRONT_WIPER_MAINTENANCE_2 = 59
        const val FUNC_AVAS_DISABLE = 60
        const val FUNC_AVM_CALIBRATION = 61
        const val FUNC_SUPER_LOCK = 62
        const val FUNC_DRIVE_MODE_1 = 63
        const val FUNC_DRIVE_MODE_2 = 64
        const val FUNC_ENERGY_RECOVERY = 65
        const val FUNC_BLIND_SPOT = 66
        const val FUNC_WARNING_VOLUME = 67
        const val FUNC_AMBIENT_LINK_DRIVE = 68
        const val FUNC_AMBIENT_SWITCH = 69
        const val FUNC_AMBIENT_BRIGHTNESS = 70
        const val FUNC_AMBIENT_COLOR = 71
        const val FUNC_EXTERIOR_LIGHTING = 85
        const val FUNC_HDC = 86
        const val FUNC_TRUNK_OPEN_DEGREE = 87
        const val FUNC_REVERSE_TILT_MIRROR = 88
        const val FUNC_SEAT_EASY_ACCESS = 89
        const val FUNC_WIRELESS_CHARGING = 90
        const val FUNC_FLOWING_LIGHT = 91
        const val FUNC_CARGO_LIGHT = 92
        const val FUNC_AUTO_HOLD = 93
        const val FUNC_WELCOME_LAMP_SIGNAL = 94
        const val FUNC_LINK_SPEED = 95
        const val FUNC_BREATHE = 96
        const val FUNC_BUZZER = 128

        private val _instance by lazy { CarBaseController() }

        @JvmStatic
        fun singleton() = _instance

    }

    // ==================== 通用发送 ====================
    /**
     * 发送 CAN 指令
     * @param cmdType 命令类型，如 CMD_SET、CMD_QUERY 等
     * @param funcCode 功能码
     * @param value 值
     */
    private fun sendCmd(cmdType: Int, funcCode: Int, value: Int) {
        DataCanbus.PROXY.cmd(cmdType, intArrayOf(funcCode, value), null, null)
    }

    /**
     * 发送 RGB 指令
     */
    private fun sendRgb(r: Int, g: Int, b: Int) {
        DataCanbus.PROXY.cmd(CMD_RGB, intArrayOf(r, g, b), null, null)
    }

    // ==================== 初始化 / 注销 ====================
    /**
     * 初始化查询所有状态。
     * 原代码：for (int i = 1; i <= 70; i++) 发送 cmd(7,{78,i})，跳过 i=44，每 5 条 sleep 20ms。
     * 这里简化为直接循环，如需延时请自行添加。
     */
    fun queryAllStatus() {
        for (i in 1..70) {
            if (i == 44) continue
            sendCmd(CMD_QUERY, 78, i)
        }
    }

    /**
     * 注销 / 退出时发送
     */
    fun release() {
        DataCanbus.PROXY.cmd(CMD_SPECIAL_5, IntArray(1), null, null)
    }

    // ==================== 状态获取（读取 DataCanbus.DATA） ====================
    fun getInt(updateCode: Int): Int = DataCanbus.DATA[updateCode]

    fun getBoolean(updateCode: Int): Boolean = DataCanbus.DATA[updateCode] == 1

    // ==================== 车门锁 / 遥控 ====================
    /** 车速锁止功能：更新码 98，功能码 0，1=开，0=关 */
    fun isSpeedLockEnabled(): Boolean = getBoolean(UPDATE_SPEED_LOCK)
    fun setSpeedLockEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_SPEED_LOCK, if (enabled) 1 else 0)

    /** 停车解锁：更新码 99，功能码 1 */
    fun isParkUnlockEnabled(): Boolean = getBoolean(UPDATE_PARK_UNLOCK)
    fun setParkUnlockEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_PARK_UNLOCK, if (enabled) 1 else 0)

    /** 开门转向灯双闪提示：更新码 100，功能码 2 */
    fun isOpenDoorLightEnabled(): Boolean = getBoolean(UPDATE_OPEN_DOOR_LIGHT)
    fun setOpenDoorLightEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_OPEN_DOOR_LIGHT, if (enabled) 1 else 0)

    /** 遥控锁车反馈：更新码 101，功能码 3 */
    fun isRemoteLockFeedbackEnabled(): Boolean = getBoolean(UPDATE_REMOTE_LOCK_FEEDBACK)
    fun setRemoteLockFeedbackEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_REMOTE_LOCK_FEEDBACK, if (enabled) 1 else 0)

    /** 闭锁后自动熄灭位置灯：更新码 102，功能码 4 */
    fun isLockLightOffEnabled(): Boolean = getBoolean(UPDATE_LOCK_LIGHT_OFF)
    fun setLockLightOffEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_LOCK_LIGHT_OFF, if (enabled) 1 else 0)

    /** 智能弯道灯：更新码 111，功能码 5 */
    fun isSmartCornerLightEnabled(): Boolean = getBoolean(UPDATE_SMART_CORNER_LIGHT)
    fun setSmartCornerLightEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_SMART_CORNER_LIGHT, if (enabled) 1 else 0)

    /** 后视镜自动折叠：更新码 130，功能码 20 */
    fun isMirrorAutoFoldEnabled(): Boolean = getBoolean(UPDATE_MIRROR_AUTO_FOLD)
    fun setMirrorAutoFoldEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_MIRROR_AUTO_FOLD, if (enabled) 1 else 0)

    /** 熄火自动解锁：更新码 131，功能码 1 */
    fun isAccOffAutolockEnabled(): Boolean = getBoolean(UPDATE_ACC_OFF_AUTOLOCK)
    fun setAccOffAutolockEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_PARK_UNLOCK, if (enabled) 1 else 0)

    /** 遥控落锁提示：更新码 132，功能码 3 */
    fun isRemoteLockPromptEnabled(): Boolean = getBoolean(UPDATE_REMOTE_LOCK_PROMPT)
    fun setRemoteLockPromptEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_REMOTE_LOCK_FEEDBACK, if (enabled) 1 else 0)

    /** 车窗防夹报警：更新码 166，功能码 33 */
    fun isWindowAntiPinchEnabled(): Boolean = getBoolean(UPDATE_WINDOW_ANTI_PINCH)
    fun setWindowAntiPinchEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_WINDOW_ANTI_PINCH, if (enabled) 1 else 0)

    /** 后备箱自动开启：更新码 173，功能码 34 */
    fun isTrunkAutoOpenEnabled(): Boolean = getBoolean(UPDATE_TRUNK_AUTO_OPEN)
    fun setTrunkAutoOpenEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_TRUNK_AUTO_OPEN, if (enabled) 1 else 0)

    /** 后备箱无钥匙解锁：更新码 174，功能码 36 */
    fun isTrunkUnlockNoKeyEnabled(): Boolean = getBoolean(UPDATE_TRUNK_UNLOCK_NO_KEY)
    fun setTrunkUnlockNoKeyEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_TRUNK_UNLOCK_NO_KEY, if (enabled) 1 else 0)

    /** 智能近车解锁：更新码 176，功能码 31 */
    fun isSmartNearUnlockEnabled(): Boolean = getBoolean(UPDATE_SMART_NEAR_UNLOCK)
    fun setSmartNearUnlockEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_SMART_NEAR_UNLOCK, if (enabled) 1 else 0)

    /** 离开自动落锁：更新码 177，功能码 32 */
    fun isLeaveAutoLockEnabled(): Boolean = getBoolean(UPDATE_LEAVE_AUTO_LOCK)
    fun setLeaveAutoLockEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_LEAVE_AUTO_LOCK, if (enabled) 1 else 0)

    /** 闭锁车门自动关窗：更新码 178，功能码 8 */
    fun isLockAutoCloseWindowEnabled(): Boolean = getBoolean(UPDATE_LOCK_AUTO_CLOSE_WINDOW)
    fun setLockAutoCloseWindowEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_CLOSE_WINDOW, if (enabled) 1 else 0)

    /** 闭锁车门自动关窗（多档）：更新码 156，功能码 8，值 0=锁车自动关窗，1=长按钥匙自动关窗，2=关闭 */
    fun getAutoCloseWindowMode(): Int = getInt(UPDATE_AUTO_CLOSE_WINDOW)
    fun setAutoCloseWindowMode(mode: Int) = sendCmd(CMD_SET, FUNC_CLOSE_WINDOW, mode)

    // ==================== 灯光 / 迎宾 ====================
    /** 日间行车灯：更新码 167，功能码 10 */
    fun isDaytimeRunningLightEnabled(): Boolean = getBoolean(UPDATE_DAYTIME_RUNNING_LIGHT)
    fun setDaytimeRunningLightEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_DAYTIME_LIGHT, if (enabled) 1 else 0)

    /** 迎宾照明：更新码 192，功能码 47 */
    fun isWelcomeLightingEnabled(): Boolean = getBoolean(UPDATE_WELCOME_LIGHTING)
    fun setWelcomeLightingEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_WELCOME_LIGHTING, if (enabled) 1 else 0)

    /** 伴我回家持续时间：更新码 181，功能码 39，值 0=30s，1=60s，2=90s */
    fun getHomeDelay(): Int = getInt(UPDATE_HOME_DELAY)

    fun setHomeDelay(value: Int) = sendCmd(CMD_SET, FUNC_HOME_DELAY, value)

    /** 主题颜色设置：更新码 158，功能码 28，值 0=蓝，1=红，2=与车速联动，3=黄 */
    fun getAmbientThemeColor(): Int = getInt(UPDATE_AMBIENT_THEME_COLOR)
    fun setAmbientThemeColor(value: Int) = sendCmd(CMD_SET, FUNC_AMBIENT_THEME_COLOR, value)

    /** 车外照明：更新码 229，功能码 85，值 0=off，1=前开，2=后开，3=全开 */
    fun getExteriorLighting(): Int = getInt(UPDATE_EXTERIOR_LIGHTING)
    fun setExteriorLighting(value: Int) = sendCmd(CMD_SET, FUNC_EXTERIOR_LIGHTING, value)

    /** 寻车指示：更新码 206，功能码 58，值 0=仅灯光，1=灯光与喇叭 */
    fun getFindCarIndicator(): Int = getInt(UPDATE_FIND_CAR_INDICATOR)
    fun setFindCarIndicator(value: Int) = sendCmd(CMD_SET, FUNC_FIND_CAR_INDICATOR, value)

    // ==================== 空调 / 座舱 ====================
    /** PM2.5检测：更新码 179，功能码 38 */
    fun isPm25TestEnabled(): Boolean = getBoolean(UPDATE_PM25_TEST)
    fun setPm25TestEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_PM25_TEST, if (enabled) 1 else 0)

    /** 自动风量等级：更新码 183，功能码 42，值 0=弱，1=中，2=强 */
    fun getAirAutoLevel(): Int = getInt(UPDATE_AIR_AUTO_LEVEL)
    fun setAirAutoLevel(value: Int) = sendCmd(CMD_SET, FUNC_AIR_AUTO_LEVEL, value)

    /** 主动座舱清洁：更新码 201，功能码 53 */
    fun isActiveCabinCleanEnabled(): Boolean = getBoolean(UPDATE_ACTIVE_CABIN_CLEAN)
    fun setActiveCabinCleanEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_ACTIVE_CABIN_CLEAN, if (enabled) 1 else 0)

    /** 空调自干燥：更新码 202，功能码 54 */
    fun isAirAutoDryEnabled(): Boolean = getBoolean(UPDATE_AIR_AUTO_DRY)
    fun setAirAutoDryEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_AIR_AUTO_DRY, if (enabled) 1 else 0)

    /** 定时通风：更新码 203，功能码 55 */
    fun isRegularVentilationEnabled(): Boolean = getBoolean(UPDATE_REGULAR_VENTILATION)
    fun setRegularVentilationEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_REGULAR_VENTILATION, if (enabled) 1 else 0)

    // ==================== 驾驶辅助 / 其他 ====================
    /** 泊车辅助设置：更新码 204，功能码 56 */
    fun isRadarSetEnabled(): Boolean = getBoolean(UPDATE_RADAR_SET)
    fun setRadarSetEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_RADAR_SET, if (enabled) 1 else 0)

    /** 前雨刮维护功能：更新码 205，功能码 57，特殊：发送当前值 */
    fun isFrontWiperMaintenanceEnabled(): Boolean = getBoolean(UPDATE_FRONT_WIPER_MAINTENANCE)
    fun setFrontWiperMaintenanceEnabled(enabled: Boolean) {
        // 原代码逻辑：发送当前值，而不是取反
        sendCmd(CMD_SET, FUNC_FRONT_WIPER_MAINTENANCE, if (enabled) 1 else 0)
    }

    /** AVAS 禁用：更新码 208，功能码 60 */
    fun isAvasDisabled(): Boolean = getBoolean(UPDATE_AVAS_DISABLE)
    fun setAvasDisabled(disabled: Boolean) =
        sendCmd(CMD_SET, FUNC_AVAS_DISABLE, if (disabled) 1 else 0)

    /** AVM 道路中标定：更新码 209，功能码 61 */
    fun isAvmCalibrationEnabled(): Boolean = getBoolean(UPDATE_AVM_CALIBRATION)
    fun setAvmCalibrationEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_AVM_CALIBRATION, if (enabled) 1 else 0)

    /** 盲点监测器：更新码 221，功能码 66 */
    fun isBlindSpotEnabled(): Boolean = getBoolean(UPDATE_BLIND_SPOT)
    fun setBlindSpotEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_BLIND_SPOT, if (enabled) 1 else 0)

    /** 陡坡缓降：更新码 230，功能码 86 */
    fun isHdcEnabled(): Boolean = getBoolean(UPDATE_HDC)
    fun setHdcEnabled(enabled: Boolean) = sendCmd(CMD_SET, FUNC_HDC, if (enabled) 1 else 0)

    /** 座椅便利进出：更新码 234，功能码 89 */
    fun isSeatEasyAccessEnabled(): Boolean = getBoolean(UPDATE_SEAT_EASY_ACCESS)
    fun setSeatEasyAccessEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_SEAT_EASY_ACCESS, if (enabled) 1 else 0)

    /** 无线充电：更新码 235，功能码 90 */
    fun isWirelessChargingEnabled(): Boolean = getBoolean(UPDATE_WIRELESS_CHARGING)
    fun setWirelessChargingEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_WIRELESS_CHARGING, if (enabled) 1 else 0)

    /** 流光灯：更新码 236，功能码 91 */
    fun isFlowingLightEnabled(): Boolean = getBoolean(UPDATE_FLOWING_LIGHT)
    fun setFlowingLightEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_FLOWING_LIGHT, if (enabled) 1 else 0)

    /** 警告音音量设置：更新码 240，功能码 67 */
    fun isWarningVolumeEnabled(): Boolean = getBoolean(UPDATE_WARNING_VOLUME)
    fun setWarningVolumeEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_WARNING_VOLUME, if (enabled) 1 else 0)

    /** Auto Hold：更新码 243，功能码 93 */
    fun isAutoHoldEnabled(): Boolean = getBoolean(UPDATE_AUTO_HOLD)
    fun setAutoHoldEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_AUTO_HOLD, if (enabled) 1 else 0)

    /** 迎宾灯语：更新码 244，功能码 94 */
    fun isWelcomeLampSignalEnabled(): Boolean = getBoolean(UPDATE_WELCOME_LAMP_SIGNAL)
    fun setWelcomeLampSignalEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_WELCOME_LAMP_SIGNAL, if (enabled) 1 else 0)

    /** 与车速联动：更新码 245，功能码 95 */
    fun isLinkSpeedEnabled(): Boolean = getBoolean(UPDATE_LINK_SPEED)
    fun setLinkSpeedEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_LINK_SPEED, if (enabled) 1 else 0)

    /** 呼吸：更新码 246，功能码 96 */
    fun isBreatheEnabled(): Boolean = getBoolean(UPDATE_BREATHE)
    fun setBreatheEnabled(enabled: Boolean) = sendCmd(CMD_SET, FUNC_BREATHE, if (enabled) 1 else 0)

    // ==================== 氛围灯 ====================
    /** 氛围灯-关联驾驶模式：更新码 217，功能码 68 */
    fun isAmbientLinkDriveEnabled(): Boolean = getBoolean(UPDATE_AMBIENT_LINK_DRIVE)
    fun setAmbientLinkDriveEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_AMBIENT_LINK_DRIVE, if (enabled) 1 else 0)

    /** 氛围灯开关：更新码 218，功能码 69 */
    fun isAmbientSwitchEnabled(): Boolean = getBoolean(UPDATE_AMBIENT_SWITCH)
    fun setAmbientSwitchEnabled(enabled: Boolean) =
        sendCmd(CMD_SET, FUNC_AMBIENT_SWITCH, if (enabled) 1 else 0)

    /** 氛围灯亮度等级：更新码 219，功能码 70，值 1~10 */
    fun getAmbientBrightness(): Int = getInt(UPDATE_AMBIENT_BRIGHTNESS)
    fun setAmbientBrightness(value: Int) = sendCmd(CMD_SET, FUNC_AMBIENT_BRIGHTNESS, value)

    /** 氛围灯颜色：更新码 220，功能码 71，值 0~10 */
    fun getAmbientColor(): Int = getInt(UPDATE_AMBIENT_COLOR)
    fun setAmbientColor(value: Int) = sendCmd(CMD_SET, FUNC_AMBIENT_COLOR, value)

    /** RGB 红色值：更新码 237 */
    fun getRgbR(): Int = getInt(UPDATE_RGB_R)

    /** RGB 绿色值：更新码 238 */
    fun getRgbG(): Int = getInt(UPDATE_RGB_G)

    /** RGB 蓝色值：更新码 239 */
    fun getRgbB(): Int = getInt(UPDATE_RGB_B)

    /** 设置 RGB，命令类型 9，参数 {R, G, B} */
    fun setRgb(r: Int, g: Int, b: Int) = sendRgb(r, g, b)

    // ==================== 多档 / 特殊 ====================
    /** 低速警告音：更新码 188，功能码 27，值 0=低，1=中，2=高；部分车型带 bit7 开关 */
    fun getLowSpeedWarning(): Int = getInt(UPDATE_LOW_SPEED_WARNING)
    fun setLowSpeedWarning(value: Int) = sendCmd(CMD_SET, FUNC_LOW_SPEED_WARNING, value)

    /** 疲劳驾驶提醒时间：更新码 195，命令类型 6，值 0~9 对应 off/0.5~4.5小时 */
    fun getFatigueTime(): Int = getInt(UPDATE_FATIGUE_TIME)
    fun setFatigueTime(value: Int) {
        DataCanbus.PROXY.cmd(CMD_SPECIAL_6, intArrayOf(value), null, null)
    }

    /** 行车自动落锁：更新码 197，功能码 49，值 0=off，1=10km/h，2=20km/h */
    fun getRunAutoLock(): Int = getInt(UPDATE_RUN_AUTO_LOCK)
    fun setRunAutoLock(value: Int) = sendCmd(CMD_SET, FUNC_RUN_AUTO_LOCK, value)

    /** 超级锁设置：更新码 210，功能码 62，值 0=两次锁车开启，1=两次锁车关闭 */
    fun getSuperLock(): Int = getInt(UPDATE_SUPER_LOCK)
    fun setSuperLock(value: Int) = sendCmd(CMD_SET, FUNC_SUPER_LOCK, value)

    /** 驾驶模式1：更新码 211，功能码 63，值 0=Smart，1=REV，2=EV，3=Save */
    fun getDriveMode1(): Int = getInt(UPDATE_DRIVE_MODE_1)
    fun setDriveMode1(value: Int) = sendCmd(CMD_SET, FUNC_DRIVE_MODE_1, value)

    /** 驾驶模式2：更新码 212，功能码 64，值 0~9 对应不同模式 */
    fun getDriveMode2(): Int = getInt(UPDATE_DRIVE_MODE_2)
    fun setDriveMode2(value: Int) = sendCmd(CMD_SET, FUNC_DRIVE_MODE_2, value)

    /** 能量回收：更新码 213，功能码 65，值 1=L1，2=L2，3=L3 */
    fun getEnergyRecovery(): Int = getInt(UPDATE_ENERGY_RECOVERY)
    fun setEnergyRecovery(value: Int) = sendCmd(CMD_SET, FUNC_ENERGY_RECOVERY, value)

    /** 后备箱开启程度：更新码 232，功能码 87，值 1~3 */
    fun getTrunkOpenDegree(): Int = getInt(UPDATE_TRUNK_OPEN_DEGREE)
    fun setTrunkOpenDegree(value: Int) = sendCmd(CMD_SET, FUNC_TRUNK_OPEN_DEGREE, value)

    /** 倒车时倾斜后视镜：更新码 233，功能码 88，值 1=主驾驶侧，2=副驾驶侧，3=全部，4=全不 */
    fun getReverseTiltMirror(): Int = getInt(UPDATE_REVERSE_TILT_MIRROR)
    fun setReverseTiltMirror(value: Int) = sendCmd(CMD_SET, FUNC_REVERSE_TILT_MIRROR, value)

    /** 货箱照明灯：更新码 241，功能码 92，值 1=Off，2=ON，3=常开 */
    fun getCargoLight(): Int = getInt(UPDATE_CARGO_LIGHT)
    fun setCargoLight(value: Int) = sendCmd(CMD_SET, FUNC_CARGO_LIGHT, value)

    // ==================== 特殊入口 ====================
    /** 右转向灯开时进入右视：更新码 185，命令类型 4，值 0/1 */
    fun isRightCameraEnabled(): Boolean = getBoolean(UPDATE_RIGHT_CAMERA)
    fun setRightCameraEnabled(enabled: Boolean) {
        DataCanbus.PROXY.cmd(CMD_SPECIAL_4, intArrayOf(if (enabled) 1 else 0), null, null)
    }

    /** 原车时间同步：更新码 242，命令类型 10，值 0/1 */
    fun isTimeSyncEnabled(): Boolean = getBoolean(UPDATE_TIME_SYNC)
    fun setTimeSyncEnabled(enabled: Boolean) {
        DataCanbus.PROXY.cmd(CMD_TIME_SYNC, intArrayOf(if (enabled) 1 else 0), null, null)
    }

    /** 胎压监测系统校准：功能码 40，固定发送值 1 */
    fun calibrateTirePressure() {
        sendCmd(CMD_SET, FUNC_TIRE_CLEAN, 1)
    }

    /** 保养信息：命令类型 5，参数 {1} */
    fun requestMaintenanceInfo() {
        DataCanbus.PROXY.cmd(CMD_SPECIAL_5, intArrayOf(1), null, null)
    }

    /** 语言设置：功能码 26，值 0=中文，1=英语，3=俄语（根据实际列表） */
    fun setLanguage(langValue: Int) {
        sendCmd(CMD_SET, 26, langValue)
    }

    /** 驾驶模式弹窗选择：功能码 44，值根据车型不同 */
    fun setDriveMode(modeValue: Int) {
        sendCmd(CMD_SET, 44, modeValue)
    }
}