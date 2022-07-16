package com.example.esportslogomaker.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.Toast
import com.example.esportslogomaker.R

object Constant {

    const val CAMERA_IMAGE = 10101
    const val PICK_IMAGE = 20202

    var screenWidth: Double = 720.0

    val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    val cameraPermission = arrayOf(Manifest.permission.CAMERA)

    var permissionPosition: Int = 1

    @JvmStatic
    var colorArray = arrayOf(
        R.color.black,
        R.color.white,
        R.color.red_100,
        R.color.red_300, R.color.red_500, R.color.red_700, R.color.blue_100,
        R.color.blue_300, R.color.blue_500, R.color.blue_700, R.color.green_100, R.color.green_300,
        R.color.green_500, R.color.green_700, R.color.Gold
    )

    const val applicationId = "com.example.esportslogomaker"

    @JvmStatic
    val fileProvider: String = "${applicationId}.fileprovider"

    @JvmStatic
    var labelNumber: Int = 1

    @JvmStatic
    var labelCategory: String = "esports"

    @Suppress("USELESS_CAST")
    @JvmStatic
    fun showToast(c: Context, message: String) {
        try {
            if (!(c as Activity).isFinishing) {
                val activity = c as Activity
                activity.runOnUiThread { //show your Toast here..
                    Toast.makeText(c.applicationContext, message, Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}