package com.example.kotlinapp

import android.content.Context
import java.util.*

object SessionManagerUtil {
    private const val SESSION_PREFS_KEY = "pref"
    private const val SESSION_EXPIRATION_KEY = "session_expiration"

    fun startUserSession(context: Context, expiresIn: Int) {
        val sharedPreferences = context.getSharedPreferences(SESSION_PREFS_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val currentTime = Date().time
        val expirationTime = currentTime + expiresIn * 1000
        editor.putLong(SESSION_EXPIRATION_KEY, expirationTime)
        editor.apply()
    }

    fun isSessionActive(currentTime: Date, context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(SESSION_PREFS_KEY, Context.MODE_PRIVATE)
        val expirationTime = sharedPreferences.getLong(SESSION_EXPIRATION_KEY, 0)
        return expirationTime >= currentTime.time
    }

    fun endUserSession(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SESSION_PREFS_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(SESSION_EXPIRATION_KEY)
        editor.remove("username")
        editor.remove("phoneNumber")
        editor.remove("password")
        editor.remove("role")
        editor.apply()
    }
}
