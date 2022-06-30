package com.example.esportslogomaker.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.GravityCompat
import com.bumptech.glide.util.Util
import com.example.esportslogomaker.R
import com.example.esportslogomaker.customCallBack.TemplateClickCallBack
import com.example.esportslogomaker.databinding.ActivityMainBinding
import com.example.esportslogomaker.utils.Constant
import com.example.esportslogomaker.utils.FeedbackUtils

class MainActivity : AppCompatActivity(), TemplateClickCallBack {

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
                                Uri.parse("https://coreelgo.blogspot.com/privacy-policy")
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

        workerHandler.post {
            mainBinding.homeUi.updateUI(this@MainActivity)
        }
    }

    override fun onBackPressed() {
        if (mainBinding.drawer.isDrawerOpen(GravityCompat.START)) {
            mainBinding.drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onItemClickListener(labelStatus: Boolean) {
        Constant.showToast(this,"calling")
    }


}