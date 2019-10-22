package io.demo.fedchenko.giphyclient.model

import com.google.gson.annotations.SerializedName

class ResponseModel {
    @SerializedName("data")
    var gifModels: List<GifNotParsedModel> = emptyList()
}

class GifNotParsedModel {
    @SerializedName("images")
    var images: Images = Images()
}

class Images {
    @SerializedName("original")
    var gifInfo: GifInfo = GifInfo()
}

class GifInfo {
    @SerializedName("width")
    var width: Int = 0

    @SerializedName("height")
    var height: Int = 0

    @SerializedName("url")
    var url: String = ""
}

