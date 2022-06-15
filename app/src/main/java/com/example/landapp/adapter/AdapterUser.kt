package com.example.landapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.landapp.R
import com.example.landapp.model.User
import com.example.landapp.ui.activity.ProfileActivity
import com.example.landapp.ui.activity.ProfileUserActivity
import com.google.firebase.database.FirebaseDatabase

class AdapterUser(
    private var context: Context,
    private val userList: ArrayList<User>
) : RecyclerView.Adapter<AdapterUser.HolderViewUser>(){

    inner class HolderViewUser(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.nameManage)
        val email : TextView = itemView.findViewById(R.id.emailManage)
        val deleteBtn : ImageView = itemView.findViewById(R.id.deleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderViewUser {
        return HolderViewUser(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_rv_user,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HolderViewUser, position: Int) {
        val model = userList[position]
        val uid = model.uid
        val name = model.name
        val email = model.email

        holder.name.text = name
        holder.email.text = email
        holder.deleteBtn.setOnClickListener {
            deleteUser(model, holder)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context,ProfileUserActivity::class.java)
            intent.putExtra("uid",uid)
            context.startActivity(intent)
        }
    }

    private fun deleteUser(model: User, holder: HolderViewUser) {
        val uid = model.uid
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context , "Đã xoá người dùng", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int {
        return userList.size
    }


}