package com.plcoding.stockmarketapp.data.repository

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.toCompanyInfo
import com.plcoding.stockmarketapp.data.mapper.toCompanyListing
import com.plcoding.stockmarketapp.data.mapper.toCompanyListingEntity
import com.plcoding.stockmarketapp.data.remote.StockApi
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(val api: StockApi, val db: StockDatabase, val companyListingsParser: CSVParser<CompanyListing>, val intradayInfoParser: CSVParser<IntradayInfo>): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(fetchFromRemote: Boolean, query: String): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))

            val localListings = dao.searchCompanyListing(query)

            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldLoadFromCache = !isDbEmpty && !fetchFromRemote

            if (shouldLoadFromCache){
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())

            }catch (e:IOException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data, $e"))
                null

            }catch (e:HttpException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data, $e"))
                null

            }

            remoteListings?.let { listings->
                dao.clearCompanyListings()
                dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() })
                emit(Resource.Success(
                    data = dao.searchCompanyListing("").map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))

            }



        }
    }

    override suspend fun getintradayInfo(symbol: String): Resource<List<IntradayInfo>> {
       return try {
           val response = api.getIntradayInfo(symbol)
           val results = intradayInfoParser.parse(response.byteStream())
           Resource.Success(results)

       } catch (e: IOException){
           e.printStackTrace()
           Resource.Error("Couldn't load intraday info")
       }catch (e:HttpException){
           e.printStackTrace()
           Resource.Error("Couldn't load intraday info")
       }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {

            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())


        } catch (e: IOException){
            e.printStackTrace()
            Resource.Error("Couldn't load company info")
        }catch (e:HttpException){
            e.printStackTrace()
            Resource.Error("Couldn't load company info")
        }
    }


}