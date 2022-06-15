package com.example.landapp.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.landapp.model.Project
import com.example.landapp.adapter.AdapterProjectUser
import com.example.landapp.databinding.FragmentProjectListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception


class ProjectListFragment : Fragment() {

    private lateinit var binding: FragmentProjectListBinding
    companion object {
        const val TAG = "Fragment"

        fun newInstance(categoryId: String, uid: String): ProjectListFragment {
            val fragment = ProjectListFragment()
            val args = Bundle()
            args.putString("categoryId",categoryId)
            args.putString("uid",uid)
            fragment.arguments = args
            return fragment
        }
    }

    private var categoryId =""
    private var uid = ""
    private lateinit var projectList : ArrayList<Project>
    private lateinit var adapterProjectUser : AdapterProjectUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null){
            categoryId = args.getString("categoryId")!!
            uid = args.getString("uid")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProjectListBinding.inflate(LayoutInflater.from(context), container, false)

        when (categoryId) {
            "Tất cả" -> {
                loadAll()
            }
            "Xem nhiều nhất" -> {
                loadMostViewed("viewsCount")
            }
            else -> {
                loadCategorizedProject()
            }
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterProjectUser.filter.filter(p0)
                }
                catch (e: Exception){
                    Log.e(TAG,"${e.message}")
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        return binding.root
    }

    private fun loadCategorizedProject() {
        projectList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Projects")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    projectList.clear()
                    for (ds in snapshot.children){
                        val model = ds.getValue(Project::class.java)
                        projectList.add(model!!)
                    }
                    adapterProjectUser = AdapterProjectUser(context!!,projectList)
                    binding.projectsRv.adapter = adapterProjectUser
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun loadMostViewed(s: String) {
        projectList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Projects")
        ref.orderByChild(s).limitToLast(10)
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                projectList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(Project::class.java)
                    projectList.add(model!!)
                }
                adapterProjectUser = AdapterProjectUser(context,projectList)
                binding.projectsRv.adapter = adapterProjectUser
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loadAll() {
        projectList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Projects")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                projectList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(Project::class.java)
                    projectList.add(model!!)
                }
                adapterProjectUser = AdapterProjectUser(context!!,projectList)
                binding.projectsRv.adapter = adapterProjectUser
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}