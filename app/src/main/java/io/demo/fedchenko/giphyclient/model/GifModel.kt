package io.demo.fedchenko.giphyclient.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GifModel(
    val original: GifProperties,
    val preview: GifProperties,
    val userName: String,
    val title: String,
    val importDateTime: String
):Parcelable

@Parcelize
data class GifProperties(
    val width: Int,
    val height: Int,
    val url: String,
    val size: Int
):Parcelable

//Подсвечивание ошибки - баг студии. Все нормально работает