package com.plcoding.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import retrofit2.http.DELETE

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListings(companyListingEntities:List<CompanyListingEntity>)

    @Query("DELETE FROM companyListingEntity")
    suspend fun clearCompanyListings()

    @Query(""" SELECT * FROM companylistingentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR UPPER(:query) == symbol """)
    suspend fun searchCompanyListing(query:String): List<CompanyListingEntity>

}