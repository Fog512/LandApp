package com.example.landapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.landapp.model.Category
import com.example.landapp.adapter.AdapterCategory
import com.example.landapp.databinding.ActivityDashBoardAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardAdminBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var categoryArrayList: ArrayList<Category>
    private lateinit var adapter: AdapterCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        checkUser()
        loadCategories()
        binding.logoutBtn.setOnClickListener {
            mAuth.signOut()
            checkUser()
        }

        binding.addCategoryBtn.setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
            overridePendingTransition(0,0)
        }

        binding.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    adapter.filter.filter(p0)
                }
                catch (e : Exception){ }
            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.addFab.setOnClickListener {
            startActivity(Intent(this, AddProjectActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this,ProfileAdminActivity::class.java))
        }

    }

    private fun loadCategories() {
        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(Category::class.java)
                    categoryArrayList.add(model!!)
                }
                adapter = AdapterCategory(this@DashboardAdminActivity,categoryArrayList)
                binding.rvCategories.layoutManager = LinearLayoutManager(this@DashboardAdminActivity)
                binding.rvCategories.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun checkUser() {
        val user = mAuth.currentUser
        if (user == null){
            startActivity(Intent(this@DashboardAdminActivity, MainActivity::class.java))
            finish()
        }
        else {
            val email = user.email
            binding.subTitleTv.text = email
        }
    }
}