package com.example.esportslogomaker.customUi

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
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
        }
    }
}