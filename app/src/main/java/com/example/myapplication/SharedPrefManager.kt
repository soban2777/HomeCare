package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

object SharedPrefManager {

    private const val PREF_NAME = "UserSessionPrefs"
    private const val KEY_IS_PROFILE_COMPLETE = "isProfileComplete"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setProfileComplete(context: Context, isComplete: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_PROFILE_COMPLETE, isComplete)
        editor.apply()
    }

    fun isProfileComplete(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_PROFILE_COMPLETE, false) // Default to false
    }
    // Optional: Call this when user logs out to reset the flag
    fun clearSession(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_IS_PROFILE_COMPLETE) // Or editor.clear() to remove all prefs
        editor.apply()
    }
}