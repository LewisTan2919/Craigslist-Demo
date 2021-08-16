package com.example.cis_600_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SignUpActivity : AppCompatActivity(), SignUpFirstPage.OnFragmentInteractionListener, SignUpSecondPage.OnFragmentInteractionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportFragmentManager.beginTransaction().add(R.id.signup_frag, SignUpFirstPage()).commit()
    }

    override fun toSignUpSecond(view: View){
        supportFragmentManager.beginTransaction().replace(R.id.signup_frag, SignUpSecondPage()).addToBackStack(null).commit()
    }

    override fun toMainPage(view: View){
        val intent = Intent(this, MainPageActivity::class.java)
        intent.putExtra("action", 0)
        startActivity(intent)
    }
}
