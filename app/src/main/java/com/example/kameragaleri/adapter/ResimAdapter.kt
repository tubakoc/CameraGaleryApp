package com.example.kameragaleri.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kameragaleri.R
import com.example.kameragaleri.viewholder.ResimViewHolder

class ResimAdapter(val context : Context, var liste : ArrayList<Uri>, val itemClick : (position : Int)->Unit, val itemLongClick : (position : Int)->Boolean) : RecyclerView.Adapter<ResimViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResimViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.rvc_resim, parent, false)

        return ResimViewHolder(v, itemClick, itemLongClick)
    }

    override fun onBindViewHolder(holder: ResimViewHolder, position: Int) {
        holder.bindData(context, liste.get(position))
    }

    override fun getItemCount(): Int {
        return liste.size
    }
}