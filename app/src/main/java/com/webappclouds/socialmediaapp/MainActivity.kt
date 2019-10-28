package com.webappclouds.socialmediaapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_ticket.view.*

class MainActivity : AppCompatActivity() {

    var listTweets = ArrayList<Ticket>()
    var adapter:MyTweetAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Dummy Data

        listTweets.add(Ticket("0", "him", "url", "add"))
        listTweets.add(Ticket("1", "him", "url", "bill"))

        var adapter = MyTweetAdapter(this,listTweets)
        lvTweets.adapter=adapter
    }

    inner class MyTweetAdapter : BaseAdapter {
        var listNotesAdpater = ArrayList<Ticket>()
        var context: Context? = null

        constructor(context: Context, listNotesAdpater: ArrayList<Ticket>) : super() {
            this.listNotesAdpater = listNotesAdpater
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var mytweet = listNotesAdpater[p0]
            if (mytweet.tweetPersonUID.equals("add")) {
                var myView = layoutInflater.inflate(R.layout.add_ticket, null)

                return myView

            } else {
                var myView = layoutInflater.inflate(R.layout.tweets_ticket, null)

                return myView
            }
        }

        override fun getItem(p0: Int): Any {
            return listNotesAdpater[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdpater.size
        }
    }

    val PICK_IMAGE_CODE = 123
    fun loadImage() {

        var intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }
}
