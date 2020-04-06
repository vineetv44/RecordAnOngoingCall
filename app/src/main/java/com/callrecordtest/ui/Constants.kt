package com.callrecordtest.ui

class Constants {
    companion object{
        val TAG = "Call recorder";
        val FILE_NAME_PATTERN = "^[\\d]{14}_[_\\d]*\\..+$";
        val STATE_INCOMING_NUMBER = 1;
        val STATE_CALL_START = 2;
        val STATE_CALL_END = 3;
        val RECORDING_ENABLED = 4;
        val RECORDING_DISABLED = 5;
    }
}