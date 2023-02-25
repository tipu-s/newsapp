package com.androiddevs.news

import android.app.Application

class NewsApplication: Application() {
    lateinit var appService: AppService
    companion object{
        lateinit var instance: NewsApplication
    }

    override fun onCreate() {
        super.onCreate()
        appService = AppService(this)
        instance = this
    }

    //val appService by lazy { AppService(this) }
}