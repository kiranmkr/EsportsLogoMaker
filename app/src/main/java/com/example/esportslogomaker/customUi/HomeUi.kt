package com.example.esportslogomaker.customUi

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.esportslogomaker.R
import com.example.esportslogomaker.customCallBack.TemplateClickCallBack
import com.example.esportslogomaker.recyclerAdapter.HomeLabelAdapter

class HomeUi @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var rootLayout: View
    private var recyclerViewShape: RecyclerView

    init {

        val mInflater =
            getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rootLayout = mInflater.inflate(R.layout.home_ui, this, true)

        recyclerViewShape = rootLayout.findViewById(R.id.recyclerViewShape)

    }

    fun updateUI(callBack: TemplateClickCallBack) {
        recyclerViewShape.apply {
            setHasFixedSize(true)
            adapter = HomeLabelAdapter(callBack)
            showAnimation()
        }
    }

    private fun showAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val controller = AnimationUtils.loadLayoutAnimation(
                rootLayout.context,
                R.anim.layout_animation_up_to_down
            )

            recyclerViewShape.layoutAnimation = controller
            recyclerViewShape.scheduleLayoutAnimation()
        }
    }
}