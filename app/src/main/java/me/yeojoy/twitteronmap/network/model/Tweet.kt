package me.yeojoy.twitteronmap.network.model

import com.google.gson.annotations.SerializedName

data class Tweet(
    @SerializedName("created_at") val createAt: String,
    val user: TwitterUser,
    val id: Long,
    val text: String,
    val geo: Geo
)