package com.compose.stock.data.mapper

import com.compose.stock.data.local.CompanyListingEntity
import com.compose.stock.data.remote.dto.CompanyInfoDto
import com.compose.stock.domain.model.CompanyInfo
import com.compose.stock.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing():CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyListing.toCompanyListingEntity():CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo{
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}