package com.example.landapp.ui.activity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.landapp.model.Category
import com.example.landapp.R
import com.example.landapp.databinding.ActivityAddProjectBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AddProjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProjectBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var categoryArrayList: ArrayList<Category>
    private var imageUri : Uri?= null
    private var imageUri2 : Uri?= null
    private var imageUri3 : Uri?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        loadCategory()

        val list = listOf("Cho thuê", "Rao Bán")
        val adapter = ArrayAdapter(this@AddProjectActivity, R.layout.list_item,list)
        binding.businessEdt.setAdapter(adapter)

        binding.imBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(0,0)
        }

        binding.image1.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@AddProjectActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                getImage()
            }
            else {
                requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

        }
        binding.image2.setOnClickListener {
            getImage2()
        }

        binding.image3.setOnClickListener {
            getImage3()
        }

        binding.smBtn.setOnClickListener {
            validateData()
        }
    }

    private var title = ""
    private var description = ""
    private var price = ""
    private var business = ""
    private var category = ""

    private fun validateData() {
        title = binding.titleEdt.text.toString().trim()
        description = binding.desEdt.text.toString().trim()
        price = binding.costEdt.text.toString().trim()
        business = binding.businessEdt.text.toString().trim()
        category = binding.categoryEdt.text.toString().trim()

        if (title.isEmpty()){
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
        else if (price.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
        else if (business.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
        else if (category.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
        else if (imageUri == null && imageUri2 == null && imageUri3 == null) {
            Toast.makeText(this, "Vui lòng thêm ảnh dự án", Toast.LENGTH_SHORT).show()
        }
        else {
            binding.progressBar.visibility = View.VISIBLE
            upLoadImageToStorage()
        }
    }

    private fun upLoadImageToStorage(){
        val timestamp = System.currentTimeMillis()
        val filePathAndName = "Images/$timestamp"
        var strRef = FirebaseStorage.getInstance().getReference(filePathAndName)
        strRef.putFile(imageUri!!)
            .addOnSuccessListener {
                val uriTask : Task<Uri> = it.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadUrl = "${uriTask.result}"
                uploadImIntoDb(uploadUrl,timestamp)
                binding.progressBar.visibility = View.INVISIBLE

                strRef = FirebaseStorage.getInstance().getReference(filePathAndName + "a")
                strRef.putFile(imageUri2!!)
                    .addOnSuccessListener { it2 ->
                        val uriTask2 : Task<Uri> = it2.storage.downloadUrl
                        while(!uriTask2.isSuccessful);
                        val uploadUrl2 = "${uriTask2.result}"
                        updateImIntoDb(uploadUrl2, timestamp)
                    }

                strRef = FirebaseStorage.getInstance().getReference(filePathAndName + "b")
                strRef.putFile(imageUri3!!)
                    .addOnSuccessListener {it3 ->
                        val uriTask3 : Task<Uri> = it3.storage.downloadUrl
                        while(!uriTask3.isSuccessful);
                        val uploadUrl3 = "${uriTask3.result}"
                        updateImIntoDb3(uploadUrl3, timestamp)
                    }

            }


    }

    private fun updateImIntoDb3(uploadUrl: String, timestamp: Long) {
        val hashMap = HashMap<String, Any>()
        hashMap["url3"] = uploadUrl
        val ref = FirebaseDatabase.getInstance().getReference("Projects")
        ref.child(timestamp.toString())
            .updateChildren(hashMap)
            .addOnSuccessListener {
                imageUri3 = null
            }
    }

    private fun updateImIntoDb(uploadUrl: String, timestamp: Long) {
        val hashMap = HashMap<String, Any>()
        hashMap["url2"] = uploadUrl
        val ref = FirebaseDatabase.getInstance().getReference("Projects")
        ref.child(timestamp.toString())
            .updateChildren(hashMap)
            .addOnSuccessListener {
                imageUri2 = null
            }
    }

    private fun uploadImIntoDb(uploadUrl: String, timestamp: Long) {
        val uid  = mAuth.uid
        val hashMap : HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"]  = "$timestamp"
        hashMap["title"] = title
        hashMap["description"] = description
        hashMap["categoryId"] = category
        hashMap["businessId"] = business
        hashMap["url"] = uploadUrl
        hashMap["url2"] = ""
        hashMap["url3"] = ""
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0
        hashMap["price"] = price

        val ref = FirebaseDatabase.getInstance().getReference("Projects")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this@AddProjectActivity, "Thêm thành công", Toast.LENGTH_SHORT).show()
                imageUri = null
            }
            .addOnFailureListener {
                Toast.makeText(this@AddProjectActivity, "Thất bại do ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadCategory() {
        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(Category::class.java)
                    categoryArrayList.add(model!!)
                }
                val categories = arrayOfNulls<String>(categoryArrayList.size)
                for (i in 0 until categoryArrayList.size){
                    categories[i] = categoryArrayList[i].category
                }
                val adapter = ArrayAdapter(this@AddProjectActivity, R.layout.list_item,categories)
                binding.categoryEdt.setAdapter(adapter)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        if (it){
            Log.d("AAA","onCreate: Storage permission is already granted")
        }
        else {
            Log.d("AAA","onCreate: Storage permission is denied")
        }
    }

    private fun getImage(){
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.bottom_sheet_dialog,
            binding.root,
            false
        )
        bottomSheetView.findViewById<ImageView>(R.id.camera).setOnClickListener {
            pickImageCamera()
            bottomSheetDialog.dismiss()
        }
        bottomSheetView.findViewById<ImageView>(R.id.gallery).setOnClickListener {
            pickImageGallery()
            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun getImage2(){
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.bottom_sheet_dialog,
            binding.root,
            false
        )
        bottomSheetView.findViewById<ImageView>(R.id.camera).setOnClickListener {
            pickImageCamera2()
            bottomSheetDialog.dismiss()
        }
        bottomSheetView.findViewById<ImageView>(R.id.gallery).setOnClickListener {
            pickImageGallery2()
            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun getImage3(){
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.bottom_sheet_dialog,
            binding.root,
            false
        )
        bottomSheetView.findViewById<ImageView>(R.id.camera).setOnClickListener {
            pickImageCamera3()
            bottomSheetDialog.dismiss()
        }
        bottomSheetView.findViewById<ImageView>(R.id.gallery).setOnClickListener {
            pickImageGallery3()
            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture(),
        ActivityResultCallback {
            if (it) {
                binding.image1.setImageURI(imageUri)
                binding.image2.visibility = View.VISIBLE
            }
        }
    )

    private val getPicture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if (it.resultCode == RESULT_OK){
                imageUri = it.data!!.data
                binding.image1.setImageURI(imageUri)
                binding.image2.visibility = View.VISIBLE
            }
            else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getPicture.launch(intent)
    }

    private fun pickImageCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Description")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        takePicture.launch(imageUri)
    }

    private val takePicture2 = registerForActivityResult(
        ActivityResultContracts.TakePicture(),
        ActivityResultCallback {
            if (it) {
                binding.image2.setImageURI(imageUri2)
                binding.image3.visibility = View.VISIBLE
            }
        }
    )

    private val getPicture2 = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if (it.resultCode == RESULT_OK){
                imageUri2 = it.data!!.data
                binding.image2.setImageURI(imageUri2)
                binding.image3.visibility = View.VISIBLE
            }
            else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun pickImageGallery2() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getPicture2.launch(intent)
    }

    private fun pickImageCamera2() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Description")
        imageUri2 = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        takePicture2.launch(imageUri2)
    }


    private val takePicture3 = registerForActivityResult(
        ActivityResultContracts.TakePicture(),
        ActivityResultCallback {
            if (it) {
                binding.image3.setImageURI(imageUri3)
            }
        }
    )

    private val getPicture3 = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if (it.resultCode == RESULT_OK){
                imageUri3 = it.data!!.data
                binding.image3.setImageURI(imageUri3)
            }
            else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun pickImageGallery3() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getPicture3.launch(intent)
    }

    private fun pickImageCamera3() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Description")
        imageUri3 = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        takePicture3.launch(imageUri3)
    }

}