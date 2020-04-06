package com.callrecordtest.ui

import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.callrecordtest.R
import kotlinx.android.synthetic.main.calling_activity.*


class CallDetailsActivity : AppCompatActivity() {
    var mStartRecording = true
    var textString : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calling_activity)

        bStart.setOnClickListener {
            textString = when (mStartRecording) {
                true -> "Stop"
                false -> "Start"
            }
            bStart.text = textString
            mStartRecording = !mStartRecording
            workWithChronometer(mStartRecording)
            UserPreferences.setEnabled(mStartRecording)
        }

        tvPhoneNumber.text = UserPreferences.getContactNumber()

        val contactName : String =
            FileHelper.getContactName(UserPreferences.getContactNumber(),this).toString()

        if (contactName.isEmpty()){
            tvName.text = "Unknown Number"
        }else{
            tvName.text = contactName
        }
    }

    private fun workWithChronometer(mStartRecording: Boolean) {
        if(!mStartRecording){
            startChronometer()
        }else{
            stopChronometer()
        }
    }

    private fun startChronometer(){
        mChronometer.base = SystemClock.elapsedRealtime();
        mChronometer.start();
    }

    private fun stopChronometer(){
        mChronometer.stop();
    }
}