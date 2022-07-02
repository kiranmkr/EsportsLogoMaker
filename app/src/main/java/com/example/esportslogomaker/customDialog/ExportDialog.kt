    package com.example.logodesign.customDialog


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.example.esportslogomaker.R
import com.example.esportslogomaker.customCallBack.ExportDialogCallBack


    class ExportDialog(mActivity: Activity, var callBack: ExportDialogCallBack) {


    private var customDialog: Dialog

    init {

        customDialog = Dialog(mActivity)
        val layoutInflater = LayoutInflater.from(mActivity)

        @SuppressLint("InflateParams")
        val view = layoutInflater.inflate(R.layout.export_dialog, null)

        customDialog.window?.let {
            it.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.attributes?.windowAnimations = R.style.DialogAnimation
            }
        }

        customDialog.setContentView(view)

        view.findViewById<ImageView>(R.id.imageView42).setOnClickListener {
            hideDialog()
        }

        view.findViewById<TextView>(R.id.print_btn).setOnClickListener {
            hideDialog()
            callBack.saveAsImageFile()
        }

        view.findViewById<TextView>(R.id.print_btn2).setOnClickListener {
            hideDialog()
            callBack.saveAsPdfFile()
        }


    }

    fun isShowing(): Boolean {
        return customDialog.isShowing
    }

    fun showDialog() {
        customDialog.show()
    }

    fun hideDialog() {
        customDialog.hide()
    }
}