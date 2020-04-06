package com.callrecordtest.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat.startActivity

class PhoneReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
        val action: String = intent!!.action.toString()
        if (action != TelephonyManager.ACTION_PHONE_STATE_CHANGED &&
            action != Intent.ACTION_NEW_OUTGOING_CALL
        ) {
            Log.e(Constants.TAG, "PhoneReceiver: Received unexpected intent: $action")
            return
        }

        UserPreferences.init(context!!)
        if(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) == null)
            return

        val phoneNumber: String = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        UserPreferences.setContactNumber(phoneNumber)
        val extraState: String = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        Log.d(Constants.TAG, "PhoneReceiver phone number: $phoneNumber")
        if (!FileHelper.isStorageWritable(context)) return
        if (extraState != null) {
            dispatchExtra(context, intent, phoneNumber, extraState)
        } else if (phoneNumber != null) {
            Log.e(Constants.TAG, "PhoneReceiver: $phoneNumber")
            context.startService(
                Intent(context, RecordService::class.java)
                    .putExtra("commandType", Constants.STATE_INCOMING_NUMBER)
                    .putExtra("phoneNumber", phoneNumber)
            )
        }
    }

    private fun dispatchExtra(
        context: Context, intent: Intent,
        phoneNumber: String, extraState: String
    ) {
        var phoneNumberValue: String? = phoneNumber
        when (extraState) {
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                Log.e(Constants.TAG, "PhoneReceiver: EXTRA_STATE_OFFHOOK")
                context.startService(
                    Intent(context, RecordService::class.java)
                        .putExtra("commandType", Constants.STATE_CALL_START)
                )
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                Log.e(Constants.TAG, "PhoneReceiver: EXTRA_STATE_IDLE")
                context.startService(
                    Intent(context, RecordService::class.java)
                        .putExtra("commandType", Constants.STATE_CALL_END)
                )
            }
            TelephonyManager.EXTRA_STATE_RINGING -> {
                Log.e(Constants.TAG, "PhoneReceiver: EXTRA_STATE_RINGING")
                if (phoneNumberValue == null) phoneNumberValue = intent.getStringExtra(
                    TelephonyManager.EXTRA_INCOMING_NUMBER
                )
                context.startService(
                    Intent(context, RecordService::class.java)
                        .putExtra("commandType", Constants.STATE_INCOMING_NUMBER)
                        .putExtra("phoneNumber", phoneNumberValue)
                )
            }
        }
    }
}