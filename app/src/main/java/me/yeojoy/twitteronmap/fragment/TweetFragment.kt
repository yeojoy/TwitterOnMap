package me.yeojoy.twitteronmap.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import me.yeojoy.twitteronmap.network.model.Tweet

class TweetFragment : Fragment() {

    companion object {
        private val TAG = TweetFragment::class.java.simpleName
        private val KEY_TWEET = "tweet_"

        fun newInstance(tweet: Tweet) : TweetFragment {
            // If it needs some arguments, set up here!
            val fragment = TweetFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_TWEET, tweet)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // TODO make layout for this fragment
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tweet = arguments!!.getParcelable<Tweet>(KEY_TWEET)
        tweet.let {
            Log.d(TAG, Gson().toJson(tweet))


        }
    }


}