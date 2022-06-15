package com.example.landapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.landapp.MyApplication
import com.example.landapp.model.Project
import com.example.landapp.adapter.AdapterFavoriteProject
import com.example.landapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.log

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var projectList : ArrayList<Project>
    private lateinit var adapterFavoriteProject: AdapterFavoriteProject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
            mAuth = FirebaseAuth.getInstance()
        loadUser()
        loadFavorite()

        binding.btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(0,0)
        }

    }

    private fun loadFavorite() {
        projectList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(mAuth.uid!!).child("Favorites")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    projectList.clear()
                    for (ds in snapshot.children){
                        val projectId = ds.child("projectId").value
                        val model = Project()
                        model.id = projectId.toString()
                        projectList.add(model)
                    }
                    binding.FavoriteTV.text = projectList.size.toString()
                    adapterFavoriteProject = AdapterFavoriteProject(this@ProfileActivity,projectList)
                    binding.rvProjectsFv.layoutManager = LinearLayoutManager(this@ProfileActivity)
                    binding.rvProjectsFv.adapter = adapterFavoriteProject
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun loadUser() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(mAuth.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = snapshot.child("email").value
                    val name = snapshot.child("name").value
                    val timestamp = snapshot.child("timestamp").value
                    val userType = snapshot.child("userType").value
                    try {
                        val  date = MyApplication.formatTimeStamp(timestamp.toString().toLong())
                        binding.memberTv.text = date
                    }
                    catch (e: Exception){}


                    binding.nameTv.text = name.toString()
                    binding.emailTv.text = email.toString()
                    binding.accountTypeTv.text = userType.toString()

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
}