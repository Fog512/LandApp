package com.example.landapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.landapp.model.Category
import com.example.landapp.filter.FilterCategory
import com.example.landapp.ui.activity.ProjectListActivityAdmin
import com.example.landapp.R
import com.google.firebase.database.FirebaseDatabase

class AdapterCategory(
    private val c : Context,
    private var list : ArrayList<Category>
) : RecyclerView.Adapter<AdapterCategory.HolderCategory>() , Filterable {

    val context : Context = c
    var categoryArrayList : ArrayList<Category> = list
    val filterList : ArrayList<Category> = categoryArrayList
    private var filter : FilterCategory? = null

    inner class HolderCategory(itemView: View) : RecyclerView.ViewHolder(itemView){
        var categoryTv : TextView = itemView.findViewById(R.id.categoryTv)
        var deleteIb : ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        return HolderCategory(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_rv_category,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp = model.timestamp

        holder.categoryTv.text = category
        holder.deleteIb.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Xoá")
                .setMessage("Bạn có chắc muốn xoá?")
                .setPositiveButton("Có"){a, d ->
                    deleteCategory(model, holder)
                }
                .setNegativeButton("Không"){a, d->
                    a.dismiss()
                }
                .show()
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProjectListActivityAdmin::class.java)
            intent.putExtra("categoryId",id)
            intent.putExtra("category",category)
            context.startActivity(intent)
        }
    }

    private fun deleteCategory(model: Category, holder: HolderCategory) {
        val id = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Xoá Thành Công", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Thất bại do ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    override fun getFilter(): Filter {
        if (filter == null){
            FilterCategory(filterList,this).also {
                filter = it
            }
        }
        return filter as FilterCategory
    }


}