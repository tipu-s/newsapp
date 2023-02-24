package com.androiddevs.news

import android.app.Application

class NewsApplication: Application() {
    lateinit var appService: AppService

    override fun onCreate() {
        super.onCreate()
        appService = AppService(this)
    }

    //val appService by lazy { AppService(this) }
}