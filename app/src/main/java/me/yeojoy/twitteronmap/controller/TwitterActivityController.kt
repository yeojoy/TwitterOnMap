package me.yeojoy.twitteronmap.controller

import android.content.Context
import android.util.Base64
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import me.yeojoy.twitteronmap.R
import me.yeojoy.twitteronmap.network.ApiManager

class TwitterActivityController(view: TwitterActivityView) {

    private val TAG = "TwitterAcController"
    private var twitterActivityView: TwitterActivityView

    init {
        this.twitterActivityView = view
    }

    fun loginTwitter() {
        val activity = twitterActivityView.getContext()
        val sourceText = "${activity.getString(R.string.twitter_consumer_key)}:${activity.getString(R.string.twitterPconsumer_secret)}"

        val basicKey = Base64.encodeToString(sourceText.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        Log.d(TAG, "basicKey : $basicKey")

        // TODO https://developer.android.com/training/volley/request-custom
        val url = "https://api.twitter.com"
        var apiManager = ApiManager.getInstance(twitterActivityView.getContext())
        val loginRequest = StringRequest(Request.Method.POST, url, Response.Listener<String> {
            response ->
            val responseString = response
            Log.d(TAG, "responseString >>> $responseString")
            twitterActivityView.onSuccessTwitterLogin(responseString)

        }, Response.ErrorListener {
            Log.e(TAG, "error occurs. status code >>> ${it.networkResponse.statusCode}")
            twitterActivityView.onFailTwitterLogin()
        })

        apiManager.addToRequestQueue(loginRequest)
    }

    fun requestTweets() {

    }

    interface TwitterActivityView {
        fun onSuccessTwitterLogin(response: String)
        fun onFailTwitterLogin()

        fun getContext() : Context
    }
}