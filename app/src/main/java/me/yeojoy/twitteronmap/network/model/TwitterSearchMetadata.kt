package me.yeojoy.twitteronmap.network.model

import com.google.gson.annotations.SerializedName

data class TwitterSearchMetadata(
    @SerializedName("completed_in") val completedIn: Float,
    @SerializedName("max_id") val maxId: Long,
    @SerializedName("max_id_str") val maxIdString: String,
    @SerializedName("next_results") val nextResultsUrl: String,
    @SerializedName("query") val query: String,
    @SerializedName("refresh_url") val refreshUrl: String,
    @SerializedName("count") val count: Int,
    @SerializedName("since_id") val sinceId: Long,
    @SerializedName("since_id_str") val sinceIdString: String
)