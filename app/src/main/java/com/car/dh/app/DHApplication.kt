package com.car.dh.app

import android.app.Application
import com.car.dh.canbus.ConnectionCanbus
import com.car.dh.canbus.ConnectionMain
import com.car.dh.canbus.up.ConnectionCanUp

/** Application对象 **/
class DHApplication : Application() {

    companion object {

        private lateinit var _instance: DHApplication

        @JvmStatic
        fun singleton(): DHApplication = _instance

    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
        startConnectService()
    }

    /** 获取屏幕方向 **/
    fun getOrientationFromConfiguration(): Int = resources.configuration.orientation


    /** 启动连接服务任务 **/
    private fun startConnectService() {
        val msToolkitConnection = MsToolkitConnection.getInstance()
        msToolkitConnection.addObserver(ConnectionCanbus.getInstance())
        msToolkitConnection.addObserver(ConnectionMain.getInstance())
        // msToolkitConnection.addObserver(ConnectionSound.getInstance())
        msToolkitConnection.addObserver(ConnectionCanUp.getInstance())
        msToolkitConnection.connect(this)
    }

}