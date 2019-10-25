package io.demo.fedchenko.giphyclient.model

data class GifModel(
    val original: GifProperties,
    val preview: GifProperties,
    val userName: String,
    val title: String,
    val importDateTime: String
)

data class GifProperties(
    val width: Int,
    val height: Int,
    val url: String,
    val size: Int
)