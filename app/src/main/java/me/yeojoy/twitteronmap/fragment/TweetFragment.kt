package me.yeojoy.twitteronmap.fragment

import androidx.fragment.app.Fragment

class TweetFragment : Fragment() {

    companion object {
        private val TAG = TweetFragment::class.java.simpleName

        fun newInstance() : TweetFragment {
            // If it needs some arguments, set up here!
            val fragment = TweetFragment()
            return fragment
        }
    }


}