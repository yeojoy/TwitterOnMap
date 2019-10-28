package me.yeojoy.twitteronmap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.text.TextUtilsCompat
import kotlinx.android.synthetic.main.activity_twitter.*
import me.yeojoy.twitteronmap.app.BaseActivity
import me.yeojoy.twitteronmap.app.Constants
import me.yeojoy.twitteronmap.controller.TwitterLoginController
import me.yeojoy.twitteronmap.network.model.TwitterAccessToken

class TwitterActivity : BaseActivity(), TwitterLoginController.TwitterLoginView {
    private val TAG = "TwitterActivity"

    private lateinit var twitterLoginController: TwitterLoginController
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)

        twitterLoginController = TwitterLoginController()

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        buttonTwitterLogin.setOnClickListener {
            twitterLoginController.loginTwitter(this)
        }

        val authorizationString = sharedPreferences.getString(Constants.SHARED_KEY_TWITTER_AUTH, "")

        // If user already loged in, go directly.
        if (!TextUtils.isEmpty(authorizationString)) {
            moveMapActivity()
        }
    }

    override fun onSuccessTwitterLogin(accessToken: TwitterAccessToken) {
        Log.i(TAG, "onSuccessTwitterLogin()")

        val editor = sharedPreferences.edit()
        val authorizationString = "${accessToken.tokenType} ${accessToken.accessToken}"
        textViewStatus.append("authorizationString : $authorizationString")

        editor.putString(Constants.SHARED_KEY_TWITTER_AUTH, authorizationString)
        editor.apply()

        Toast.makeText(this, R.string.toast_success_to_login_at_twitter, Toast.LENGTH_SHORT).show()

        moveMapActivity()
    }

    override fun onFailTwitterLogin(statusCode: Int, message: String) {
        Log.i(TAG, "onFailTwitterLogin(), statusCode : $statusCode, message : $message")
        Toast.makeText(this, R.string.toast_fail_to_login_at_twitter, Toast.LENGTH_SHORT).show()
    }

    private fun moveMapActivity() {
        val intent = Intent(this@TwitterActivity, MapsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
