package com.plcoding.stockmarketapp.di

import android.app.Application
import androidx.room.Room
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.remote.StockApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun providesStockApi():StockApi{
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(StockApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()

    }

    @Provides
    @Singleton
    fun providesStockDatabase(app: Application):StockDatabase{
        return Room.databaseBuilder(
            app,
            StockDatabase::class.java,
            "stock.db"
        ).build()
    }


}