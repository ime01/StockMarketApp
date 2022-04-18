package com.plcoding.stockmarketapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


data class CompanyListing(
    val name:String,
    val symbol:String,
    val exchange:String
)
