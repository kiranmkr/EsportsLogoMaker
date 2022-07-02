package com.example.esportslogomaker.recyclerAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.esportslogomaker.R
import com.example.esportslogomaker.utils.loadThumbnail
import java.io.IOException


class SVGLayersAdapter(
    var categoryName: String,
    var layerSize: Int,
    var labelNumber: Int,
    itemCallBack: SvgLayersClick
) :
    RecyclerView.Adapter<SVGLayersAdapter.ViewHolder>() {

    private var context: Context? = null
    private var callBack: SvgLayersClick = itemCallBack

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        context = parent.context

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layer_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var path: String? = null

        try {
            path = "file:///android_asset/category/$categoryName/layer/$labelNumber/$position.png"
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        path?.let {
            Log.d("myPosition", it)
            holder.thumbNail.loadThumbnail(it, null)
        }

    }

    override fun getItemCount(): Int {
        return layerSize
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumbNail: ImageView

        init {

            thumbNail = itemView.findViewById(R.id.imageView52)

            itemView.setOnClickListener {
                callBack.setOnLayersClickListener(adapterPosition)
            }

        }

    }

    interface SvgLayersClick {
        fun setOnLayersClickListener(position: Int?)
    }
}