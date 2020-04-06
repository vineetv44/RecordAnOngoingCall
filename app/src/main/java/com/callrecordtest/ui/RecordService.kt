package com.callrecordtest.ui

import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.callrecordtest.ui.Constants.Companion.TAG
import java.io.File
import java.io.IOException


class RecordService : Service() {
    private var recorder: MediaRecorder? = null
    private var phoneNumber: String? = null
    private var file: DocumentFile? = null
    private var onCall = false
    private var recording = false
    private var onForeground = false
    private var fileName: String = ""

    var path = "/sdcard/CallRecordTest"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        UserPreferences.init(this)
        val exists = File(path).exists()
        if (!exists) {
            File(path).mkdirs()
        }
//        fileName = "${externalCacheDir!!.absolutePath}/audioRecordTest.3gp"
        fileName = "${path}/audioRecordTest.3gp"

        Log.d(TAG, "file path created: $fileName")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(Constants.TAG, "RecordService onStartCommand")
        if (intent == null) return START_NOT_STICKY

        val commandType = intent.getIntExtra("commandType", 0)
        if (commandType == 0) return START_NOT_STICKY

        val enabled = UserPreferences.getEnabled()
        when (commandType) {
            Constants.RECORDING_ENABLED -> {
                Log.d(Constants.TAG, "RecordService RECORDING_ENABLED")
                if (!enabled && phoneNumber != null && onCall && !recording) {
                    Log.d(Constants.TAG, "RecordService STATE_START_RECORDING")
//                    startService()
                    startRecording()
                }
            }
            Constants.RECORDING_DISABLED -> {
                Log.d(Constants.TAG, "RecordService RECORDING_DISABLED")
                if (onCall && phoneNumber != null && recording) {
                    Log.d(Constants.TAG, "RecordService STATE_STOP_RECORDING")
                    recorder?.release()
                    recorder = null
                    recording = false
                    stopRecording()
                }
            }
            Constants.STATE_INCOMING_NUMBER -> {
                Log.d(Constants.TAG, "RecordService STATE_INCOMING_NUMBER")
                if (phoneNumber == null)
                    phoneNumber = intent.getStringExtra("phoneNumber")
            }
            Constants.STATE_CALL_START -> {
                Log.d(Constants.TAG, "RecordService STATE_CALL_START")
                onCall = true
                if (phoneNumber != null && !recording) {
                    startService()
//                    startRecording()
                }
            }
            Constants.STATE_CALL_END -> {
                Log.d(Constants.TAG, "RecordService STATE_CALL_END")
                onCall = false
                phoneNumber = null
                recorder?.stop();
                recorder?.reset();
                recorder?.release()
                recorder = null
                recording = false
                stopRecording()
                stopService()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(Constants.TAG, "RecordService onDestroy")
        recorder?.stop();
        recorder?.reset();
        recorder?.release();
        recorder = null
        stopRecording()
        stopService()
        super.onDestroy()
    }

    private fun startService() {
        if (onForeground) return
        Log.d(Constants.TAG, "RecordService startService")
        val intent = Intent(this, CallDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        onForeground = true
    }

    private fun stopService() {
        Log.d(Constants.TAG, "RecordService stopService")
        stopForeground(true)
        onForeground = false
        this.stopSelf()
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

           /* file = FileHelper.getFile(this@RecordService, phoneNumber.toString())
            val fd = contentResolver
                .openFileDescriptor(file!!.uri, "w")
                ?: throw Exception("Failed open recording file.")
            setOutputFile(fd.fileDescriptor)*/

            setOutputFile(fileName)  //This is working and saving the audio file

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
            }
            start()
            recording = true
            Log.d(Constants.TAG, "startRecording called")
        }
    }

    private fun stopRecording() {
        Log.d(Constants.TAG, "stopRecording called")
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
}