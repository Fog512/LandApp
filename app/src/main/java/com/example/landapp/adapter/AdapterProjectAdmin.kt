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
import com.example.landapp.filter.FilterProjectAdmin
import com.example.landapp.model.Project
import com.example.landapp.ui.activity.DetailProjectActivity
import java.lang.Exception

class AdapterProjectAdmin(
    c: Context,
    List : ArrayList<Project>
) : RecyclerView.Adapter<AdapterProjectAdmin.HolderProjectAdmin>() , Filterable {

    var context : Context = c
    var projectList : ArrayList<Project> = List
    private val filterList : ArrayList<Project> = projectList
    private var filter :  FilterProjectAdmin?= null

    inner class HolderProjectAdmin(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<ImageView>(R.id.imageViewer)!!
        val progressBar : ProgressBar = itemView.findViewById(R.id.progressBarImage)
        val titleTv : TextView = itemView.findViewById(R.id.titleTvProject)
        val desTv : TextView = itemView.findViewById(R.id.desTvProject)
        val priceTv : TextView = itemView.findViewById(R.id.priceTvProject)
        val dateTv : TextView = itemView.findViewById(R.id.dateTv)
        val moreBtn : ImageButton = itemView.findViewById(R.id.imbMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProjectAdmin {
        return HolderProjectAdmin(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_rv_project,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HolderProjectAdmin, position: Int) {
        val model = projectList[position]
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
            Glide.with(context).load(projectUrl)
                .placeholder(R.drawable.pic1).into(holder.imageView)
        }
        catch (e: Exception){

        }
        holder.dateTv.text = date
        holder.priceTv.text = price

        holder.moreBtn.setOnClickListener {
            MyApplication.deleteProject(context, projectId, projectUrl, title)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailProjectActivity::class.java)
            intent.putExtra("projectId",projectId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterProjectAdmin(filterList,this)
        }
        return filter as FilterProjectAdmin
    }
}