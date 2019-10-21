package me.yeojoy.twitteronmap.network.model

import com.google.gson.annotations.SerializedName

data class Tweet(
    @SerializedName("created_at") val createAt: String,
    @SerializedName("user") val user: TwitterUser,
    @SerializedName("id") val id: Long,
    @SerializedName("text") val text: String
)