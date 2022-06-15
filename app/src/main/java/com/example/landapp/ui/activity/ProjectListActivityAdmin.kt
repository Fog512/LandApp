package com.example.landapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.landapp.model.Project
import com.example.landapp.adapter.AdapterProjectAdmin
import com.example.landapp.databinding.ActivityProjectListAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProjectListActivityAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityProjectListAdminBinding
    private lateinit var projectArrayList : ArrayList<Project>
    private lateinit var adapterProjectAdmin : AdapterProjectAdmin

    private var categoryId = ""
    private var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!

        binding.backBtn.setOnClickListener {
            onBackPressed()
            overridePendingTransition(0,0)
        }

        binding.subTitleTv.text = category


        loadProjectList()

    }

    private fun loadProjectList() {
        projectArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Projects")
        ref.orderByChild("categoryId").equalTo(category)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    projectArrayList.clear()
                    for (ds in snapshot.children){
                        val model = ds.getValue(Project::class.java)
                        if (model != null)
                        {
                            projectArrayList.add(model)
                        }
                    }
                    adapterProjectAdmin = AdapterProjectAdmin(this@ProjectListActivityAdmin,projectArrayList)
                    binding.rvProject.adapter = adapterProjectAdmin
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}