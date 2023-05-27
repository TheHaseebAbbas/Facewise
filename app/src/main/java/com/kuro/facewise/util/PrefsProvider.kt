package com.kuro.facewise.util

import android.content.Context
import android.content.SharedPreferences


class PrefsProvider(context: Context) {
    companion object {
        const val PREF_NAME = "FaceWiseApp"
    }

    private val sharedPreferences: SharedPreferences =
        context.createDeviceProtectedStorageContext().getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    fun getString(key: String) = sharedPreferences.getString(key, null)

    fun setString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value)
            .apply()
    }

    fun getInt(key: String) = sharedPreferences.getInt(key, -1)

    fun setInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value)
            .apply()
    }

    fun getFloat(key: String) = sharedPreferences.getFloat(key, -1F)

    fun setFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value)
            .apply()
    }

    fun getLong(key: String) = sharedPreferences.getLong(key, -1L)

    fun setLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value)
            .apply()
    }

    fun getBool(key: String) = sharedPreferences.getBoolean(key, false)

    fun setBool(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value)
            .apply()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}