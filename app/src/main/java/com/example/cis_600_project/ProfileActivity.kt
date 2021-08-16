package com.example.cis_600_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(),View.OnClickListener {


    private lateinit var vp: androidx.viewpager.widget.ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(myToolbar5)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var userid=intent.getStringExtra("userID")
        val sellerRef = FirebaseDatabase.getInstance().reference.child("users").child(userid)
        sellerRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot != null){
                    Picasso.get().load(dataSnapshot.child("profileImageUrl").value.toString()).fit().into(other_photo)
                    other_name.text=dataSnapshot.child("username").value.toString()
                    other_email.text=dataSnapshot.child("useremail").value.toString()
                }
            }
        })


        vp = findViewById(R.id.viewP2)

        vp.adapter = SimplePagerAdapter(supportFragmentManager,userid!!)
        vp.currentItem = 0
        txtFirst.setOnClickListener(this)
        txtFirst.tag = 0
        txtFirst.isSelected = true
        txtSecond.setOnClickListener(this)
        txtSecond.tag = 1
        txtThird.setOnClickListener(this)
        txtThird.tag = 2
    }

    override fun onClick(v: View?) {
        val tag: Int = v?.tag.toString().toInt()
        var i = 0

        while (i < 3){
            txtPanel.findViewWithTag<TextView>(i).isSelected = tag == i
            i++
        }
        vp.currentItem = tag
    }


    class SimplePagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager,userid:String) :
        androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {
        val userid=userid
        override fun getItem(p0: Int): androidx.fragment.app.Fragment {
            when (p0){
                0 -> {
                    return FavoriteListFrag.newInstance(userid!!)
                }
                1 -> {
                    return SellingListFrag.newInstance(userid!!)
                }
                2 -> {
                    return SoldListFrag.newInstance(userid!!)
                }
            }
            return FavoriteListFrag.newInstance(userid!!)
        }

        override fun getCount(): Int {
            return 3
        }

    }

}
