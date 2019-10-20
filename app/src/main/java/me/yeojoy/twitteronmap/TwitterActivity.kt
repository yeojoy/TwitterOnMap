package me.yeojoy.twitteronmap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_twitter.*
import me.yeojoy.twitteronmap.controller.TwitterActivityController

class TwitterActivity : AppCompatActivity(), TwitterActivityController.TwitterActivityView {
    private val TAG = "TwitterActivity"

    private lateinit var controller: TwitterActivityController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)

        controller = TwitterActivityController(this)

        buttonTwitterLogin.setOnClickListener {
            controller.loginTwitter()
        }

        buttonDisplayGoogleMap.setOnClickListener {
            val intent = Intent(this@TwitterActivity, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSuccessTwitterLogin(response: String) {
        Log.i(TAG, "onSuccessTwitterLogin()")
    }

    override fun onFailTwitterLogin() {
        Log.i(TAG, "onFailTwitterLogin()")
    }

    override fun getContext(): Context {
        return this
    }
}
