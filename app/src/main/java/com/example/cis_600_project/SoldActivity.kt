package com.example.cis_600_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sold.*

class SoldActivity : AppCompatActivity(), SoldListFrag.OnRecyclerInteractionListener, ItemDetail.OnBackListner {

    private var position = -1
    private var product:item? = null
    val userID = FirebaseAuth.getInstance().uid ?: ""
    var fr = SoldListFrag()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold)

        setSupportActionBar(toolbar_sold)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.title="Sold List"

        supportFragmentManager.beginTransaction().add(R.id.sold_contain, SoldListFrag.newInstance(userID)).commit()
    }
    override fun onItemClicked(product: item) {
        this.product = product
        supportFragmentManager.beginTransaction().replace(R.id.sold_contain,
            ItemDetail.newInstance(product!!)).addToBackStack(null).commit()
    }

    override fun onBackClick(v: View) {
        //val fr = ListFragment()
        supportFragmentManager.beginTransaction().replace(R.id.sold_contain, fr!!).commit()
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
    }
}
