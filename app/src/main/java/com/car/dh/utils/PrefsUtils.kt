package com.car.dh.utils

import android.content.Context
import androidx.core.content.edit
import com.car.dh.app.DHApplication

/** 配置信息存储类 **/
internal class PrefsUtils private constructor() {

    companion object {

        private val _instance by lazy { PrefsUtils() }

        @JvmStatic
        fun singleton() = _instance

    }

    private val prefs by lazy {
        DHApplication.singleton()
            .getSharedPreferences("dh_config", Context.MODE_PRIVATE)
    }

    fun clear() = prefs.edit(true) { this.clear() }

    fun remove(key: String) = prefs.edit(true) { this.remove(key) }

    fun put(key: String, value: Int) {
        prefs.edit(true) { putInt(key, value) }
    }

    fun getInt(key: String, defValue: Int) = prefs.getInt(key, defValue)

    fun put(key: String, value: Long) {
        prefs.edit(true) { putLong(key, value) }
    }

    fun getLong(key: String, defValue: Long) = prefs.getLong(key, defValue)

    fun put(key: String, value: Boolean) {
        prefs.edit(true) { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defValue: Boolean) = prefs.getBoolean(key, defValue)

    fun put(key: String, value: String) {
        prefs.edit(true) { putString(key, value) }
    }

    fun getString(key: String, defValue: String) = prefs.getString(key, defValue)

    fun put(key: String, value: Set<String>) {
        prefs.edit(true) { putStringSet(key, value) }
    }

    fun getStringSet(key: String, defValue: Set<String>) = prefs.getStringSet(key, defValue)

}