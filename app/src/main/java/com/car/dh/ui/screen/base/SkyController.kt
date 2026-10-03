package com.car.dh.ui.screen.base

import com.car.dh.canbus.data.DataCanbus

/** 天窗控制类 **/
object SkyController {

    /** 停止天窗 **/
    @JvmStatic
    fun stop() {
        DataCanbus.PROXY.cmd(1, intArrayOf(52, 0), null, null)
    }

    /** 关闭天窗 **/
    @JvmStatic
    fun close() {
        DataCanbus.PROXY.cmd(1, intArrayOf(52, 1), null, null)
    }

    /** 打开天窗 **/
    @JvmStatic
    fun open() {
        DataCanbus.PROXY.cmd(1, intArrayOf(52, 2), null, null)
    }

    /**  开天窗透气 **/
    @JvmStatic
    fun freshAir() {
        DataCanbus.PROXY.cmd(1, intArrayOf(52, 3), null, null)
    }

}