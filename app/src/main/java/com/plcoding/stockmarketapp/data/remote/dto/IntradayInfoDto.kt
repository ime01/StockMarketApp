package com.plcoding.stockmarketapp.data.remote.dto

import com.squareup.moshi.Json

data class IntradayInfoDto(
    val timestamp:String,
    val close:Double,

)
