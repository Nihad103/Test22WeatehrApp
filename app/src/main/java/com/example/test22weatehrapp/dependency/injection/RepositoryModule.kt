package com.example.test22weatehrapp.dependency.injection

import com.example.test22weatehrapp.network.repository.WeatherDataRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { WeatherDataRepository(weatherApi = get()) }
}