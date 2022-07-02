package com.example.esportslogomaker.recyclerAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.esportslogomaker.R
import com.example.esportslogomaker.customCallBack.StickerClick
import com.example.esportslogomaker.utils.loadThumbnail

class StickerAdapter(callBack: StickerClick) :
    RecyclerView.Adapter<StickerAdapter.ViewHolder>() {

    private var context: Context? = null
    private var itemCallBAck: StickerClick = callBack


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layer_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = "file:///android_asset/category/sticker/${position + 1}.webp"

        Log.d("myShapePath", path)

        holder.thumbNail.loadThumbnail(path, null)
    }

    override fun getItemCount(): Int {
        return 99
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumbNail: ImageView

        init {

            thumbNail = itemView.findViewById(R.id.imageView52)

            itemView.setOnClickListener {
                itemCallBAck.setOnStickerClickListener(adapterPosition+1, false)
            }

        }

    }

}