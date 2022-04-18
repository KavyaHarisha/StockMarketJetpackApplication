package com.compose.stock.di

import com.compose.stock.data.csv.CSVParser
import com.compose.stock.data.csv.CompanyListingParser
import com.compose.stock.data.csv.IntradayInfoParser
import com.compose.stock.data.repository.StockRepositoryImpl
import com.compose.stock.domain.model.CompanyListing
import com.compose.stock.domain.model.IntradayInfo
import com.compose.stock.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingParser(
        companyListingParser: CompanyListingParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}