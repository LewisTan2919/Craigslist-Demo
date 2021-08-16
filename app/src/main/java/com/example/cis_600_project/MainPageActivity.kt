package com.example.cis_600_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SearchListFrag.OnRecyclerInteractionListener, ItemDetail.OnBackListner{

    private var position = -1
    private var product:item? = null

    var fr = SearchListFrag()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        //Main Page Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)



        //Navigation Bar
        val toggle = ActionBarDrawerToggle(this, mainAct, toolbar, R.string.open_nav, R.string.close_nav)
        mainAct.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        isLoggedIn()
        val headerView = navView.getHeaderView(0)
        val profileUid = headerView.findViewById<TextView>(R.id.profUid)
        val profileEmail = headerView.findViewById<TextView>(R.id.profEmail)
        val profileImage = headerView.findViewById<CircularImageView>(R.id.profImg)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val profileRef = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        profileRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot != null){
                    profileEmail.text = dataSnapshot.child("useremail").value.toString()
                    profileUid.text = dataSnapshot.child("username").value.toString()
                    Picasso.get().load(dataSnapshot.child("profileImageUrl").value.toString()).fit().into(profileImage)
                }
            }
        })




        supportFragmentManager.beginTransaction().add(R.id.mainpage_contain, SearchListFrag()).commit()
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean { // handler navigation menu item selection!
        when(item.itemId){
            R.id.favor_list -> {
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
            }
            R.id.selling_list -> {
                val intent = Intent(this, SellingActivity::class.java)
                startActivity(intent)
            }
            R.id.sold_list -> {
                val intent = Intent(this, SoldActivity::class.java)
                startActivity(intent)
            }
            R.id.sign_out->{
                signOut()
            }
        }
        mainAct.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onItemClicked(product: item) {
        this.product = product
        supportFragmentManager.beginTransaction().replace(R.id.mainpage_contain,
            ItemDetail.newInstance(product!!)).addToBackStack(null).commit()
    }

    override fun onBackPressed() {
        if(mainAct.isDrawerOpen(GravityCompat.START)){
            mainAct.closeDrawer(GravityCompat.START)
        }
        else
            super.onBackPressed()
    }

    override fun onBackClick(v: View) {
        //val fr = ListFragment()
        supportFragmentManager.beginTransaction().replace(R.id.mainpage_contain, fr!!).commit()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
// launch the Login activity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    private fun isLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid // check current uid of authentication!
        if(uid == null){
// launch the Login activity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
