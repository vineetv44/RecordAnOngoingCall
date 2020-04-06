package com.callrecordtest

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPerms();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkPerms() {
        val perms = arrayOf<String>(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.PROCESS_OUTGOING_CALLS
        )
        val requestingPerms: MutableList<String> = ArrayList()
        for (perm in perms) {
            if (checkSelfPermission(perm) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestingPerms.add(perm)
            }
        }
        if (requestingPerms.size > 0) {
            requestPermissions(requestingPerms.toTypedArray(), 0)
        }
    }

}
