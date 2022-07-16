package com.example.esportslogomaker.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.view.GravityCompat
import com.example.esportslogomaker.R
import com.example.esportslogomaker.customCallBack.TemplateClickCallBack
import com.example.esportslogomaker.databinding.ActivityMainBinding
import com.example.esportslogomaker.utils.Constant
import com.example.esportslogomaker.utils.FeedbackUtils
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), TemplateClickCallBack,
    EasyPermissions.PermissionCallbacks {

    private lateinit var mainBinding: ActivityMainBinding
    private val workerHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.btnMenu.setOnClickListener {
            mainBinding.drawer.openDrawer(GravityCompat.START)
        }

        mainBinding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_rate_us -> {
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW, Uri
                                    .parse("market://details?id=" + Constant.applicationId)
                            )
                        )
                    } catch (ex: Exception) {
                        ex.printStackTrace()

                    }
                }
                R.id.nav_contact_us -> {
                    FeedbackUtils.startFeedbackEmail(this@MainActivity)
                }

                R.id.nav_share_app -> {

                    try {
                        val i = Intent(Intent.ACTION_SEND)
                        i.type = "text/plain"
                        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                        var sAux = "\nLet me recommend you this application\n\n"
                        sAux = """
                ${sAux}https://play.google.com/store/apps/details?id=${Constant.applicationId}
                """.trimIndent()
                        i.putExtra(Intent.EXTRA_TEXT, sAux)
                        startActivity(
                            Intent.createChooser(
                                i,
                                resources.getString(R.string.choose_one)
                            )
                        )
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                }

                R.id.nav_privacy_policy -> {
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://coreelgo.blogspot.com/2022/07/esport-logo-maker-privacy-policy.html")
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                R.id.nav_more_apps -> {

                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW, Uri
                                    .parse("market://details?id=" + Constant.applicationId)
                            )
                        )
                    } catch (ex: Exception) {
                        ex.printStackTrace()

                    }

                }
            }
            true
        }

        mainBinding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    Constant.showToast(this, "calling Home")
                }
                R.id.navigation_my_work -> {
                    Constant.showToast(this, "calling My work")
                }

            }
            return@setOnItemSelectedListener true
        }

        mainBinding.toolBar.post {
            Log.d("myWidth", "${mainBinding.toolBar.width}")
            Constant.screenWidth = mainBinding.toolBar.width.toDouble()
        }
        workerHandler.post {
            mainBinding.homeUi.updateUI(this@MainActivity)
        }
    }

    override fun onBackPressed() {
        if (mainBinding.drawer.isDrawerOpen(GravityCompat.START)) {
            mainBinding.drawer.closeDrawer(GravityCompat.START)
        } else {
            showBackDialog()
        }
    }

    private fun showBackDialog() {

        AlertDialog.Builder(this@MainActivity)
            .setMessage(getString(R.string.are_you_sure))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()

    }

    override fun onItemClickListener(labelStatus: Boolean) {
        if (EasyPermissions.hasPermissions(this, *Constant.readPermission)) {
            openEditingScreen()
        } else {
            EasyPermissions.requestPermissions(
                this, "We need permissions because this and that",
                Constant.PICK_IMAGE, *Constant.readPermission
            )
        }
    }

    private fun openEditingScreen() {
        val intent = Intent(this, EditingScreen::class.java)
        startActivity(intent)
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openEditingScreen()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("myPermission", "not allow")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}