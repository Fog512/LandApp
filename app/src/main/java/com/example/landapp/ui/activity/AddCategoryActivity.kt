package com.example.landapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.landapp.databinding.ActivityAddCategoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.imBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(0,0)
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var category = ""
    private fun validateData() {
        category = binding.categoryEdt.text.toString().trim()
        if (category.isEmpty()){
            Toast.makeText(this@AddCategoryActivity, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
        }
        else {
            binding.progressBar.visibility = View.VISIBLE
            addCategory()
        }
    }

    private fun addCategory() {
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = timestamp.toString()
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = mAuth.uid.toString()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                binding.categoryEdt.text = null
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.INVISIBLE
            }
            .addOnFailureListener {
                Toast.makeText(this, "Thêm thất bại do ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}