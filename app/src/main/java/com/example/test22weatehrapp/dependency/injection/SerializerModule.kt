package com.example.test22weatehrapp.dependency.injection

import com.google.gson.Gson
import org.koin.dsl.module

val serializerModule = module {
    single { Gson() }
}