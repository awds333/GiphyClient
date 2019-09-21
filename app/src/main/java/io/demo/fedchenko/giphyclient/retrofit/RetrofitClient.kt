package io.demo.fedchenko.giphyclient.retrofit

import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var ourInstance: Retrofit? = null

    val instance: Retrofit
        get() {
            if(ourInstance == null){
                ourInstance = Retrofit.Builder()
                    .baseUrl("https://api.giphy.com/v1/gifs/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))

                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return ourInstance!!
        }
}