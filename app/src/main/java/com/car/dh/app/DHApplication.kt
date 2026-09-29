package com.car.dh.app

import android.app.Application
import android.content.Intent
import com.car.dh.canbus.ConnectionCanbus
import com.car.dh.canbus.ConnectionMain

/** Application对象 **/
class DHApplication : Application() {

    companion object {

        @JvmField
        var isAirWindowFront: Boolean = false

        @JvmField
        var isJumpNewAir: Boolean = false

        @JvmField
        var isBtFront: Boolean = false

        private lateinit var _instance: DHApplication

        @JvmStatic
        fun singleton(): DHApplication = _instance

    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
        startConnectService()
    }

    /**
     * 发送通知
     * @param action 通知行为
     * @param extras 额外参数
     */
    fun sendBroadcast(action: String, extras: Intent.() -> Unit) {
        val intent = Intent(action)
            .apply(extras)
        sendBroadcast(intent)
    }

    /** 获取屏幕方向 **/
    fun getOrientationFromConfiguration(): Int = resources.configuration.orientation


    /** 启动连接服务任务 **/
    private fun startConnectService() {
        val msToolkitConnection =  MsToolkitConnection.getInstance()
        msToolkitConnection.addObserver(ConnectionCanbus.getInstance())
        msToolkitConnection.addObserver(ConnectionMain.getInstance())
        // msToolkitConnection.addObserver(ConnectionSound.getInstance())
        // msToolkitConnection.addObserver(ConnectionCanUp.getInstance())
        msToolkitConnection.connect(this)
    }

}