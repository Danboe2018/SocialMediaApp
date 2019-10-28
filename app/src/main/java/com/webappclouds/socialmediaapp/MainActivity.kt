package com.webappclouds.socialmediaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    var listTweets = ArrayList<Ticket>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Dummy Data

        listTweets.add(Ticket("0","him","url","add"))
        listTweets.add(Ticket("1","him","url","bill"))


    }
}
