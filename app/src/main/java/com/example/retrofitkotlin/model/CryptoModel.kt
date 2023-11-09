package com.example.retrofitkotlin.model

import android.os.Parcel
import android.os.Parcelable

data class CryptoModel(

    val name: String = "",
    val price: Double = 0.0,
    val icon: String = "",
    val rank: Int = 0,
    val priceChange1d: Double = 0.0,
    val symbol: String = "",
    val marketCap : Double = 0.0,
    val volume : Double = 0.0,
    val availableSupply : Double = 0.0,
    val totalSupply: Double = 0.0,
    val twitterUrl: String = "",
    val websiteUrl: String = "",
    val id: String = ""

): Parcelable {

    constructor(parcel: Parcel) : this(

        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeString(icon)
        parcel.writeInt(rank)
        parcel.writeDouble(priceChange1d)
        parcel.writeString(symbol)
        parcel.writeDouble(marketCap)
        parcel.writeDouble(volume)
        parcel.writeDouble(availableSupply)
        parcel.writeDouble(totalSupply)
        parcel.writeString(twitterUrl)
        parcel.writeString(websiteUrl)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CryptoModel> {
        override fun createFromParcel(parcel: Parcel): CryptoModel {
            return CryptoModel(parcel)
        }

        override fun newArray(size: Int): Array<CryptoModel?> {
            return arrayOfNulls(size)
        }
    }

}

