package me.yeojoy.twitteronmap.network.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Tweets(
    @SerializedName("statuses") val tweets: List<Tweet>,
    @SerializedName("search_metadata") val searchMetadata: TwitterSearchMetadata
) : Parcelable {
    constructor(parcel: Parcel) : this(
            arrayListOf<Tweet>().apply {
                parcel.readArrayList(Tweet::class.java.classLoader)
            },
            parcel.readParcelable(TwitterSearchMetadata::class.java.classLoader)!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(tweets)
        parcel.writeParcelable(searchMetadata, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Tweets> {
        override fun createFromParcel(parcel: Parcel): Tweets {
            return Tweets(parcel)
        }

        override fun newArray(size: Int): Array<Tweets?> {
            return arrayOfNulls(size)
        }
    }
}
