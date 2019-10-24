package io.demo.fedchenko.giphyclient.model

data class GifModel(
    val original: GifProperties,
    val preview: GifProperties
)

data class GifProperties(
    val width: Int,
    val height: Int,
    val url: String
)