package com.example.landapp.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.landapp.model.Category
import com.example.landapp.databinding.ActivityDashboardUserBinding
import com.example.landapp.ui.fragment.ProjectListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var categoryArrayList: ArrayList<Category>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        checkUser()

        setupWithViewPagerAdapter(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.logoutBtn.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

    }

    private fun setupWithViewPagerAdapter(viewPager: ViewPager){
        viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
            this
        )
        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()

                val modelAll = Category("1","Tất cả",1,"")
                val modelMostViewed = Category("1","Xem nhiều nhất",1,"")

                categoryArrayList.add(modelAll)
                categoryArrayList.add(modelMostViewed)

                viewPagerAdapter.addFragment(
                    ProjectListFragment.newInstance(
                        modelAll.category,
                        modelAll.uid
                    ), modelAll.category
                )

                viewPagerAdapter.addFragment(
                    ProjectListFragment.newInstance(
                        modelMostViewed.category,
                        modelMostViewed.uid
                    ), modelMostViewed.category
                )

                viewPagerAdapter.notifyDataSetChanged()
                for (ds in snapshot.children){
                    val model = ds.getValue(Category::class.java)
                    categoryArrayList.add(model!!)

                    viewPagerAdapter.addFragment(
                        ProjectListFragment.newInstance(
                            model.category,
                            model.uid
                        ),model.category
                    )
                    viewPagerAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        viewPager.adapter = viewPagerAdapter
    }


    class ViewPagerAdapter(
        fm : FragmentManager, behavior : Int, context: Context
    ) : FragmentPagerAdapter(fm, behavior) {
        private val fragmentList : ArrayList<ProjectListFragment> = ArrayList()
        private val fragmentTitleList : ArrayList<String> = ArrayList()
        private val context : Context
        init {
            this.context = context
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList[position]
        }

        fun addFragment(fragment: ProjectListFragment, title : String){
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }
    }

    private fun checkUser() {
        val user = mAuth.currentUser
        if (user == null) {
            binding.subTitleTv.text = "Not logged In"
            binding.btnProfile.visibility = View.GONE
            binding.logoutBtn.visibility = View.GONE
        } else {
            val email = user.email
            binding.subTitleTv.text = email
            binding.btnProfile.visibility = View.VISIBLE
            binding.logoutBtn.visibility = View.VISIBLE
        }
    }

}