package com.callrecordtest.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import com.callrecordtest.R

class UserPreferences {
    companion object {
        private var context: Context? = null
        private var prefs: SharedPreferences? = null
        private var defaultStorage: Uri? = null

        fun init(ctx: Context) {
            context = ctx
            PreferenceManager.setDefaultValues(context, R.xml.preferences, false)
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            defaultStorage = Uri.fromFile(context!!.filesDir)
        }

        private fun setString(key: String, value: String) {
            val editor = prefs!!.edit()
                .putString(key, value)
            editor.apply()
        }

        private fun setBoolean(key: String, value: Boolean) {
            val editor = prefs!!.edit()
                .putBoolean(key, value)
            editor.apply()
        }

        fun getStorageUri(): Uri? {
            val str = prefs!!.getString("storage_location", null)
            return if (str == null) defaultStorage else Uri.parse(str)
        }

        fun setStorageUri(uri: Uri) {
            setString("storage_location", uri.toString())
        }

        fun getEnabled(): Boolean {
            return prefs!!.getBoolean("enabled", false)
        }

        fun setEnabled(enabled: Boolean) {
            setBoolean("enabled", enabled)
            context!!.startService(
                Intent(context, RecordService::class.java)
                    .putExtra(
                        "commandType",
                        if (enabled) Constants.RECORDING_DISABLED else Constants.RECORDING_ENABLED
                    )
                    .putExtra("enabled", enabled)
            )
        }

        fun setContactNumber(phoneNumber: String){
            setString("contact_number", phoneNumber)
        }

        fun getContactNumber() : String {
            val str = prefs!!.getString("contact_number", null)
            return str ?: ""
        }
    }
}