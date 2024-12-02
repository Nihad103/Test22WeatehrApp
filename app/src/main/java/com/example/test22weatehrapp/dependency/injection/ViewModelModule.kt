package com.example.test22weatehrapp.dependency.injection

import com.example.test22weatehrapp.fragments.home.HomeViewModel
import com.example.test22weatehrapp.fragments.location.LocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(weatherDataRepository = get()) }
    viewModel { LocationViewModel(weatherDataRepository = get()) }
}