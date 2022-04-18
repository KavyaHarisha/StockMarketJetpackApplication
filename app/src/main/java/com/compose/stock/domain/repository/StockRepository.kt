package com.compose.stock.domain.repository

import com.compose.stock.domain.model.CompanyInfo
import com.compose.stock.domain.model.CompanyListing
import com.compose.stock.domain.model.IntradayInfo
import com.compose.stock.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntrodayInfo(
        symbol: String
    ):Resource<List<IntradayInfo>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>

}