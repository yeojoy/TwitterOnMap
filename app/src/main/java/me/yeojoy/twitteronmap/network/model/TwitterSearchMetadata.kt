package me.yeojoy.twitteronmap.network.model

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readFloat(),
            parcel.readLong(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readLong(),
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(completedIn)
        parcel.writeLong(maxId)
        parcel.writeString(maxIdString)
        parcel.writeString(nextResultsUrl)
        parcel.writeString(query)
        parcel.writeString(refreshUrl)
        parcel.writeInt(count)
        parcel.writeLong(sinceId)
        parcel.writeString(sinceIdString)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<TwitterSearchMetadata> {
        override fun createFromParcel(parcel: Parcel): TwitterSearchMetadata {
            return TwitterSearchMetadata(parcel)
        }

        override fun newArray(size: Int): Array<TwitterSearchMetadata?> {
            return arrayOfNulls(size)
        }
    }
}