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
import com.example.landapp.filter.FilterFavoriteProject
import com.example.landapp.model.Project
import com.example.landapp.ui.activity.DetailProjectActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class AdapterFavoriteProject(
    c: Context,
    List : ArrayList<Project>
) : RecyclerView.Adapter<AdapterFavoriteProject.HolderFavoriteProject>() , Filterable {

    var context : Context = c
    var projectList : ArrayList<Project> = List
    private val filterList : ArrayList<Project> = projectList
    private var filter :  FilterFavoriteProject?= null

    inner class HolderFavoriteProject(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<ImageView>(R.id.imageViewerFv)!!
        val titleTv : TextView = itemView.findViewById(R.id.titleTvProjectFv)
        val desTv : TextView = itemView.findViewById(R.id.desTvProjectFv)
        val priceTv : TextView = itemView.findViewById(R.id.priceTvProjectFv)
        val dateTv : TextView = itemView.findViewById(R.id.dateTvFv)
        val moreBtn : ImageButton = itemView.findViewById(R.id.imbMoreFv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFavoriteProject {
        return HolderFavoriteProject(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_rv_favorite,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HolderFavoriteProject, position: Int) {
        val model = projectList[position]
        loadDetail(model, holder)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailProjectActivity::class.java)
            intent.putExtra("projectId",model.id)
            context.startActivity(intent)
        }
    }

    private fun loadDetail(model: Project, holder: HolderFavoriteProject) {
        val projectId = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Projects")
        ref.child(projectId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryId = snapshot.child("categoryId").value
                    val address = snapshot.child("description").value
                    val businessId = snapshot.child("business").value
                    val id = snapshot.child("id").value
                    val price = snapshot.child("price").value
                    val timestamp = snapshot.child("timestamp").value
                    val title = snapshot.child("title").value
                    val uid = snapshot.child("uid").value
                    val url = snapshot.child("url").value

                    val date = MyApplication.formatTimeStamp(timestamp.toString().toLong())
                    holder.dateTv.text = date
                    holder.priceTv.text = price.toString()
                    holder.titleTv.text = title.toString()
                    holder.desTv.text = address.toString()
                    try {
                        Glide.with(context).load(url).placeholder(R.drawable.pic1).into(holder.imageView)
                    }
                    catch (e:Exception){
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterFavoriteProject(filterList,this)
        }
        return filter as FilterFavoriteProject
    }
}