package io.demo.fedchenko.giphyclient.model

import com.google.gson.annotations.SerializedName

class ResponseModel {
    @SerializedName("data")
    val gifModels: List<GifNotParsedModel> = emptyList()
}

class GifNotParsedModel {
    @SerializedName("images")
    val images: Images = Images()

    @SerializedName("user")
    val user: User = User()

    @SerializedName("title")
    val title: String = ""

    @SerializedName("import_datetime")
    val importDateTime: String = ""
}

class Images {
    @SerializedName("original")
    val gifInfo: GifInfo = GifInfo()

    @SerializedName("preview_gif")
    val previewGifInfo: GifInfo = GifInfo()
}

class GifInfo {
    @SerializedName("width")
    val width: Int = 0

    @SerializedName("height")
    val height: Int = 0

    @SerializedName("url")
    val url: String = ""

    @SerializedName("size")
    val size: Int = 0
}

class User {
    @SerializedName("username")
    val name: String = ""
}