package me.yeojoy.twitteronmap.controller

import android.content.Context
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import me.yeojoy.twitteronmap.Constants
import me.yeojoy.twitteronmap.R
import me.yeojoy.twitteronmap.network.ApiManager
import me.yeojoy.twitteronmap.network.model.Tweets
import me.yeojoy.twitteronmap.network.model.TwitterAccessToken

class TwitterActivityController() {

    private val TAG = "TwitterAcController"

    fun loginTwitter(twitterLoginView: TwitterLoginView) {
        val activity = twitterLoginView.getContext()
        val sourceText =
                "${activity.getString(R.string.twitter_consumer_key)}:${activity.getString(R.string.twitterPconsumer_secret)}"

        val basicValue = "Basic ${Base64.encodeToString(sourceText.toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP)}"
        Log.d(TAG, "basicKey : $basicValue")

        val url = "https://api.twitter.com/oauth2/token"
        var apiManager = ApiManager.getInstance(twitterLoginView.getContext())

        val loginRequest =
                object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                    Log.d(TAG, "responseString >>> $response")
                    val gson = Gson()
                    val twitterAccessToken = gson.fromJson(response, TwitterAccessToken::class.java)

                    twitterLoginView.onSuccessTwitterLogin(twitterAccessToken)

                }, Response.ErrorListener {
                    val statusCode = it.networkResponse.statusCode
                    val message = String(it.networkResponse.data, Charsets.UTF_8)
                    Log.e(TAG, "error occurs. status code >>> $statusCode\n message >>> $message")
                    twitterLoginView.onFailTwitterLogin(statusCode, message)
                }) {
                    override fun getBodyContentType(): String {
                        return "application/x-www-form-urlencoded;chaset=UTF-8"
                    }

                    override fun getParams(): MutableMap<String, String> {
                        var body = mutableMapOf<String, String>()
                        body.put("grant_type", "client_credentials")
                        return body
                    }

                    override fun getHeaders(): MutableMap<String, String> {
                        var headers = mutableMapOf<String, String>()
                        headers.put("Authorization", "$basicValue")
                        headers.put("Content-Type",
                                "application/x-www-form-urlencoded;chaset=UTF-8")
                        return headers
                    }
                }

        apiManager.addToRequestQueue(loginRequest)
    }

    fun requestTweets(twitterRequestTweetsView: TwitterRequestTweetsView,
                      query: String, latLng: LatLng, radius: Int) {
        val sharedPreferences = twitterRequestTweetsView.getContext()
                .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val tokenType = sharedPreferences.getString(Constants.SHARED_KEY_TOKEN_TYPE, "")
        val accessToken = sharedPreferences.getString(Constants.SHARED_KEY_ACCESS_TOKEN, "")

        if (TextUtils.isEmpty(tokenType) || TextUtils.isEmpty(accessToken)) {

            return
        }

        val authorizationString = "$tokenType $accessToken"

        val url = "https://api.twitter.com/1.1/search/tweets.json?q=$query&count=100&locale=ca&geocode=${latLng.latitude},${latLng.longitude},${radius}km"
        var apiManager = ApiManager.getInstance(twitterRequestTweetsView.getContext())

        val tweetsRequest =
                object : StringRequest(Method.GET, url, Response.Listener<String> { response ->
                    Log.d(TAG, "responseString >>> $response")
                    val gson = Gson()
                    val tweets = gson.fromJson(response, Tweets::class.java)

                    twitterRequestTweetsView.onGetTweets(tweets)

                }, Response.ErrorListener {
                    val statusCode = it.networkResponse.statusCode
                    val message = String(it.networkResponse.data, Charsets.UTF_8)
                    Log.e(TAG, "error occurs. status code >>> $statusCode\n message >>> $message")
                }) {

                    override fun getHeaders(): MutableMap<String, String> {
                        var headers = mutableMapOf<String, String>()
                        headers.put("Authorization", authorizationString)
                        return headers
                    }
                }

        Log.d(TAG, "headers >>> ${tweetsRequest.headers}")
        Log.d(TAG, "url >>> ${tweetsRequest.url}")
        apiManager.addToRequestQueue(tweetsRequest)
    }

    interface TwitterLoginView : AppView {
        fun onSuccessTwitterLogin(twitterAccessToken: TwitterAccessToken)
        fun onFailTwitterLogin(statusCode: Int, message: String)
    }

    interface TwitterRequestTweetsView : AppView {
        fun onGetTweets(tweets: Tweets)
    }
}