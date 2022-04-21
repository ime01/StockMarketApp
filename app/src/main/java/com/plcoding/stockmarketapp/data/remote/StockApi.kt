package com.plcoding.stockmarketapp.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(
        @Query("apikey") apiKey: String = API_KEY
    ): ResponseBody

    companion object{
        const val API_KEY = "Q63Y9NX3TUF587NF"
        const val BASE_URL = "https://alphavantage.co"

        //const val API_KEY = "O0Y67BC89C103328"
       // const val BASE_URL = "https://alphavantage.co"
    }
}
