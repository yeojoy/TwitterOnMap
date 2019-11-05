package me.yeojoy.twitteronmap.network.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class TwitterUser(
    @SerializedName("created_at") val createAt: String,
    @SerializedName("name") val name: String,
    @SerializedName("screen_name") val screenName: String,
    @SerializedName("id") val id: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readLong())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(createAt)
        dest?.writeString(name)
        dest?.writeString(screenName)
        dest?.writeLong(id)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<TwitterUser> {
        override fun createFromParcel(parcel: Parcel): TwitterUser {
            return TwitterUser(parcel)
        }

        override fun newArray(size: Int): Array<TwitterUser?> {
            return arrayOfNulls(size)
        }
    }

}