package me.yeojoy.twitteronmap

import android.app.Application
import twitter4j.TwitterFactory

class TwitterOnMapApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val twitter = TwitterFactory.getSingleton()
        twitter.setOAuthConsumer(getString(R.string.twitter_consumer_key),
            getString((R.string.twitterPconsumer_secret)))

//        val base64Encoder = Base64.getEncoder()
    }
}