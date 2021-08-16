package com.example.cis_600_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_favorite.*

class FavoriteActivity : AppCompatActivity(),
    FavoriteListFrag.OnRecyclerInteractionListener,
    ItemDetail.OnBackListner {

    private var position = -1
    private var product: item? = null
    var fr = FavoriteListFrag()
    val userID = FirebaseAuth.getInstance().uid ?: ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        setSupportActionBar(toolbar_favorite)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        supportFragmentManager.beginTransaction().add(
            R.id.favor_contain,
            FavoriteListFrag.newInstance(
                userID
            )
        ).commit()
    }
    override fun onItemClicked(product: item) {
        this.product = product
        supportFragmentManager.beginTransaction().replace(
            R.id.favor_contain,
            ItemDetail.newInstance(product!!)
        ).addToBackStack(null).commit()
    }

    override fun onBackClick(v: View) {
        //val fr = ListFragment()
        supportFragmentManager.beginTransaction().replace(R.id.favor_contain, fr!!).commit()
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
    }
}

