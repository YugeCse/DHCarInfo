package com.car.dh.app

import android.app.Application
import com.car.dh.canbus.connection.ConnectionCanbus
import com.car.dh.canbus.connection.ConnectionMain
import com.car.dh.canbus.connection.ConnectionObserver
import com.car.dh.canbus.connection.MsToolkitConnection
import com.car.dh.canbus.ipc.IRemoteToolkit
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

    /** 启动连接服务任务 **/
    private fun startConnectService() {
        MsToolkitConnection.getInstance().apply {
            addObserver(ConnectionCanbus.getInstance())
            addObserver(ConnectionMain.getInstance())
            // addObserver(ConnectionSound.getInstance())
            addObserver(ConnectionCanUp.getInstance())
            addObserver(InitializerServiceConnectionObserver())
        }.connect(this)
    }

    /** 初始化时，服务连接上后的操作 **/
    private class InitializerServiceConnectionObserver : ConnectionObserver {

        override fun onConnected(iRemoteToolkit: IRemoteToolkit?) {
            GlobalConfig.initConfigs() //初始化配置数据
        }

    }

}