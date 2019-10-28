package com.webappclouds.socialmediaapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_ticket.view.*
import kotlinx.android.synthetic.main.tweets_ticket.view.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var listTweets = ArrayList<Ticket>()
    var adapter: MyTweetAdapter? = null
    var myEmail: String? = null
    var userUID: String? = null

    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bundle: Bundle = intent.extras!!
        myEmail = bundle.getString("email")
        userUID = bundle.getString("uid")

        //Dummy Data

        listTweets.add(Ticket("0", "him", "url", "add"))
        listTweets.add(Ticket("0", "him", "url", "bill"))

        var adapter = MyTweetAdapter(this, listTweets)
        lvTweets.adapter = adapter

        loadPost()
    }

    inner class MyTweetAdapter : BaseAdapter {
        var listNotesAdpater = ArrayList<Ticket>()
        var context: Context? = null

        constructor(context: Context, listNotesAdpater: ArrayList<Ticket>) : super() {
            this.listNotesAdpater = listNotesAdpater
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var myTweet = listNotesAdpater[p0]
            if (myTweet.tweetPersonUID.equals("add")) {
                var myView = layoutInflater.inflate(R.layout.add_ticket, null)
                myView.iv_attach.setOnClickListener(View.OnClickListener {
                    loadImage()
                })

                myView.iv_post.setOnClickListener(View.OnClickListener {
                    //upload server
                    myRef.child("posts").push().setValue(
                        PostInfo(
                            userUID!!,
                            myView.etPost.text.toString(), downloadURL!!
                        )
                    )
                    myView.etPost.setText("")
                })
                return myView

            } else {
                var myView = layoutInflater.inflate(R.layout.tweets_ticket, null)
                myView.txt_tweet.text = myTweet.tweetText
                Picasso.get().load(myTweet.tweetImageURL).into(myView.tweet_picture)
                myRef.child("Users").child(myTweet.tweetPersonUID!!)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            try {
                                var td = dataSnapshot!!.value as HashMap<String, Any>
                                for (key in td.keys) {
                                    var userInfo = td[key] as String
                                    if (key.equals("ProfileImage")) {
                                        Picasso.get().load(userInfo)
                                            .into(myView.picture_path)
                                    } else {
                                        myView.txtUserName.text = userInfo
                                    }
                                }
                            } catch (ex: Exception) {
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE && data != null && resultCode == RESULT_OK) {

            val selectedImage = data.data
            val filePathColum = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColum, null, null, null)
            cursor!!.moveToFirst()
            val coulomIndex = cursor!!.getColumnIndex(filePathColum[0])
            val picturePath = cursor!!.getString(coulomIndex)
            cursor!!.close()
            UploadImage(BitmapFactory.decodeFile(picturePath))
        }

    }

    var downloadURL: String? = ""
    fun UploadImage(bitmap: Bitmap) {
        listTweets.add(0, Ticket("0", "him", "url", "loading"))
//        adapter!!.notifyDataSetChanged()

        val storage = FirebaseStorage.getInstance()
        val storgaRef = storage.getReferenceFromUrl("gs://socialmediaapp-264ae.appspot.com")
        val df = SimpleDateFormat("ddMMyyHHmmss")
        val dataobj = Date()
        val imagePath = SplitString(myEmail!!) + "." + df.format(dataobj) + ".jpg"
        val ImageRef = storgaRef.child("imagePost/" + imagePath)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = ImageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(applicationContext, "Failed to upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapshot ->
            downloadURL = taskSnapshot.storage.downloadUrl.toString()!!
            listTweets.removeAt(0)
            adapter!!.notifyDataSetChanged()
        }
    }

    fun SplitString(email: String): String {
        val split = email.split("@")
        return split[0]
    }

    fun loadPost() {
        myRef.child("posts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        listTweets.clear()
                        listTweets.add(Ticket("0", "him", "url", "add"))
                        listTweets.add(Ticket("0", "him", "url", "ads"))
                        var td = dataSnapshot!!.value as HashMap<String, Any>
                        for (key in td.keys) {
                            var post = td[key] as HashMap<String, Any>
                            listTweets.add(
                                Ticket(
                                    key,
                                    post["text"] as String,
                                    post["postImage"] as String
                                    , post["userUID"] as String
                                )
                            )
                        }
                        adapter!!.notifyDataSetChanged()
                    } catch (ex: Exception) {
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }
}
