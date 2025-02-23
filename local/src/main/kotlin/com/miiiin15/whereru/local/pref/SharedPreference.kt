package com.miiiin15.whereru.local.pref

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.reflect.TypeToken
import com.miiiin15.whereru.common.extension.gson
import com.miiiin15.whereru.common.extension.toJson
import java.util.Date

open class SharedPreference(context: Context) {

    val sharedPreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun contains(vararg keys: String): Boolean =
        keys.all(sharedPreference::contains)

    fun remove(vararg keys: String): Boolean =
        sharedPreference.edit().apply { keys.forEach { key -> remove(key) } }.commit()

    fun clearAll() {
        sharedPreference.edit().clear().apply()
    }

    fun put(key: String, value: Any) =
        sharedPreference.edit().run {
            @Suppress("UNCHECKED_CAST")
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Boolean -> putBoolean(key, value)
                is Set<*> -> putStringSet(key, value as? Set<String>)
                is Date -> putLong(key, value.time)
                else -> putString(key, value.toJson())
            }
        }.commit()


    inline fun <reified T> get(key: String, defaultValue: T): T {
        return sharedPreference.run {
            when (defaultValue) {
                is String -> getString(key, defaultValue) as T
                is Int -> getInt(key, defaultValue) as T
                is Long -> getLong(key, defaultValue) as T
                is Float -> getFloat(key, defaultValue) as T
                is Boolean -> getBoolean(key, defaultValue) as T
                is Set<*> -> (getStringSet(key, null) ?: defaultValue) as T
                is Date -> Date(getLong(key, defaultValue.time)) as T
                else -> {
                    println("해당 타입은 SharedPreference에서 지원하지 않는 함수입니다.")
                    defaultValue
                }
            }
        }
    }

    inline fun <reified T> get(key: String): T? {
        val json = sharedPreference.getString(key, null) ?: return null
        val result = gson.fromJson<T>(json, object : TypeToken<T>() {}.type)
        return if (result is List<*>) {
            result.filterNotNull() as? T
        } else {
            result
        }
    }

}
