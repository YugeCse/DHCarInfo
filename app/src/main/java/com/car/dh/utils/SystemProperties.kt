package com.car.dh.utils

import android.annotation.SuppressLint

@SuppressLint("PrivateApi")
object SystemProperties {

    private val clazz by lazy {
        Class.forName("android.os.SystemProperties")
    }

    @JvmStatic
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val method = clazz.getDeclaredMethod(
            "getBoolean",
            String::class.java,
            Boolean::class.java
        ).apply { isAccessible = true }
        return method.invoke(null, key, defaultValue) as Boolean
    }

}