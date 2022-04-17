package com.compose.stock.domain.repository

import com.compose.stock.domain.model.CompanyListing
import com.compose.stock.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

}