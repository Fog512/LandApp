package com.example.landapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.landapp.adapter.AdapterUser
import com.example.landapp.databinding.ActivityProfileAdminBinding
import com.example.landapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileAdminActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileAdminBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var arrayUserList : ArrayList<User>
    private lateinit var adapterUser: AdapterUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        loadUser()

        binding.btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(0,0)
        }

    }

    private fun loadUser() {
        arrayUserList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayUserList.clear()
                for (ds in snapshot.children){
                    val model = User(
                        ds.child("name").value.toString(),
                        ds.child("email").value.toString(),
                        ds.child("uid").value.toString(),
                        ds.child("userType").value.toString()
                    )
                    if (model.userType == "user"){
                        arrayUserList.add(model)
                    }

                }
                adapterUser = AdapterUser(this@ProfileAdminActivity,arrayUserList)
                binding.rvUsers.layoutManager = LinearLayoutManager(this@ProfileAdminActivity)
                binding.rvUsers.adapter = adapterUser
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}