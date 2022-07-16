package com.example.esportslogomaker.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.esportslogomaker.R
import com.example.esportslogomaker.customCallBack.AddNewTextCallBack
import com.example.esportslogomaker.customCallBack.ExportDialogCallBack
import com.example.esportslogomaker.customCallBack.FontAdapterCallBack
import com.example.esportslogomaker.customCallBack.StickerClick
import com.example.esportslogomaker.customDialog.AddNewText
import com.example.esportslogomaker.customSticker.CustomImageView
import com.example.esportslogomaker.datamodel.Root
import com.example.esportslogomaker.recyclerAdapter.*
import com.example.esportslogomaker.utils.Constant
import com.example.esportslogomaker.utils.MoveViewTouchListener
import com.example.esportslogomaker.utils.Utils
import com.example.logodesign.customDialog.ExportDialog
import com.example.logodesign.customDialog.UpdateNewText
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt


class EditingScreen : AppCompatActivity(), CustomImageView.CustomImageCallBack,
    ExportDialogCallBack, MoveViewTouchListener.EditTextCallBacks, SVGColorAdapter.SvgColorClick,
    StickerClick, AddNewTextCallBack, FontAdapterCallBack, EasyPermissions.PermissionCallbacks {

    private val workerThread: ExecutorService = Executors.newCachedThreadPool()
    private val workerHandler = Handler(Looper.getMainLooper())

    private var toolBack: ImageView? = null
    private var toolSave: ImageView? = null

    private var svgLayers: ConstraintLayout? = null
    private var svgAddText: ConstraintLayout? = null
    private var svgAddShape: ConstraintLayout? = null
    private var svgAddSticker: ConstraintLayout? = null
    private var svgAddImage: ConstraintLayout? = null

    private var textFont: ConstraintLayout? = null
    private var textSize: ConstraintLayout? = null
    private var textColor: ConstraintLayout? = null
    private var textOpacity: ConstraintLayout? = null
    private var textStyle: ConstraintLayout? = null

    private var svgContainerIcon: ArrayList<Any?> = ArrayList()
    private var textContainerIcon: ArrayList<Any?> = ArrayList()

    private var svgAddStickerContainer: View? = null
    private var svgAddImageContainer: View? = null

    private var reTextColor: RecyclerView? = null
    private var textFontContainer: View? = null
    private var textSizeContainer: View? = null
    private var textOpacityContainer: View? = null
    private var textStyleContainer: View? = null

    private var svgRootContainer: ConstraintLayout? = null
    private var textRootContainer: ConstraintLayout? = null
    private var svgDownBtn: ImageView? = null
    private var textDownBtn: ImageView? = null

    private var svgColor: RecyclerView? = null
    private var svgColorAdapter: SVGColorAdapter? = null

    private var svgColorPicker: ConstraintLayout? = null
    private var svgColorPickerImage: ImageView? = null
    private var svgColorPrimaryImage: AppCompatImageView? = null
    private var svgColorBlackImage: ImageView? = null
    private var svgColorPickerDownImage: AppCompatImageView? = null

    private var svgSeekAlpha: SeekBar? = null

    private var importSticker: ConstraintLayout? = null
    private var reShape: RecyclerView? = null
    private var reSticker: RecyclerView? = null
    private var shapeAdapter: ShapeAdapter? = null
    private var stickerAdapter: StickerAdapter? = null
    private var importText: TextView? = null

    private var cameraBtn: ImageView? = null
    private var galleryBtn: ImageView? = null

    private var cameraStatus = false

    //String var
    private var labelCategory: String? = null
    private var labelNumber: Int = 0

    var currentText: TextView? = null

    private var addNewText: AddNewText? = null
    private var updateNewText: UpdateNewText? = null

    var customSticker: CustomImageView? = null

    private var textRoot: View? = null
    private var layerRoot: View? = null
    private var stickerRoot: View? = null
    private var reTextFont: RecyclerView? = null

    private var reFontAdapter: FontAdapter? = null

    //ArrayList var
    private var fileNames: Array<String?>? = null
    private var tvSeekAlpha: SeekBar? = null
    private var tvSizeSeekBar: SeekBar? = null
    private var stickerOpacitySeekBar: SeekBar? = null
    private var stickerDelete: ImageView? = null
    private var stickerFlip: ImageView? = null
    private var stickerCheckmark: ImageView? = null

    private var textBold: ImageView? = null
    private var textItalic: ImageView? = null
    private var textCapital: ImageView? = null
    private var textSmall: ImageView? = null
    private var textUnderline: ImageView? = null

    //Boolean var
    private var boldState = false
    private var italicState = false
    private var underlineState = false
    private var exportDialog: ExportDialog? = null

    private var dialog: Dialog? = null

    private var imageViewArray: ArrayList<com.example.esportslogomaker.datamodel.ImageView> =
        ArrayList()
    private var textViewJson: ArrayList<com.example.esportslogomaker.datamodel.TextView> =
        ArrayList()
    private var rootLayout: RelativeLayout? = null
    private var screenRatioFactor: Double = 1.0

    private var imageViewSVG: ImageView? = null

    private var btnDeleteText: ImageView? = null
    private var btnChangeText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editing_screen)

        exportDialog = ExportDialog(this@EditingScreen, this)

        //Window dialog code
        dialog = Dialog(this@EditingScreen)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dilog_svg_loader)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        workerHandler.postDelayed({

            labelCategory = Constant.labelCategory
            labelNumber = Constant.labelNumber

            Log.d("myCategoryData", "$labelCategory -- $labelNumber -- ${Constant.screenWidth}")

            initID()

        }, 0)

    }

    private fun initID() {

        toolBack = findViewById(R.id.imageView)
        toolSave = findViewById(R.id.imageView4)

        textRoot = findViewById(R.id.text_root)
        layerRoot = findViewById(R.id.layer_root)
        stickerRoot = findViewById(R.id.sticker_root)

        rootLayout = findViewById(R.id.root_layout)
        imageViewSVG = findViewById(R.id.imageView52)
        svgLayers = findViewById(R.id.svgLayers)
        svgAddText = findViewById(R.id.svgAddText)
        svgAddShape = findViewById(R.id.svgAddShape)
        svgAddSticker = findViewById(R.id.svgAddSticker)
        svgAddImage = findViewById(R.id.svgAddImage)

        btnChangeText = findViewById(R.id.textView7)
        btnDeleteText = findViewById(R.id.imageView12)

        textFont = findViewById(R.id.textFont)
        textSize = findViewById(R.id.textSize)
        textColor = findViewById(R.id.textColor)
        textOpacity = findViewById(R.id.textOpacity)
        textStyle = findViewById(R.id.textStyle)

        svgAddStickerContainer = findViewById(R.id.svgAddStickerContainer)
        svgAddImageContainer = findViewById(R.id.svgAddImageContainer)

        reTextColor = findViewById(R.id.reTextColor)
        reTextColor?.setHasFixedSize(true)
        textFontContainer = findViewById(R.id.textFontContainer)
        textSizeContainer = findViewById(R.id.textSizeContainer)
        textOpacityContainer = findViewById(R.id.textOpacityContainer)
        textStyleContainer = findViewById(R.id.textStyleContainer)

        svgDownBtn = findViewById(R.id.imageView10)
        textDownBtn = findViewById(R.id.imageView19)
        svgRootContainer = findViewById(R.id.constraintLayout5)
        textRootContainer = findViewById(R.id.textRootContainer)

        reTextFont = findViewById(R.id.reTextFont)
        reTextFont?.setHasFixedSize(true)

        textBold = findViewById(R.id.imageView20)
        textItalic = findViewById(R.id.imageView21)
        textCapital = findViewById(R.id.imageView22)
        textSmall = findViewById(R.id.imageView23)
        textUnderline = findViewById(R.id.imageView24)

        tvSeekAlpha = findViewById(R.id.textOpacitySeekBar)
        tvSizeSeekBar = findViewById(R.id.textSizeSeekBar)
        stickerOpacitySeekBar = findViewById(R.id.stickerOpacitySeekBar)
        stickerDelete = findViewById(R.id.imageView333)
        stickerFlip = findViewById(R.id.imageView35)
        stickerCheckmark = findViewById(R.id.imageView343)

        svgContainerIcon.add(R.id.svgLayers)
        svgContainerIcon.add(R.id.svgAddShape)
        svgContainerIcon.add(R.id.svgAddSticker)
        svgContainerIcon.add(R.id.svgAddImage)

        textContainerIcon.add(R.id.textFont)
        textContainerIcon.add(R.id.textSize)
        textContainerIcon.add(R.id.textColor)
        textContainerIcon.add(R.id.textOpacity)
        textContainerIcon.add(R.id.textStyle)

        importText = findViewById(R.id.textView299)

        importSticker = findViewById(R.id.constraintLayout2)
        reShape = findViewById(R.id.reShape)
        reShape?.setHasFixedSize(true)

        reSticker = findViewById(R.id.reSticker)
        reSticker?.setHasFixedSize(true)

        cameraBtn = findViewById(R.id.imageView17)
        galleryBtn = findViewById(R.id.imageView16)

        if (rootLayout != null) {

            rootLayout!!.post {

                Log.d("myWidth", "${rootLayout!!.width}")

                Constant.screenWidth = rootLayout!!.width.toDouble()

                if (loadJSONFromAsset() != null) {

                    val obj = JSONObject(loadJSONFromAsset()!!)
                    val om = ObjectMapper()
                    val root: Root = om.readValue(obj.toString(), Root::class.java)

                    if (root.absoluteLayout != null) {
                        screenRatioFactor =
                            Constant.screenWidth / root.absoluteLayout!!.androidLayoutWidth!!.replace(
                                "dp",
                                ""
                            ).toDouble()

                        if (root.absoluteLayout!!.imageView != null) {
                            root.absoluteLayout!!.imageView!!.forEachIndexed { index, imageView ->
                                imageViewArray.add(index, imageView)
                            }
                        }
                        if (root.absoluteLayout!!.textView != null) {
                            root.absoluteLayout?.textView?.forEachIndexed { index, textview ->
                                textViewJson.add(index, textview)
                            }
                        }
                    }

                    if (imageViewArray.size > 0) {
                        addImage(imageViewArray)
                    }

                    if (textViewJson.size > 0) {
                        addText(textViewJson)
                    }

                } else {
                    Log.e("myError", "wrong json")
                    Utils.showToast(this@EditingScreen, "Wrong json")
                    finish()
                }
            }

        } else {
            Log.d("myDebugger", "view is null")
            Utils.showToast(this, "View is null")
        }

        clickHandler()

    }

    private fun clickHandler() {

        btnChangeText?.setOnClickListener {
            if (currentText != null) {
                updateNewText?.show(currentText!!.text.toString())
            }
        }

        btnDeleteText?.setOnClickListener {
            if (currentText != null) {
                rootLayout!!.removeView(currentText)
                currentText = null
                textRoot?.visibility = View.GONE
                layerRoot?.visibility = View.VISIBLE
            }
        }

        toolBack?.setOnClickListener {
            showBackDialog()
        }

        toolSave?.setOnClickListener {

            if (customSticker != null) {
                customSticker?.disableAllOthers()
            }


            if (currentText != null) {
                currentText!!.setBackgroundColor(Color.TRANSPARENT)
            }

            showBottomSheetDialog()

        }

        svgDownBtn?.setOnClickListener {
            svgDownBtn?.visibility = View.GONE
            svgRootContainer?.visibility = View.GONE
            alphaManagerForAll(this, svgContainerIcon)
        }

        textDownBtn?.setOnClickListener {

            textDownBtn?.visibility = View.GONE
            textRootContainer?.visibility = View.GONE

            alphaManagerForAll(this, textContainerIcon)

            if (textRoot?.visibility == View.VISIBLE) {
                textRoot?.visibility = View.GONE
            }

            if (stickerRoot?.visibility == View.VISIBLE) {
                stickerRoot?.visibility = View.GONE
            }

            if (layerRoot?.visibility == View.GONE) {
                layerRoot?.visibility = View.VISIBLE
            }
        }

        svgLayers?.setOnClickListener(svgClickListener)
        svgAddText?.setOnClickListener(svgClickListener)
        svgAddShape?.setOnClickListener(svgClickListener)
        svgAddSticker?.setOnClickListener(svgClickListener)
        svgAddImage?.setOnClickListener(svgClickListener)

        textFont?.setOnClickListener(textMenuClickListener)
        textSize?.setOnClickListener(textMenuClickListener)
        textColor?.setOnClickListener(textMenuClickListener)
        textOpacity?.setOnClickListener(textMenuClickListener)
        textStyle?.setOnClickListener(textMenuClickListener)

        stickerRoot?.setOnClickListener(emptyClickListener)

        updateSVGColorAdapter()

        svgColorPickerImage?.setOnClickListener { svgColorPicker?.visibility = View.VISIBLE }

        svgColorPickerDownImage?.setOnClickListener { svgColorPicker?.visibility = View.GONE }

        svgColorPrimaryImage?.setOnClickListener {
            /* if (currentSelectPath != null) {
                 currentSelectPath?.fillColor = Color.parseColor("#b39ddb")
             } else {
                 Utils.showToast(this, "Plz select path")
             }*/
        }

        svgColorBlackImage?.setOnClickListener {
            /*if (currentSelectPath != null) {
                currentSelectPath?.fillColor = Color.parseColor("#000000")
            } else {
                Utils.showToast(this, "Plz select path")
            }*/
        }

        //code svg Seek Alpha
        svgSeekAlpha?.max = 10
        svgSeekAlpha?.progress = 10

        svgSeekAlpha?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                if (fromUser) {
                    Log.d("mySeekVal", "$progress")

                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })

        importSticker?.setOnClickListener {
            cameraStatus = false
            openGallery()
        }

        updateShapeAdapter()

        updateStickerAdapter()

        cameraBtn?.setOnClickListener {
            cameraStatus = true
            openCamera()
        }

        galleryBtn?.setOnClickListener {
            cameraStatus = false
            openGallery()
        }

        addNewText = AddNewText(this@EditingScreen, this)
        updateNewText = UpdateNewText(this@EditingScreen, this)

        try {

            fileNames = assets.list("font")

            if (fileNames != null) {
                reFontAdapter = FontAdapter(fileNames, this)
                reTextFont?.adapter = reFontAdapter
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        //SeekAlpha Code
        tvSeekAlpha?.max = 10
        tvSeekAlpha?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    if (currentText != null) {
                        if (progress == 10) {
                            currentText?.alpha = 1.0f
                        } else {
                            currentText?.alpha = "0.$progress".toFloat()
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        tvSizeSeekBar?.max = 300
        tvSizeSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    if (currentText != null) {
                        if (progress > 10) {
                            changeFontSize(progress, currentText!!)
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        //SeekAlpha Code
        stickerOpacitySeekBar?.max = 10
        stickerOpacitySeekBar?.progress = 10
        stickerOpacitySeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    Log.e("mySeek", "$progress")
                    if (customSticker != null) {
                        if (progress == 10) {
                            customSticker?.imageView?.alpha = 1.0f
                        } else {
                            customSticker?.imageView?.alpha = "0.$progress".toFloat()
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        stickerDelete?.setOnClickListener {
            customSticker?.deleteObject()
            stickerRoot?.visibility = View.GONE
        }

        stickerCheckmark?.setOnClickListener {
            customSticker?.disableAllOthers()
            stickerRoot?.visibility = View.GONE
            if (textRoot?.visibility == View.VISIBLE) {
                textRoot?.visibility = View.GONE
            }
            if (layerRoot?.visibility == View.GONE) {
                layerRoot?.visibility = View.VISIBLE
            }
        }

        stickerFlip?.setOnClickListener {
            customSticker?.flipRoot()
        }

        textBold?.setOnClickListener(textStyleListener)
        textItalic?.setOnClickListener(textStyleListener)
        textCapital?.setOnClickListener(textStyleListener)
        textSmall?.setOnClickListener(textStyleListener)
        textUnderline?.setOnClickListener(textStyleListener)
    }

    private fun openGallery() {
        if (EasyPermissions.hasPermissions(this, *Constant.readPermission)) {
            openGalleryNewWay()
        } else {
            EasyPermissions.requestPermissions(
                this, "We need permissions because this and that",
                Constant.PICK_IMAGE, *Constant.readPermission
            )
        }
    }

    private fun openGalleryNewWay() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        getResult.launch(intent)
    }

    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {

                val value = it.data?.data

                if (value != null) {

                    customSticker = CustomImageView(this)
                    customSticker?.updateCallBack(this)

                    if (customSticker != null) {

                        Glide.with(this@EditingScreen)
                            .load(value)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(customSticker!!.imageView)

                        rootLayout?.addView(customSticker)
                    }


                }

            }
        }


    private fun openCamera() {
        if (EasyPermissions.hasPermissions(this, *Constant.cameraPermission)) {
            openCameraNewWay()
        } else {
            EasyPermissions.requestPermissions(
                this, "We need permissions because this and that",
                Constant.CAMERA_IMAGE, *Constant.cameraPermission
            )
        }
    }

    private var camUri: Uri? = null

    private fun openCameraNewWay() {
        camUri = initTempUri()
        camUri?.let {
            takePicture.launch(it)
        }
    }

    private fun initTempUri(): Uri? {
        //gets the temp_images dir
        val tempImagesDir = File(
            applicationContext.filesDir, //this function gets the external cache dir
            getString(R.string.temp_images_dir)
        ) //gets the directory for the temporary images dir

        tempImagesDir.mkdir() //Create the temp_images dir

        //Creates the temp_image.jpg file
        val tempImage = File(
            tempImagesDir, //prefix the new abstract path with the temporary images dir path
            getString(R.string.temp_image)
        ) //gets the abstract temp_image file name

        return if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                applicationContext,
                Constant.fileProvider,
                tempImage
            )
        } else {
            Uri.fromFile(tempImage)
        }

    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->

            // The image was saved into the given Uri -> do something with it
            if (success) {

                if (camUri != null) {

                    customSticker = CustomImageView(this)
                    customSticker?.updateCallBack(this)

                    if (customSticker != null) {

                        Glide.with(this@EditingScreen)
                            .load(camUri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(customSticker!!.imageView)

                        rootLayout?.addView(customSticker)
                    }

                } else {
                    Log.d("myCameraImage", "image is not save")
                }

            }
        }

    private fun showBackDialog() {
        AlertDialog.Builder(this@EditingScreen)
            .setMessage(getString(R.string.are_you_sure_editing))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                Utils.clearGarbageCollection()
                finish()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (cameraStatus) {
            openCamera()
        } else {
            openGallery()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("myPermission", "not allow")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private var bottomSheetDialog: BottomSheetDialog? = null

    private fun showBottomSheetDialog() {

        bottomSheetDialog = BottomSheetDialog(this@EditingScreen)
        bottomSheetDialog?.setContentView(R.layout.bottom_sheet_item)

        bottomSheetDialog?.findViewById<View>(R.id.constraintLayout12)?.setOnClickListener {
            bottomSheetDialog?.hide()
        }

        bottomSheetDialog?.show()
    }

    private fun alphaManagerForAll(activity: Activity, views: ArrayList<Any?>) {
        for (i in views.indices) {
            activity.findViewById<View>((views[i] as Int?)!!).alpha = "0.40".toFloat()
        }
    }

    private val svgClickListener = View.OnClickListener { v: View ->

        alphaManager(this, svgContainerIcon, v.id)

        if (svgDownBtn?.visibility == View.GONE) {
            svgDownBtn?.visibility = View.VISIBLE
        }

        if (svgRootContainer?.visibility == View.GONE) {
            svgRootContainer?.visibility = View.VISIBLE
        }

        when (v.id) {

            R.id.svgLayers -> {
                showLayerContainer()
            }

            R.id.svgAddText -> {
                addNewText?.show()
            }

            R.id.svgAddShape -> {
                showStickerContainer(1)
            }

            R.id.svgAddSticker -> {
                showStickerContainer(2)
            }

            R.id.svgAddImage -> {
                showImageContainer()
            }

            else -> {
                Utils.showToast(this, "No thing click")
            }
        }
    }

    private fun showLayerContainer() {

        alphaManager(this, svgContainerIcon, R.id.svgLayers)

        if (svgRootContainer?.visibility == View.GONE) {
            svgRootContainer?.visibility = View.VISIBLE
        }

        svgAddStickerContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        svgAddImageContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        if (currentText != null) {
            currentText!!.setBackgroundColor(Color.TRANSPARENT)
        }


    }

    private fun showStickerContainer(values: Int) {

        Log.d("myShape", "$values")

        if (values == 1) {
            if (reShape?.visibility == View.GONE) {
                importText?.setText(R.string.import_shape)
                reShape?.visibility = View.VISIBLE
                reSticker?.visibility = View.GONE
            }
        } else {
            if (reSticker?.visibility == View.GONE) {
                importText?.setText(R.string.import_sticker)
                reSticker?.visibility = View.VISIBLE
                reShape?.visibility = View.GONE
            }
        }

        svgAddStickerContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        svgAddImageContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

    }

    private fun showImageContainer() {

        svgAddStickerContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        svgAddImageContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }
    }

    private fun alphaManager(activity: Activity, views: ArrayList<Any?>, view_id: Int) {
        for (i in views.indices) {
            if (views[i] == view_id) {
                activity.findViewById<View>((views[i] as Int?)!!).alpha = "1.0".toFloat()
            } else {
                activity.findViewById<View>((views[i] as Int?)!!).alpha = "0.40".toFloat()
            }
        }
    }

    private val emptyClickListener = View.OnClickListener {
        Log.d("myEmptyClick", "Click")
    }

    private fun updateStickerAdapter() {
        stickerAdapter = StickerAdapter(this)
        reSticker?.adapter = stickerAdapter
    }

    private fun updateShapeAdapter() {
        shapeAdapter = ShapeAdapter(this)
        reShape?.adapter = shapeAdapter
    }

    private fun changeFontSize(fontSize: Int, currentEditText: TextView) {

        val oldW: Int = currentEditText.width
        val oldH: Int = currentEditText.height

        Log.e("changeFontSize", "OLD= $oldW, $oldH, ${currentEditText.x}, ${currentEditText.y}")

        currentEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize.toFloat())

        val newW: Float = currentEditText.width.toFloat()
        val factor = newW / oldW

        val cmDist: Float = (currentEditText.cameraDistance * factor)

        if (cmDist > 0) {

            try {
                currentEditText.cameraDistance = cmDist
            } catch (ex: IllegalArgumentException) {
            }
        }

    }

    private val textStyleListener = View.OnClickListener { v: View ->
        currentText?.let {
            when (v.id) {

                R.id.imageView20 -> if (boldState) {
                    if (italicState) {
                        textBold!!.isSelected = false
                        boldState = false
                        currentText?.let {
                            setItalicStyle(true)
                        }

                    } else {
                        textBold!!.isSelected = false
                        boldState = false
                        currentText?.let {
                            setBoldStyle(false)
                        }
                    }

                } else {

                    if (italicState) {
                        textBold!!.isSelected = true
                        boldState = true
                        currentText?.let {
                            setBoldItalic()
                        }
                    } else {
                        textBold!!.isSelected = true
                        boldState = true
                        currentText?.let {
                            setBoldStyle(true)
                        }
                    }
                }

                R.id.imageView21 -> if (italicState) {
                    if (boldState) {
                        textItalic!!.isSelected = false
                        currentText?.let {
                            setBoldStyle(true)
                        }
                        italicState = false
                    } else {
                        textItalic!!.isSelected = false
                        currentText?.let {
                            setItalicStyle(false)
                        }
                        italicState = false
                    }
                } else {

                    if (boldState) {
                        textItalic!!.isSelected = true
                        currentText?.let {
                            setBoldItalic()
                        }
                        italicState = true
                    } else {
                        textItalic!!.isSelected = true
                        currentText?.let {
                            setItalicStyle(true)
                        }
                        italicState = true
                    }
                }
                R.id.imageView22 -> {
                    setUppercase()
                    textSmall!!.isSelected = false
                    textCapital!!.isSelected = true
                }
                R.id.imageView23 -> {
                    setLowerCase()
                    textSmall!!.isSelected = true
                    textCapital!!.isSelected = false
                }
                R.id.imageView24 -> if (underlineState) {
                    setUnderLine()
                    textUnderline!!.isSelected = false
                    underlineState = false
                } else {
                    setUnderLine()
                    textUnderline!!.isSelected = true
                    underlineState = true
                }
                else -> {
                    Utils.showToast(this, "default")
                }
            }
        }
    }

    private fun setItalicStyle(isBold: Boolean) {
        val oldTypeface =
            if (currentText?.typeface != null) currentText?.typeface else if (isBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        currentText?.typeface =
            Typeface.create(oldTypeface, if (isBold) Typeface.ITALIC else Typeface.NORMAL)
    }

    private fun setBoldStyle(isBold: Boolean) {
        val oldTypeface =
            if (currentText?.typeface != null) currentText?.typeface else if (isBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        currentText?.typeface =
            Typeface.create(oldTypeface, if (isBold) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun setBoldItalic() {
        val typeface = Typeface.create(currentText?.typeface, Typeface.BOLD_ITALIC)
        (currentText)?.typeface = typeface
    }

    private fun setUppercase() {
        currentText?.let {
            it.text = it.text.toString().uppercase(Locale.ROOT)
        }
    }

    private fun setLowerCase() {
        currentText?.let {
            it.text = it.text.toString().lowercase(Locale.ROOT)
        }
    }

    private fun setUnderLine() {

        try {

            currentText?.let {
                if (it.paintFlags == Paint.UNDERLINE_TEXT_FLAG) {
                    it.paintFlags = it.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                } else {
                    it.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                }
            }

        } catch (exception: IllegalStateException) {
            exception.printStackTrace()
        }
    }

    private val textMenuClickListener = View.OnClickListener { v: View ->

        alphaManager(this, textContainerIcon, v.id)

        if (textDownBtn?.visibility == View.GONE) {
            textDownBtn?.visibility = View.VISIBLE
        }

        if (textRootContainer?.visibility == View.GONE) {
            textRootContainer?.visibility = View.VISIBLE
        }

        when (v.id) {

            R.id.textFont -> {
                showTextFontContainer()
            }

            R.id.textSize -> {
                showTextSizeContainer()
            }

            R.id.textColor -> {
                showTextColorContainer()
            }

            R.id.textOpacity -> {
                showTextOpacityContainer()
            }

            R.id.textStyle -> {
                showTextStyleContainer()
            }
            else -> {
                Utils.showToast(this, "No thing click")
            }

        }


    }

    private fun showTextFontContainer() {

        textFontContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        textSizeContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textOpacityContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textStyleContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        reTextColor?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }
    }

    private fun showTextSizeContainer() {

        textFontContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textSizeContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        textOpacityContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textStyleContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        reTextColor?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }
    }

    private fun showTextColorContainer() {

        textFontContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textSizeContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textOpacityContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textStyleContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        reTextColor?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }
    }

    private fun showTextOpacityContainer() {

        textFontContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textSizeContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textOpacityContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        textStyleContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        reTextColor?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }
    }

    private fun showTextStyleContainer() {

        textFontContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textSizeContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textOpacityContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textStyleContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        reTextColor?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }
    }

    private fun updateSVGColorAdapter() {
        svgColorAdapter = SVGColorAdapter(this)
        svgColor?.adapter = svgColorAdapter
        reTextColor?.adapter = svgColorAdapter
    }

    private fun loadJSONFromAsset(): String? {

        val json: String? = try {
            val `is`: InputStream =
                this.assets.open("category/${Constant.labelCategory}/json/${Constant.labelNumber}.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json

    }

    private var newImageView: ImageView? = null

    private fun addImage(array: ArrayList<com.example.esportslogomaker.datamodel.ImageView>) {

        array.forEachIndexed { index, imageView ->

            if (index == 0) {

                Log.d("myPosition", " if $index")

                newImageView = ImageView(this@EditingScreen)

                var path: String? = null

                if (imageView.appSrcCompat != null) {

                    try {
                        path =
                            "file:///android_asset/category/${Constant.labelCategory}/assets/${Constant.labelNumber}/$index.png"
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }

                    path?.let {
                        Glide.with(this@EditingScreen)
                            .load(it)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(newImageView!!)

                        newImageView?.setTag(R.id.imagePath, "$path")
                    }

                }

                if (imageView.androidLayoutWidth != null && imageView.androidLayoutHeight != null) {

                    val width = (imageView.androidLayoutWidth.toString()
                        .replace("dp", "")).toInt() * screenRatioFactor
                    val height = (imageView.androidLayoutHeight.toString()
                        .replace("dp", "")).toInt() * screenRatioFactor
                    val layoutParams = RelativeLayout.LayoutParams(width.toInt(), height.toInt())
                    newImageView?.layoutParams = layoutParams
                }

                if (imageView.androidLayoutX != null) {
                    newImageView?.x = (imageView.androidLayoutX!!.replace("dp", "").toDouble()
                            * screenRatioFactor).toFloat()
                }

                if (imageView.androidLayoutY != null) {
                    newImageView?.y = (imageView.androidLayoutY!!.replace("dp", "").toDouble()
                            * screenRatioFactor).toFloat()
                }

                if (imageView.androidRotation != null) {
                    newImageView?.rotation = imageView.androidRotation!!.toFloat()
                }

                if (imageView.androidAlpha != null) {
                    newImageView?.alpha = imageView.androidAlpha!!.toFloat()
                }

                rootLayout?.addView(newImageView)

                newImageView?.setOnClickListener {

                    Glide.with(this@EditingScreen)
                        .load(newImageView?.getTag(R.id.imagePath))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageViewSVG!!)

                    textRoot?.visibility = View.GONE
                    stickerRoot?.visibility = View.GONE
                    layerRoot?.visibility = View.VISIBLE

                    if (currentText != null) {
                        currentText!!.setBackgroundColor(Color.TRANSPARENT)
                    }

                    if (customSticker != null) {
                        customSticker?.disableAllOthers()
                    }

                }

            } else {

                Log.d("myPosition", "else $index")

                customSticker = CustomImageView(this)
                customSticker?.updateCallBack(this)

                var path: String? = null

                if (imageView.appSrcCompat != null) {

                    try {
                        path =
                            "file:///android_asset/category/${Constant.labelCategory}/assets/${Constant.labelNumber}/$index.png"
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }

                    path?.let {
                        Glide.with(this@EditingScreen)
                            .load(it)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(customSticker!!.imageView)
                    }

                    customSticker!!.imageView.setTag(R.id.imagePath, "$path")

                }

                if (imageView.androidLayoutWidth != null && imageView.androidLayoutHeight != null) {

                    val width = (imageView.androidLayoutWidth.toString()
                        .replace("dp", "")).toInt() * screenRatioFactor
                    val height = (imageView.androidLayoutHeight.toString()
                        .replace("dp", "")).toInt() * screenRatioFactor
                    val layoutParams = FrameLayout.LayoutParams(width.toInt(), height.toInt())
                    customSticker!!.imageView.layoutParams = layoutParams
                }

                if (imageView.androidLayoutX != null) {
                    customSticker!!.x =
                        (imageView.androidLayoutX!!.replace("dp", "").toDouble()
                                * screenRatioFactor).toFloat()
                }

                if (imageView.androidLayoutY != null) {
                    customSticker!!.y = (imageView.androidLayoutY!!.replace("dp", "").toDouble()
                            * screenRatioFactor).toFloat()
                }

                if (imageView.androidRotation != null) {
                    customSticker!!.rotation = imageView.androidRotation!!.toFloat()
                }

                if (imageView.androidAlpha != null) {
                    customSticker!!.imageView.alpha = imageView.androidAlpha!!.toFloat()
                }

                rootLayout?.addView(customSticker)

            }

        }

    }

    private var newTextView: TextView? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun addText(artist: ArrayList<com.example.esportslogomaker.datamodel.TextView>) {

        artist.forEachIndexed { _, textView ->

            newTextView = TextView(this@EditingScreen)

            if (!textView.androidLayoutWidth.equals("wrap_content") && !textView.androidLayoutHeight.equals(
                    "wrap_content"
                )
            ) {

                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                newTextView?.layoutParams = params

            }

            if (textView.androidText != null) {
                newTextView?.text = textView.androidText
            }

            if (textView.androidTextColor != null) {
                newTextView?.setTextColor(Color.parseColor(textView.androidTextColor))
            }

            if (textView.androidLayoutX != null) {
                newTextView?.x = (textView.androidLayoutX!!.replace("dp", "").toDouble()
                        * screenRatioFactor).toFloat()
            }

            if (textView.androidLayoutY != null) {
                newTextView?.y = (textView.androidLayoutY!!.replace("dp", "").toDouble()
                        * screenRatioFactor).toFloat()
            }

            if (textView.androidTextSize != null) {
                newTextView?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    ((textView.androidTextSize?.replace(
                        "sp",
                        ""
                    ))?.toFloat()?.times(screenRatioFactor))!!.toFloat()
                )
            }

            if (textView.androidBackground != null) {
                newTextView?.setBackgroundColor(Color.parseColor(textView.androidBackground))
            }

            if (textView.androidLetterSpacing != null) {
                newTextView?.letterSpacing = textView.androidLetterSpacing!!.toFloat()
            }

            if (textView.androidRotation != null) {
                newTextView?.rotation = textView.androidRotation!!.toFloat()
            }

            if (textView.androidTextAlignment != null) {

                when {
                    textView.androidTextAlignment.equals("center", ignoreCase = true) -> {
                        newTextView?.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    }
                    textView.androidTextAlignment.equals("textEnd", ignoreCase = true) -> {
                        newTextView?.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

                    }
                    textView.androidTextAlignment.equals("textStart", ignoreCase = true) -> {
                        newTextView?.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                    }
                }
            }

            if (textView.androidAlpha != null) {
                newTextView?.alpha = textView.androidAlpha!!.toFloat()
            }

            if (textView.androidFontFamily != null) {

                val typeface = try {
                    Typeface.createFromAsset(
                        assets,
                        "font/${textView.androidFontFamily.toString().replace("@font/", "")}.ttf"
                    )
                } catch (ex: java.lang.Exception) {
                    ex.printStackTrace()
                    null
                }

                if (typeface != null) {
                    newTextView?.typeface = typeface
                }

                if (textView.androidTextStyle != null) {
                    when {
                        textView.androidTextStyle.equals("bold") -> {
                            newTextView?.setTypeface(typeface, Typeface.BOLD)
                        }
                        textView.androidTextStyle.equals("italic") -> {
                            newTextView?.setTypeface(typeface, Typeface.ITALIC)
                        }
                        textView.androidTextStyle.equals("normal") -> {
                            newTextView?.setTypeface(typeface, Typeface.NORMAL)
                        }
                    }
                }

            }

            val moveViewTouchListener = MoveViewTouchListener(this, newTextView)

            newTextView?.setOnTouchListener(moveViewTouchListener)
            moveViewTouchListener.callBacks = this
            rootLayout?.addView(newTextView)

        }
    }

    override fun stickerViewClickDown(currentView: CustomImageView) {

        customSticker = currentView

        Glide.with(this@EditingScreen)
            .load(customSticker!!.imageView.getTag(R.id.imagePath))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageViewSVG!!)

        if (customSticker != null) {
            customSticker?.disableAllOthers()
        }

        showLayerContainer()

        if (layerRoot?.visibility == View.GONE) {
            layerRoot?.visibility = View.VISIBLE
        }

        stickerRoot?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        if (customSticker?.imageView?.alpha.toString().replace("0.", "") == "1.0") {
            stickerOpacitySeekBar?.progress = 10
        } else {
            stickerOpacitySeekBar?.progress = customSticker?.imageView?.alpha.toString()
                .replace("0.", "").toFloat().roundToInt().toString().toInt()
        }
    }

    override fun stickerViewDeleteClick() {

    }

    override fun stickerViewScrollViewEnable() {

    }

    override fun stickerViewScrollViewDisable() {

    }

    override fun saveAsImageFile() {

    }

    override fun saveAsPdfFile() {

    }

    override fun showTextControls() {

    }

    override fun setCurrentText(view: View) {
        callingText(view)
    }

    private fun callingText(view: View) {

        if (customSticker != null) {
            customSticker?.disableAllOthers()
        }

        textDownBtn?.visibility = View.VISIBLE
        textRootContainer?.visibility = View.VISIBLE

        if (textRoot?.visibility == View.GONE) {
            textRoot?.visibility = View.VISIBLE
        }

        if (layerRoot?.visibility == View.VISIBLE) {
            layerRoot?.visibility = View.GONE
        }

        if (stickerRoot?.visibility == View.VISIBLE) {
            stickerRoot?.visibility = View.GONE
        }

        if (currentText != null) {

            currentText!!.setBackgroundColor(Color.TRANSPARENT)
            currentText = view as TextView
            currentText!!.background = ContextCompat.getDrawable(this, R.drawable.my_border)

        } else {

            currentText = view as TextView
            currentText!!.background = ContextCompat.getDrawable(this, R.drawable.my_border)

        }

        if (currentText!!.alpha.toString().replace("0.", "") == "1.0") {
            tvSeekAlpha!!.progress = 10
        } else {
            tvSeekAlpha!!.progress =
                currentText!!.alpha.toString().replace("0.", "").toFloat().roundToInt().toString()
                    .toInt()
        }

        if (currentText?.textSize?.toInt()!! <= 300) {
            tvSizeSeekBar?.progress = (currentText?.textSize?.toInt()!!)
        }

        when (currentText!!.typeface.style) {

            Typeface.BOLD_ITALIC -> {
                textBold!!.isSelected = true
                textItalic!!.isSelected = true
                boldState = true
                italicState = true
            }

            Typeface.BOLD -> {
                textBold!!.isSelected = true
                textItalic!!.isSelected = false
                boldState = true
                italicState = false
            }

            Typeface.ITALIC -> {
                textBold!!.isSelected = false
                textItalic!!.isSelected = true
                boldState = false
                italicState = true
            }

            else -> {
                textBold!!.isSelected = false
                textItalic!!.isSelected = false
                boldState = false
                italicState = false
            }
        }

        if (currentText!!.text.toString().matches(Regex(".*[a-z].*"))
            && !currentText!!.text.toString().matches(Regex(".*[A-Z].*"))
        ) {
            textCapital!!.isSelected = false
            textSmall!!.isSelected = true
        } else if (currentText!!.text.toString().matches(Regex(".*[A-Z].*"))
            && !currentText!!.text.toString().matches(Regex(".*[a-z].*"))
        ) {
            textCapital!!.isSelected = true
            textSmall!!.isSelected = false
        } else {
            textSmall!!.isSelected = false
            textCapital!!.isSelected = false
        }

        if (currentText!!.paintFlags == Paint.UNDERLINE_TEXT_FLAG) {
            underlineState = true
            textUnderline!!.isSelected = true
        } else {
            underlineState = false
            textUnderline!!.isSelected = false
        }

    }

    override fun setOnColorClickListener(position: Int) {
        if (currentText != null) {
            currentText!!.setTextColor(ContextCompat.getColor(this, Constant.colorArray[position]))
        }
    }

    override fun setOnStickerClickListener(position: Int, isShapeOrNot: Boolean) {
        customSticker = CustomImageView(this)
        customSticker?.updateCallBack(this)

        if (isShapeOrNot) {

            //val path = "category/shape/${position}.webp"
            val path = "file:///android_asset/category/shape/$position.webp"

            Log.d("myStickerIs", path)

            //   val newBit: Bitmap? = Utils.getBitmapFromAsset(this, path)

            customSticker?.let {

                Glide.with(this@EditingScreen)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(it.imageView)

                it.imageView.setTag(R.id.imagePath, path)

                rootLayout?.addView(it)
            }


        } else {

            //val path = "category/sticker/${position}.webp"
            val path = "file:///android_asset/category/sticker/$position.webp"
            Log.d("myStickerIs", path)

            //val newBit: Bitmap? = Utils.getBitmapFromAsset(this, path)

            customSticker?.let {

                Glide.with(this@EditingScreen)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(it.imageView)

                it.imageView.setTag(R.id.imagePath, path)

                rootLayout?.addView(it)
            }

        }
    }

    override fun addText(string: String?) {
        if (string != null) {

            val newText = TextView(this)

            newText.id = View.generateViewId()
            newText.text = string
            newText.isCursorVisible = false
            newText.setTextColor(Color.BLACK)

            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            newText.layoutParams = params

            val textSizePx = Utils.dpToPx(30f, this)


            newText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx.toFloat())

            newText.y = 100f
            newText.x = 150f

            rootLayout!!.addView(newText)

            if (rootLayout?.childCount == 0) {
                //no child in the layout
                Log.e("childCounter", "" + rootLayout?.childCount)

            } else {

                for (i in 0 until rootLayout!!.childCount) {

                    if (rootLayout!!.getChildAt(i) is TextView) {

                        val textView = rootLayout!!.getChildAt(i) as TextView

                        val viewTreeObserver = textView.viewTreeObserver

                        if (viewTreeObserver.isAlive) {

                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {

                                override fun onGlobalLayout() {
                                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    /*  parentWidth = layoutMove!!.width
                                      parentHeight = layoutMove!!.height
                                      newTextColor = textView.currentTextColor*/

                                }
                            })
                        }

                        val moveViewTouchListener =
                            MoveViewTouchListener(this, rootLayout!!.getChildAt(i) as TextView)
                        rootLayout!!.getChildAt(i).setOnTouchListener(moveViewTouchListener)
                        moveViewTouchListener.callBacks = this
                    }
                }
            }

        } else {
            Log.d("myTextValue", "Text is null")
        }

    }

    override fun updateText(string: String) {
        currentText?.let {
            it.text = string
        }
    }

    override fun setFont(fontName: String) {
        currentText?.let {
            val tf = Typeface.createFromAsset(assets, "font/$fontName")
            it.typeface = tf
        }
    }
}