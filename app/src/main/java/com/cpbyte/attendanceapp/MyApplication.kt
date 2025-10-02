package com.cpbyte.attendanceapp

import android.app.Application
import io.ktor.http.ContentType
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(listOf(networkModule, repositoryModule, viewModelModule, dataStoreModule))
        }
    }
}