package com.car.dh.app

import android.app.Application
import com.car.dh.canbus.connection.ConnectionCanbus
import com.car.dh.canbus.connection.ConnectionMain
import com.car.dh.canbus.connection.ConnectionObserver
import com.car.dh.canbus.connection.MsToolkitConnection
import com.car.dh.canbus.ipc.IRemoteToolkit
import com.car.dh.canbus.up.ConnectionCanUp
import com.car.dh.ui.screen.base.CarDataBusDataObserver
import com.car.dh.ui.screen.widget.AirControlAppWidget
import com.car.dh.ui.screen.widget.CarControlAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            // 车机服务连上后 CAN 数据才可读：此刻再刷一次小组件，
            // 避免开机时小组件先按默认值渲染后一直不刷新
            CarDataBusDataObserver.notifyDataChanged()
            CoroutineScope(Dispatchers.Default).launch {
                runCatching { AirControlAppWidget.updateAll() }
                runCatching { CarControlAppWidget.updateAll() }
            }
        }

    }

}