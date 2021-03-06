package io.demo.fedchenko.giphyclient.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GifModel(
    val id: String,
    val original: GifProperties,
    val preview: GifProperties,
    val userName: String,
    val title: String,
    val importDateTime: String,
    var isFavorite: Boolean = false
):Parcelable

@Parcelize
data class GifProperties(
    val width: Int,
    val height: Int,
    val url: String,
    val size: Int
):Parcelable

//Подсвечивание ошибки - баг студии. Все нормально работает