package me.yeojoy.twitteronmap.network.model

import com.google.gson.annotations.SerializedName

data class Tweets(
    @SerializedName("statuses") val tweets: MutableList<Tweet>,
    @SerializedName("search_metadata") val searchMetadata: TwitterSearchMetadata
)
