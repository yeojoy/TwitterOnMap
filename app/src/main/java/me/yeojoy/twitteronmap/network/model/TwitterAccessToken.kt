package me.yeojoy.twitteronmap.network.model

import com.google.gson.annotations.SerializedName

data class TwitterAccessToken(
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("access_token") val accessToken: String
)