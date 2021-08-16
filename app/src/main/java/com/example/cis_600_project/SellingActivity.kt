package com.example.cis_600_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_selling.*

class SellingActivity : AppCompatActivity(), SellingListFrag.OnRecyclerInteractionListener,ItemDetail.OnBackListner {

    private var position = -1
    private var product:item? = null
    val userID = FirebaseAuth.getInstance().uid ?: ""
    var fr = SellingListFrag()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selling)

        setSupportActionBar(toolbar_selling)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.title="Selling List"
        upload_btn.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)

        }
        supportFragmentManager.beginTransaction().add(R.id.selling_contain, SellingListFrag.newInstance(userID)).commit()
    }
    override fun onItemClicked(product: item) {
        this.product = product
        supportFragmentManager.beginTransaction().replace(R.id.selling_contain,
            ItemDetail.newInstance(product!!)).addToBackStack(null).commit()
    }

    override fun onBackClick(v: View) {
        //val fr = ListFragment()
        supportFragmentManager.beginTransaction().replace(R.id.selling_contain, fr!!).commit()
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
    }


}
