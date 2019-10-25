package io.demo.fedchenko.giphyclient.model

import com.google.gson.annotations.SerializedName

class ResponseModel {
    @SerializedName("data")
    var gifModels: List<GifNotParsedModel> = emptyList()
}

class GifNotParsedModel {
    @SerializedName("images")
    var images: Images = Images()

    @SerializedName("user")
    var user: User = User()

    @SerializedName("title")
    var title: String = ""

    @SerializedName("import_datetime")
    var importDateTime: String = ""
}

class Images {
    @SerializedName("original")
    var gifInfo: GifInfo = GifInfo()

    @SerializedName("preview_gif")
    var previewGifInfo: GifInfo = GifInfo()
}

class GifInfo {
    @SerializedName("width")
    var width: Int = 0

    @SerializedName("height")
    var height: Int = 0

    @SerializedName("url")
    var url: String = ""

    @SerializedName("size")
    var size: Int = 0
}

class User {
    @SerializedName("username")
    var name: String = ""
}