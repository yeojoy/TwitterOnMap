package me.yeojoy.twitteronmap.network.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Tweet(@SerializedName("created_at") val createAt: String, val user: TwitterUser,
                 val id: Long,
                 val text: String,
                 val geo: Geo) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!,
            parcel.readParcelable(TwitterUser::class.java.classLoader)!!,
            parcel.readLong(),
            parcel.readString()!!,
            parcel.readParcelable(Geo::class.java.classLoader)!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createAt)
        parcel.writeParcelable(user, flags)
        parcel.writeLong(id)
        parcel.writeString(text)
        parcel.writeParcelable(geo, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Tweet> {
        override fun createFromParcel(parcel: Parcel): Tweet {
            return Tweet(parcel)
        }

        override fun newArray(size: Int): Array<Tweet?> {
            return arrayOfNulls(size)
        }
    }
}