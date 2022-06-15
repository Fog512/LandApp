package com.example.landapp.filter

import android.annotation.SuppressLint
import android.widget.Filter
import com.example.landapp.adapter.AdapterFavoriteProject
import com.example.landapp.model.Project

class FilterFavoriteProject(
    list : ArrayList<Project>,
    adapter : AdapterFavoriteProject
): Filter() {

    var filterList : ArrayList<Project> = list
    private var adapterFavoriteProject : AdapterFavoriteProject = adapter

    override fun performFiltering(p0: CharSequence?): FilterResults {
        var constraint : CharSequence? = p0
        val results = FilterResults()
        if (constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().lowercase()
            var filterModels = ArrayList<Project>()
            for (i in filterList.indices){
                if (filterList[i].title.lowercase().contains(constraint)){
                    filterModels.add(filterList[i])
                }
            }
            results.count = filterModels.size
            results.values = filterModels
        }
        else{
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun publishResults(p0: CharSequence?, p1: FilterResults) {
        adapterFavoriteProject.projectList = p1.values as ArrayList<Project> /* = java.util.ArrayList<com.example.landapp.model.Project> */
        adapterFavoriteProject.notifyDataSetChanged()
    }
}