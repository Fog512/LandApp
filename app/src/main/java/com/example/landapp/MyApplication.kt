package com.example.landapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        fun formatTimeStamp(timestamp : Long) : String {
            val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm")
            return sdf.format(timestamp)
        }

        fun deleteProject(context: Context, projectId : String, projectUrl : String, projectTitle : String){
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(projectUrl)
            storageRef.delete()
                .addOnSuccessListener {
                    val ref = FirebaseDatabase.getInstance().getReference("Projects")
                    ref.child(projectId)
                        .removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to delete due to ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to delete due to ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        fun incrementBookView(projectId: String){
            val ref = FirebaseDatabase.getInstance().getReference("Projects")
            ref.child(projectId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var viewCount = "" + snapshot.child("viewsCount").value
                        if (viewCount == "" || viewCount == "null"){
                            viewCount = "0"
                        }
                        val newsViewCount = viewCount.toLong() +  1
                        val hashMap = HashMap<String, Any>()
                        hashMap["viewsCount"] = newsViewCount
                        val dbRef = FirebaseDatabase.getInstance().getReference("Projects")
                        dbRef.child(projectId)
                            .updateChildren(hashMap)
                    }
                    override fun onCancelled(error: DatabaseError) {
                    } })
        }
    }
}