package com.callrecordtest.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.text.format.DateFormat
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.util.*


class FileHelper {
    companion object{
        @Throws(Exception::class)
        fun getFile(context: Context?, phoneNumber: String): DocumentFile? {
            val date = DateFormat.format("yyyyMMddHHmmss", Date()) as String
            val filename = date + "_" + cleanNumber(phoneNumber)
            return getStorageFile(context)!!.createFile("audio/3gp", filename)
        }

        /// Obtains a contact name corresponding to a phone number.
        public fun getContactName(phoneNum: String, context: Context): String? {
            var res = PhoneNumberUtils.formatNumber(phoneNum)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context.checkSelfPermission(Manifest.permission.READ_CONTACTS) !== PackageManager.PERMISSION_GRANTED
            ) {
                return res
            }
            val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val names: Cursor = context.contentResolver.query(
                uri, projection,
                null, null, null
            )
                ?: return res
            val indexName: Int =
                names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val indexNumber: Int =
                names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            if (names.count > 0) {
                names.moveToFirst()
                do {
                    val name: String = names.getString(indexName)
                    val number = cleanNumber(names.getString(indexNumber))
                    if (PhoneNumberUtils.compare(number, phoneNum)) {
                        res = name
                        break
                    }
                } while (names.moveToNext())
            }
            names.close()
            return res
        }

        private fun cleanNumber(phoneNumber: String): String {
            return phoneNumber.replace("[^0-9]".toRegex(), "")
        }

        private fun getStorageFile(context: Context?): DocumentFile? {
            val uri: Uri? = UserPreferences.getStorageUri()
            val scheme: String = uri!!.scheme.toString()
            return if (scheme == "file") {
                DocumentFile.fromFile(File(uri.path))
            } else {
                DocumentFile.fromTreeUri(context!!, uri)
            }
        }

        fun isStorageWritable(context: Context?): Boolean {
            return getStorageFile(context)!!.canWrite()
        }
    }
}