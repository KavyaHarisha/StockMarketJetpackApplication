package com.compose.stock.data.repository

import com.compose.stock.data.csv.CSVParser
import com.compose.stock.data.csv.CompanyListingParser
import com.compose.stock.data.local.StockDatabase
import com.compose.stock.data.mapper.toCompanyListing
import com.compose.stock.data.mapper.toCompanyListingEntity
import com.compose.stock.data.remote.StockApi
import com.compose.stock.domain.model.CompanyListing
import com.compose.stock.domain.repository.StockRepository
import com.compose.stock.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingParser: CSVParser<CompanyListing>
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListing = dao.searchCompanyListing(query = query)
            emit(Resource.Success(
                localListing.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListing.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = isDbEmpty.not() && fetchFromRemote.not()
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListing = try {
                val response = api.getListings()
                companyListingParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load the data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load the data"))
                null
            }
            remoteListing?.let { listing ->
                dao.clearCompanyListing()
                dao.insertCompanyListing(listing.map { it.toCompanyListingEntity() })
                emit(Resource.Success(
                    dao.searchCompanyListing("").map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }
}