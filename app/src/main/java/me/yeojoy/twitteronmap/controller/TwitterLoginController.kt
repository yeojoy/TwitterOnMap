package me.yeojoy.twitteronmap.controller

import android.util.Base64
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import me.yeojoy.twitteronmap.R
import me.yeojoy.twitteronmap.network.ApiManager
import me.yeojoy.twitteronmap.network.model.TwitterAccessToken

class TwitterLoginController {

    private val TAG = "TwitterLoginController"

    fun loginTwitter(twitterLoginView: TwitterLoginView) {
        val context = twitterLoginView.getContext()
        val sourceText =
            "${context.getString(R.string.twitter_consumer_key)}:${context.getString(R.string.twitterPconsumer_secret)}"

        val basicValue = "Basic ${Base64.encodeToString(
            sourceText.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )}"

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
                it.networkResponse?.let {
                    val statusCode = it.statusCode
                    val message = String(it.data, Charsets.UTF_8)
                    Log.e(TAG, "error occurs. status code >>> $statusCode\n message >>> $message")
                    twitterLoginView.onFailTwitterLogin(statusCode, message)
                }
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
                    headers.put(
                        "Content-Type",
                        "application/x-www-form-urlencoded;chaset=UTF-8"
                    )
                    return headers
                }
            }

        apiManager.addToRequestQueue(loginRequest)
    }

    /**
     * This interface is to access an Activity to display login status.
     */
    interface TwitterLoginView : AppView {
        fun onSuccessTwitterLogin(twitterAccessToken: TwitterAccessToken)
        fun onFailTwitterLogin(statusCode: Int, message: String)
    }
}