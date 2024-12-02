package com.example.test22weatehrapp.dependency.injection

import com.example.test22weatehrapp.network.api.WeatherApi
import com.example.test22weatehrapp.network.api.WeatherApi.Companion.BASE_URL
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    factory { okHttpClient() }
    single { retrofit(okHttpClient = get()) }
    factory { weatherAPI(retrofit= get()) }
}


private fun okHttpClient() = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .retryOnConnectionFailure(false)
    .build()

private fun retrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
    .client(OkHttpClient())
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private fun weatherAPI(retrofit: Retrofit) = retrofit.create(WeatherApi::class.java)