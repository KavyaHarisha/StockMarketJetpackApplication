package com.compose.stock.data.repository

import com.compose.stock.data.csv.CSVParser
import com.compose.stock.data.local.StockDatabase
import com.compose.stock.data.mapper.toCompanyInfo
import com.compose.stock.data.mapper.toCompanyListing
import com.compose.stock.data.mapper.toCompanyListingEntity
import com.compose.stock.data.remote.StockApi
import com.compose.stock.domain.model.CompanyInfo
import com.compose.stock.domain.model.CompanyListing
import com.compose.stock.domain.model.IntradayInfo
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
    private val companyListingParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>
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

    override suspend fun getIntrodayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol = symbol)
            val results = intradayInfoParser.parse(response.byteStream())
            Resource.Success(results)
        }catch (e: IOException){
            e.printStackTrace()
            Resource.Error("Couldn't load introday info")
        } catch (e: HttpException){
            e.printStackTrace()
            Resource.Error("Couldn't load introday info")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol = symbol)
            Resource.Success(result.toCompanyInfo())
        }catch (e: IOException){
            e.printStackTrace()
            Resource.Error("Couldn't load company info")
        } catch (e: HttpException){
            e.printStackTrace()
            Resource.Error("Couldn't load company info")
        }
    }


}