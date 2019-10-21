package me.yeojoy.twitteronmap

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_twitter.*
import me.yeojoy.twitteronmap.controller.TwitterActivityController
import me.yeojoy.twitteronmap.network.model.Tweets
import me.yeojoy.twitteronmap.network.model.TwitterAccessToken

class TwitterActivity : AppCompatActivity(), TwitterActivityController.TwitterLoginView,
    TwitterActivityController.TwitterRequestTweetsView {
    private val TAG = "TwitterActivity"

    private lateinit var controller: TwitterActivityController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)

        controller = TwitterActivityController()

        buttonTwitterLogin.setOnClickListener {
            controller.loginTwitter(this)
        }

        buttonDisplayGoogleMap.setOnClickListener {
//            val intent = Intent(this@TwitterActivity, MapsActivity::class.java)
//            startActivity(intent)
//            finish()
            controller.requestTweets(this)
        }
    }

    override fun onSuccessTwitterLogin(accessToken: TwitterAccessToken) {
        Log.i(TAG, "onSuccessTwitterLogin()")
        textViewStatus.append("tokenType : ${accessToken.tokenType}, accessToken : ${accessToken.accessToken}\n")
        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.SHARED_KEY_TOKEN_TYPE, accessToken.tokenType)
        editor.putString(Constants.SHARED_KEY_ACCESS_TOKEN, accessToken.accessToken)
        editor.apply()
    }

    override fun onFailTwitterLogin(statusCode: Int, message: String) {
        Log.i(TAG, "onFailTwitterLogin(), statusCode : $statusCode, message : $message")
    }

    override fun getContext(): Context {
        return this
    }

    override fun onGetTweets(tweets: Tweets) {
        Log.i(TAG, "onGetTweets()")

    }
}
