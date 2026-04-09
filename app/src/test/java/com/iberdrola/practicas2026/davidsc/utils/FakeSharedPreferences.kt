package com.iberdrola.practicas2026.davidsc.utils

import android.content.SharedPreferences

class FakeSharedPreferences : SharedPreferences {

    private val map = mutableMapOf<String, Any>()

    override fun getBoolean(key: String, defValue: Boolean) =
        map[key] as? Boolean ?: defValue

    override fun getInt(key: String, defValue: Int) =
        map[key] as? Int ?: defValue

    fun putInt(key: String, value: Int) {
        map[key] = value
    }

    override fun edit(): SharedPreferences.Editor = Editor()

    inner class Editor : SharedPreferences.Editor {
        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun apply() {}

        // Métodos no usados
        override fun clear() = this
        override fun remove(key: String?) = this
        override fun commit() = true
        override fun putLong(key: String?, value: Long) = this
        override fun putFloat(key: String?, value: Float) = this
        override fun putString(key: String?, value: String?) = this
        override fun putStringSet(key: String?, values: MutableSet<String>?) = this
    }

    // Métodos no usados
    override fun contains(key: String?) = map.containsKey(key)
    override fun getAll(): MutableMap<String, *> = map
    override fun getLong(key: String?, defValue: Long) = defValue
    override fun getFloat(key: String?, defValue: Float) = defValue
    override fun getString(key: String?, defValue: String?) = defValue
    override fun getStringSet(key: String?, defValues: MutableSet<String>?) = defValues
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
}