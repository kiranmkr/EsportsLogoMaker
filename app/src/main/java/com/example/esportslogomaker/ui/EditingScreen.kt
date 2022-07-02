package com.example.esportslogomaker.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.esportslogomaker.R
import com.example.esportslogomaker.customCallBack.ExportDialogCallBack
import com.example.esportslogomaker.customDialog.AddNewText
import com.example.esportslogomaker.customSticker.CustomImageView
import com.example.esportslogomaker.datamodel.Root
import com.example.esportslogomaker.recyclerAdapter.*
import com.example.esportslogomaker.utils.Constant
import com.example.esportslogomaker.utils.MoveViewTouchListener
import com.example.logodesign.customDialog.ExportDialog
import com.example.logodesign.customDialog.UpdateNewText
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EditingScreen : AppCompatActivity(), CustomImageView.CustomImageCallBack,
    ExportDialogCallBack, MoveViewTouchListener.EditTextCallBacks {

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

    private var svgRecyclerView: RecyclerView? = null
    private var svgColor: RecyclerView? = null
    private var svgLayersAdapter: SVGLayersAdapter? = null
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

    //String var
    var labelCategory: String? = null
    var labelNumber: Int = 0

    var layerSize: Int = 1

    var currentText: TextView? = null

    //ArrayList var
    var layerListOfAssest: Array<String?>? = null
    var addNewText: AddNewText? = null
    var updateNewText: UpdateNewText? = null

    var customSticker: CustomImageView? = null

    var currentRequestCode = 0
    var currentPhotoPath: String? = null

    var textRoot: View? = null
    var layerRoot: View? = null
    var stickerRoot: View? = null
    var reTextFont: RecyclerView? = null

    var reFontAdapter: FontAdapter? = null

    //ArrayList var
    var fileNames: Array<String?>? = null
    var tvSeekAlpha: SeekBar? = null
    var tvSizeSeekBar: SeekBar? = null
    var stickerOpacitySeekBar: SeekBar? = null
    var stickerDelete: ImageView? = null
    var stickerFlip: ImageView? = null
    var stickerCheckmark: ImageView? = null

    var textBold: ImageView? = null
    var textItalic: ImageView? = null
    var textCapital: ImageView? = null
    var textSmall: ImageView? = null
    var textUnderline: ImageView? = null

    //Boolean var
    var boldState = false
    var italicState = false
    var underlineState = false
    var layerStatusColor = false
    var exportDialog: ExportDialog? = null

    private var dialog: Dialog? = null

    private var imageViewArray: ArrayList<com.example.esportslogomaker.datamodel.ImageView> =
        ArrayList()
    private var textViewJson: ArrayList<com.example.esportslogomaker.datamodel.TextView> =
        ArrayList()
    private var rootLayout: RelativeLayout? = null
    private var screenRatioFactor: Double = 1.0
    private var screenWidth: Double = 720.0

    private var imageViewSVG: ImageView? = null
    private var currentLayer: ImageView? = null

    private var btnDeleteText: ImageView? = null
    private var btnChangeText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editing_screen)

        exportDialog = ExportDialog(this@EditingScreen, this)

        workerThread.execute {

            labelCategory = Constant.labelCategory
            labelNumber = Constant.labelNumber

            Log.d("myCategoryData", "$labelCategory -- $labelNumber")

            workerHandler.postDelayed({
                initID()
            }, 0)

        }

        //Window dialog code
        dialog = Dialog(this@EditingScreen)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dilog_svg_loader)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
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


        val viewTreeObserver = rootLayout?.viewTreeObserver
        viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rootLayout?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                if (rootLayout != null) {
                    screenWidth = rootLayout!!.width.toDouble()
                }

                if (loadJSONFromAsset() != null) {

                    val obj = JSONObject(loadJSONFromAsset()!!)
                    val om = ObjectMapper()
                    val root: Root = om.readValue(obj.toString(), Root::class.java)

                    if (root.absoluteLayout != null) {
                        screenRatioFactor =
                            screenWidth / root.absoluteLayout!!.androidLayoutWidth!!.replace(
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
                }

            }
        })

        //clickHandler()

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
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(newImageView!!)
                        newImageView?.setTag(R.id.imagePath, "$path")
                    }

                }

                /*  if (imageView.androidBackground != null) {

                      try {
                          path =
                              "file:///android_asset/category/${Constant.labelCategory}/assets/${Constant.labelNumber}/0.png"
                      } catch (ex: IOException) {
                          ex.printStackTrace()
                      }

                      path?.let {

                          Glide.with(this@EditingScreen)
                              .load(it)
                              .diskCacheStrategy(DiskCacheStrategy.ALL)
                              .into(newImageView!!)

                          Glide.with(this@EditingScreen)
                              .load(it)
                              .diskCacheStrategy(DiskCacheStrategy.ALL)
                              .into(imageViewSVG!!)

                          currentLayer = newImageView
                      }

                  }*/

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
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
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

                /*if (imageView.androidBackground != null) {

                    try {
                        path =
                            "file:///android_asset/category/${Constant.labelCategory}/assets/${Constant.labelNumber}/0.png"
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }

                    path?.let {

                        Glide.with(this@EditingScreen)
                            .load(it)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(customSticker!!.imageView)

                        Glide.with(this@EditingScreen)
                            .load(it)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageViewSVG!!)

                        currentLayer = customSticker!!.imageView
                    }

                }*/

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

    private fun addImage(bitmap: Bitmap) {

        try {

            customSticker = CustomImageView(this)
            customSticker?.updateCallBack(this@EditingScreen)

            customSticker?.let {

                it.setBitMap(bitmap)

                rootLayout?.addView(it)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stickerViewClickDown(currentView: CustomImageView) {

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

    override fun setCurrentText(view: View?) {

    }
}