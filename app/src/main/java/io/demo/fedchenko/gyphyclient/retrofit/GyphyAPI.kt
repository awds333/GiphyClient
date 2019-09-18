package io.demo.fedchenko.gyphyclient.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface GyphyAPI {
    @GET("api.giphy.com/v1/gifs/search")
    fun fingGifsByTerm(@Query("q") q: String, @Query("offset") offset: Int = 0
                       , @Query("api_key") key: String = "g0huuU56R74KkSQCYLdzfqCDyr4JmssE")
}