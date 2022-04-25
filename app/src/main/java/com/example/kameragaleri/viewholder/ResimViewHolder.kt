package com.example.kameragaleri.viewholder

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.kameragaleri.R

class ResimViewHolder(itemView : View, itemClick : (position : Int)->Unit, itemLongClick : (position : Int)->Boolean) : RecyclerView.ViewHolder(itemView)
{
    var ivResim : ImageView

    init {
        ivResim = itemView.findViewById(R.id.ivResim)

        itemView.setOnClickListener { itemClick(adapterPosition) }

        itemView.setOnLongClickListener { itemLongClick(adapterPosition) }
    }

    fun bindData(context : Context, uri : Uri)
    {
        ivResim.setImageURI(uri)
    }
}