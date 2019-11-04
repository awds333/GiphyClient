package io.demo.fedchenko.giphyclient.retrofit

import io.demo.fedchenko.giphyclient.model.ResponseModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyAPI {

    @GET("search")
    fun findGifsByTerm(
        @Query("q") term: String, @Query("limit") limit: Int
        , @Query("offset") offset: Int
        , @Query("api_key") key: String
    )
            : Single<ResponseModel>

    @GET("trending")
    fun getTrendingGifs(
        @Query("limit") limit: Int
        , @Query("offset") offset: Int
        , @Query("api_key") key: String
    )
            : Single<ResponseModel>
}