package io.demo.fedchenko.gyphyclient.model

import com.google.gson.annotations.SerializedName

class ResponseModel {
    @SerializedName("data")
    var GifModels: List<GifNotParsedModel> = emptyList()
}

class GifNotParsedModel() {
    @SerializedName("title")
    var title: String = ""

    @SerializedName("images")
    var images: Images = Images()
}

class Images(){
    @SerializedName("original")
    var gifInfo: GifInfo = GifInfo()
}

class GifInfo(){
    @SerializedName("width")
    var width: Int = 0

    @SerializedName("height")
    var height: Int = 0

    @SerializedName("url")
    var url: String = ""
}