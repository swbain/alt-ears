package com.altears.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class FestivalApi(private val client: HttpClient) {
    
    suspend fun getDashboard(festivalId: Int = FESTIVAL_ID): DashboardResponse {
        return client.get("$BASE_URL/api/v1/festival/$festivalId/dashboard").body()
    }
    
    companion object {
        private const val BASE_URL = "https://api.tmsqr.app"
        const val FESTIVAL_ID = 455
    }
}
