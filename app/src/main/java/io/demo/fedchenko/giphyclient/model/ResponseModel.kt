package io.demo.fedchenko.giphyclient.model

import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("data") val gifModels: List<GifNotParsedModel> = emptyList()
)

data class GifNotParsedModel(
    @SerializedName("images") val images: Images? = null,
    @SerializedName("user") val user: User? = null,
    @SerializedName("title") val title: String = "",
    @SerializedName("import_datetime") val importDateTime: String = "",
    @SerializedName("id") val id: String = ""
)

data class Images(
    @SerializedName("original") val gifInfo: GifInfo? = null,
    @SerializedName("preview_gif") val previewGifInfo: GifInfo? = null
)

data class GifInfo(
    @SerializedName("width") val width: Int = 0,
    @SerializedName("height") val height: Int = 0,
    @SerializedName("url") val url: String = "",
    @SerializedName("size") val size: Int = 0
)

data class User(
    @SerializedName("username")
    val name: String = ""
)