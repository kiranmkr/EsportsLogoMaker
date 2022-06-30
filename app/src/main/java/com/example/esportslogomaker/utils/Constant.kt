package com.example.esportslogomaker.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast

object Constant {

    const val applicationId = "com.example.esportslogomaker"

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