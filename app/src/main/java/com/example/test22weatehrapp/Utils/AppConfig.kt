package com.example.test22weatehrapp.Utils

import android.app.Application
import com.example.test22weatehrapp.dependency.injection.networkModule
import com.example.test22weatehrapp.dependency.injection.repositoryModule
import com.example.test22weatehrapp.dependency.injection.serializerModule
import com.example.test22weatehrapp.dependency.injection.storageModule
import com.example.test22weatehrapp.dependency.injection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppConfig : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppConfig)
            modules(
                listOf(
                    repositoryModule,
                    viewModelModule,
                    serializerModule,
                    storageModule,
                    networkModule
                )
            )
        }
    }
}