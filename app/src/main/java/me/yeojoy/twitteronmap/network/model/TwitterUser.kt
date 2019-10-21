package me.yeojoy.twitteronmap.network.model

import com.google.gson.annotations.SerializedName

data class TwitterUser(
    @SerializedName("created_at") val createAt: String,
    @SerializedName("name") val name: String,
    @SerializedName("screen_name") val screenName: String,
    @SerializedName("id") val id: Long

)