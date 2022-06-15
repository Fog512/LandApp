package com.example.landapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.landapp.*
import com.example.landapp.filter.FilterProjectUser
import com.example.landapp.model.Project
import com.example.landapp.ui.activity.DetailProjectActivity
import java.lang.Exception

class AdapterProjectUser(
    c: Context?,
    list: ArrayList<Project>
) : RecyclerView.Adapter<AdapterProjectUser.HolderProjectUser>() , Filterable {

    var context : Context? = c
    var projectArrayList : ArrayList<Project> = list
    private val filterList : ArrayList<Project> = projectArrayList
    private var filter :  FilterProjectUser?= null

    inner class HolderProjectUser(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<ImageView>(R.id.imageViewerUser)!!
        val titleTv : TextView = itemView.findViewById(R.id.titleTvProjectUser)
        val desTv : TextView = itemView.findViewById(R.id.desTvProjectUser)
        val priceTv : TextView = itemView.findViewById(R.id.priceTvProjectUser)
        val dateTv : TextView = itemView.findViewById(R.id.dateTvUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProjectUser {
        return HolderProjectUser(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_rv_project_user,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HolderProjectUser, position: Int) {
        val model = projectArrayList[position]
        val projectId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val price = model.price
        val description = model.description
        val projectUrl = model.url
        val timestamp = model.timestamp
        val date = MyApplication.formatTimeStamp(timestamp)

        holder.titleTv.text = title
        holder.desTv.text = description
        try {
            Glide.with(context!!).load(projectUrl)
                .placeholder(R.drawable.pic1).into(holder.imageView)
        }
        catch (e: Exception){

        }
        holder.dateTv.text = date
        holder.priceTv.text = price


        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailProjectActivity::class.java)
            intent.putExtra("projectId",projectId)
            context!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return projectArrayList.size
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterProjectUser(filterList,this)
        }
        return filter as FilterProjectUser
    }
}