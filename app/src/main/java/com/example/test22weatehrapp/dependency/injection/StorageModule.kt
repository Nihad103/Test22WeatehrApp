package com.example.test22weatehrapp.dependency.injection

import com.example.test22weatehrapp.storage.SharedPreferencesManager
import org.koin.dsl.module

val storageModule = module {
    single {
        SharedPreferencesManager(get(), get())
    }
}

// This is right code

//val storageModule = module {
//    single {
//        SharedPreferencesManager(context = get(), gson = get())
//    }
//}
