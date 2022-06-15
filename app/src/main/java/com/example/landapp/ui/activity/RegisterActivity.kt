package com.example.landapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.landapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth : FirebaseAuth
    private val tag  = "Register"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        binding.registerBtn.setOnClickListener {
            validateData()
        }
    }

    private var name = " "
    private var email = " "
    private var passoword = " "

    private fun validateData() {
        name = binding.nameEdt.text.toString().trim()
        email = binding.emailEdt.text.toString().trim()
        passoword = binding.passwordEdt.text.toString().trim()
        val cPassword = binding.passwordConfirmEdt.text.toString().trim()
        if (name.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
        }
        else if (passoword.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show()
        }
        else if (passoword.isEmpty()) {
            Toast.makeText(this, "Vui lòng xác nhận mật khẩu", Toast.LENGTH_SHORT).show()
        }
        else if (passoword != cPassword) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
        }
        else {
            signUp()
        }
    }

    private fun signUp() {
        mAuth.createUserWithEmailAndPassword(email,passoword)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        val timestamp = System.currentTimeMillis()
        val uid = mAuth.uid
        val hashMap : HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this@RegisterActivity, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Log.d(tag,"Failed register ${it.message}")
            }
    }
}