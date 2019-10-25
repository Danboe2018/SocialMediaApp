package com.webappclouds.socialmediaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ivImagePerson.setOnClickListener( View.OnClickListener {

        })
    }

    fun butLogin(view: View) {
        var ID = view.id
        Log.d("TAG",ID.toString())

    }
}
