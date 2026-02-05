package com.altears.di

import com.altears.data.remote.FestivalApi
import com.altears.data.repository.FestivalRepository
import com.altears.domain.usecase.GetArtistDetailUseCase
import com.altears.domain.usecase.GetArtistsUseCase
import com.altears.domain.usecase.GetScheduleUseCase
import com.altears.domain.usecase.GetShowsUseCase
import com.altears.domain.usecase.ToggleScheduleUseCase
import com.altears.ui.artists.ArtistsViewModel
import com.altears.ui.schedule.ScheduleViewModel
import com.altears.ui.shows.ShowsViewModel
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Database
    single { createDatabase() }
    
    // HTTP Client
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 15000
            }
        }
    }
    
    // API
    singleOf(::FestivalApi)
    
    // Repository
    singleOf(::FestivalRepository)
    
    // Use cases
    singleOf(::GetArtistsUseCase)
    singleOf(::GetShowsUseCase)
    singleOf(::GetScheduleUseCase)
    singleOf(::GetArtistDetailUseCase)
    singleOf(::ToggleScheduleUseCase)
    
    // ViewModels
    viewModelOf(::ArtistsViewModel)
    viewModelOf(::ShowsViewModel)
    viewModelOf(::ScheduleViewModel)
}
