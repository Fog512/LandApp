package com.example.landapp.filter

import android.annotation.SuppressLint
import android.widget.Filter
import com.example.landapp.adapter.AdapterCategory
import com.example.landapp.model.Category

class FilterCategory(
    private  val filterList : ArrayList<Category>,
    private  var adapter : AdapterCategory
) : Filter() {



    override fun performFiltering(p0: CharSequence?): FilterResults {
        var constraint = p0
        val result = FilterResults()

        if (constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().uppercase()
            val filteredModel : ArrayList<Category> = ArrayList()
            for (i in 0 until filterList.size){
                if (filterList[i].category.uppercase().contains(constraint)){
                    filteredModel.add(filterList[i])
                }
            }
            result.count = filteredModel.size
            result.values = filteredModel
        }
        else {
            result.count = filterList.size
            result.values = filterList
        }
        return  result
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun publishResults(p0: CharSequence?, p1: FilterResults) {
        adapter.categoryArrayList = p1.values as ArrayList<Category>
        adapter.notifyDataSetChanged()
    }
}