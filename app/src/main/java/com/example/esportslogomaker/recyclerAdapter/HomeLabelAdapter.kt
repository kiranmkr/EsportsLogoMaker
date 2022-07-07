package com.example.esportslogomaker.recyclerAdapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.esportslogomaker.R
import com.example.esportslogomaker.customCallBack.TemplateClickCallBack

import com.example.esportslogomaker.utils.Constant
import com.example.esportslogomaker.utils.loadThumbnail


class HomeLabelAdapter(callBack: TemplateClickCallBack) : RecyclerView.Adapter<HomeLabelAdapter.ViewHolder>() {

    private var mContext: Activity? = null
    private var category: String = "esports"
    private var callBackMain: TemplateClickCallBack = callBack

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        mContext = parent.context as Activity
        val view = LayoutInflater.from(parent.context).inflate(R.layout.re_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val path: String =
            "file:///android_asset/category/${category}/thumbnail/" + (position + 1) + ".webp"

        Log.d("myPosition", path)

        holder.thumbNail.loadThumbnail(path, null)
    }

    override fun getItemCount(): Int {
        return 56
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumbNail: ImageView


        init {

            thumbNail = itemView.findViewById(R.id.placeHolder1)

            itemView.setOnClickListener {

                Constant.labelNumber = adapterPosition + 1
                Constant.labelCategory = category

                callBackMain.onItemClickListener(
                    false
                )

            }

        }

    }

}