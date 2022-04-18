package com.compose.stock.presentation.company_Info

import com.compose.stock.domain.model.CompanyInfo
import com.compose.stock.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
