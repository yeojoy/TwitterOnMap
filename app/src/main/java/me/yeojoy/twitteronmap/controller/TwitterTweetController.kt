package me.yeojoy.twitteronmap.controller

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import me.yeojoy.twitteronmap.app.Constants
import me.yeojoy.twitteronmap.network.ApiManager
import me.yeojoy.twitteronmap.network.model.Tweets

class TwitterTweetController {

    private val TAG = "TwitterTweetController"

    fun requestTweets(
        twitterRequestTweetsView: TwitterRequestTweetsView,
        query: String, latLng: LatLng, radius: Int
    ) {
        Log.i(TAG, "requestTweets()")
        val sharedPreferences = twitterRequestTweetsView.getContext()
            .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val authorizationString = sharedPreferences.getString(Constants.SHARED_KEY_TWITTER_AUTH, "")

        if (TextUtils.isEmpty(authorizationString)) {
            Log.e(TAG, "There is no authrizationString.")
            return
        }

        val url =
            "https://api.twitter.com/1.1/search/tweets.json?q=$query&count=100&geocode=${latLng.latitude},${latLng.longitude},${radius}km"

        val apiManager = ApiManager.getInstance(twitterRequestTweetsView.getContext())

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
                    headers.put("Authorization", authorizationString!!)
                    return headers
                }
            }

        Log.d(TAG, "headers >>> ${tweetsRequest.headers}")
        Log.d(TAG, "url >>> ${tweetsRequest.url}")
        apiManager.addToRequestQueue(tweetsRequest)
    }

    /**
     * This interface is to access an Activity to display tweets.
     */
    interface TwitterRequestTweetsView : AppView {
        fun onGetTweets(tweets: Tweets)
    }
}