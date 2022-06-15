package com.example.landapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.landapp.MyApplication
import com.example.landapp.databinding.ActivityDetailProjectBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailProjectActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailProjectBinding
    private lateinit var mAuth : FirebaseAuth

    private companion object {
        const val TAG = "DETAILS"

    }
    private var projectId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        projectId = intent.getStringExtra("projectId")!!

        MyApplication.incrementBookView(projectId)

        loadProjectDetail()
        binding.backBtn.setOnClickListener {
            onBackPressed()
            overridePendingTransition(0,0)
        }

        binding.favoriteBtn.setOnClickListener {
            if (mAuth.currentUser == null){
                Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
            }
            else {
                addToFavorite()
            }
        }

    }

    private fun addToFavorite() {
        val timestamp = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["projectId"] = projectId
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(mAuth.uid!!).child("Favorites").child(projectId)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Đã lưu", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProjectDetail() {
        val ref = FirebaseDatabase.getInstance().getReference("Projects")
        ref.child(projectId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val businessId = snapshot.child("businessId").value
                    val categoryId = snapshot.child("categoryId").value
                    val address = snapshot.child("description").value
                    val id = snapshot.child("id").value
                    val price = snapshot.child("price").value
                    val timestamp = snapshot.child("timestamp").value
                    val title = snapshot.child("title").value
                    val uid = snapshot.child("uid").value
                    val url = snapshot.child("url").value
                    val url2 = snapshot.child("url2").value
                    val url3 = snapshot.child("url3").value
                    val view = snapshot.child("viewsCount").value

                    val date = MyApplication.formatTimeStamp(timestamp.toString().toLong())
                    val listImage = ArrayList<SlideModel>()
                    listImage.add(SlideModel(url.toString(),"1"))
                    listImage.add(SlideModel(url2.toString(),"2"))
                    listImage.add(SlideModel(url3.toString(),"3"))
                    binding.imageSlider.setImageList(listImage,ScaleTypes.FIT)
                    binding.dateProject.text = date
                    binding.addressProject.text = address.toString()
                    binding.subTitleTvDetail.text = title.toString()
                    binding.priceProject.text = price.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

}