package me.yeojoy.twitteronmap.network.model

import android.os.Parcel
import android.os.Parcelable

data class Geo(
    val coordinates: List<Double>,
    val type: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            arrayListOf<Double>().apply {
                parcel.readArrayList(Double::class.java.classLoader)
            }, parcel.readString()!!)

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeList(coordinates)
        dest?.writeString(type)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Geo> {
        override fun createFromParcel(parcel: Parcel): Geo {
            return Geo(parcel)
        }

        override fun newArray(size: Int): Array<Geo?> {
            return arrayOfNulls(size)
        }
    }

}